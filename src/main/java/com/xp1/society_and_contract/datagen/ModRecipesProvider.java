package com.xp1.society_and_contract.datagen;

import com.xp1.society_and_contract.Item.ModItems;
import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    //定义List属性变量,内容为List.of(物品1,物品2,物品n),所有在List.of内的物品都可以成为原料,然后在oreSmelting/Blasting中调用
    public static final List<ItemLike> TEAM_CREAT_INGOT=List.of(ModItems.Team_Create_Material);

    //创建合成表
    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        //TEAM_CREAT_INGOT冶炼
        oreSmelting(recipeOutput,TEAM_CREAT_INGOT,RecipeCategory.MISC,ModItems.Team_Create_Ingot,1.3F,200,"team_creat_ingot");
        oreBlasting(recipeOutput,TEAM_CREAT_INGOT,RecipeCategory.MISC,ModItems.Team_Create_Ingot,1.5F,100,"team_creat_ingot");

        //TEAM_CREAT_TOOL合成
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.Team_Create_Tool)
                .pattern("#Eg")
                .pattern("EPE")
                .pattern("iEc")
                .define('#', Items.DIAMOND)
                .define('E',ModItems.Team_Create_Ingot)
                .define('g', Items.GOLD_INGOT)
                .define('i',Items.IRON_INGOT)
                .define('c',Items.COPPER_INGOT)
                .define('P',Items.PAPER)
                .unlockedBy(getHasName(ModItems.Team_Create_Ingot),has(ModItems.Team_Create_Ingot))
                .save(recipeOutput); //.save(recipeOutput,MODid + ":" + "物品"),默认命名空间是合成物的命名空间,用此方法更改命名空间
    }


    //熔炉
    protected static void oreSmelting(
            RecipeOutput recipeOutput,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group
    ) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.SMELTING_RECIPE,
                SmeltingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_smelting");
    }

    //高炉
    protected static void oreBlasting(
            RecipeOutput recipeOutput,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group
    ) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.BLASTING_RECIPE,
                BlastingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_blasting"
        );
    }

    //冶炼函数
    protected static <T extends AbstractCookingRecipe> void oreCooking(
            RecipeOutput recipeOutput,
            RecipeSerializer<T> serializer,
            AbstractCookingRecipe.Factory<T> recipeFactory,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group,
            String suffix
    ) {
        for(ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(new ItemLike[]{itemlike}), category, result, experience, cookingTime, serializer, recipeFactory)
                    .group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, SocietyandContract.MOD_ID+":"+getItemName(result) + suffix + "_" + getItemName(itemlike));//改成本模组命名空间下的物品
        }

    }
}
