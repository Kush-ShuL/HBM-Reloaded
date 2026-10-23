package com.hbm.machine.rtgfurnace;

import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Actual source cube icons/metadata, light15 on-state and no block item drop; no OBJ or new heat/HE interface. */
public final class RtgFurnaceBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private final Random dropRandom=new Random();
    public final boolean active;
    public RtgFurnaceBlock(boolean active){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state->active?15:0));this.active=active;registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends RtgFurnaceBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta>=2&&meta<=5?rotate(state,mirror.getRotation(Direction.from3DDataValue(meta))):state;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RtgFurnaceEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,RtgFurnaceContent.ENTITY.get(),RtgFurnaceEntity::tick);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity entity,ItemStack stack){super.setPlacedBy(level,pos,state,entity,stack);if(stack.has(DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos) instanceof RtgFurnaceEntity furnace)furnace.setCustomName(stack.getHoverName().getString());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof RtgFurnaceEntity furnace)server.openMenu(furnace);return InteractionResult.CONSUME;}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(RtgFurnaceContent.OFF_ITEM.get());}
    @Override protected java.util.List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return java.util.List.of();}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(next.getBlock() instanceof RtgFurnaceBlock)return;
        if(!level.isClientSide&&level.getBlockEntity(pos) instanceof RtgFurnaceEntity furnace){
            for(int slot=0;slot<furnace.getContainerSize();slot++){
                var stack=furnace.getItem(slot);if(stack.isEmpty())continue;
                float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;
                while(!stack.isEmpty()){
                    int amount=Math.min(dropRandom.nextInt(21)+10,stack.getCount());var drop=stack.split(amount);
                    var item=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,drop);
                    item.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(item);
                }
            }
            level.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,level,pos,next,moving);
    }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){
        if(!active)return;int meta=state.getValue(META);
        float x=pos.getX()+.5F,y=pos.getY()+.25F+random.nextFloat()*6F/16F,z=pos.getZ()+.5F;
        float offset=.52F,side=random.nextFloat()*.6F-.3F;random.nextFloat();random.nextFloat();
        if(meta==4){x-=offset;z+=side;}else if(meta==5){x+=offset;z+=side;}else if(meta==2){x+=side;z-=offset;}else if(meta==3){x+=side;z+=offset;}else return;
        level.addParticle(ParticleTypes.SMOKE,x,y,z,0,0,0);level.addParticle(ParticleTypes.FLAME,x,y,z,0,0,0);
    }
}
