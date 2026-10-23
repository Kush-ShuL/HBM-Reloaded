package com.hbm.machine.oilflare;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
/** Original twelve-high occupied volume and four actual high-stack collision boxes. */
public final class FlareBlock extends DummyableBlock {
    private static final MapCodec<FlareBlock> CODEC=simpleCodec(ignored->new FlareBlock());
    private static final double[][] BOXES={{-1.5,0,-1.5,1.5,3.875,1.5},{-.75,3.875,-.75,.75,9,.75},{-1.5,9,-1.5,1.5,9.375,1.5},{-.75,9.375,-.75,.75,12,.75}};
    public FlareBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{11,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide){var core=pos.relative(placementFacing(state),-offset());for(var port:FlareEntity.portPositions(core))setPort(level,port,true);}}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new FlareEntity(pos,state):isPort(state)?new FlarePort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,FlareContent.ENTITY.get(),FlareEntity::tick):null;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){var core=findCore(level,pos);if(core==null)return Shapes.empty();var cell=new AABB(pos);VoxelShape shape=Shapes.empty();for(var box:BOXES){var world=new AABB(core.getX()+.5+box[0],core.getY()+box[1],core.getZ()+.5+box[2],core.getX()+.5+box[3],core.getY()+box[4],core.getZ()+.5+box[5]);if(world.intersects(cell))shape=Shapes.or(shape,Shapes.create(world.intersect(cell).move(-pos.getX(),-pos.getY(),-pos.getZ())));}return shape;}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
}
