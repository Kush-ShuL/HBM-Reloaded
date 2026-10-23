package com.hbm.pneumatic.tube;
import com.hbm.pneumatic.anchor.*;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class PneumaticTubeContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<PneumoTubeBlock> TUBE=BLOCKS.register("pneumatic_tube",()->new PneumoTubeBlock(false)),PAINTABLE=BLOCKS.register("pneumatic_tube_paintable",()->new PneumoTubeBlock(true));
 public static final DeferredBlock<PneumaticAnchorBlock> ANCHOR=BLOCKS.register("pipe_anchor_pneumatic",PneumaticAnchorBlock::new);
 public static final DeferredItem<BlockItem> TUBE_ITEM=ITEMS.register("pneumatic_tube",()->new PneumaticTubeItem(TUBE.get(),"pneumatic_tube")),PAINT_ITEM=ITEMS.register("pneumatic_tube_paintable",()->new PneumaticTubeItem(PAINTABLE.get(),"pneumatic_tube_paintable")),ANCHOR_ITEM=ITEMS.register("pipe_anchor_pneumatic",()->new PneumaticTubeItem(ANCHOR.get(),"pipe_anchor_pneumatic"));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PneumoTubeBlockEntity>> ENTITY=ENTITIES.register("pneumatic_tube",()->BlockEntityType.Builder.of(PneumoTubeBlockEntity::new,TUBE.get(),PAINTABLE.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PneumaticAnchorBlockEntity>> ANCHOR_ENTITY=ENTITIES.register("pipe_anchor_pneumatic",()->BlockEntityType.Builder.of(PneumaticAnchorBlockEntity::new,ANCHOR.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<PneumoTubeMenu>> MENU=MENUS.register("pneumatic_tube",()->IMenuTypeExtension.create(PneumoTubeMenu::new));
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(TUBE_ITEM.get()),new ItemStack(PAINT_ITEM.get()),new ItemStack(ANCHOR_ITEM.get())):List.of();}private PneumaticTubeContent(){}
}
