package com.hbm.bomb.nuclear.programmable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Original NukeCustom: redstone consumes its real payload, or releases the original falling bomb. */
public final class CustomNuclearBlock extends ProgrammableBombBlock {
 public CustomNuclearBlock(){this(sourceProperties());}private CustomNuclearBlock(Properties properties){super(properties);}
 @Override protected boolean timed(){return false;}
 @Override protected MapCodec<? extends CustomNuclearBlock> codec(){return simpleCodec(CustomNuclearBlock::new);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new CustomNuclearBlockEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ProgrammableNuclearContent.CUSTOM_ENTITY.get(),CustomNuclearBlockEntity::tick);}
 @Override public BombReturnCode explode(Level level,int x,int y,int z){if(level.isClientSide)return BombReturnCode.UNDEFINED;BlockPos pos=new BlockPos(x,y,z);if(!(level.getBlockEntity(pos) instanceof CustomNuclearBlockEntity bomb))return BombReturnCode.ERROR_MISSING_COMPONENT;CustomBombPayload payload=bomb.payload;boolean falling=bomb.isFalling();int meta=bomb.getBlockState().getValue(META);bomb.clearContent();level.removeBlock(pos,false);if(falling){var entity=new FallingCustomNuclearBomb(level,payload);entity.metadata(meta);entity.moveTo(x+.5,y,z+.5,0,0);level.addFreshEntity(entity);return BombReturnCode.TRIGGERED;}CustomNuclearExplosion.explode(level,x+.5,y+.5,z+.5,payload);return BombReturnCode.DETONATED;}
}
