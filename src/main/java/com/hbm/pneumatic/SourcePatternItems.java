package com.hbm.pneumatic;

import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.Dynamic;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;

/** Original Item identity/metadata after vanilla flattening; explicit source tuples, never tag or material equivalence. */
public final class SourcePatternItems {
    private record Identity(String sourceItem,Object metadata,boolean hasSubtypes) {}
    private record PotionMetadata(boolean splash,String potion) {}
    private static final class SourceTable {
        private static final Map<Item,Identity> ITEMS=load();
        private static Map<Item,Identity> load(){
            var result=new IdentityHashMap<Item,Identity>();var ambiguous=Collections.newSetFromMap(new IdentityHashMap<Item,Boolean>());
            try(var input=SourcePatternItems.class.getResourceAsStream("/hbm/pattern-source-identities.json")){
                if(input==null)throw new IllegalStateException("Missing actual source pattern identities: run port_machine_autocrafter.py");
                var root=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();int sourceVersion=root.get("sourceDataVersion").getAsInt();
                for(var value:root.getAsJsonArray("entries")){
                    var row=value.getAsJsonObject();String oldName=row.get("sourceItem").getAsString();int metadata=row.get("sourceMetadata").getAsInt();
                    var old=new CompoundTag();old.putString("id",oldName);old.putByte("Count",(byte)1);old.putShort("Damage",(short)metadata);
                    if(row.has("entityId1343")){var entity=new CompoundTag();entity.putString("id",row.get("entityId1343").getAsString());var tag=new CompoundTag();tag.put("EntityTag",entity);old.put("tag",tag);}
                    // The checked-in tuples are actual1.7 Item registrations. Official fixes supply later renames as well as flattening.
                    var fixed=DataFixers.getDataFixer().update(References.ITEM_STACK,new Dynamic<>(NbtOps.INSTANCE,old),sourceVersion,SharedConstants.getCurrentVersion().getDataVersion().getVersion());
                    if(!(fixed.getValue() instanceof CompoundTag modern))throw new IllegalStateException("Official source item fix did not return a compound");
                    var id=ResourceLocation.tryParse(modern.getString("id"));if(id==null||!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Unresolved actual source pattern item "+oldName+"@"+metadata+" -> "+modern);
                    var item=BuiltInRegistries.ITEM.get(id);if(ambiguous.contains(item))continue;
                    var identity=new Identity(oldName,metadata,row.get("hasSubtypes").getAsBoolean());var previous=result.get(item);
                    if(previous!=null&&!previous.equals(identity)){
                        // E.g. old deadbush0 and tallgrass0 have exactly the same modern item. A native stack has no old provenance.
                        // Keep the modern singleton rather than silently merging two distinct old Items or inventing a hidden component.
                        result.remove(item);ambiguous.add(item);
                    }else result.put(item,identity);
                }
                return Collections.unmodifiableMap(result);
            }catch(IOException error){throw new IllegalStateException("Unable to load actual source pattern identities",error);}
        }
    }
    private static Identity identity(ItemStack stack){
        var id=BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(stack.getItem() instanceof com.hbm.item.autogen.AutogenItem)return new Identity(id.toString(),com.hbm.material.HbmMaterials.metadata(stack),true);
        if(id.equals(ResourceLocation.withDefaultNamespace("potion"))||id.equals(ResourceLocation.withDefaultNamespace("splash_potion"))){
            String potion=stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).potion().map(holder->holder.unwrapKey().map(key->key.location().toString()).orElse("")).orElse("");
            return new Identity("minecraft:potion",new PotionMetadata(id.getPath().equals("splash_potion"),potion),true);
        }
        if(id.equals(ResourceLocation.withDefaultNamespace("filled_map")))return new Identity("minecraft:filled_map",stack.get(DataComponents.MAP_ID),true);
        var mapped=id.getNamespace().equals("minecraft")?SourceTable.ITEMS.get(stack.getItem()):null;if(mapped!=null)return mapped;
        // HBM source providers retain one real Item plus the actual legacy_metadata component. Do not rewrite or infer their families.
        return new Identity(id.toString(),stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),stack.getItem() instanceof com.hbm.item.EnumMultiItem||stack.has(HbmHazards.LEGACY_METADATA.get()));
    }
    public static boolean sameItem(ItemStack first,ItemStack second){return identity(first).sourceItem.equals(identity(second).sourceItem);}
    public static boolean sameExact(ItemStack first,ItemStack second){var a=identity(first);var b=identity(second);return a.sourceItem.equals(b.sourceItem)&&Objects.equals(a.metadata,b.metadata);}
    public static boolean hasSubtypes(ItemStack stack){return identity(stack).hasSubtypes;}
    private SourcePatternItems(){}
}
