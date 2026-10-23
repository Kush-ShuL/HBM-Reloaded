package com.hbm.machine.keyforge;

import com.hbm.item.lock.*;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Original locksmith table and configured keys/locks, all source-visible entries in consumables. */
public final class KeyForgeContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<KeyForgeBlock> TABLE=BLOCKS.register("machine_keyforge",KeyForgeBlock::new);
    public static final DeferredItem<BlockItem> TABLE_ITEM=ITEMS.register("machine_keyforge",()->new BlockItem(TABLE.get(),new Item.Properties()));
    public static final DeferredItem<KeyPinItem> KEY=ITEMS.register("key",()->new KeyPinItem(false));
    public static final DeferredItem<KeyPinItem> FAKE_KEY=ITEMS.register("key_fake",()->new KeyPinItem(true));
    public static final DeferredItem<CounterfeitKeysItem> KIT=ITEMS.register("key_kit",CounterfeitKeysItem::new);
    public static final DeferredItem<PadlockItem> RUSTY=ITEMS.register("padlock_rusty",()->new PadlockItem(1));
    public static final DeferredItem<PadlockItem> PADLOCK=ITEMS.register("padlock",()->new PadlockItem(.1));
    public static final DeferredItem<PadlockItem> REINFORCED=ITEMS.register("padlock_reinforced",()->new PadlockItem(.02));
    public static final DeferredItem<PadlockItem> UNBREAKABLE=ITEMS.register("padlock_unbreakable",()->new PadlockItem(0));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<KeyForgeBlockEntity>> ENTITY=ENTITIES.register("key_forge",()->BlockEntityType.Builder.of(KeyForgeBlockEntity::new,TABLE.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<KeyForgeMenu>> MENU=MENUS.register("key_forge",()->new MenuType<>((IContainerFactory<KeyForgeMenu>)KeyForgeMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(KeyForgeContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(TABLE_ITEM.get()),new ItemStack(KEY.get()),new ItemStack(FAKE_KEY.get()),new ItemStack(KIT.get()),new ItemStack(RUSTY.get()),new ItemStack(PADLOCK.get()),new ItemStack(REINFORCED.get())):List.of();}
    private KeyForgeContent(){}
}
