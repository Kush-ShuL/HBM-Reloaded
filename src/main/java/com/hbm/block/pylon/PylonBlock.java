package com.hbm.block.pylon;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class PylonBlock extends DummyableBlock {
    public final PylonKind kind;
    public PylonBlock(PylonKind kind) { super(properties(kind)); this.kind=kind; }
    private static Properties properties(PylonKind kind) {
        var properties=Properties.of().strength(5,6).noOcclusion();
        return kind.medium()&&!kind.steel()?properties.ignitedByLava():properties.requiresCorrectToolForDrops();
    }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return simpleCodec(properties->new PylonBlock(kind)); }
    @Override public int[] dimensions() { return kind.dimensions(); }
    @Override public int offset() { return kind==PylonKind.SUBSTATION?1:0; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=super.getStateForPlacement(context);
        if(state!=null&&kind==PylonKind.LARGE&&context.getPlayer()!=null) {
            int i=(int)Math.floor(context.getPlayer().getYRot()*4F/180F+.5D)&3;
            Direction direction=new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST}[i];
            state=state.setValue(LEGACY_META,direction.get3DDataValue()+10);
        }
        return state;
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);
        if(level.isClientSide||kind!=PylonKind.SUBSTATION) return;
        BlockPos core=pos.relative(facing(state),-offset());
        for(int x:new int[]{-1,1}) for(int z:new int[]{-1,1}) setPort(level,core.offset(x,0,z),true);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return isCore(state)?new PylonBlockEntity(pos,state):kind==PylonKind.SUBSTATION&&isPort(state)?new PylonPortBlockEntity(pos,state):null;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return level.isClientSide||!isCore(state)?null:createTickerHelper(type,PylonContent.ENTITY.get(),PylonBlockEntity::tick); }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit) {
        var result=super.useItemOn(stack,state,level,pos,player,hand,hit);
        if(result!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION||player.isShiftKeyDown()) return result;
        if(level.isClientSide) return PylonBlockEntity.dyeColor(stack)!=0?ItemInteractionResult.SUCCESS:result;
        BlockPos core=findCore(level,pos);
        return core!=null&&level.getBlockEntity(core) instanceof PylonBlockEntity pylon&&pylon.setColor(stack)?ItemInteractionResult.SUCCESS:result;
    }
    // This multiblock has no menu. Consuming the fallback would prevent ItemWiring.useOn.
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) { return InteractionResult.PASS; }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving) {
        if(!replacement.is(this)&&level.getBlockEntity(pos) instanceof PylonBlockEntity pylon) pylon.disconnectAll();
        super.onRemove(state,level,pos,replacement,moving);
    }
}
