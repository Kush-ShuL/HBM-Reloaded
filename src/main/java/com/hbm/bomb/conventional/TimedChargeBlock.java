package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.interfaces.IBomb;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original BlockChargeBase + the two dynamite-model source subclasses. */
public final class TimedChargeBlock extends Block implements EntityBlock,IBomb,IFuckingExplode {
    public static final DirectionProperty FACE=DirectionProperty.create("face");
    public static boolean safe;
    private static final int[] TIMERS={0,100,200,300,600,1200,3600,6000};
    public enum Kind { DYNAMITE, MINER, C4, SEMTEX }
    private final Kind kind;
    public TimedChargeBlock(boolean mining){this(mining?Kind.MINER:Kind.DYNAMITE);}
    public TimedChargeBlock(Kind kind){super(Properties.of().mapColor(MapColor.FIRE).strength(0,.6F).noOcclusion().noCollission());this.kind=kind;registerDefaultState(defaultBlockState().setValue(FACE,Direction.UP));}
    public boolean mining(){return kind==Kind.MINER;}
    public String modelName(){return kind==Kind.C4||kind==Kind.SEMTEX?"charge_c4":"charge_dynamite";}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACE);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=defaultBlockState().setValue(FACE,context.getClickedFace());return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){Direction face=state.getValue(FACE);var support=pos.relative(face.getOpposite());return level.getBlockState(support).isFaceSturdy(level,support,face);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(FACE)){case DOWN->box(0,10,0,16,16,16);case UP->box(0,0,0,16,6,16);case NORTH->box(0,0,10,16,16,16);case SOUTH->box(0,0,0,16,16,6);case WEST->box(10,0,0,16,16,16);case EAST->box(0,0,0,6,16,16);};}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!canSurvive(state,level,pos))level.removeBlock(pos,false);}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){super.onRemove(state,level,pos,next,moving);if(!next.is(this)){level.removeBlockEntity(pos);if(!safe)explode(level,pos.getX(),pos.getY(),pos.getZ());}}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new TimedChargeBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide||type!=ConventionalContent.CHARGE_ENTITY.get()?null:(world,pos,block,entity)->TimedChargeBlockEntity.tick(world,pos,block,(TimedChargeBlockEntity)entity);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(level.getBlockEntity(pos) instanceof TimedChargeBlockEntity charge&&!charge.started){
            if(player.isShiftKeyDown()){if(charge.timer>0){charge.started=true;level.playSound(null,pos,HbmSounds.get("weapon.fstbmbStart"),SoundSource.BLOCKS,1,1);}}
            else{int current=-1;for(int i=0;i<TIMERS.length;i++)if(charge.timer==TIMERS[i])current=i;charge.timer=current>=0&&current<TIMERS.length-1?TIMERS[current+1]:0;level.playSound(null,pos,HbmSounds.get("item.techBoop"),SoundSource.BLOCKS,1,1);}
            charge.setChanged();charge.synchronize();
        }
        return InteractionResult.PASS;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:defuser"))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(level.getBlockEntity(pos) instanceof TimedChargeBlockEntity charge){
            if(charge.started){charge.started=false;level.playSound(null,pos,HbmSounds.get("weapon.fstbmbStart"),SoundSource.BLOCKS,1,1);charge.setChanged();charge.synchronize();}
            else{safe=true;try{level.removeBlock(pos,false);dropDefused(level,pos);}finally{safe=false;}}
            if(!level.isClientSide){if(stack.getItem() instanceof com.hbm.item.tool.ToolingItem tooling)tooling.onToolUsed(stack,player,hand);else if(stack.isDamageableItem())stack.hurtAndBreak(1,player,net.minecraft.world.entity.LivingEntity.getSlotForHand(hand));}
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    private void dropDefused(Level level,BlockPos pos){
        float x=level.random.nextFloat()*.6F+.2F,y=level.random.nextFloat()*.2F,z=level.random.nextFloat()*.6F+.2F;
        var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y+1,pos.getZ()+z,new ItemStack(this));
        entity.setDeltaMovement((float)level.random.nextGaussian()*.05F,(float)level.random.nextGaussian()*.05F+.2F,(float)level.random.nextGaussian()*.05F);
        if(!level.isClientSide)level.addFreshEntity(entity);
    }
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(level.isClientSide)return BombReturnCode.UNDEFINED;
        safe=true;try{level.removeBlock(new BlockPos(x,y,z),false);}finally{safe=false;}
        if(kind==Kind.DYNAMITE||kind==Kind.MINER){var explosion=new ExplosionNT(level,null,x+.5,y+.5,z+.5,4F);if(mining())explosion.addAllAttrib(ExAttrib.NOHURT,ExAttrib.ALLDROP);explosion.explode();ConventionalEffects.compose(level,x+.5,y+.5,z+.5,15,3F,1.25F);}
        else {
            var explosion=new com.hbm.explosion.vanillant.ExplosionVNT(level,x+.5,y+.5,z+.5,kind==Kind.C4?15F:10F).setBlockAllocator(new com.hbm.explosion.vanillant.standard.BlockAllocatorStandard(32));
            if(kind==Kind.C4)explosion.setBlockProcessor(new com.hbm.explosion.vanillant.standard.BlockProcessorStandard().setNoDrop()).setEntityProcessor(new com.hbm.explosion.vanillant.standard.EntityProcessorStandard()).setPlayerProcessor(new com.hbm.explosion.vanillant.standard.PlayerProcessorStandard());
            else explosion.setBlockProcessor(new com.hbm.explosion.vanillant.standard.BlockProcessorStandard().setAllDrop().setFortune(3));
            explosion.explode();LargeConventionalEffects.small(level,x+.5,y+1,z+.5);
        }
        return BombReturnCode.DETONATED;
    }
    @Override public boolean dropFromExplosion(Explosion explosion){return false;}
    @Override public void wasExploded(Level level,BlockPos pos,Explosion explosion){if(!level.isClientSide){var entity=new PrimedExplosive(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,explosion==null?null:explosion.getIndirectSourceEntity(),this);entity.setFuse(0);level.addFreshEntity(entity);}}
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive entity){var pos=BlockPos.containing(x,y,z);explode(level,pos.getX(),pos.getY(),pos.getZ());}
    @Override public void appendHoverText(ItemStack stack,net.minecraft.world.item.Item.TooltipContext context,List<Component> text,TooltipFlag flag){
        text.add(Component.translatable("hbm.explosives.timer.change").withStyle(ChatFormatting.YELLOW));text.add(Component.translatable("hbm.explosives.timer.arm").withStyle(ChatFormatting.YELLOW));text.add(Component.translatable("hbm.explosives.timer.defuse").withStyle(ChatFormatting.RED));
        if(mining()||kind==Kind.SEMTEX){text.add(Component.translatable("hbm.explosives.drop.all").withStyle(ChatFormatting.BLUE));text.add(Component.translatable("hbm.explosives.damage.none").withStyle(ChatFormatting.BLUE));}
        if(kind==Kind.C4)text.add(Component.translatable("hbm.explosives.drop.none").withStyle(ChatFormatting.BLUE));
        if(kind==Kind.SEMTEX){text.add(Component.literal("").withStyle(ChatFormatting.BLUE));text.add(Component.translatable("enchantment.minecraft.fortune").append(" III").withStyle(ChatFormatting.LIGHT_PURPLE));}
    }
}
