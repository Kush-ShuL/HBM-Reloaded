package com.hbm.machine.cargoelevator;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

/** Original three-by-three directional parent chains and moving collision surface. */
public final class CargoElevatorBlock extends DummyableBlock {
    public CargoElevatorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().dynamicShape());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{0,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CargoElevatorEntity(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CargoElevatorContent.ENTITY.get(),CargoElevatorEntity::tick):null;}
    public CargoElevatorEntity elevator(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core)instanceof CargoElevatorEntity lift?lift:null;}
    private InteractionResult activate(Level level,BlockPos pos,Player player,ItemStack held){
        // Source acknowledges every client click before its server-only sneaking check.
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(player.isShiftKeyDown())return InteractionResult.PASS;
        var lift=elevator(level,pos);
        if(lift!=null){
            if(held.is(this.asItem())){
                var next=lift.getBlockPos().above(lift.height+1);boolean replaceable=true;
                for(var cell:BlockPos.betweenClosed(next.offset(-1,0,-1),next.offset(1,0,1)))if(!level.isInWorldBounds(cell)||!level.hasChunkAt(cell)||!level.getBlockState(cell).canBeReplaced()){replaceable=false;break;}
                if(replaceable){
                    safely(()->{for(var cell:BlockPos.betweenClosed(next.offset(-1,0,-1),next.offset(1,0,1)))level.setBlock(cell,defaultBlockState().setValue(LEGACY_META,1),UPDATE_ALL);});
                    lift.height++;lift.setChanged();if(!player.getAbilities().instabuild)held.shrink(1);
                }
            }else lift.toggleElevator();
        }
        return InteractionResult.CONSUME;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        var result=activate(level,pos,player,held);
        return result==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player,ItemStack.EMPTY);}
    /** Source adds the same world-space boxes from each occupied proxy, including upper shaft cells. */
    public static AABB[] boxes(CargoElevatorEntity lift){
        var p=lift.getBlockPos();double x=p.getX(),y=p.getY(),z=p.getZ(),height=lift.height+1;
        return new AABB[]{new AABB(x-1,y,z-1,x-.75,y+height,z-.75),new AABB(x-1,y,z+1.75,x-.75,y+height,z+2),new AABB(x+1.75,y,z-1,x+2,y+height,z-.75),new AABB(x+1.75,y,z+1.75,x+2,y+height,z+2),new AABB(x-1,y+.75+lift.extension,z-1,x+2,y+1+lift.extension,z+2)};
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){
        var lift=elevator(level,pos);if(lift==null)return Shapes.empty();var shape=Shapes.empty();for(var box:boxes(lift))shape=Shapes.or(shape,Shapes.create(box.move(-pos.getX(),-pos.getY(),-pos.getZ())));return shape;
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){
        var origin=params.getOptionalParameter(LootContextParams.ORIGIN);var lift=origin==null?null:elevator(params.getLevel(),BlockPos.containing(origin));
        if(lift==null)return super.getDrops(state,params);
        var result=new ArrayList<ItemStack>();for(int remaining=lift.height+1;remaining>0;){int count=Math.min(remaining,64);result.add(new ItemStack(asItem(),count));remaining-=count;}return result;
    }
}
