package com.xp1.society_and_contract.client;

import com.xp1.society_and_contract.SocietyandContract;
import net.minecraft.resources.ResourceLocation;

public class ModClientConstants {

    // 所有 GUI overlay / HUD / 自定义界面的 ID 都放这里
    public static final ResourceLocation TEAM_LIST_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "team_list");
    public static final ResourceLocation Sidebar_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "sidebar");

    //按键
    public static final String KEY_CATEGORY =
            "key.category.society_and_contract";

    public static final String KEY_TEAM_LIST =
            "key.society_and_contract.team_list";


    // 如果以后有纹理、sprite、声音等，也放这里
    // public static final ResourceLocation GUI_BACKGROUND = 
    //     ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "textures/gui/my_gui.png");

    private ModClientConstants() {}
}