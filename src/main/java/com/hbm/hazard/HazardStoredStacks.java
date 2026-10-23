package com.hbm.hazard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Source container layouts plus modern item components; numeric legacy IDs remain explicit unresolved. */
public final class HazardStoredStacks {
    private HazardStoredStacks(){}
    public static List<ItemStack> read(ItemStack stack,String kind,int slots,Map<String,String> aliases){
        if(stack.getItem() instanceof HazardInventoryItem inventory)return inventory.hazardContents(stack,slots);
        var contents=stack.get(DataComponents.CONTAINER);
        if(contents!=null){
            List<ItemStack> result=new ArrayList<>();
            for(int i=0;i<Math.min(contents.getSlots(),slots);i++)if(!contents.getStackInSlot(i).isEmpty())result.add(contents.getStackInSlot(i));
            return result;
        }
        var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom==null)return List.of();
        CompoundTag data=custom.copyTag();List<ItemStack> result=new ArrayList<>();
        if(kind.equals("crate")){
            for(int i=0;i<slots;i++){ItemStack held=parse(data.getCompound("slot"+i),aliases);if(!held.isEmpty())result.add(held);}
        }else{
            var list=data.getList("items",Tag.TAG_COMPOUND);
            for(int i=0;i<Math.min(slots,list.size());i++){
                var tag=list.getCompound(i);int slot=tag.getByte("slot");
                if(slot<0||slot>=slots)continue;
                ItemStack held=parse(tag,aliases);if(!held.isEmpty())result.add(held);
            }
        }
        return result;
    }
    public static ItemStack parse(CompoundTag tag,Map<String,String> aliases){
        if(!tag.contains("id"))return ItemStack.EMPTY;
        if(!tag.contains("id",Tag.TAG_STRING))throw new HazardStateUnavailable("Legacy numeric stored item requires world-registry conversion");
        String stored=tag.getString("id");String mapped=aliases.getOrDefault(stored,stored);
        ResourceLocation id=ResourceLocation.tryParse(mapped);
        if(id==null||!BuiltInRegistries.ITEM.containsKey(id))throw new HazardStateUnavailable("Stored item not registered: "+stored);
        if(tag.contains("Count")){
            int count=tag.getByte("Count");if(count<=0)return ItemStack.EMPTY;
            ItemStack stack=new ItemStack(BuiltInRegistries.ITEM.get(id),count);
            int metadata=tag.getShort("Damage");
            if(metadata>=0){
                if(stack.isDamageableItem())stack.setDamageValue(metadata);
                else stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);
            }
            if(tag.contains("tag",Tag.TAG_COMPOUND))stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag.getCompound("tag")));
            return stack;
        }
        var lookup=HazardContext.lookup();
        if(lookup==null)throw new HazardStateUnavailable("Stored component inventory requires registry lookup context");
        CompoundTag modern=tag.copy();modern.putString("id",mapped);
        ItemStack result=ItemStack.parseOptional(lookup,modern);
        if(result.isEmpty()&&(!tag.contains("count")||tag.getInt("count")>0))throw new HazardStateUnavailable("Unable to decode stored item "+stored);
        return result;
    }
}
