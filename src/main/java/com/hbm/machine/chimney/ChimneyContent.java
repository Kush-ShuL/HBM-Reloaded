package com.hbm.machine.chimney;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.*;
public final class ChimneyContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<ChimneyBlock> BRICK=BLOCKS.register("chimney_brick",()->new ChimneyBlock(false)),INDUSTRIAL=BLOCKS.register("chimney_industrial",()->new ChimneyBlock(true));public static final DeferredItem<BlockItem> BRICK_ITEM=ITEMS.registerSimpleBlockItem("chimney_brick",BRICK),INDUSTRIAL_ITEM=ITEMS.registerSimpleBlockItem("chimney_industrial",INDUSTRIAL);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChimneyBlockEntity>> ENTITY=ENTITIES.register("chimney",()->BlockEntityType.Builder.of(ChimneyBlockEntity::new,BRICK.get(),INDUSTRIAL.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChimneyProxy>> PROXY=ENTITIES.register("chimney_proxy",()->BlockEntityType.Builder.of(ChimneyProxy::new,BRICK.get(),INDUSTRIAL.get()).build(null));
 public static void register(IEventBus bus,ModContainer container){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);container.registerConfig(ModConfig.Type.COMMON,ChimneyConfig.SPEC,"hbm-smokestacks.toml");}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(BRICK_ITEM.get()),new ItemStack(INDUSTRIAL_ITEM.get())):List.of();}private ChimneyContent(){}
}
