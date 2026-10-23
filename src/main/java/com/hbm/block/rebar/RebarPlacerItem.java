package com.hbm.block.rebar;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
/** True ItemRebarPlacer two-corner region placement with a non-consuming concrete pattern, source iterationY/Z/X. */
public final class RebarPlacerItem extends Item {
 public RebarPlacerItem(){super(new Properties().stacksTo(1));}
 public static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 public static void data(ItemStack stack,CompoundTag tag){stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 public static ItemStack pattern(ItemStack stack,HolderLookup.Provider lookup){var items=data(stack).getList("items",10);if(items.isEmpty())return ItemStack.EMPTY;var tag=items.getCompound(0);var result=ItemStack.parseOptional(lookup,tag);if(result.isEmpty()&&tag.contains("id")){result=com.hbm.material.MaterialDistributionConfig.legacyStack(tag.getString("id"),tag.getShort("Damage"));if(!result.isEmpty())result.setCount(1);}return result;}
 public static void pattern(ItemStack stack,ItemStack pattern,HolderLookup.Provider lookup){var tag=data(stack);var items=new ListTag();if(!pattern.isEmpty()){var value=pattern.copyWithCount(1).save(lookup);if(value instanceof CompoundTag compound){compound.putByte("slot",(byte)0);items.add(compound);}}tag.put("items",items);data(stack,tag);}
 @Override public int getUseDuration(ItemStack stack,net.minecraft.world.entity.LivingEntity holder){return 1;}
 @Override public void inventoryTick(ItemStack stack,Level level,Entity holder,int slot,boolean selected){var tag=data(stack);if(!tag.contains("pos"))return;boolean held=selected||holder instanceof Player player&&(player.getMainHandItem()==stack||player.getOffhandItem()==stack);if(!held||!RebarConcrete.valid(pattern(stack,level.registryAccess()))){tag.remove("pos");data(stack,tag);}}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!level.isClientSide&&player.isShiftKeyDown()&&player instanceof ServerPlayer server){int slot=hand==InteractionHand.OFF_HAND?40:player.getInventory().selected;server.openMenu(new SimpleMenuProvider((id,inventory,p)->new RebarPlacerMenu(id,inventory,slot),Component.translatable("container.rebar")),buffer->buffer.writeVarInt(slot));}return InteractionResultHolder.pass(stack);}
 public static int available(Player player){int count=0;for(int i=0;i<36;i++)if(player.getInventory().getItem(i).is(RebarContent.ITEM.get()))count+=player.getInventory().getItem(i).getCount();return count;}
 private static void consume(Player player,int amount){for(int i=0;i<36&&amount>0;i++){var stack=player.getInventory().getItem(i);if(!stack.is(RebarContent.ITEM.get()))continue;int used=Math.min(stack.getCount(),amount);stack.shrink(used);amount-=used;}player.getInventory().setChanged();}
 private static void message(Player player,String key,ChatFormatting color,Object... args){player.displayClientMessage(Component.literal("[").withStyle(ChatFormatting.DARK_AQUA).append(Component.translatable("item.hbm.rebar_placer")).append("] ").append(Component.translatable(key,args).withStyle(color)),false);}
 @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();if(level.isClientSide)return InteractionResult.SUCCESS;var player=context.getPlayer();if(player==null)return InteractionResult.PASS;var stack=context.getItemInHand();if(!stack.has(DataComponents.CUSTOM_DATA))pattern(stack,new ItemStack(RebarConcrete.fallback().getBlock()),level.registryAccess());var concrete=pattern(stack,level.registryAccess());if(!RebarConcrete.valid(concrete)){message(player,"message.hbm.rebar.invalid",ChatFormatting.RED);return InteractionResult.SUCCESS;}var tag=data(stack);var second=context.getClickedPos().relative(context.getClickedFace());if(!tag.contains("pos")){tag.putIntArray("pos",new int[]{second.getX(),second.getY(),second.getZ()});data(stack,tag);return InteractionResult.SUCCESS;}int count=available(player);if(count<=0){message(player,"message.hbm.rebar.empty",ChatFormatting.RED);tag.remove("pos");data(stack,tag);return InteractionResult.SUCCESS;}int[] first=tag.getIntArray("pos");if(first.length!=3){tag.remove("pos");data(stack,tag);return InteractionResult.SUCCESS;}int minX=Math.min(first[0],second.getX()),maxX=Math.max(first[0],second.getX()),minY=Math.min(first[1],second.getY()),maxY=Math.max(first[1],second.getY()),minZ=Math.min(first[2],second.getZ()),maxZ=Math.max(first[2],second.getZ());int used=0;outer:for(int y=minY;y<=maxY;y++)for(int z=minZ;z<=maxZ;z++)for(int x=minX;x<=maxX;x++){if(count<=0)break outer;var pos=new BlockPos(x,y,z);if(level.isInWorldBounds(pos)&&level.getBlockState(pos).canBeReplaced()&&player.mayUseItemAt(pos,context.getClickedFace(),stack)){if(level.setBlock(pos,RebarContent.BLOCK.get().defaultBlockState(),3)){if(level.getBlockEntity(pos) instanceof RebarBlockEntity entity)entity.setup(RebarConcrete.state(concrete));used++;count--;}}}consume(player,used);message(player,"message.hbm.rebar.placed",ChatFormatting.GREEN,used);tag.remove("pos");data(stack,tag);player.inventoryMenu.broadcastChanges();return InteractionResult.SUCCESS;}
}
