package com.xp1.society_and_contract.datagen;

import com.xp1.society_and_contract.Item.ModItems;
import com.xp1.society_and_contract.SocietyandContract;
import com.xp1.society_and_contract.block.ModBlocks;
import com.xp1.society_and_contract.client.ModClientConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output) {
        super(output, SocietyandContract.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.Team_Create_Material.get(),"队伍创建器碎片");
        add(ModItems.Team_Create_Ingot.get(),"队伍创建器原锭");
        add(ModItems.Team_Create_Tool.get(),"队伍创建器");

        add(ModBlocks.TEAM_CREAT_ORE.get(),"队伍创建器原矿");

        add("itemGroup.societyandcontract_material_tab","社会与契约-原料");
        add("itemGroup.societyandcontract_block_tab","社会与契约-方块");
        add("itemGroup.societyandcontract_tool_tab","社会与契约-工具");

        add(ModClientConstants.KEY_CATEGORY,"社会与契约");
        add(ModClientConstants.KEY_TEAM_LIST,"显示队伍列表");
    }
}
