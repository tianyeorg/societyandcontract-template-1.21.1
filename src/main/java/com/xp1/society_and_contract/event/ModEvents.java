package com.xp1.society_and_contract.event;

import com.xp1.society_and_contract.SocietyandContract;

import com.xp1.society_and_contract.client.ModCommands;
import com.xp1.society_and_contract.nerworking.ModNetworking;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = SocietyandContract.MOD_ID)
public class ModEvents {
    //添加命令
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    //注册网络
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        ModNetworking.register(event);
    }

}
