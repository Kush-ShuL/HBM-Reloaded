package com.hbm.machine.bat9000;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

/** True deprecated-but-registered2048000mB BAT9000;Barrel menu/control remain the unique shared implementations. */
public final class Bat9000Content {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<Bat9000Block> BLOCK=BLOCKS.register("machine_bat9000",Bat9000Block::new);
    public static final DeferredItem<Bat9000Item> ITEM=ITEMS.register("machine_bat9000",()->new Bat9000Item(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Bat9000BlockEntity>> ENTITY=ENTITIES.register("bat9000",()->BlockEntityType.Builder.of(Bat9000BlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Bat9000Port>> PORT=ENTITIES.register("bat9000_port",()->BlockEntityType.Builder.of(Bat9000Port::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((RegisterCapabilitiesEvent event)->event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private Bat9000Content(){}
}
