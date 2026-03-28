package com.xp1.society_and_contract.datagen;

import com.xp1.society_and_contract.Item.ModItems;
import com.xp1.society_and_contract.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

//方块的战利品表
public class ModBlockLootsTablesProvider extends BlockLootSubProvider {
    public ModBlockLootsTablesProvider(HolderLookup.Provider registries) {
        super(Set.of(),FeatureFlags.REGISTRY.allFlags(),registries);
    }


    //掉落物配置
    @Override
    protected void generate() {
        //dropSelf(ModBlocks.TEAM_CREAT_ORE.get());

        add(ModBlocks.TEAM_CREAT_ORE.get(),block -> CustomOreDrops(ModBlocks.TEAM_CREAT_ORE.get(), ModItems.Team_Create_Material.get(),2.0F,6.0F));
    }

    //矿物掉落的函数，可以配置所掉物品的多少
    protected LootTable.Builder CustomOreDrops(Block block,Item item,float min,float max) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                (LootPoolEntryContainer.Builder)this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
