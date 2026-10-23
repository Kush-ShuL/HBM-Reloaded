package com.hbm.block.laminatepane;

import com.hbm.world.structure.dependencies.industrial44.BlastPaneItem;
import com.hbm.world.structure.dependencies.industrial44.ReinforcedPaneBlock;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source BlockNTMGlassPane(1,rock,true), with the original shared pane geometry/connection/waterlogging backend. */
public final class LaminatePaneContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ReinforcedPaneBlock> PANE=BLOCKS.register("reinforced_laminate_pane",()->new ReinforcedPaneBlock(
        BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(15,180).sound(SoundType.STONE)
            .requiresCorrectToolForDrops().noOcclusion().isValidSpawn((state,world,pos,type)->false)
            .isViewBlocking((state,world,pos)->false).isSuffocating((state,world,pos)->false),true));
    public static final DeferredItem<BlastPaneItem> ITEM=ITEMS.register("reinforced_laminate_pane",()->new BlastPaneItem(PANE.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
    private LaminatePaneContent(){}
}
