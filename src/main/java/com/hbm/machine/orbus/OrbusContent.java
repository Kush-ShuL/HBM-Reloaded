package com.hbm.machine.orbus;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
public final class OrbusContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<OrbusBlock> BLOCK=BLOCKS.register("machine_orbus",OrbusBlock::new);public static final DeferredItem<OrbusItem> ITEM=ITEMS.register("machine_orbus",()->new OrbusItem(BLOCK.get()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OrbusEntity>> ENTITY=ENTITIES.register("orbus",()->BlockEntityType.Builder.of(OrbusEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OrbusPort>> PORT=ENTITIES.register("orbus_port",()->BlockEntityType.Builder.of(OrbusPort::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((RegisterCapabilitiesEvent e)->e.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)));}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private OrbusContent(){}
}
