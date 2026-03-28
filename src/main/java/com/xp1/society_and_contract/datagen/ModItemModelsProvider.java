package com.xp1.society_and_contract.datagen;

import com.xp1.society_and_contract.Item.ModItems;
import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SocietyandContract.MOD_ID, existingFileHelper);
    }

    //注册物品
    public void CustomBasicItem(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        withExistingParent(
                "item/" + id.getPath(),
                mcLoc("item/generated")
        ).texture(
                "layer0",
                modLoc("item/" + id.getPath())
        );
    }

    //注册方块对应的物品
    protected void CustomBlockItem(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        withExistingParent(
                "item/" + id.getPath(),       //json路径,models下item+方块id
                modLoc(id.getPath())  //json指向路径
        );
    }


    @Override
    protected void registerModels() {
        CustomBasicItem(ModItems.Team_Create_Material.get());
        CustomBasicItem(ModItems.Team_Create_Ingot.get());
        CustomBasicItem(ModItems.Team_Create_Tool.get());

        CustomBlockItem(ModBlocks.TEAM_CREAT_ORE.get().asItem());
    }
}
