package com.xp1.society_and_contract.event;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.nerworking.packet.GameSyncPacket;
import com.xp1.society_and_contract.nerworking.packet.PlayerDataPacket;
import com.xp1.society_and_contract.server.data.PlayerPersonalData;
import com.xp1.society_and_contract.server.data.ServerTeamData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SocietyandContract.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerTeamData data = ServerTeamData.get(player.serverLevel());

            // 发送队伍列表包
            GameSyncPacket teamPacket = new GameSyncPacket(data.getTeams());
            PacketDistributor.sendToPlayer(player, teamPacket);

            // 加载玩家个人数据
            PlayerPersonalData info = ServerTeamData.loadPlayerPersonalData(player);

            // 新增：检查队伍是否存在，如果不存在，重置为无队伍
            boolean teamExists = data.getTeams().stream()
                    .anyMatch(t -> t.name.equalsIgnoreCase(info.teamName));
            if (!teamExists && !info.teamName.equals("无队伍")) {
                info.teamName = "无队伍";
                info.teamLeader="无队伍";
                info.teamColor = 0xFFFFFFFF;
                info.position = "无职位";
                ServerTeamData.savePlayerPersonalData(player, info);
                System.out.println("玩家 " + player.getName().getString() + " 的队伍已删除，重置侧边栏数据");
            }

            // 发送个人数据包
            PlayerDataPacket personalPacket = new PlayerDataPacket(
                    info.teamName,info.teamLeader, info.teamColor, info.position, info.money
            );
            PacketDistributor.sendToPlayer(player, personalPacket);

            System.out.println("玩家 " + player.getName().getString() + " 登录，发送队伍 + 个人数据");
        }
    }
}
