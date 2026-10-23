package com.hbm.machine.rtgblast;

import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** True source MachineDiFurnaceRTG cube, including top exhaust and family-state BE retention. */
public final class RtgBlastBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private final Random dropRandom=new Random();public final boolean active;
    public RtgBlastBlock(boolean active){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state->active?15:0));this.active=active;registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends RtgBlastBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){
        super.onPlace(state,level,pos,old,moving);
        // Native explicit final placement states already include the source onBlockPlacedBy yaw.
        if(level.isClientSide||old.getBlock() instanceof RtgBlastBlock||state.getValue(META)!=0)return;
        boolean north=opaque(level,pos.north()),south=opaque(level,pos.south()),west=opaque(level,pos.west()),east=opaque(level,pos.east());int facing=3;
        if(north&&!south)facing=3;if(south&&!north)facing=2;if(west&&!east)facing=5;if(east&&!west)facing=4;
        level.setBlock(pos,state.setValue(META,facing),2);
    }
    private static boolean opaque(Level level,BlockPos pos){return level.getBlockState(pos).isSolidRender(level,pos);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta>=2&&meta<=5?rotate(state,mirror.getRotation(Direction.from3DDataValue(meta))):state;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RtgBlastEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,RtgBlastContent.ENTITY.get(),RtgBlastEntity::tick);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof RtgBlastEntity furnace)server.openMenu(furnace);return InteractionResult.CONSUME;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(stack.has(DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos) instanceof RtgBlastEntity furnace)furnace.setCustomName(stack.getHoverName());}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(RtgBlastContent.OFF_ITEM.get());}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of(new ItemStack(RtgBlastContent.OFF_ITEM.get()));}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(next.getBlock() instanceof RtgBlastBlock)return;
        if(!level.isClientSide&&level.getBlockEntity(pos) instanceof RtgBlastEntity furnace){
            for(int i=0;i<9;i++){var stack=furnace.getItem(i);if(stack.isEmpty())continue;
                float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;
                while(!stack.isEmpty()){int count=Math.min(stack.getCount(),dropRandom.nextInt(21)+10);var item=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));item.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(item);}
            }level.updateNeighbourForOutputSignal(pos,this);
        }super.onRemove(state,level,pos,next,moving);
    }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){
        if(!active)return;int meta=state.getValue(META);float x=pos.getX()+.5F,y=pos.getY()+.25F+random.nextFloat()*6F/16F,z=pos.getZ()+.5F;
        float side=random.nextFloat()*.5F-.25F,topX=random.nextFloat()*.75F+.125F,topZ=random.nextFloat()*.75F+.125F;
        if(meta==4){x-=.52F;z+=side;}else if(meta==5){x+=.52F;z+=side;}else if(meta==2){x+=side;z-=.52F;}else if(meta==3){x+=side;z+=.52F;}else return;
        level.addParticle(ParticleTypes.FLAME,x,y,z,0,0,0);level.addParticle(ParticleTypes.SMOKE,pos.getX()+topX,(double)pos.getY()+1,pos.getZ()+topZ,0,0,0);
    }
}
