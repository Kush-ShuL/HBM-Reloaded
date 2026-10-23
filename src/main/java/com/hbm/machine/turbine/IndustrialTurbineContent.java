package com.hbm.machine.turbine;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class IndustrialTurbineContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<IndustrialTurbineBlock> BLOCK=BLOCKS.register("machine_industrial_turbine",IndustrialTurbineBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("machine_industrial_turbine",()->new IndustrialTurbineItem(BLOCK.get(),new Item.Properties()));
    public static final List<DeferredItem<ExpensiveComponentItem>> EXPENSIVE_PARTS=ExpensiveComponentItem.register(ITEMS);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IndustrialTurbineBlockEntity>> ENTITY=ENTITIES.register("machine_industrial_turbine",()->BlockEntityType.Builder.of(IndustrialTurbineBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IndustrialTurbineProxy>> PROXY=ENTITIES.register("machine_industrial_turbine_proxy",()->BlockEntityType.Builder.of(IndustrialTurbineProxy::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(IndustrialTurbineConfig::load));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("parts")?ExpensiveComponentItem.creativeStacks():List.of();}
    private IndustrialTurbineContent(){}
}
