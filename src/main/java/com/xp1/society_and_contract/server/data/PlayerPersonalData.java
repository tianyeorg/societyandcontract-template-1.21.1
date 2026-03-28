package com.xp1.society_and_contract.server.data;

import net.minecraft.nbt.CompoundTag;

public class PlayerPersonalData {
    public String teamName = "无队伍";
    public String teamLeader="无队伍";
    public int teamColor = 0xFFFFFFFF;  // ARGB
    public String position = "无职位";
    public int money = 0;

    public PlayerPersonalData() {}

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("teamName", teamName);
        tag.putString("teamLeader",teamLeader);
        tag.putInt("teamColor", teamColor);
        tag.putString("position", position);
        tag.putInt("money", money);
        return tag;
    }

    public void load(CompoundTag tag) {
        teamName = tag.getString("teamName");
        teamLeader=tag.getString("teamLeader");
        teamColor = tag.getInt("teamColor");
        position = tag.getString("position");
        money = tag.getInt("money");
    }
}