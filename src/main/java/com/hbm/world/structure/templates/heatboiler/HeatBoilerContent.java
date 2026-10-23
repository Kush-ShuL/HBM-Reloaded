package com.hbm.world.structure.templates.heatboiler;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class HeatBoilerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<HeatBoilerBlock> BLOCK=BLOCKS.register("machine_boiler",HeatBoilerBlock::new);
    public static final DeferredItem<HeatBoilerItem> ITEM=ITEMS.register("machine_boiler",()->new HeatBoilerItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HeatBoilerBlockEntity>> ENTITY=ENTITIES.register("machine_boiler",()->BlockEntityType.Builder.of(HeatBoilerBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HeatBoilerProxy>> PROXY=ENTITIES.register("machine_boiler_proxy",()->BlockEntityType.Builder.of(HeatBoilerProxy::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    private HeatBoilerContent(){}
}
