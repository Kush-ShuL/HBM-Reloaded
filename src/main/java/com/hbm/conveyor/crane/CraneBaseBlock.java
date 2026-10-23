package com.hbm.conveyor.crane;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.conveyor.belt.*;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Sole source BlockCraneBase: real side configuration, icons, source piston placement and drop RNG. */
public abstract class CraneBaseBlock extends BaseEntityBlock implements ConveyorCraneEndpoint,ToolableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);private final Random dropRandom=new Random();
    protected CraneBaseBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    public abstract CraneBaseEntity createCrane(BlockPos pos,BlockState state);
    /** Source ordering: top, side, in, sideIn, out, sideOut, directional, up, down, topLeft, topRight, then the8 side bends. */
    public abstract String[] sourceTextures();
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return createCrane(pos,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return (world,pos,s,tile)->{if(tile instanceof CraneBaseEntity crane)CraneBaseEntity.tick(world,pos,s,crane);};}
    public static Direction direction(int metadata){return metadata>=0&&metadata<6?Direction.from3DDataValue(metadata):null;}
    private static ForgeDirection source(Direction side){return ForgeDirection.getOrientation(side==null?6:side.get3DDataValue());}
    public static int handRotation(Direction output,Direction input){return source(output).getRotation(source(input)).ordinal();}
    public Direction getInputSide(BlockGetter level,BlockPos pos){return direction(level.getBlockState(pos).getValue(META));}
    public Direction getOutputOverride(BlockGetter level,BlockPos pos){return level.getBlockEntity(pos)instanceof CraneBaseEntity crane?crane.getOutputOverride():null;}
    public Direction getOutputSide(BlockGetter level,BlockPos pos){return level.getBlockEntity(pos)instanceof CraneBaseEntity crane?crane.getOutputSide():null;}
    public static int placement(BlockPos pos,LivingEntity entity){if(Math.abs((float)entity.getX()-pos.getX())<2&&Math.abs((float)entity.getZ()-pos.getZ())<2){double eye=entity.getY()+1.82;if(eye-pos.getY()>2)return 1;if(pos.getY()-eye>0)return 0;}return switch(Mth.floor(entity.getYRot()*4F/360F+.5)&3){case 0->2;case 1->5;case 2->3;default->4;};}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getPlayer()==null?0:placement(context.getClickedPos(),context.getPlayer()));}
    @Override public void setPlacedBy(Level world,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)&&world.getBlockEntity(pos)instanceof CraneBaseEntity crane)crane.setCustomName(stack.getHoverName());}
    private static boolean tool(ItemStack stack){return stack.getItem()instanceof com.hbm.item.tool.ToolingItem||ToolType.getType(stack)!=null||stack.getItem()instanceof ConveyorWandItem;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(tool(stack))return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(tool(player.getMainHandItem()))return InteractionResult.PASS;if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.getBlockEntity(pos)instanceof CraneBaseEntity crane)player.openMenu(crane);return InteractionResult.CONSUME;}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType type){if(type!=ToolType.SCREWDRIVER||!(level.getBlockEntity(pos)instanceof CraneBaseEntity crane))return false;if(player.isShiftKeyDown())crane.setOutputOverride(side);else crane.setInput(side);return true;}
    @Override protected void onRemove(BlockState state,Level world,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&world.getBlockEntity(pos)instanceof CraneBaseEntity crane){dropContents(world,pos,crane,0,crane.getContainerSize());world.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,world,pos,next,moving);}
    protected void dropContents(Level world,BlockPos pos,CraneBaseEntity crane,int start,int end){for(int i=start;i<end;i++){var stack=crane.slots[i];if(stack.isEmpty())continue;float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(stack.getCount(),dropRandom.nextInt(21)+10);var entity=new ItemEntity(world,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));float speed=.05F;entity.setDeltaMovement((float)dropRandom.nextGaussian()*speed,(float)dropRandom.nextGaussian()*speed+.2F,(float)dropRandom.nextGaussian()*speed);world.addFreshEntity(entity);}}}
    public int worldIconIndex(int metadata,int override,int side){
        var input=ForgeDirection.getOrientation(metadata);var outOverride=ForgeDirection.getOrientation(override);boolean overridden=outOverride!=ForgeDirection.UNKNOWN&&outOverride.getOpposite()!=input;var output=overridden?outOverride:input.getOpposite();var left=output.getRotation(input);
        if(side==0||side==1){if(side==output.ordinal())return 4;if(side==input.ordinal())return 2;if(side==1){if(overridden){if(left==ForgeDirection.UP)return 9;if(left==ForgeDirection.DOWN)return 10;}else return 6;}return 0;}
        if(side==output.ordinal())return 5;if(side==input.ordinal())return 3;
        if(overridden){if(left.ordinal()==side){if(output==ForgeDirection.UP)return 11;if(output==ForgeDirection.DOWN)return 14;if(input==ForgeDirection.UP)return 16;if(input==ForgeDirection.DOWN)return 17;}if(left.getOpposite().ordinal()==side){if(output==ForgeDirection.UP)return 12;if(output==ForgeDirection.DOWN)return 13;if(input==ForgeDirection.UP)return 15;if(input==ForgeDirection.DOWN)return 18;}}
        else{if(output==ForgeDirection.UP)return 7;if(output==ForgeDirection.DOWN)return 8;}return 1;
    }
    public int inventoryIconIndex(int side,int metadata){var opposite=ForgeDirection.getOrientation(metadata).getOpposite();if(side==0||side==1){if(side==metadata)return 4;if(side==opposite.ordinal())return 2;return side==1?6:0;}if(side==metadata)return 5;if(side==opposite.ordinal())return 3;if(metadata==0)return 7;if(metadata==1)return 8;return 1;}
    /** Exact source RenderBlocks uvRotateTop code, not a blockstate yaw. */
    public int sourceRotation(int metadata,int override,int side){return side==1?switch(metadata){case 2->3;case 3->0;case 4->1;case 5->2;default->0;}:0;}
}
