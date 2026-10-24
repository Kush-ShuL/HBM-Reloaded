package com.hbm.block.rebar;
import java.util.*;
import com.hbm.world.structure.materials.ColoredConcreteBlock;
import com.hbm.world.structure.materials.ColoredConcreteItem;
import com.hbm.world.structure.materials.MeteorMaterialContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
/** Exact ItemRebarPlacer.acceptableConk list; only genuine admitted block types, no substitutions. */
public final class RebarConcrete {
 public static List<ItemStack> patterns(){var result=new ArrayList<ItemStack>();for(String name:List.of("concrete","concrete_rebar","concrete_smooth","concrete_pillar","concrete_colored","concrete_colored_ext")){var id=ResourceLocation.parse("hbm:"+name);if(!BuiltInRegistries.BLOCK.containsKey(id))continue;var block=BuiltInRegistries.BLOCK.get(id);if(block instanceof ColoredConcreteBlock){result.addAll(MeteorMaterialContent.coloredStacks());continue;}int count=name.equals("concrete_colored")?16:name.equals("concrete_colored_ext")?8:1;for(int meta=0;meta<count;meta++){var stack=new ItemStack(block);if(count>1)stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),meta);result.add(stack);}}return result;}
 public static boolean valid(ItemStack stack){if(stack.isEmpty())return false;var item=stack.getItem();if(item instanceof ColoredConcreteItem)return true;int meta=stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());for(var expected:patterns())if(expected.getItem()==item&&expected.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0)==meta)return true;return false;}
 public static boolean valid(BlockState state){if(state.getBlock() instanceof ColoredConcreteBlock)return true;var stack=new ItemStack(state.getBlock());var property=state.getBlock().getStateDefinition().getProperty("legacy_meta");if(property!=null)stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),Integer.parseInt(value(state,property)));return valid(stack);}
 @SuppressWarnings({"rawtypes","unchecked"})private static String value(BlockState state,net.minecraft.world.level.block.state.properties.Property property){return property.getName(state.getValue(property));}
 public static BlockState state(ItemStack stack){if(stack.getItem() instanceof ColoredConcreteItem item)return item.getBlock().defaultBlockState().setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,item.color().getId());if(!(stack.getItem() instanceof BlockItem item)||!valid(stack))return null;var state=item.getBlock().defaultBlockState();var property=state.getBlock().getStateDefinition().getProperty("legacy_meta");return property==null?state:meta(state,property,stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0));}
 @SuppressWarnings({"rawtypes","unchecked"})private static BlockState meta(BlockState state,net.minecraft.world.level.block.state.properties.Property property,int value){var parsed=property.getValue(Integer.toString(value));return parsed.isPresent()?state.setValue(property,(Comparable)parsed.get()):state;}
 public static BlockState fallback(){return BuiltInRegistries.BLOCK.get(ResourceLocation.parse("hbm:concrete_rebar")).defaultBlockState();}
 private RebarConcrete(){}
}
