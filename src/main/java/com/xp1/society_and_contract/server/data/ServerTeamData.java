package com.xp1.society_and_contract.server.data;

import com.xp1.society_and_contract.nerworking.packet.GameSyncPacket;
import com.xp1.society_and_contract.nerworking.packet.PlayerDataPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerTeamData extends SavedData {
    private final List<CustomTeam> teams = new ArrayList<>();

    public static ServerTeamData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ServerTeamData::new, ServerTeamData::load),
                "custom_teams"
        );
    }

    public static ServerTeamData load(CompoundTag tag, HolderLookup.Provider provider) {
        ServerTeamData data = new ServerTeamData();
        ListTag list = tag.getList("teams", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            data.teams.add(CustomTeam.load(list.getCompound(i)));
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ListTag list = new ListTag();
        for (CustomTeam team : teams) {
            list.add(team.save());
        }
        tag.put("teams", list);
        return tag;
    }

    // 统一全服同步队伍列表
    private void syncAll(ServerLevel level) {
        GameSyncPacket packet = new GameSyncPacket(getTeams());
        MinecraftServer server = level.getServer();
        if (server != null) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                PacketDistributor.sendToPlayer(p, packet);
            }
        }
    }

    // 统一发送个人侧边栏数据给指定玩家
    private void syncPersonalData(ServerPlayer player) {
        PlayerPersonalData info = loadPlayerPersonalData(player);
        PlayerDataPacket packet = new PlayerDataPacket(
                info.teamName,info.teamLeader ,info.teamColor, info.position, info.money
        );
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static PlayerPersonalData loadPlayerPersonalData(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        PlayerPersonalData info = new PlayerPersonalData();
        if (persistent.contains("PlayerPersonalData")) {
            info.load(persistent.getCompound("PlayerPersonalData"));
        }
        return info;
    }

    public static void savePlayerPersonalData(ServerPlayer player, PlayerPersonalData info) {
        CompoundTag persistent = player.getPersistentData();
        persistent.put("PlayerPersonalData", info.save());
    }

    public List<CustomTeam> getTeams() {
        return teams;
    }

    public boolean addTeam(String name, TextColor color, ServerPlayer leader) {
        if (teams.stream().anyMatch(t -> t.name.equalsIgnoreCase(name))) {
            return false;
        }
        teams.add(new CustomTeam(name, color, leader.getUUID(), leader.getGameProfile().getName()));

        // 更新队长个人数据
        PlayerPersonalData info = loadPlayerPersonalData(leader);
        info.teamName = name;
        info.teamLeader = String.valueOf(leader.getName());
        info.teamColor = color.getValue();
        info.position = "队长";
        savePlayerPersonalData(leader, info);

        // 同步：全服队伍列表 + 队长个人数据
        syncAll(leader.serverLevel());
        syncPersonalData(leader);

        setDirty();
        return true;
    }

    public boolean removeTeam(String name, ServerLevel level) {
        boolean removed = false;
        CustomTeam teamToRemove = null;

        // 找到要删除的队伍
        for (CustomTeam team : teams) {
            if (team.name.equalsIgnoreCase(name)) {
                teamToRemove = team;
                removed = true;
                break;
            }
        }

        if (!removed) {
            return false;
        }

        // 重置在线成员的个人数据 + 从队伍列表移除（关键！）
        for (MemberInfo member : teamToRemove.members) {
            ServerPlayer onlinePlayer = level.getServer().getPlayerList().getPlayer(member.uuid);
            if (onlinePlayer != null) {
                PlayerPersonalData info = loadPlayerPersonalData(onlinePlayer);
                info.teamName = "无队伍";
                info.teamLeader="无队伍";
                info.teamColor = 0xFFFFFFFF;
                info.position = "无职位";
                savePlayerPersonalData(onlinePlayer, info);

                // 发送更新包
                PlayerDataPacket packet = new PlayerDataPacket(
                        info.teamName,info.teamLeader, info.teamColor, info.position, info.money
                );
                PacketDistributor.sendToPlayer(onlinePlayer, packet);
            }
        }

        // 关键修复：清空队伍成员列表（防止幽灵成员）
        teamToRemove.members.clear();

        // 删除队伍
        teams.remove(teamToRemove);
        setDirty();

        // 全服同步队伍列表（让其他玩家 HUD 更新）
        syncAll(level);

        return true;
    }

    public boolean joinTeam(String teamName, ServerPlayer player) {
        for (CustomTeam team : teams) {
            if (team.name.equalsIgnoreCase(teamName)) {
                if (team.join(player)) {
                    PlayerPersonalData info = loadPlayerPersonalData(player);
                    info.teamName = team.name;
                    info.teamLeader=team.leaderName;
                    info.teamColor = team.color.getValue();
                    info.position = "成员";
                    savePlayerPersonalData(player, info);

                    syncPersonalData(player);
                    syncAll(player.serverLevel());
                    setDirty();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean leaveTeam(String teamName, ServerPlayer player) {
        UUID uuid = player.getUUID();
        for (CustomTeam team : teams) {
            if (team.name.equalsIgnoreCase(teamName)) {
                if (team.leave(uuid)) {
                    PlayerPersonalData info = loadPlayerPersonalData(player);
                    info.teamName = "无队伍";
                    info.teamLeader="无队伍";
                    info.teamColor = 0xFFFFFFFF;
                    info.position = "无职位";
                    savePlayerPersonalData(player, info);

                    syncPersonalData(player);
                    syncAll(player.serverLevel());
                    setDirty();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    // 获取指定队伍的成员列表（名字 + UUID）
    @Nullable
    public List<MemberInfo> getTeamMembers(String teamName) {
        for (CustomTeam team : teams) {
            if (team.name.equalsIgnoreCase(teamName)) {
                return team.members;
            }
        }
        return null;
    }

    // 获取指定队伍的成员名字列表
    @Nullable
    public List<String> getTeamMemberNames(String teamName) {
        List<MemberInfo> members = getTeamMembers(teamName);
        if (members == null) return null;
        return members.stream().map(m -> m.name).collect(Collectors.toList());
    }

    // 获取队伍人数
    @Nullable
    public Integer getTeamMemberCount(String teamName) {
        for (CustomTeam team : teams) {
            if (team.name.equalsIgnoreCase(teamName)) {
                return team.getMemberCount();
            }
        }
        return null;
    }
}
