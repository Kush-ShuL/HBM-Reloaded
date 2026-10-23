package com.hbm.block.structurebasic;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Source BlockWriting extends BlockPillar: fixed Y end texture; metadata never selects another axis. */
public final class MarkedConcreteBlock extends Block {
 public MarkedConcreteBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(15,96).requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends MarkedConcreteBlock> codec(){return simpleCodec(ignored->new MarkedConcreteBlock());}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;for(int line=0;line<10;line++)player.sendSystemMessage(Component.translatable("message.hbm.marked_concrete."+line).withStyle(ChatFormatting.RED));return InteractionResult.CONSUME;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return useWithoutItem(state,level,pos,player,hit)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
