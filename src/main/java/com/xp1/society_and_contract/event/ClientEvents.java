package com.xp1.society_and_contract.event;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.client.ModClientConstants;
import com.xp1.society_and_contract.client.ModKeyBindings;
import com.xp1.society_and_contract.client.hud.SidebarOverlay;
import com.xp1.society_and_contract.client.hud.TeamListOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = SocietyandContract.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    //GUI
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ModClientConstants.TEAM_LIST_OVERLAY, new TeamListOverlay());
        event.registerAboveAll(ModClientConstants.Sidebar_OVERLAY, new SidebarOverlay());

        // 或者用 lambda 包装（如果你想加一些条件逻辑）
        // event.registerAboveAll(id, (g, d) -> new TeamListOverlay().render(g, d));
    }

    @SubscribeEvent
    //按键注册
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.TEAM_LIST_KEY);
    }

    @SubscribeEvent
    //按键监听
    public static void onClientTick(ClientTickEvent.Post event) {
        TeamListOverlay.SHOW = ModKeyBindings.TEAM_LIST_KEY.isDown();
    }
}