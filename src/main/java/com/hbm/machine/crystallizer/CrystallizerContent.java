package com.hbm.machine.crystallizer;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class CrystallizerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<CrystallizerBlock> BLOCK=BLOCKS.register("machine_crystallizer",CrystallizerBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final java.util.List<DeferredItem<EffectUpgradeItem>> UPGRADES=java.util.stream.IntStream.rangeClosed(1,3).mapToObj(tier->ITEMS.register("upgrade_effect_"+tier,()->new EffectUpgradeItem(tier))).toList();
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CrystallizerBlockEntity>> ENTITY=ENTITIES.register("crystallizer",()->BlockEntityType.Builder.of(CrystallizerBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CrystallizerPort>> PORT=ENTITIES.register("crystallizer_port",()->BlockEntityType.Builder.of(CrystallizerPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<CrystallizerMenu>> MENU=MENUS.register("crystallizer",()->new MenuType<>((IContainerFactory<CrystallizerMenu>)CrystallizerMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<CrystallizerRecipe>> TYPE=TYPES.register("crystallizer",()->new RecipeType<CrystallizerRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,CrystallizerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("crystallizer",CrystallizerRecipe.Serializer::new);
    private CrystallizerContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);CrystallizerRecipeConfig.register();bus.addListener(CrystallizerContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(port,side)->port.core()==null?null:side==null?new InvWrapper(port.core()):new SidedInvWrapper(port.core(),side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("control")?UPGRADES.stream().map(i->new ItemStack(i.get())).toList():List.of();}
}
