package com.hbm.machine.radiationcleanup;

import com.hbm.radiation.HbmRadiation;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Actual BlockAbsorber: four source tiers, scheduled10ticks plus original random-tick calls; no energy or tank. */
public final class AbsorberBlock extends Block {
    public enum Tier{
        BASE(2.5F,"absorber"),RED(10,"absorber_red"),GREEN(100,"absorber_green"),PINK(10000,"absorber_pink");
        public final float amount;public final String texture;
        Tier(float amount,String texture){this.amount=amount;this.texture=texture;}
    }
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public AbsorberBlock(){this(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().randomTicks());}
    private AbsorberBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends AbsorberBlock> codec(){return simpleCodec(AbsorberBlock::new);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    public static int rectify(int meta){return Math.abs(meta%Tier.values().length);}
    public static Tier tier(int meta){return Tier.values()[rectify(meta)];}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moved){super.onPlace(state,level,pos,old,moved);if(!old.is(this))level.scheduleTick(pos,this,10);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){HbmRadiation.decrementRad(level,pos.getX(),pos.getY(),pos.getZ(),tier(state.getValue(META)).amount);level.scheduleTick(pos,this,10);}
    @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){tick(state,level,pos,random);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);return radius!=null&&context.getLevel().random.nextFloat()>1F/radius?List.of():List.of(RadiationCleanupContent.absorberStack(rectify(state.getValue(META))));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return RadiationCleanupContent.absorberStack(rectify(state.getValue(META)));}
}
