package com.hbm.machine.refueler;

import com.hbm.item.fluid.FluidIdentifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
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
import net.minecraft.world.phys.shapes.*;

/** Source metadata and thin wall-side collision, with the original fluid-identifier interaction. */
public final class RefuelerBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public RefuelerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RefuelerEntity(pos,state);}
    @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,RefuelerContent.ENTITY.get(),RefuelerEntity::tick);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(META)){case 2->box(0,0,12,16,16,16);case 3->box(0,0,0,16,16,4);case 4->box(12,0,0,16,16,16);case 5->box(0,0,0,4,16,16);default->Shapes.block();};}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(level.isClientSide||player.isShiftKeyDown())return ItemInteractionResult.SUCCESS;
        if(!(stack.getItem()instanceof FluidIdentifier identifier)||!(level.getBlockEntity(pos)instanceof RefuelerEntity station))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var type=identifier.getType(level,pos,stack);station.tank.setTankType(type);station.setChanged();
        Component fluid=type.hasLocalizedOverride()?Component.literal(type.getConditionalName()):Component.translatable(type.getConditionalName());
        player.displayClientMessage(Component.translatable("message.hbm.refueler.fluid",fluid).withStyle(ChatFormatting.YELLOW),false);
        return ItemInteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return level.isClientSide||player.isShiftKeyDown()?InteractionResult.SUCCESS:InteractionResult.PASS;}
}
