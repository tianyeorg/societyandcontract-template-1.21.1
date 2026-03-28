package com.xp1.society_and_contract.datagen;

import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

//方块的状态,模型贴图
public class ModBlockStatesProvider extends BlockStateProvider {
    public ModBlockStatesProvider(PackOutput output, ExistingFileHelper existingFileHelper){
        super(output, SocietyandContract.MOD_ID,existingFileHelper);
    }

    //生成方块json
    protected void CustomSimpleBlock(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        simpleBlock(
                block,
                models().cubeAll(
                        "block/" + name,    //json路径 models下block/+name
                        modLoc("block/" + name) //json内容
                )
        );
    }

    //注册方块
    @Override
    protected void registerStatesAndModels() {
        CustomSimpleBlock(ModBlocks.TEAM_CREAT_ORE.get());
    }
}
