package com.hbm.bomb.conventional;

import com.hbm.entity.effect.EntityMist;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.fluid.HbmFluids;
import com.hbm.interfaces.IBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Source BombMulti: four TNT slots and two independently combinable conventional payloads. */
public final class MultiBombBlock extends Block implements EntityBlock,IBomb {
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public MultiBombBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(0,120).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getCounterClockWise());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return box(0,0,0,16,8,16);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MultiBombBlockEntity(pos,state);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.getBlockEntity(pos) instanceof MultiBombBlockEntity bomb)player.openMenu(bomb);return InteractionResult.CONSUME;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof MultiBombBlockEntity bomb&&bomb.isLoaded())explode(level,pos.getX(),pos.getY(),pos.getZ());}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
        if(!state.is(replacement.getBlock())&&level.getBlockEntity(pos) instanceof MultiBombBlockEntity bomb){
            for(int slot=0;slot<bomb.getContainerSize();slot++){
                var stack=bomb.getItem(slot);if(stack.isEmpty())continue;
                float x=level.random.nextFloat()*.8F+.1F,y=level.random.nextFloat()*.8F+.1F,z=level.random.nextFloat()*.8F+.1F;
                while(!stack.isEmpty()){int count=Math.min(stack.getCount(),level.random.nextInt(21)+10);var item=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));item.setDeltaMovement((float)level.random.nextGaussian()*.05F,(float)level.random.nextGaussian()*.05F+.2F,(float)level.random.nextGaussian()*.05F);level.addFreshEntity(item);}
            }
            level.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,level,pos,replacement,moving);
    }
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(level.isClientSide)return BombReturnCode.UNDEFINED;
        var pos=new BlockPos(x,y,z);if(!(level.getBlockEntity(pos) instanceof MultiBombBlockEntity bomb)||!bomb.isLoaded())return BombReturnCode.ERROR_MISSING_COMPONENT;
        float strength=8;int cluster=0,fire=0,poison=0,gas=0;
        for(int slot:new int[]{2,5})switch(bomb.type(slot)){case 1->strength+=1;case 2->strength+=4;case 3->cluster+=50;case 4->fire+=10;case 5->poison+=15;case 6->gas+=50;}
        bomb.clearContent();level.removeBlock(pos,false);ExplosionLarge.explode(level,x,y,z,strength,true,true,true);
        if(cluster>0)ExplosionChaos.cluster(level,x+.5,y+.5,z+.5,cluster,0,(float)Math.PI*.5F,(float)Math.PI*2F,(float)Math.PI*.125F,.375F);
        if(fire>0)ExplosionChaos.igniteAllBlocks(level,x,y,z,fire);
        if(poison>0)ExplosionNukeGeneric.wasteNoSchrab(level,x,y,z,poison);
        if(gas>0){var mist=new EntityMist(level);mist.setType(HbmFluids.CHLORINE);mist.setPos(x+.5,y+.5,z+.5);mist.setArea(gas*15F/50F,gas*7.5F/50F);level.addFreshEntity(mist);}
        return BombReturnCode.DETONATED;
    }
}
