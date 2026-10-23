package com.hbm.machine.oilextraction;
import com.mojang.serialization.MapCodec;import java.util.List;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.block.*;import net.minecraft.world.level.material.MapColor;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.level.storage.loot.LootParams;
/** Source BlockNoDrop(Material.iron), solid drilling column; not a general liquid pipe. */
public final class OilPipeBlock extends Block {
 private static final MapCodec<OilPipeBlock> CODEC=simpleCodec(p->new OilPipeBlock());public OilPipeBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.STONE));}@Override protected MapCodec<? extends Block> codec(){return CODEC;}@Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
}
