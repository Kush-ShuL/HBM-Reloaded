package com.hbm.machine.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Source slotN item payload, with modern ItemStack codecs; no second container inventory component. */
public final class CrateStackData {
    public static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    public static void data(ItemStack stack,CompoundTag tag){stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    public static ItemStack slot(CompoundTag tag,int slot,HolderLookup.Provider lookup){return ItemStack.parseOptional(lookup,tag.getCompound("slot"+slot));}
    public static void saveSlots(CompoundTag tag,Container container,HolderLookup.Provider lookup){for(int i=0;i<container.getContainerSize();i++){var item=container.getItem(i);if(item.isEmpty())tag.remove("slot"+i);else tag.put("slot"+i,item.save(lookup));}}
    public static void loadSlots(CompoundTag tag,Container container,HolderLookup.Provider lookup){for(int i=0;i<container.getContainerSize();i++)container.setItem(i,slot(tag,i,lookup));}
    public static int compressedSize(ItemStack stack,HolderLookup.Provider lookup)throws IOException{
        //All migrated item NBT lives in the actual component patch. Count/id and old metadata are excluded.
        var encoded=(CompoundTag)stack.save(lookup);var measured=encoded.getCompound("components").copy();measured.remove("minecraft:damage");measured.remove("hbm:legacy_metadata");
        try(var bytes=new ByteArrayOutputStream()){NbtIo.writeCompressed(measured,bytes);return bytes.size();}
    }
    private static boolean sourceData(net.minecraft.core.component.DataComponentType<?> type){return type!=DataComponents.DAMAGE&&type!=com.hbm.hazard.HbmHazards.LEGACY_METADATA.get();}
    public static boolean hasSourceData(ItemStack stack){return stack.getComponentsPatch().entrySet().stream().anyMatch(entry->sourceData(entry.getKey()));}
    public static boolean hasNonPayloadData(ItemStack stack){return stack.getComponentsPatch().entrySet().stream().anyMatch(entry->sourceData(entry.getKey())&&entry.getKey()!=DataComponents.CUSTOM_DATA);}
    private static <T> void resetComponent(ItemStack stack,net.minecraft.core.component.DataComponentType<T> type){T value=stack.getItem().components().get(type);if(value==null)stack.remove(type);else stack.set(type,value);}
    private static <T> void copyComponent(ItemStack target,ItemStack source,net.minecraft.core.component.DataComponentType<T> type){T value=source.get(type);if(value==null)target.remove(type);else target.set(type,value);}
    public static void clearSourceData(ItemStack stack){for(var entry:new java.util.ArrayList<>(stack.getComponentsPatch().entrySet()))if(sourceData(entry.getKey()))resetComponent(stack,entry.getKey());}
    public static void copySourceData(ItemStack target,ItemStack source){clearSourceData(target);for(var entry:source.getComponentsPatch().entrySet())if(sourceData(entry.getKey()))copyComponent(target,source,entry.getKey());}
    private CrateStackData(){}
}
