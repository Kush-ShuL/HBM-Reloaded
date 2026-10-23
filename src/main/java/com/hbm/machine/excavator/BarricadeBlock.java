package com.hbm.machine.excavator;
import com.mojang.serialization.MapCodec;import java.util.List;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.block.*;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.level.material.MapColor;import net.minecraft.world.level.storage.loot.LootParams;
/** Original BlockNoDrop(Material.sand), hardness1/resistance2.5, hidden from creative. */
public final class BarricadeBlock extends Block {public BarricadeBlock(){super(Properties.of().mapColor(MapColor.SAND).strength(1,1.5F).sound(SoundType.STONE));}@Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}@Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of();}}
