package com.hbm.door.sourceseal;

import com.hbm.interfaces.IBomb;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original BlockSeal: native full cube, raw orientation/latch metadata, horizontal variable frame. Not BlockDoorGeneric. */
public final class SourceSealBlock extends Block implements IBomb {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public SourceSealBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(10,60).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return !(context.getLevel()instanceof ServerLevel server)||SourceSealActions.haloLoaded(server,context.getClickedPos())?defaultBlockState():null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){int quadrant=net.minecraft.util.Mth.floor(placer.getYRot()*4F/360F+.5D)&3;level.setBlock(pos,state.setValue(META,new int[]{2,1,3,0}[quadrant]),2);}
    private InteractionResult activate(Level level,BlockPos pos,BlockState state,Player player){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;SourceSealActions.toggle((ServerLevel)level,pos,state.getValue(META));return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,state,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(level,pos,state,player).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){if(!(level instanceof ServerLevel server))return BombReturnCode.UNDEFINED;var pos=new BlockPos(x,y,z);int result=SourceSealActions.toggle(server,pos,level.getBlockState(pos).getValue(META));return result<0?BombReturnCode.UNDEFINED:result==0?BombReturnCode.ERROR_INCOMPATIBLE:BombReturnCode.TRIGGERED;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){
        // Same server-side source state machine; clients only receive authoritative latch/blocks, not cold-world speculative mutations.
        if(level instanceof ServerLevel server)SourceSealActions.redstone(server,pos);
        super.neighborChanged(state,level,pos,neighbor,from,moving);
    }
}
