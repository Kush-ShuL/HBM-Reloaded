package com.hbm.door.generic;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.config.SourceServerConfig;
import com.hbm.interfaces.IBomb;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
/** Source machine door backend; real source cell-dependent selection/collision, no ordinary 1x2 door substitute. */
public class GenericDoorBlock extends DummyableBlock implements IBomb,ToolableBlock {
    public final GenericDoorDeclaration declaration;
    public GenericDoorBlock(GenericDoorDeclaration declaration){super(Properties.of().mapColor(MapColor.METAL).strength(declaration.hardness,declaration.resistance).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().dynamicShape());this.declaration=declaration;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return declaration.dimensions();}@Override public int offset(){return declaration.offset;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new GenericDoorBlockEntity(pos,state):null;}
    @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,GenericDoorContent.ENTITY.get(),GenericDoorBlockEntity::tick);}
    private InteractionResult activate(Level level,BlockPos pos,Player player){if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.isClientSide)return InteractionResult.SUCCESS;var core=findCore(level,pos);if(core==null)return InteractionResult.PASS;if(level.getBlockEntity(core)instanceof GenericDoorBlockEntity door){if(!SourceServerConfig.ULTRA_LARP_MODE.get()||declaration.neverDemandRedstone())return door.tryToggle(player)?InteractionResult.CONSUME:InteractionResult.PASS;}return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var result=activate(level,pos,player);return result.consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER||!player.isShiftKeyDown())return false;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core)instanceof GenericDoorBlockEntity door)||door.declaration().skins<=0)return false;if(!level.isClientSide)door.cycleSkinIndex();return true;}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){var core=findCore(level,new BlockPos(x,y,z));if(core!=null&&level.getBlockEntity(core)instanceof GenericDoorBlockEntity door&&declaration.remoteControllable&&door.tryToggle((Player)null))return BombReturnCode.TRIGGERED;return BombReturnCode.ERROR_INCOMPATIBLE;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moved){if(!level.isClientSide){var core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core)instanceof GenericDoorBlockEntity door)door.updateRedstonePower(pos);}super.neighborChanged(state,level,pos,neighbor,from,moved);}
    @Override protected boolean isPathfindable(BlockState state,PathComputationType type){return isPort(state);}
    private VoxelShape shape(BlockState state,BlockGetter level,BlockPos pos,boolean collision){
        var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core)instanceof GenericDoorBlockEntity door))return Shapes.block();
        boolean open=isPort(state)||pos.equals(core)&&door.state!=0;var direction=facing(door.getBlockState());
        var local=GenericDoorBlockEntity.rotate(pos.subtract(core),GenericDoorBlockEntity.sourceRotation(direction)+3);
        var b=declaration.bounds(local.getX(),local.getY(),local.getZ(),open,collision);double x1=b.minX,y1=b.minY,z1=b.minZ,x2=b.maxX,y2=b.maxY,z2=b.maxZ;
        var result=switch(direction){case NORTH->new AABB(1-x1,y1,1-z1,1-x2,y2,1-z2);case WEST->new AABB(1-z1,y1,x1,1-z2,y2,x2);case EAST->new AABB(z1,y1,1-x1,z2,y2,1-x2);default->new AABB(x1,y1,z1,x2,y2,z2);};
        if(collision&&result.minY==0&&result.maxY==0||result.getXsize()==0&&result.getYsize()==0&&result.getZsize()==0)return Shapes.empty();
        return Shapes.create(result);
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return shape(state,level,pos,false);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return shape(state,level,pos,true);}
}
