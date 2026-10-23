package com.hbm.machine.precisionassembler;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
/** Original BrokenItem: exactly itemID + itemMeta, original count, no copied output NBT or fabricated repair mechanic. */
public final class BrokenItem extends Item {
 public BrokenItem(){super(new Properties());}
 public static ItemStack make(ItemStack output){var result=new ItemStack(PrecisionAssemblerContent.BROKEN.get(),output.getCount());var tag=new CompoundTag();tag.putString("itemID",PrecisionAssemblerRecipeConfig.sourceName(output.getItem()));tag.putInt("itemMeta",output.getOrDefault(HbmHazards.LEGACY_METADATA.get(),output.getDamageValue()));result.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return result;}
 public static CompoundTag sourceData(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 public static ItemStack original(ItemStack stack){var tag=sourceData(stack);if(!tag.contains("itemID"))return ItemStack.EMPTY;try{return com.hbm.recipe.generic.GenericRecipeConfigCodec.legacyStack(tag.getString("itemID"),tag.getInt("itemMeta"),PrecisionAssemblerRecipeConfig.aliases());}catch(IllegalArgumentException unknown){return ItemStack.EMPTY;}}
 @Override public Component getName(ItemStack stack){var original=original(stack);return original.isEmpty()?Component.translatable("item.broken_item.name"):Component.translatable("item.broken_item.prefix",original.getHoverName());}
}
