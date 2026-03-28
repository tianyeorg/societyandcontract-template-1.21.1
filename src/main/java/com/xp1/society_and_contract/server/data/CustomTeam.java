package com.xp1.society_and_contract.server.data;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomTeam {
    public String name;
    public TextColor color;
    public UUID leaderUUID;
    public String leaderName;
    public List<MemberInfo> members = new ArrayList<>();

    public CustomTeam(String name, TextColor color, UUID leaderUUID, String leaderName) {
        this.name = name;
        this.color = color;
        this.leaderUUID = leaderUUID;
        this.leaderName = leaderName != null && !leaderName.isEmpty() ? leaderName : "未知";

        // 队长加入成员列表（用 PlayerTeamData）
        MemberInfo leaderInfo = new MemberInfo(leaderUUID, leaderName);
        leaderInfo.uuid=leaderUUID;
        leaderInfo.name=leaderName;
        this.members.add(leaderInfo);
    }

    // 保存nbt
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("color", color.serialize());

        // 防 null：如果 leaderUUID null，不存这个字段
        if (leaderUUID != null) {
            tag.putUUID("leaderUUID", leaderUUID);
        }

        tag.putString("leaderName", leaderName);

        // 保存成员列表
        ListTag membersTag = new ListTag();
        for (MemberInfo member : members) {
            membersTag.add(member.save());
        }
        tag.put("members", membersTag);
        return tag;
    }

    // 从 NBT 加载
    public static CustomTeam load(CompoundTag tag) {
        String name = tag.getString("name");
        String colorStr = tag.getString("color");
        TextColor color = TextColor.parseColor(colorStr)
                .resultOrPartial(msg -> System.err.println("Invalid color: " + colorStr + " -> " + msg))
                .orElse(TextColor.fromLegacyFormat(ChatFormatting.WHITE));

        // 防 null：如果没有 "leaderUUID"，设为 null
        UUID leaderUUID = tag.contains("leaderUUID") ? tag.getUUID("leaderUUID") : null;

        String leaderName = tag.getString("leaderName");
        CustomTeam team = new CustomTeam(name, color, leaderUUID, leaderName);

        // 加载成员列表
        ListTag membersTag = tag.getList("members", Tag.TAG_COMPOUND);
        team.members.clear();
        for (int i = 0; i < membersTag.size(); i++) {
            team.members.add(MemberInfo.load(membersTag.getCompound(i)));
        }
        return team;
    }

    // 成员数直接计算
    public int getMemberCount() {
        return members.size();
    }

    public boolean join(ServerPlayer player) {
        UUID uuid = player.getUUID();
        // 检查是否已加入（用 UUID 判断）
        if (members.stream().anyMatch(m -> m.uuid.equals(uuid))) {
            return false;
        }
        // 创建 MemberInfo 对象（带名字）
        MemberInfo newMember = new MemberInfo(uuid, player.getGameProfile().getName());
        members.add(newMember);
        return true;
    }

    // 离开队伍
    public boolean leave(UUID uuid) {
        boolean removed = members.removeIf(m -> m.uuid.equals(uuid));
        if (removed) {
            if (uuid.equals(leaderUUID)) {
                if (!members.isEmpty()) {
                    // 自动转移队长给第一个成员
                    MemberInfo newLeader = members.get(0);
                    leaderUUID = newLeader.uuid;
                    leaderName = newLeader.name;
                } else {
                    // 队伍空了，清空队长
                    //leaderUUID = null;
                    leaderName = "无队长";
                }
            }
        }
        return removed;
    }
}