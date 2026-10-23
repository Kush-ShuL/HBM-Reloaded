package com.hbm.machine.woodprocessing;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.fluid.FluidIdentifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Original full-cube iron base, no facing metadata, real fluid-identifier selection and screwdriver suspension. */
public final class AutosawBlock extends BaseEntityBlock implements ToolableBlock {
    public AutosawBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new AutosawBlock());}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new AutosawEntity(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,WoodProcessingContent.AUTOSAW_ENTITY.get(),AutosawEntity::tick);}
    private InteractionResult activate(Level level,BlockPos pos,Player player){if(level.isClientSide||player.isShiftKeyDown())return InteractionResult.SUCCESS;var held=player.getMainHandItem();if(held.getItem() instanceof FluidIdentifier identifier&&level.getBlockEntity(pos) instanceof AutosawEntity saw){var type=identifier.getType(level,pos,held);if(AutosawEntity.ACCEPTED_FUELS.contains(type)){saw.tank.setTankType(type);saw.setChanged();player.displayClientMessage(Component.translatable("message.hbm.woodprocessing.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);return InteractionResult.SUCCESS;}}return InteractionResult.PASS;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}@Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float hitX,float hitY,float hitZ,ToolType tool){if(tool!=ToolType.SCREWDRIVER||!(level.getBlockEntity(pos) instanceof AutosawEntity saw))return false;saw.isSuspended=!saw.isSuspended;saw.setChanged();return true;}
}
