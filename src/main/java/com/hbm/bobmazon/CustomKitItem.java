package com.hbm.bobmazon;

import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

/** ItemKitCustom inherits the actual ItemKitNBT open/ordered NBT semantics; there is no second kit identity. */
public final class CustomKitItem extends Item {
 public CustomKitItem(){super(new Properties().stacksTo(1));}
 public static ItemStack create(HolderLookup.Provider lookup,List<ItemStack> contents,String name,List<String> lore,int first,int second){
  var kit=new ItemStack(BobmazonContent.KIT.get());var tag=new CompoundTag();tag.putInt("color1",first);tag.putInt("color2",second);var items=new ListTag();
  for(int i=0;i<contents.size();i++)if(!contents.get(i).isEmpty()){var row=(CompoundTag)contents.get(i).save(lookup);row.putByte("slot",(byte)i);items.add(row);}tag.put("items",items);kit.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
  if(name!=null)kit.set(DataComponents.CUSTOM_NAME,Component.literal(name).withStyle(style->style.withItalic(false)));
  if(!lore.isEmpty())kit.set(DataComponents.LORE,new ItemLore(lore.stream().map(line->(Component)Component.literal(line).withStyle(style->style.withColor(ChatFormatting.GRAY).withItalic(false))).toList()));
  return kit;
 }
 public static int color(ItemStack stack,int pass){return pass==0?0xffffff:stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("color"+pass);}
 public static List<ItemStack> contents(ItemStack kit,HolderLookup.Provider lookup){
  var rows=kit.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getList("items",Tag.TAG_COMPOUND);var result=new ArrayList<ItemStack>(Collections.nCopies(rows.size(),ItemStack.EMPTY));
  for(var entry:rows){var row=(CompoundTag)entry;int slot=row.getByte("slot");if(slot>=0&&slot<result.size())result.set(slot,ItemStack.parseOptional(lookup,row));}return result;
 }
 @Override public Component getName(ItemStack stack){return Component.translatable("item.kit_custom.name");}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
  var kit=player.getItemInHand(hand);
  // The source intentionally ignores leftovers when the actual player inventory is full.
  for(var item:contents(kit,level.registryAccess()))if(!item.isEmpty())player.getInventory().add(item.copy());
  kit.shrink(1);level.playSound(player,player.getX(),player.getY(),player.getZ(),com.hbm.registry.HbmSounds.get("item.unpack"),SoundSource.PLAYERS,1,1);return InteractionResultHolder.sidedSuccess(kit,level.isClientSide);
 }
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
  if(context.registries()==null)return;var rows=contents(stack,context.registries());if(!rows.isEmpty()){lines.add(Component.translatable("tooltip.hbm.bobmazon.contains"));for(var item:rows)if(!item.isEmpty())lines.add(Component.literal("-").append(item.getHoverName()).append(item.getCount()>1?" x"+item.getCount():""));}
 }
}
