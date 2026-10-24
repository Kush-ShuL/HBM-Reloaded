package com.hbm.conveyor.belt;

import java.util.*;
import java.util.function.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** True ItemConveyorWand: original stackable variants, two-click routing, dry-run, inventory consumption and cleanup. */
public final class ConveyorWandItem extends Item {
 public enum Type{REGULAR,EXPRESS,DOUBLE,TRIPLE}
 public static BooleanSupplier LEFT_SHIFT=()->false;public static BiConsumer<ItemStack,Player> CLIENT_TICK=(stack,player)->{};public static Runnable CLEAR_PREVIEW=()->{};
 private final Type type;
 public ConveyorWandItem(Type type){super(new Properties());this.type=Objects.requireNonNull(type);}
 public static Type type(ItemStack stack){if(stack==null||stack.isEmpty())return Type.REGULAR;if(!(stack.getItem() instanceof ConveyorWandItem item))throw new IllegalArgumentException("Not a conveyor constructor");return item.type;}
 public static ItemStack make(Type type,int count){return new ItemStack(ConveyorContent.wand(type),count);}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.hbm.conveyor_wand_"+type.name().toLowerCase(Locale.ROOT));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flag){if(LEFT_SHIFT.getAsBoolean()){for(String line:Component.translatable("item.conveyor_wand.desc").getString().split("\\$",-1))text.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));if(type(stack)==Type.REGULAR)text.add(Component.translatable("item.conveyor_wand.vertical.desc").withStyle(ChatFormatting.AQUA));}else text.add(Component.translatable("gui.hbm.conveyor.shift",Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
 private static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 @Override public InteractionResult useOn(UseOnContext context){var player=context.getPlayer();if(player==null)return InteractionResult.PASS;var world=context.getLevel();var stack=context.getItemInHand();BlockPos clicked=context.getClickedPos();Direction side=context.getClickedFace();Type type=type(stack);
  if(player.isShiftKeyDown()&&!stack.has(DataComponents.CUSTOM_DATA)){var on=world.getBlockState(clicked);var block=on.getBlock();if(type==Type.REGULAR&&block==ConveyorContent.REGULAR.get()&&on.getValue(ConveyorBeltBlock.META)<6){if(side==Direction.UP)block=ConveyorContent.LIFT.get();else if(side==Direction.DOWN)block=ConveyorContent.CHUTE.get();if(block!=on.getBlock())world.setBlock(clicked,block.defaultBlockState().setValue(ConveyorBeltBlock.META,on.getValue(ConveyorBeltBlock.META)),3);}var toPlace=ConveyorContent.belt(type).get();if(type==Type.REGULAR){if(block==ConveyorContent.LIFT.get()&&side==Direction.UP)toPlace=ConveyorContent.LIFT.get();if(block==ConveyorContent.CHUTE.get()&&side==Direction.DOWN)toPlace=ConveyorContent.CHUTE.get();}var pos=clicked.relative(side);if(world.getBlockState(pos).canBeReplaced()){world.setBlock(pos,toPlace.defaultBlockState().setValue(ConveyorBeltBlock.META,ConveyorBeltBlock.facingMeta(player)),3);stack.shrink(1);}return InteractionResult.sidedSuccess(world.isClientSide);}
  if(world.getBlockState(clicked).getBlock() instanceof ConveyorBeltBlock belt&&belt.bendable()){var direction=stack.has(DataComponents.CUSTOM_DATA)?belt.input(world,clicked):belt.output(world,clicked);if(direction!=null&&world.getBlockState(clicked.relative(direction)).canBeReplaced())side=direction;}
  if(!stack.has(DataComponents.CUSTOM_DATA)){var tag=new CompoundTag();tag.putInt("x",clicked.getX());tag.putInt("y",clicked.getY());tag.putInt("z",clicked.getZ());tag.putInt("side",side.get3DDataValue());int count=0;if(player.getAbilities().instabuild)count=256;else for(var held:player.getInventory().items)if(!held.isEmpty()&&held.getItem()==this&&type(held)==type)count+=held.getCount();tag.putInt("count",count);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
  else{var tag=data(stack);int x=tag.getInt("x"),y=tag.getInt("y"),z=tag.getInt("z"),max=tag.getInt("count");var start=ConveyorBeltBlock.direction(tag.getInt("side"));if(start!=null&&!world.isClientSide){int result=ConveyorRoute.construct(world,null,type,player,x,y,z,start,clicked.getX(),clicked.getY(),clicked.getZ(),side,max);if(result>0){int remaining=ConveyorRoute.construct(world,(pos,state)->world.setBlock(pos,state,3),type,player,x,y,z,start,clicked.getX(),clicked.getY(),clicked.getZ(),side,max);if(!player.getAbilities().instabuild)for(var held:player.getInventory().items){if(!held.isEmpty()&&held.getItem()==this&&type(held)==type){int removed=Math.min(remaining,held.getCount());held.shrink(removed);remaining-=removed;}if(remaining<=0)break;}player.inventoryMenu.broadcastChanges();player.displayClientMessage(Component.translatable("message.hbm.conveyor.built"),false);}else player.displayClientMessage(Component.translatable(result==0?"message.hbm.conveyor.insufficient":"message.hbm.conveyor.obstructed"),false);}else if(world.isClientSide)CLEAR_PREVIEW.run();stack.remove(DataComponents.CUSTOM_DATA);}
  return InteractionResult.sidedSuccess(world.isClientSide);
 }
 @Override public void inventoryTick(ItemStack stack,Level world,Entity entity,int slot,boolean selected){if(!(entity instanceof Player player))return;if(!selected&&stack.has(DataComponents.CUSTOM_DATA)){var held=player.getMainHandItem();if(held.isEmpty()||held.getItem()!=this||type(held)!=type(stack)){stack.remove(DataComponents.CUSTOM_DATA);if(world.isClientSide)CLEAR_PREVIEW.run();}}if(world.isClientSide&&selected)CLIENT_TICK.accept(stack,player);}
 public static void beforeBreak(net.neoforged.neoforge.event.level.BlockEvent.BreakEvent event){if(event.isCanceled()||!(event.getPlayer().getMainHandItem().getItem() instanceof ConveyorWandItem)||BREAKING.get())return;BREAKING.set(true);try{breakLine(event.getPos(),event.getPlayer());}finally{BREAKING.set(false);}}
 private static final ThreadLocal<Boolean> BREAKING=ThreadLocal.withInitial(()->false);
 private static boolean breakLine(BlockPos pos,Player player){if(!player.isShiftKeyDown()||!player.getAbilities().instabuild||!(player instanceof ServerPlayer server))return false;var world=player.level();if(world.getBlockState(pos).getBlock() instanceof ConveyorBeltBlock belt){var input=belt.input(world,pos);var output=belt.output(world,pos);if(input!=null)breakExtra(server,pos.relative(input),32);if(output!=null)breakExtra(server,pos.relative(output),32);}return false;}
 private static void breakExtra(ServerPlayer player,BlockPos pos,int depth){if(--depth<=0)return;var world=player.serverLevel();if(!(world.getBlockState(pos).getBlock() instanceof ConveyorBeltBlock belt))return;var input=belt.input(world,pos);var output=belt.output(world,pos);if(!player.gameMode.destroyBlock(pos))return;player.connection.send(new ClientboundBlockUpdatePacket(world,pos));if(input!=null)breakExtra(player,pos.relative(input),depth);if(output!=null)breakExtra(player,pos.relative(output),depth);}
}
