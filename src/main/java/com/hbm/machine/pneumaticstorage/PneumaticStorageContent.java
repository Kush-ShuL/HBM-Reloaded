package com.hbm.machine.pneumaticstorage;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class PneumaticStorageContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final Map<PneumaticKind,DeferredBlock<PneumaticStorageBlock>> BLOCK=new EnumMap<>(PneumaticKind.class);
    public static final Map<PneumaticKind,DeferredItem<BlockItem>> ITEM=new EnumMap<>(PneumaticKind.class);
    private static final Map<PneumaticKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<PneumaticInventory>>> ENTITY=new EnumMap<>(PneumaticKind.class);
    public static final DeferredHolder<MenuType<?>,MenuType<PneumaticStorageMenu>> MENU=MENUS.register("pneumatic_storage",()->new MenuType<>((IContainerFactory<PneumaticStorageMenu>)PneumaticStorageMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>,MenuType<PneumaticAccessMenu>> ACCESS_MENU=MENUS.register("pneumatic_storage_access",()->new MenuType<>((IContainerFactory<PneumaticAccessMenu>)PneumaticAccessMenu::new,FeatureFlags.DEFAULT_FLAGS));
    static{for(var kind:PneumaticKind.values()){var block=BLOCKS.register(kind.id(),()->new PneumaticStorageBlock(kind));BLOCK.put(kind,block);ITEM.put(kind,ITEMS.registerSimpleBlockItem(block));ENTITY.put(kind,ENTITIES.register(kind.id(),()->BlockEntityType.Builder.<PneumaticInventory>of((pos,state)->create(kind,pos,state),block.get()).build(null)));}}
    public static PneumaticInventory create(PneumaticKind kind,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.state.BlockState state){return switch(kind){case ACCESS->new PneumaticAccess(pos,state);case CLUTTER->new PneumaticClutter(pos,state);case MONO->new PneumaticMono(pos,state);case IMPORTER->new PneumaticImporter(pos,state);case EXPORTER->new PneumaticExporter(pos,state);};}
    public static BlockEntityType<PneumaticInventory> entity(PneumaticKind kind){return ENTITY.get(kind).get();}
    public static ResourceLocation id(String value){return ResourceLocation.fromNamespaceAndPath("hbm",value);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(PneumaticStoragePackets::register);bus.addListener(PneumaticStorageContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){for(var kind:PneumaticKind.values())if(kind==PneumaticKind.IMPORTER||kind==PneumaticKind.EXPORTER)event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,entity(kind),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?Arrays.stream(PneumaticKind.values()).map(kind->new ItemStack(ITEM.get(kind).get())).toList():List.of();}
    private PneumaticStorageContent(){}
}
