package com.hbm.missile.launchpad.rusted;
import com.hbm.interfaces.IBomb;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
/** Source3x3 one-layer dummyable pad, with the six original detailed collision boxes. */
public final class RustedLaunchPadBlock extends DummyableBlock implements IBomb {
    private static final VoxelShape SHAPE=Shapes.or(Shapes.box(-1.5,0,-1.5,-.5,1,-.5),Shapes.box(.5,0,-1.5,1.5,1,-.5),Shapes.box(-1.5,0,.5,-.5,1,1.5),Shapes.box(.5,0,.5,1.5,1,1.5),Shapes.box(-.5,.5,-1.5,.5,1,1.5),Shapes.box(-1.5,.5,-.5,1.5,1,.5));
    public RustedLaunchPadBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(ignored->new RustedLaunchPadBlock());}
    @Override public int[] dimensions(){return new int[]{0,0,1,1,1,1};}@Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RustedLaunchPadBlockEntity(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RustedLaunchPadContent.ENTITY.get(),RustedLaunchPadBlockEntity::tick):null;}
    private VoxelShape shape(BlockGetter level,BlockPos pos){BlockPos core=findCore(level,pos);return core==null?Shapes.empty():Shapes.join(SHAPE.move(core.getX()-pos.getX()+.5,core.getY()-pos.getY(),core.getZ()-pos.getZ()+.5),Shapes.block(),BooleanOp.AND);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return shape(level,pos);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return shape(level,pos);}
    @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of();}
    private InteractionResult activate(Level level,BlockPos pos,Player player){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.CONSUME;BlockPos core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof RustedLaunchPadBlockEntity pad))return InteractionResult.PASS;if(player instanceof ServerPlayer server)server.openMenu(pad,buffer->buffer.writeBlockPos(core));return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var tool=super.useItemOn(stack,state,level,pos,player,hand,hit);if(tool!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return tool;return activate(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){if(!level.isClientSide){BlockPos core=findCore(level,new BlockPos(x,y,z));if(core!=null&&level.getBlockEntity(core) instanceof RustedLaunchPadBlockEntity pad)return pad.launch();}return BombReturnCode.UNDEFINED;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!level.isClientSide){BlockPos core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core) instanceof RustedLaunchPadBlockEntity pad)pad.updateRedstonePower(pos);}super.neighborChanged(state,level,pos,neighbor,from,moving);}
}
