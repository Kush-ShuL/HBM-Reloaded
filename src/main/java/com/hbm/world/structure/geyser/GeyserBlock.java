package com.hbm.world.structure.geyser;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** Source full-cube geyser with its actual no-item normal/silk drops, rock texture and world phase metadata. */
public final class GeyserBlock extends BaseEntityBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);public final boolean nether;
 public GeyserBlock(boolean nether){super(Properties.of().mapColor(nether?MapColor.NETHER:MapColor.STONE).strength(nether?2:5,nether?2:5).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state->nether?15:0));this.nether=nether;registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(ignored->new GeyserBlock(nether));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new GeyserBlockEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,GeyserContent.ENTITY.get(),GeyserBlockEntity::tick);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(nether)level.addParticle(ParticleTypes.FLAME,pos.getX()+.5F,pos.getY()+1.0625F,pos.getZ()+.5F,0,0,0);}
}
