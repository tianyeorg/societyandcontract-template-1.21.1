package com.xp1.society_and_contract.Item;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.SocietyandContractClient;
import com.xp1.society_and_contract.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    //创建创造模式物品栏大类的函数
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS=
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SocietyandContract.MOD_ID);

    //创建societyandcontract_material_tab物品栏
    public static final Supplier<CreativeModeTab> SocietyandContract_Material_TAB=
            CREATIVE_MODE_TABS.register("societyandcontract_material_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.Team_Create_Material.get()))
                    .title(Component.translatable("itemGroup.societyandcontract_material_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.Team_Create_Material);
                        output.accept(ModItems.Team_Create_Ingot);
                    }).build());

    //创建societyandcontract_tool_tab物品栏
    public static final Supplier<CreativeModeTab> SocietyandContract_Tool_TAB=
            CREATIVE_MODE_TABS.register("societyandcontract_tool_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.Team_Create_Tool.get()))
                    .title(Component.translatable("itemGroup.societyandcontract_tool_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.Team_Create_Tool);
                    }).withTabsBefore(ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "societyandcontract_material_tab")).build());

    //创建societyandcontract_tool_ore物品栏
    public static final Supplier<CreativeModeTab> SocietyandContract_Block_TAB=
            CREATIVE_MODE_TABS.register("societyandcontract_block_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.TEAM_CREAT_ORE.get()))
                    .title(Component.translatable("itemGroup.societyandcontract_block_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.TEAM_CREAT_ORE);
                    }).withTabsBefore(ResourceLocation.fromNamespaceAndPath(SocietyandContract.MOD_ID, "societyandcontract_tool_tab")).build());

    //调用
    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus); //将CREATIVE_MODE_TABS添加进进程
    }
}
