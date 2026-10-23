package com.hbm.bomb.conventional;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class MultiBombContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<MultiBombBlock> BLOCK=BLOCKS.register("bomb_multi",MultiBombBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("bomb_multi",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredItem<MultiBombKitItem> KIT=ITEMS.register("multi_kit",MultiBombKitItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MultiBombBlockEntity>> ENTITY=ENTITIES.register("bomb_multi",()->BlockEntityType.Builder.of(MultiBombBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<MultiBombMenu>> MENU=MENUS.register("bomb_multi",()->new MenuType<>(MultiBombMenu::new,FeatureFlags.VANILLA_SET));
    private MultiBombContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(ITEM.get()),new ItemStack(KIT.get())):List.of();}
}
