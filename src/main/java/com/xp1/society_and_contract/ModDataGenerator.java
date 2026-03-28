package com.xp1.society_and_contract;

import com.xp1.society_and_contract.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SocietyandContract.MOD_ID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator=event.getGenerator();
        PackOutput packOutput=generator.getPackOutput();
        ExistingFileHelper existingFileHelper=event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider=event.getLookupProvider();

        //战利品表
        generator.addProvider(event.includeServer(),new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootsTablesProvider::new, LootContextParamSets.BLOCK)),lookupProvider));

        //配方表
        generator.addProvider(event.includeServer(),new ModRecipesProvider(packOutput,lookupProvider));

        //方块标签
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider,existingFileHelper);
        generator.addProvider(event.includeServer(),blockTagsProvider);
        //物品标签
        generator.addProvider(event.includeServer(),new ModItemTagsProvider(packOutput,lookupProvider,blockTagsProvider.contentsGetter(),existingFileHelper));
        //方块
        generator.addProvider(event.includeClient(),new ModBlockStatesProvider(packOutput,existingFileHelper));


        //模型
        generator.addProvider(event.includeClient(),new ModItemModelsProvider(packOutput,existingFileHelper));

        //语言
        generator.addProvider(event.includeClient(),new ModEnUsLangProvider(packOutput));
        generator.addProvider(event.includeClient(),new ModZhCnLangProvider(packOutput));
    }
}
