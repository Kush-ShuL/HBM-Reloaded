package com.hbm.machine.storage;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Source storage crates and safe, sharing one real inventory and the original lock base. */
public final class StorageCrateContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPES=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,StorageUpgradeRecipe.Serializer> UPGRADE=RECIPES.register("storage_upgrade",StorageUpgradeRecipe.Serializer::new);
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final Map<StorageCrateKind,DeferredBlock<StorageCrateBlock>> BLOCK=new EnumMap<>(StorageCrateKind.class);
    public static final Map<StorageCrateKind,DeferredItem<StorageCrateItem>> ITEM=new EnumMap<>(StorageCrateKind.class);
    public static final Map<StorageCrateKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<StorageCrateBlockEntity>>> ENTITY=new EnumMap<>(StorageCrateKind.class);
    public static final DeferredHolder<MenuType<?>,MenuType<StorageCrateMenu>> MENU=MENUS.register("storage_crate",()->new MenuType<>((IContainerFactory<StorageCrateMenu>)StorageCrateMenu::new,FeatureFlags.DEFAULT_FLAGS));
    static{for(var kind:StorageCrateKind.values()){
        BLOCK.put(kind,BLOCKS.register(kind.id,()->new StorageCrateBlock(kind)));ITEM.put(kind,ITEMS.register(kind.id,()->new StorageCrateItem(BLOCK.get(kind).get())));
        ENTITY.put(kind,ENTITIES.register(kind.id,()->BlockEntityType.Builder.of((pos,state)->createEntity(kind,pos,state),BLOCK.get(kind).get()).build(null)));
    }}
    public static StorageCrateBlockEntity createEntity(StorageCrateKind kind,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.state.BlockState state){
        return kind==StorageCrateKind.TUNGSTEN?new com.hbm.machine.tungstencrate.TungstenCrateBlockEntity(pos,state):new StorageCrateBlockEntity(kind,pos,state);
    }
    public static void register(IEventBus bus,ModContainer container){StorageCrateConfig.register(container);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);RECIPES.register(bus);bus.addListener(StorageCrateContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){for(var kind:StorageCrateKind.values())event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(kind).get(),(crate,side)->new SidedInvWrapper(crate,side==null?net.minecraft.core.Direction.DOWN:side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?ITEM.values().stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private StorageCrateContent(){}
}
