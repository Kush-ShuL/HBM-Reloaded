package com.hbm.network.rtty;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RadioTorchContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<RadioTorchBlock> SENDER=BLOCKS.register("radio_torch_sender",()->new RadioTorchBlock(true)),RECEIVER=BLOCKS.register("radio_torch_receiver",()->new RadioTorchBlock(false));public static final DeferredItem<RadioTorchItem> SENDER_ITEM=ITEMS.register("radio_torch_sender",()->new RadioTorchItem(true)),RECEIVER_ITEM=ITEMS.register("radio_torch_receiver",()->new RadioTorchItem(false));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioTorchSenderEntity>> SENDER_ENTITY=ENTITIES.register("radio_torch_sender",()->BlockEntityType.Builder.of(RadioTorchSenderEntity::new,SENDER.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioTorchReceiverEntity>> RECEIVER_ENTITY=ENTITIES.register("radio_torch_receiver",()->BlockEntityType.Builder.of(RadioTorchReceiverEntity::new,RECEIVER.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(RadioTorchControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(SENDER_ITEM.get()),new ItemStack(RECEIVER_ITEM.get())):List.of();}
 private RadioTorchContent(){}
}
