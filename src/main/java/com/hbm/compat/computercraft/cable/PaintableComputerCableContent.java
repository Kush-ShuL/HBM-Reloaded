package com.hbm.compat.computercraft.cable;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.*;
/** Source conditional content: optional OC prerequisite is replaced by actual CC:Tweaked, never a decorative fallback. */
public final class PaintableComputerCableContent {
    public static final boolean ENABLED=ModList.get().isLoaded("computercraft");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<PaintableComputerCableBlock> CABLE=BLOCKS.register("oc_cable_paintable",PaintableComputerCableBlock::new);
    public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("oc_cable_paintable",()->new BasicBlockItem(CABLE.get(),new net.minecraft.world.item.Item.Properties(),"BlockGenericTooltip","oc_cable_paintable"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PaintableComputerCableEntity>> ENTITY=ENTITIES.register("oc_cable_paintable",()->BlockEntityType.Builder.of(PaintableComputerCableEntity::new,CABLE.get()).build(null));
    public static void register(IEventBus bus){if(!ENABLED)return;BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);PaintableCableCcIntegration.register(bus);}
    public static List<ItemStack> indexedItems(){return ENABLED?List.of(new ItemStack(ITEM.get())):List.of();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?indexedItems():List.of();}
    private PaintableComputerCableContent(){}
}
