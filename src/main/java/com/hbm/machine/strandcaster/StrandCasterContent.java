package com.hbm.machine.strandcaster;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class StrandCasterContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<StrandCasterBlock> BLOCK=BLOCKS.register("machine_strand_caster",StrandCasterBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_strand_caster",BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<StrandCasterEntity>> ENTITY=BES.register("machine_strand_caster",()->BlockEntityType.Builder.of(StrandCasterEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<StrandCasterPort>> PORT=BES.register("strand_caster_port",()->BlockEntityType.Builder.of(StrandCasterPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<StrandCasterMenu>> MENU=MENUS.register("machine_strand_caster",()->new MenuType<>((IContainerFactory<StrandCasterMenu>)StrandCasterMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BES.register(bus);MENUS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}
