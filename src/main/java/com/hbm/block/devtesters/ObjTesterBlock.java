package com.hbm.block.devtesters;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
public final class ObjTesterBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final MapCodec<ObjTesterBlock> CODEC=simpleCodec(ignored->new ObjTesterBlock());
    public ObjTesterBlock(){super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(2.5F,6).sound(SoundType.STONE).noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<ObjTesterBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ObjTesterEntity(pos,state);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity entity,ItemStack item){super.setPlacedBy(level,pos,state,entity,item);int quadrant=Mth.floor(entity.getYRot()*4F/360F+.5D)&3;int value=switch(quadrant){case 0->5;case 1->3;case 2->4;default->2;};level.setBlock(pos,state.setValue(META,value),Block.UPDATE_CLIENTS);}
}
