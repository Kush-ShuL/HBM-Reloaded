package com.hbm.world.oldlantern;

import com.hbm.api.machine.IRepairable;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.pedestal.PedestalReputation;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

/** Source five one-cube collision cells and directional parent chain; the mesh's light does not emit block light. */
public final class OldLanternBlock extends DummyableBlock implements ToolableBlock {
    private static final MapCodec<OldLanternBlock> CODEC=simpleCodec(ignored->new OldLanternBlock());
    public OldLanternBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(3).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{4,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new OldLanternEntity(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,OldLanternContent.ENTITY.get(),OldLanternEntity::tick):null;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of();}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){
        if(tool!=ToolType.TORCH)return false;
        boolean repaired=IRepairable.tryRepairMultiblock(level,pos,this,player);
        if(repaired&&!level.isClientSide){int reputation=PedestalReputation.get(player);if(reputation<25)PedestalReputation.set(player,reputation+1);}
        return repaired;
    }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
        // Modern setRemoved also runs on chunk unload. Source invalidate's removal penalty
        // belongs to actual host destruction, not harmless unloading/reloading of its chunk.
        if(!level.isClientSide&&!replacement.is(this)&&isCore(state)&&level.getBlockEntity(pos) instanceof OldLanternEntity lantern)lantern.penalizeRemoval();
        super.onRemove(state,level,pos,replacement,moving);
    }
}
