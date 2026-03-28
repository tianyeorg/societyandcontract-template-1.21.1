package com.xp1.society_and_contract.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.xp1.society_and_contract.nerworking.packet.GameSyncPacket;
import com.xp1.society_and_contract.server.data.CustomTeam;
import com.xp1.society_and_contract.server.data.ServerTeamData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("soc")
                        // 1. /soc addteam <队名> <颜色>
                        .then(Commands.literal("addteam")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.string())  // 先队名，支持中文/空格
                                        .then(Commands.argument("color", StringArgumentType.greedyString())  // 后颜色，支持 #HEX
                                                .executes(ModCommands::addTeam))))
                        // 2. /soc removeteam <队名>
                        .then(Commands.literal("removeteam")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> suggestTeamNames(context, builder))
                                        .executes(ModCommands::removeTeam)))
                        // 3. /soc jointeam <队伍名>
                        .then(Commands.literal("jointeam")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestTeamNames(ctx, builder))  // 自动提示队伍名
                                        .executes(ModCommands::joinTeam)))
                        // 4. /soc leaveteam <队伍名>
                        .then(Commands.literal("leaveteam")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestTeamNames(ctx, builder))
                                        .executes(ModCommands::leaveTeam)))
                        // 5. /soc memberlist [队伍名]（可选参数）
                        .then(Commands.literal("memberlist")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestTeamNames(ctx, builder))
                                        .executes(ModCommands::teamMemberList))
                                // 无参数时执行默认（自身队伍）
                                .executes(ModCommands::teamMemberListDefault)));

    }

    private static CompletableFuture<Suggestions> suggestTeamNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return Suggestions.empty();
        }
        ServerTeamData data = ServerTeamData.get(serverLevel);
        List<String> teamNames = new ArrayList<>();
        for (CustomTeam team : data.getTeams()) {
            teamNames.add(team.name);
        }
        return SharedSuggestionProvider.suggest(teamNames, builder);
    }

    private static int addTeam(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name").trim();
        String colorStr = StringArgumentType.getString(ctx, "color").trim();

        if (name.isEmpty()) {
            source.sendFailure(Component.literal("队伍名称不能为空"));
            return 0;
        }

        if (name.length() > 16) {
            source.sendFailure(Component.literal("队名最长16个字符"));
            return 0;
        }

        if (name.matches(".*[#@].*")) {  // 防特殊字符冲突
            source.sendFailure(Component.literal("队名不能包含 # 或 @"));
            return 0;
        }

        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("必须由玩家执行此命令"));
            return 0;
        }

        TextColor color;
        try {
            color = TextColor.parseColor(colorStr).getOrThrow();
        } catch (Exception e) {
            source.sendFailure(Component.literal("无效颜色: " + colorStr + " (试试 red, blue, #FF0000)"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("必须在世界中执行"));
            return 0;
        }

        ServerTeamData data = ServerTeamData.get(serverLevel);

        if (!data.addTeam(name, color, player)) {
            source.sendFailure(Component.literal("队伍名称 '" + name + "' 已存在"));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("已添加队伍: §r" + name + " (队长: 你, 人数: 1)"), true);
        return 1;
    }

    private static int removeTeam(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name").trim();

        if (name.isEmpty()) {
            source.sendFailure(Component.literal("队伍名称不能为空"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("必须在世界中执行"));
            return 0;
        }

        ServerTeamData data = ServerTeamData.get(serverLevel);

        if (data.removeTeam(name, serverLevel)) {  // 传入 level
            source.sendSuccess(() -> Component.literal("已删除队伍: " + name), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("未找到队伍: " + name));
            return 0;
        }
    }

    // 加入队伍
    private static int joinTeam(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String teamName = StringArgumentType.getString(ctx, "name").trim();

        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("必须由玩家执行"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("必须在世界中执行"));
            return 0;
        }

        ServerTeamData data = ServerTeamData.get(serverLevel);

        if (data.joinTeam(teamName, player)) {
            source.sendSuccess(() -> Component.literal("已加入队伍: " + teamName), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("加入失败：队伍不存在、已加入或错误"));
            return 0;
        }
    }

    // 新增：离开队伍
    private static int leaveTeam(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String teamName = StringArgumentType.getString(ctx, "name").trim();

        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("必须由玩家执行"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("必须在世界中执行"));
            return 0;
        }

        ServerTeamData data = ServerTeamData.get(serverLevel);
        if (data.leaveTeam(teamName, player)) {
            source.sendSuccess(() -> Component.literal("已离开队伍: " + teamName), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("离开失败：未加入该队伍或队伍不存在"));
            return 0;
        }
    }

    // 显示队伍成员列表
    private static int teamMemberList(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String teamName = StringArgumentType.getString(ctx, "name").trim();

        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("必须在世界中执行"));
            return 0;
        }

        ServerTeamData data = ServerTeamData.get(serverLevel);
        List<String> members = data.getTeamMemberNames(teamName);

        if (members == null) {
            source.sendFailure(Component.literal("队伍不存在: " + teamName));
            return 0;
        }

        if (members.isEmpty()) {
            source.sendSuccess(() -> Component.literal("队伍 " + teamName + " 目前没有成员"), false);
        } else {
            source.sendSuccess(() -> Component.literal("队伍 " + teamName + " 的成员: " + String.join(", ", members)), false);
        }
        return 1;
    }

    private static int teamMemberListDefault(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("必须由玩家执行"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("必须在世界中执行"));
            return 0;
        }

        ServerTeamData data = ServerTeamData.get(serverLevel);

        CustomTeam playerTeam = null;
        for (CustomTeam team : data.getTeams()) {
            // 修复：用 stream anyMatch 检查 UUID
            if (team.members.stream().anyMatch(m -> m.uuid.equals(player.getUUID()))) {
                playerTeam = team;
                break;
            }
        }

        if (playerTeam == null) {
            source.sendFailure(Component.literal("你当前没有加入任何队伍。请使用 /teammemberlist <队伍名> 查看"));
            return 0;
        }

        List<String> members = data.getTeamMemberNames(playerTeam.name);
        String message = "你的队伍 " + playerTeam.name + " 的成员: " + String.join(", ", members);
        source.sendSuccess(() -> Component.literal(message), false);
        return 1;
    }
}
