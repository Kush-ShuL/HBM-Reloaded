package com.hbm.block.glowingasphalt;

import com.hbm.block.masonry.AsphaltBlock;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual X5846 BlockSpeedy(rock,1.5), reusing the sole masonry movement listener. */
public final class GlowingAsphaltContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<AsphaltBlock> ASPHALT_LIGHT=BLOCKS.register("asphalt_light",()->new AsphaltBlock(
        BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(15,72).sound(SoundType.STONE)
            .requiresCorrectToolForDrops().lightLevel(state->15)));
    public static final DeferredItem<GlowingAsphaltItem> ASPHALT_LIGHT_ITEM=ITEMS.register("asphalt_light",()->new GlowingAsphaltItem(ASPHALT_LIGHT.get(),new Item.Properties()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ASPHALT_LIGHT_ITEM.get())):List.of();}
    private GlowingAsphaltContent(){}
}
