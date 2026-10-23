package com.hbm.machine.reactor.rbmk.panels.gauge;
import com.hbm.item.BasicBlockItem;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelBlock;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RbmkGaugeContent{
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<RbmkRadioPanelBlock> BLOCK=BLOCKS.register("rbmk_gauge",()->new RbmkRadioPanelBlock(()->RbmkGaugeContent.ENTITY.get()));public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("rbmk_gauge",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"BlockGenericTooltip","rbmk_gauge"));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkGaugeEntity>> ENTITY=ENTITIES.register("rbmk_gauge",()->BlockEntityType.Builder.of(RbmkGaugeEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private RbmkGaugeContent(){}
}
