package com.hbm.block.sellafield;

import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.exposure.ExposureEffects;
import com.hbm.radiation.HbmRadiation;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** The registered generic.BlockSellafield, distinct from the unused bomb.Sellafield tile-host class. */
public final class SellafieldBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public SellafieldBlock(){this(Properties.of().mapColor(MapColor.STONE).strength(5,5).sound(SoundType.STONE).requiresCorrectToolForDrops().randomTicks());}
    private SellafieldBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends SellafieldBlock> codec(){return simpleCodec(SellafieldBlock::new);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){int metadata=state.getValue(META);if(entity instanceof LivingEntity living)living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,600,metadata<5?metadata:metadata*2));}
    /** Source suppresses BlockHazard.onBlockAdded; no scheduled heartbeat is added. */
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){int metadata=state.getValue(META);HbmRadiation.incrementRad(level,pos.getX(),pos.getY(),pos.getZ(),.5F*(metadata+1));if(random.nextInt(metadata==0?25:15)==0){if(metadata>0)level.setBlock(pos,state.setValue(META,metadata-1),2);else level.setBlock(pos,HbmWorldFluids.block("sellafield_slaked").defaultBlockState(),3);}}
    @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){tick(state,level,pos,random);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);return radius!=null&&context.getLevel().random.nextFloat()>1F/radius?List.of():List.of(SellafieldContent.stack(state.getValue(META)));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return SellafieldContent.stack(state.getValue(META));}
}
