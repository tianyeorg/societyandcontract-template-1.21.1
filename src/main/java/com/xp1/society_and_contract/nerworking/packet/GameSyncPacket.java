package com.xp1.society_and_contract.nerworking.packet;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.client.data.ClientTeamData;
import com.xp1.society_and_contract.client.hud.TeamListOverlay;
import com.xp1.society_and_contract.server.data.CustomTeam;
import com.xp1.society_and_contract.server.data.MemberInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record GameSyncPacket(List<CustomTeam> teams) implements CustomPacketPayload {

    public static final Type<GameSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "game_sync")
    );

    public static final StreamCodec<FriendlyByteBuf, GameSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeVarInt(packet.teams().size());
                for (CustomTeam team : packet.teams()) {
                    buf.writeUtf(team.name);
                    buf.writeUtf(team.color.serialize());
                    buf.writeUUID(team.leaderUUID);
                    buf.writeUtf(team.leaderName);

                    // 成员列表
                    buf.writeVarInt(team.members.size());
                    for (MemberInfo m : team.members) {
                        buf.writeUUID(m.uuid);
                        buf.writeUtf(m.name);
                    }
                }
            },
            buf -> {
                int size = buf.readVarInt();
                List<CustomTeam> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    String name = buf.readUtf();
                    String colorStr = buf.readUtf();
                    var colorResult = TextColor.parseColor(colorStr);
                    TextColor color = colorResult.result().orElse(TextColor.fromLegacyFormat(ChatFormatting.WHITE));
                    UUID leaderUUID = buf.readUUID();
                    String leaderName = buf.readUtf();

                    // 读取成员列表
                    int memberSize = buf.readVarInt();
                    List<MemberInfo> members = new ArrayList<>(memberSize);
                    for (int j = 0; j < memberSize; j++) {
                        UUID uuid = buf.readUUID();
                        String mName = buf.readUtf();
                        members.add(new MemberInfo(uuid, mName));
                    }

                    CustomTeam team = new CustomTeam(name, color, leaderUUID, leaderName);
                    team.members = members;
                    list.add(team);
                }
                return new GameSyncPacket(list);
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GameSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientTeamData.TEAMS.clear();
            for (CustomTeam t : packet.teams()) {
                int argb = 0xFF000000 | (t.color.getValue() & 0xFFFFFF);
                String leaderName = t.leaderName != null && !t.leaderName.isEmpty() ? t.leaderName : "未知";
                ClientTeamData.TEAMS.add(new TeamListOverlay.TeamEntry(t.name, argb, leaderName, t.getMemberCount()));
            }
        });
    }
}