package com.xp1.society_and_contract.nerworking;

import com.xp1.society_and_contract.nerworking.packet.CreateTeamPacket;
import com.xp1.society_and_contract.nerworking.packet.GameSyncPacket;
import com.xp1.society_and_contract.nerworking.packet.PlayerDataPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ModNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1");

        registrar.playToClient(
                GameSyncPacket.TYPE,
                GameSyncPacket.STREAM_CODEC,
                GameSyncPacket::handle
        );

        registrar.playToClient(
                PlayerDataPacket.TYPE,
                PlayerDataPacket.STREAM_CODEC,
                PlayerDataPacket::handle
        );

        registrar.playToServer(
                CreateTeamPacket.TYPE,
                CreateTeamPacket.STREAM_CODEC,
                CreateTeamPacket::handle
        );
    }
}
