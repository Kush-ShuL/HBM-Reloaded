package com.hbm.machine.reactor.rbmk.thermal;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
public final class RbmkThermalContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RbmkThermalBlock> COOLER=BLOCKS.register("rbmk_cooler",()->new RbmkThermalBlock(false)),HEATER=BLOCKS.register("rbmk_heater",()->new RbmkThermalBlock(true));public static final DeferredItem<BlockItem> COOLER_ITEM=ITEMS.registerSimpleBlockItem(COOLER),HEATER_ITEM=ITEMS.registerSimpleBlockItem(HEATER);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkCoolerEntity>> COOLER_ENTITY=ENTITIES.register("rbmk_cooler",()->BlockEntityType.Builder.of(RbmkCoolerEntity::new,COOLER.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkHeaterEntity>> HEATER_ENTITY=ENTITIES.register("rbmk_heater",()->BlockEntityType.Builder.of(RbmkHeaterEntity::new,HEATER.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkThermalPort>> PORT=ENTITIES.register("rbmk_thermal_port",()->BlockEntityType.Builder.of(RbmkThermalPort::new,COOLER.get(),HEATER.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<RbmkHeaterMenu>> MENU=MENUS.register("rbmk_heater",()->IMenuTypeExtension.create(RbmkHeaterMenu::new));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(RbmkThermalContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,HEATER_ENTITY.get(),(entity,side)->side==null?new InvWrapper(entity):new SidedInvWrapper(entity,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(COOLER_ITEM.get()),new ItemStack(HEATER_ITEM.get())):List.of();}
    private RbmkThermalContent(){}
}
