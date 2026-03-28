package com.xp1.society_and_contract.block;

import com.xp1.society_and_contract.Item.ModItems;
import com.xp1.society_and_contract.SocietyandContract;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    //注册方块的函数
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SocietyandContract.MOD_ID);

    //注册对应方块的item状态的函数
    private static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name,() -> new BlockItem(block.get(),new Item.Properties()));
    }

    //注册对应方块的方块状态，并注册对应方块的item状态(调用上面的函数)的函数
    private static <T extends Block> DeferredBlock<T> registerBlocks(String name, Supplier<T> block){
        DeferredBlock<T> blocks = BLOCKS.register(name,block);
        registerBlockItems("block/"+name,blocks);    //调用注册item的函数,"block/"适配models下item/block/后再跟方块名
        return blocks;
    }

    //创建team_create_ore(队伍创建器原矿)
    public static final DeferredBlock<Block> TEAM_CREAT_ORE=
            registerBlocks("ore/team_create_ore",() -> new Block(BlockBehaviour.Properties.of().strength(1.5F,6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    //调用
    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
