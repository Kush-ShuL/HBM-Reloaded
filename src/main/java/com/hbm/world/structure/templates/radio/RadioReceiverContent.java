package com.hbm.world.structure.templates.radio;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.*;
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public final class RadioReceiverContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<RadioReceiverBlock> BLOCK=BLOCKS.register("radiorec",RadioReceiverBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("radiorec",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioReceiverBlockEntity>> ENTITY=ENTITIES.register("radiorec",()->BlockEntityType.Builder.of(RadioReceiverBlockEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    @SubscribeEvent public static void payloads(RegisterPayloadHandlersEvent event){RadioReceiverControl.register(event);}
    private RadioReceiverContent(){}
}
