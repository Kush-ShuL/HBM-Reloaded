package com.hbm.bomb.nuclear.programmable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Source NukeBalefire: powered/IBomb detonation requires both real components. */
public final class TimedBalefireBlock extends ProgrammableBombBlock {
 public TimedBalefireBlock(){this(sourceProperties());}private TimedBalefireBlock(Properties properties){super(properties);}
 @Override protected boolean timed(){return true;}
 @Override protected MapCodec<? extends TimedBalefireBlock> codec(){return simpleCodec(TimedBalefireBlock::new);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new TimedBalefireBlockEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ProgrammableNuclearContent.TIMED_ENTITY.get(),TimedBalefireBlockEntity::tick);}
 @Override public BombReturnCode explode(Level level,int x,int y,int z){if(level.isClientSide)return BombReturnCode.UNDEFINED;if(level.getBlockEntity(new BlockPos(x,y,z)) instanceof TimedBalefireBlockEntity bomb&&bomb.isLoaded()){bomb.explode();return BombReturnCode.DETONATED;}return BombReturnCode.ERROR_MISSING_COMPONENT;}
}
