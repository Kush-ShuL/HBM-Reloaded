package com.hbm.bobmazon;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import com.hbm.advancement.SourceAchievementData;
import com.hbm.item.battery.HbmBattery;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;

/** One shared, ordered source table drives the server purchases and the original client catalog. */
public final class BobmazonOffers {
 public enum Requirement {
  NONE(null),STEEL("achBlastFurnace"),ASSEMBLY("achAssembly"),CHEMICS("achChemplant"),OIL("achDesh"),NUCLEAR("achTechnetium"),HIDDEN("bobHidden");
  public final String source;
  Requirement(String source){this.source=source;}
  public boolean fulfilled(ServerPlayer player){if(this==NONE)return true;var node=SourceAchievementData.node(source);if(node==null)return false;var holder=player.server.getAdvancements().get(SourceAchievementData.id(node));return holder!=null&&player.getAdvancements().getOrStartProgress(holder).isDone();}
 }
 public record Offer(int index,ItemStack product,Requirement requirement,int cost,int rating,String comment,String author,JsonObject source,String pending){
  public boolean available(){return pending.isEmpty()&&!product.isEmpty();}
 }
 private static final JsonObject SOURCE=read();
 private static final Map<HolderLookup.Provider,Map<Boolean,List<Offer>>> CACHE=Collections.synchronizedMap(new WeakHashMap<>());
 private static JsonObject read(){try(var input=BobmazonOffers.class.getResourceAsStream("/hbm/bobmazon-offers.json")){if(input==null)throw new IllegalStateException("Missing actual Bobmazon offers");return JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();}catch(IOException error){throw new IllegalStateException(error);}}
 public static List<Offer> offers(HolderLookup.Provider lookup,boolean hidden){synchronized(CACHE){return CACHE.computeIfAbsent(lookup,key->new HashMap<>()).computeIfAbsent(hidden,key->build(lookup,hidden));}}
 private static List<Offer> build(HolderLookup.Provider lookup,boolean hidden){
  var result=new ArrayList<Offer>();for(var value:SOURCE.getAsJsonArray(hidden?"special":"standard")){var row=value.getAsJsonObject();var product=ItemStack.EMPTY;String unavailable="";try{product=product(row.getAsJsonObject("product"),lookup);}catch(IllegalArgumentException failure){unavailable=failure.getMessage();}
   result.add(new Offer(row.get("index").getAsInt(),product,Requirement.valueOf(row.get("requirement").getAsString()),row.get("cost").getAsInt(),row.get("rating").getAsInt()*4-1,row.get("comment").getAsString(),row.get("author").getAsString(),row,unavailable));}
  return List.copyOf(result);
 }
 private static ItemStack product(JsonObject source,HolderLookup.Provider lookup){
  if(source.has("pending"))throw new IllegalArgumentException(source.get("pending").getAsString());ItemStack stack;
  if(source.has("kit")){var contents=new ArrayList<ItemStack>();for(var row:source.getAsJsonArray("kit"))contents.add(product(row.getAsJsonObject(),lookup));var lore=new ArrayList<String>();if(source.has("lore"))source.getAsJsonArray("lore").forEach(line->lore.add(line.getAsString()));stack=CustomKitItem.create(lookup,contents,source.has("name")?source.get("name").getAsString():null,lore,source.get("color1").getAsInt(),source.get("color2").getAsInt());}
  else stack=ItemStack.CODEC.parse(lookup.createSerializationContext(JsonOps.INSTANCE),source.get("stack")).getOrThrow(message->new IllegalArgumentException(message));
  if(source.has("name"))stack.set(DataComponents.CUSTOM_NAME,Component.literal(source.get("name").getAsString()).withStyle(style->style.withItalic(!(source.has("resetName")&&source.get("resetName").getAsBoolean()))));
  if(source.has("fullBattery")){if(!(stack.getItem() instanceof HbmBattery battery))throw new IllegalArgumentException("Actual full-battery offer requires its real HbmBattery provider");battery.setCharge(stack,battery.getMaxCharge(stack));}
  return stack;
 }
 public static ItemStack requirementIcon(HolderLookup.Provider lookup,Requirement requirement){var icons=SOURCE.getAsJsonObject("requirementIcons");return ItemStack.CODEC.parse(lookup.createSerializationContext(JsonOps.INSTANCE),icons.get(requirement.name())).getOrThrow(message->new IllegalArgumentException(message));}
 private BobmazonOffers(){}
}
