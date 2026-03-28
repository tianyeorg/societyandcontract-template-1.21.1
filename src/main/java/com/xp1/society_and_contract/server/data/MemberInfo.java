package com.xp1.society_and_contract.server.data;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.UUID;

public class MemberInfo {
    public UUID uuid;
    public String name;  // 加入时的名字

    public MemberInfo(UUID uuid, String name) {
        this.uuid = Objects.requireNonNull(uuid, "UUID cannot be null");
        this.name = Objects.requireNonNullElse(name, "未知");
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        tag.putString("name", name);
        return tag;
    }

    public static MemberInfo load(CompoundTag tag) {
        UUID uuid = tag.getUUID("uuid");
        String name = tag.getString("name");
        return new MemberInfo(uuid, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberInfo that = (MemberInfo) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}