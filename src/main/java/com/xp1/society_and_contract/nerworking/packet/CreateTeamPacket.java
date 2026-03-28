package com.xp1.society_and_contract.nerworking.packet;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.server.data.ServerTeamData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.chat.TextColor;

public record CreateTeamPacket(String teamName, int colorRGB) implements CustomPacketPayload {
    public static final Type<CreateTeamPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "create_team")
    );

    public static final StreamCodec<FriendlyByteBuf, CreateTeamPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeUtf(packet.teamName);
                buf.writeInt(packet.colorRGB);
            },
            buf -> new CreateTeamPacket(buf.readUtf(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CreateTeamPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                TextColor color = TextColor.fromRgb(packet.colorRGB());
                ServerTeamData data = ServerTeamData.get(player.serverLevel());
                if (data.addTeam(packet.teamName(), color, player)) {
                    player.sendSystemMessage(Component.literal("队伍创建成功：" + packet.teamName()));
                } else {
                    player.sendSystemMessage(Component.literal("创建失败：队伍名已存在"));
                }
            }
        });
    }
}