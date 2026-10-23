package com.hbm.machine.massstorage;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Source mass_storage, four original metadata tiers, one actual inventory/stockpile. */
public final class MassStorageContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<MassStorageBlock> BLOCK=BLOCKS.register("mass_storage",MassStorageBlock::new);
    public static final DeferredItem<MassStorageItem> ITEM=ITEMS.register("mass_storage",()->new MassStorageItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MassStorageBlockEntity>> ENTITY=ENTITIES.register("mass_storage",()->BlockEntityType.Builder.of(MassStorageBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<MassStorageMenu>> MENU=MENUS.register("mass_storage",()->new MenuType<>((IContainerFactory<MassStorageMenu>)MassStorageMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(MassStoragePackets::register);bus.addListener(MassStorageContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(storage,side)->new SidedInvWrapper(storage,side==null?net.minecraft.core.Direction.DOWN:side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(ITEM.get().stack(3),ITEM.get().stack(0),ITEM.get().stack(1),ITEM.get().stack(2)):List.of();}
    private MassStorageContent(){}
}
