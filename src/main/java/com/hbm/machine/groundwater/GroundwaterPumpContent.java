package com.hbm.machine.groundwater;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;

/** The two real no-menu/no-inventory MachinePump registrations. */
public final class GroundwaterPumpContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final Map<GroundwaterPumpKind,DeferredBlock<GroundwaterPumpBlock>> BLOCK=new EnumMap<>(GroundwaterPumpKind.class);
    public static final Map<GroundwaterPumpKind,DeferredItem<GroundwaterPumpItem>> ITEM=new EnumMap<>(GroundwaterPumpKind.class);
    static{for(var kind:GroundwaterPumpKind.values()){var block=BLOCKS.register(kind.id,()->new GroundwaterPumpBlock(kind));BLOCK.put(kind,block);ITEM.put(kind,ITEMS.register(kind.id,()->new GroundwaterPumpItem(block.get())));}}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SteamGroundwaterPump>> STEAM_ENTITY=ENTITIES.register("steam_groundwater_pump",()->BlockEntityType.Builder.of(SteamGroundwaterPump::new,BLOCK.get(GroundwaterPumpKind.STEAM).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ElectricGroundwaterPump>> ELECTRIC_ENTITY=ENTITIES.register("electric_groundwater_pump",()->BlockEntityType.Builder.of(ElectricGroundwaterPump::new,BLOCK.get(GroundwaterPumpKind.ELECTRIC).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GroundwaterPumpPort>> PORT=ENTITIES.register("groundwater_pump_port",()->BlockEntityType.Builder.of(GroundwaterPumpPort::new,BLOCK.get(GroundwaterPumpKind.STEAM).get(),BLOCK.get(GroundwaterPumpKind.ELECTRIC).get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(GroundwaterPumpSettings::load));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?Arrays.stream(GroundwaterPumpKind.values()).map(kind->new ItemStack(ITEM.get(kind).get())).toList():List.of();}
    private GroundwaterPumpContent(){}
}
