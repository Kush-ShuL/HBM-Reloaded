package com.hbm.world.structure.loot;
import com.google.gson.*;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.mojang.serialization.Dynamic;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
/** Per-template 1.7 numeric IDs are resolved before actual modern item-component decoding. */
public final class LegacyStructureItems {
    private static final Map<String,String> ALIASES=aliases();
    private static Map<String,String> aliases(){try(var input=LegacyStructureItems.class.getResourceAsStream("/hbm/structure-item-aliases.json")){if(input==null)throw new IllegalStateException("Run port_structure_loot.py");var result=new LinkedHashMap<String,String>();JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->result.put(entry.getKey(),entry.getValue().getAsString()));return Map.copyOf(result);}catch(IOException error){throw new IllegalStateException(error);}}
    public static CompoundTag convertInventory(CompoundTag source,Map<Integer,String> palette,HolderLookup.Provider lookup){var result=source.copy();if(result.contains("hbm_exposure_particle",Tag.TAG_COMPOUND)){var cached=result.getCompound("hbm_exposure_particle").copy();cached.putByte("Count",(byte)1);var identity=convertStack(cached,palette,lookup);if(!identity.isEmpty())result.put("hbmParticleIdentity",identity.copyWithCount(1).save(lookup));result.remove("hbm_exposure_particle");}for(String key:List.of("items","Items"))if(result.contains(key,Tag.TAG_LIST)){var items=result.getList(key,Tag.TAG_COMPOUND);var converted=new ListTag();for(var value:items){var old=(CompoundTag)value;var modern=(CompoundTag)convertStack(old,palette,lookup).saveOptional(lookup);if(old.contains("slot"))modern.put("slot",old.get("slot").copy());if(old.contains("Slot"))modern.put("Slot",old.get("Slot").copy());converted.add(modern);}result.put(key,converted);}convertSteelFurnaceLastItems(result,palette,lookup);return result;}
    public static ItemStack convertStack(CompoundTag source,Map<Integer,String> palette,HolderLookup.Provider lookup){
        if(!source.contains("Count"))return ItemStack.parse(lookup,source).orElseThrow(()->new IllegalArgumentException("Invalid modern source template item "+source));
        String sourceName;if(source.contains("id",Tag.TAG_STRING))sourceName=source.getString("id");else{int id=source.getShort("id");/* Original lighthouse omits its palette and stores official1.7 writable-book386. Do not guess other numeric/mod IDs. */sourceName=palette.containsKey(id)?palette.get(id):id==386?net.minecraft.util.datafix.fixes.ItemIdFix.getItem(id):null;if(sourceName==null)throw new IllegalArgumentException("Template itemPalette has no legacy item ID "+id);}
        int metadata=source.getShort("Damage"),count=source.getByte("Count");String name=ALIASES.getOrDefault(sourceName,sourceName);boolean hbm=sourceName.startsWith("hbm:");
        var fixed=source.copy();fixed.remove("slot");fixed.remove("Slot");fixed.putString("id",name);
        if(hbm&&fixed.contains("tag",Tag.TAG_COMPOUND))fixed.put("tag",convertNested(fixed.getCompound("tag"),palette,lookup));
        // X5846 predates data versions. After its numeric registry/metadata is
        // explicitly resolved, vanilla's 1.12.2 string-ID schema is the faithful
        // input for official display, enchantment, potion, book and component fixes.
        var dynamic=DataFixers.getDataFixer().update(References.ITEM_STACK,new Dynamic<>(NbtOps.INSTANCE,fixed),1343,SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        if(!(dynamic.getValue() instanceof CompoundTag modern))throw new IllegalStateException("Official item datafix did not return a compound");
        var id=ResourceLocation.tryParse(modern.getString("id"));if(id==null||!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalArgumentException("Unported real template item "+sourceName+"@"+metadata+" (target "+id+")");
        if(count<=0)return ItemStack.EMPTY;
        var updated=ItemStack.parse(lookup,modern).orElseThrow(()->new IllegalArgumentException("Unable to datafix real source template item "+sourceName));
        if(!hbm)return updated;
        var target=GenericRecipeConfigCodec.legacyStack(name,metadata,Map.of());target.applyComponents(updated.getComponentsPatch());target.setCount(count);return target;
    }
    private static CompoundTag convertNested(CompoundTag source,Map<Integer,String> palette,HolderLookup.Provider lookup){var result=source.copy();for(String key:new ArrayList<>(result.getAllKeys())){Tag value=result.get(key);if(value instanceof CompoundTag compound){if(compound.contains("id")&&compound.contains("Count"))result.put(key,convertStack(compound,palette,lookup).saveOptional(lookup));else result.put(key,convertNested(compound,palette,lookup));}else if(value instanceof ListTag list&&list.getElementType()==Tag.TAG_COMPOUND){var converted=new ListTag();for(var entry:list){var compound=(CompoundTag)entry;if(compound.contains("id")&&compound.contains("Count")){var modern=(CompoundTag)convertStack(compound,palette,lookup).saveOptional(lookup);if(compound.contains("slot"))modern.put("slot",compound.get("slot").copy());if(compound.contains("Slot"))modern.put("Slot",compound.get("Slot").copy());converted.add(modern);}else converted.add(convertNested(compound,palette,lookup));}result.put(key,converted);}}return result;}
    /** Only the actual source steel-furnace cached-input list, using the existing palette/datafixer. */
    private static void convertSteelFurnaceLastItems(CompoundTag source,Map<Integer,String> palette,HolderLookup.Provider lookup){
        if(!List.of("hbm:furnace_steel","tileentity_furnace_steel","hbm:tileentity_furnace_steel").contains(source.getString("id"))||!source.contains("lastItems",Tag.TAG_LIST))return;
        ListTag converted=new ListTag();for(Tag value:source.getList("lastItems",Tag.TAG_COMPOUND)){CompoundTag old=(CompoundTag)value;CompoundTag modern=(CompoundTag)convertStack(old,palette,lookup).saveOptional(lookup);if(old.contains("lastItem"))modern.put("lastItem",old.get("lastItem").copy());converted.add(modern);}source.put("lastItems",converted);
    }
    private LegacyStructureItems(){}
}
