package com.xp1.society_and_contract.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {


    public static final KeyMapping TEAM_LIST_KEY = new KeyMapping(
            ModClientConstants.KEY_TEAM_LIST,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O, // 你想用哪个键自己改
            ModClientConstants.KEY_TEAM_LIST
    );
}
