package com.xp1.society_and_contract.datagen;

import com.xp1.society_and_contract.Item.ModItems;
import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.block.ModBlocks;
import com.xp1.society_and_contract.client.ModClientConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output) {
        super(output, SocietyandContract.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.Team_Create_Material.get(),"Team Create Material");
        add(ModItems.Team_Create_Ingot.get(),"Team Create Ingot");
        add(ModItems.Team_Create_Tool.get(),"Team Create Tool");

        add(ModBlocks.TEAM_CREAT_ORE.get(),"Team Create Ore");

        add("itemGroup.societyandcontract_material_tab","Society and Contract-Material");
        add("itemGroup.societyandcontract_block_tab","Society and Contract-Block");
        add("itemGroup.societyandcontract_tool_tab","Society and Contract-Tool");

        add(ModClientConstants.KEY_CATEGORY,"社会与契约");
        add(ModClientConstants.KEY_TEAM_LIST,"显示队伍列表");
    }
}
