package com.xp1.society_and_contract.nerworking.packet;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.client.data.ClientPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerDataPacket(String teamName, String teamLeader,int teamColor, String position, int money) implements CustomPacketPayload {
    public static final Type<PlayerDataPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "player_data")
    );

    public static final StreamCodec<FriendlyByteBuf, PlayerDataPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeUtf(packet.teamName);
                buf.writeUtf(packet.teamLeader);
                buf.writeInt(packet.teamColor);
                buf.writeUtf(packet.position);
                buf.writeInt(packet.money);
            },
            buf -> new PlayerDataPacket(buf.readUtf(), buf.readUtf(),buf.readInt(), buf.readUtf(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PlayerDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPlayerData.teamName = packet.teamName;
            ClientPlayerData.teamLeader=packet.teamLeader;
            ClientPlayerData.teamColor = packet.teamColor;
            ClientPlayerData.position = packet.position;
            ClientPlayerData.money = packet.money;
        });
    }
}