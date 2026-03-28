package com.xp1.society_and_contract.Item;

import com.xp1.society_and_contract.SocietyandContract;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    //创建物品的函数
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SocietyandContract.MOD_ID);

    //加入Team_Create_Material(队伍创建器的材料)的函数
    public  static final DeferredItem<Item> Team_Create_Material =
            ITEMS.register("material/team_create_material",() -> new Item(new Item.Properties())); //material类下的team_create_material

    //加入Team_Create_Tool(队伍创建器)的函数
    /*public  static final DeferredItem<Item> Team_Create_Tool =
            ITEMS.register("tool/team_create_tool",() -> new Item(new Item.Properties()));*/

    public static final DeferredItem<Item> Team_Create_Tool = ITEMS.register("tool/team_create_tool",
            () -> new TeamCreateTool(new Item.Properties().stacksTo(1)));

    //加入Team_Create_ingot(队伍创建器锭)的函数
    public  static final DeferredItem<Item> Team_Create_Ingot =
            ITEMS.register("material/team_create_ingot",() -> new Item(new Item.Properties()));

    //调用
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);//将添加物品加入进程
    }
}
