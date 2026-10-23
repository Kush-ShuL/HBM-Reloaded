package com.hbm.block.door;

import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

/** X5846 BlockModDoor: manually usable iron material, source redstone rules and sound. */
public final class SourceDoorBlock extends DoorBlock {
    public SourceDoorBlock(float hardness,float resistance){
        // Iron keeps vanilla door-opening AI and wind charges disabled. The
        // source's explicit player callback still opens all four HBM variants.
        super(BlockSetType.IRON,BlockBehaviour.Properties.of().strength(hardness,resistance).noOcclusion().requiresCorrectToolForDrops()
            .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY));
    }
    @Override public SoundType getSoundType(BlockState state){return SoundType.STONE;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        BlockPos lower=state.getValue(HALF)==DoubleBlockHalf.UPPER?pos.below():pos;
        BlockState bottom=level.getBlockState(lower);
        if(!bottom.is(this)||bottom.getValue(HALF)!=DoubleBlockHalf.LOWER)return InteractionResult.PASS;
        changeOpen(level,pos,!bottom.getValue(OPEN),true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void setOpen(Entity entity,Level level,BlockState state,BlockPos pos,boolean open){
        if(state.is(this))changeOpen(level,pos,open,false);
    }
    /** Source func_150012_g: facing/open belong to the lower cell, hinge to the upper cell. */
    public static BlockState joinedHalf(BlockState lower,BlockState upper,DoubleBlockHalf half){
        return lower.setValue(HINGE,upper.getValue(HINGE)).setValue(HALF,half);
    }
    @Override protected BlockState updateShape(BlockState state,Direction side,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos){
        DoubleBlockHalf half=state.getValue(HALF);
        if(side==(half==DoubleBlockHalf.LOWER?Direction.UP:Direction.DOWN)){
            if(!neighbor.is(this)||neighbor.getValue(HALF)==half)return Blocks.AIR.defaultBlockState();
            return half==DoubleBlockHalf.LOWER?joinedHalf(state,neighbor,half):joinedHalf(neighbor,state,half);
        }
        return super.updateShape(state,side,neighbor,level,pos,neighborPos);
    }
    private void changeOpen(Level level,BlockPos soundPos,boolean open,boolean alwaysSound){
        BlockState clicked=level.getBlockState(soundPos);
        if(!clicked.is(this))return;
        BlockPos lower=clicked.getValue(HALF)==DoubleBlockHalf.UPPER?soundPos.below():soundPos;
        BlockState state=level.getBlockState(lower);
        if(!state.is(this)||state.getValue(HALF)!=DoubleBlockHalf.LOWER)return;
        BlockState upper=level.getBlockState(lower.above());
        boolean paired=upper.is(this)&&upper.getValue(HALF)==DoubleBlockHalf.UPPER;
        BlockState updated=(paired?joinedHalf(state,upper,DoubleBlockHalf.LOWER):state).setValue(OPEN,open);
        // Even an unchanged lower OPEN must repair a stale companion; never toggle from the stale upper copy.
        if(updated!=state)level.setBlock(lower,updated,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        if(paired){BlockState nextUpper=updated.setValue(HALF,DoubleBlockHalf.UPPER);if(nextUpper!=upper)level.setBlock(lower.above(),nextUpper,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);}
        if(alwaysSound||state.getValue(OPEN)!=open)level.playSound(null,soundPos,HbmSounds.get("block.openDoor"),SoundSource.BLOCKS,1F,level.random.nextFloat()*.1F+.9F);
    }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block changed,BlockPos neighbor,boolean moving){
        BlockPos lower=state.getValue(HALF)==DoubleBlockHalf.UPPER?pos.below():pos;
        BlockState bottom=level.getBlockState(lower);
        if(!bottom.is(this)||bottom.getValue(HALF)!=DoubleBlockHalf.LOWER)return;
        changeOpen(level,lower,bottom.getValue(OPEN),false);
        // Structural survival and companion-half removal are handled by the
        // native shape callback, including its ordinary lower-half item drop.
        boolean powered=level.hasNeighborSignal(lower)||level.hasNeighborSignal(lower.above());
        if(changed!=this&&(powered||changed.defaultBlockState().isSignalSource()))changeOpen(level,lower,powered,false);
    }
    public static net.minecraft.world.level.block.state.properties.DoorHingeSide sourceHinge(Level level,BlockPos pos,Direction facing,Block door){
        Direction offset=facing.getClockWise();
        BlockPos minus=pos.relative(offset.getOpposite()),plus=pos.relative(offset);
        int a=(level.getBlockState(minus).isSolidRender(level,minus)?1:0)+(level.getBlockState(minus.above()).isSolidRender(level,minus.above())?1:0);
        int b=(level.getBlockState(plus).isSolidRender(level,plus)?1:0)+(level.getBlockState(plus.above()).isSolidRender(level,plus.above())?1:0);
        boolean atMinus=level.getBlockState(minus).is(door)||level.getBlockState(minus.above()).is(door);
        boolean atPlus=level.getBlockState(plus).is(door)||level.getBlockState(plus.above()).is(door);
        return atMinus&&!atPlus||b>a?net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT:net.minecraft.world.level.block.state.properties.DoorHingeSide.LEFT;
    }
}
