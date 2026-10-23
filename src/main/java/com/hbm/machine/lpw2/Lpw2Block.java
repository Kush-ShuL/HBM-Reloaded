package com.hbm.machine.lpw2;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual source registered animated prototype; no GUI/ports/inventory/HE or processing methods. */
public final class Lpw2Block extends DummyableBlock {
    public Lpw2Block(){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{6,0,3,3,9,10};}
    @Override public int offset(){return 3;}
    @Override public void appendHoverText(ItemStack stack,net.minecraft.world.item.Item.TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,net.minecraft.world.item.TooltipFlag flags){super.appendHoverText(stack,context,lines,flags);lines.add(net.minecraft.network.chat.Component.translatable("tooltip.hbm.machine_lpw2.debug").withStyle(net.minecraft.ChatFormatting.GRAY));}
    // BlockDummyable source drops onHarvested before the tool gate; harvestBlock only awards stats/exhaustion.
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player){if(!level.isClientSide&&!player.getAbilities().instabuild)popResource(level,pos,new ItemStack(this));return super.playerWillDestroy(level,pos,state,player);}
    @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity entity,ItemStack tool){player.awardStat(Stats.BLOCK_MINED.get(this));player.causeFoodExhaustion(.025F);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new Lpw2Entity(pos,state):null;}
}
