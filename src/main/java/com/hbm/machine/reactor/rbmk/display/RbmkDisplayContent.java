package com.hbm.machine.reactor.rbmk.display;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RbmkDisplayContent{
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<RbmkMiniPanelBlock> BLANK=BLOCKS.register("rbmk_display_blank",RbmkMiniPanelBlock::new);public static final DeferredBlock<RbmkDisplayBlock> DISPLAY=BLOCKS.register("rbmk_display",RbmkDisplayBlock::new);public static final DeferredItem<BasicBlockItem> BLANK_ITEM=ITEMS.register("rbmk_display_blank",()->new BasicBlockItem(BLANK.get(),new Item.Properties(),"BlockGenericTooltip","rbmk_display_blank")),DISPLAY_ITEM=ITEMS.register("rbmk_display",()->new BasicBlockItem(DISPLAY.get(),new Item.Properties(),"BlockGenericTooltip","rbmk_display"));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkDisplayEntity>> ENTITY=ENTITIES.register("rbmk_display",()->BlockEntityType.Builder.of(RbmkDisplayEntity::new,DISPLAY.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(BLANK_ITEM.get()),new ItemStack(DISPLAY_ITEM.get())):List.of();}private RbmkDisplayContent(){}
}
