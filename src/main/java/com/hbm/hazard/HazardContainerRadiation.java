package com.hbm.hazard;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.item.ItemStack;

/** NBT -> source container -> external-storage transformer order is registered by HbmHazards. */
public final class HazardContainerRadiation implements HazardTransformer {
    public record StoredStack(ItemStack stack,long amount) {}
    @FunctionalInterface public interface ExternalInventory { Optional<List<StoredStack>> contents(ItemStack storage); }
    private static final List<ExternalInventory> EXTERNAL=new CopyOnWriteArrayList<>();
    private static final ThreadLocal<Integer> DEPTH=ThreadLocal.withInitial(()->0);
    private final HazardRegistry registry;
    private final JsonObject profiles;
    private final Map<String,String> aliases;
    private final boolean external;
    public HazardContainerRadiation(HazardRegistry registry,JsonObject profiles,Map<String,String> aliases,boolean external){
        this.registry=registry;this.profiles=profiles;this.aliases=Map.copyOf(aliases);this.external=external;
    }
    public static void addExternalInventory(ExternalInventory inventory){EXTERNAL.add(java.util.Objects.requireNonNull(inventory));}
    public static void removeExternalInventory(ExternalInventory inventory){EXTERNAL.remove(inventory);}
    @Override public void transformPost(HazardStack stack,List<HazardEntry> entries){
        var settings=HazardRuntime.settings();
        if(settings.lbsm()&&(external?settings.safeMEDrives():settings.safeCrates()))return;
        if(stack.original()==null)return;
        var profile=profiles.has(stack.item())?profiles.getAsJsonObject(stack.item()):null;
        if(!external&&profile==null)return;
        int depth=DEPTH.get();
        if(depth>=64){entries.add(unresolved("Nested hazard inventory exceeds safe evaluation depth"));return;}
        DEPTH.set(depth+1);
        try{
            float radiation=0;boolean found=false;
            if(external){
                for(var provider:EXTERNAL){
                    var contents=provider.contents(stack.original());
                    if(contents.isPresent()){
                        for(var held:contents.get())if(!held.stack().isEmpty()&&held.amount()>0)
                            radiation+=radiation(held.stack())*held.amount();
                        found=true;break;
                    }
                }
            }else{
                String kind=profile.get("kind").getAsString();
                radiation=sum(HazardStoredStacks.read(stack.original(),kind,profile.get("slots").getAsInt(),aliases));
                if(kind.equals("containment"))radiation=(float)squirt(radiation);
                if(kind.equals("bag"))radiation*=2F;
                found=true;
            }
            if(found&&radiation>0)entries.add(new HazardEntry(HazardType.RADIATION,radiation));
        }catch(HazardStateUnavailable missing){entries.add(unresolved(missing.getMessage()));}
        finally{if(depth==0)DEPTH.remove();else DEPTH.set(depth);}
    }
    private float sum(List<ItemStack> stacks){
        float result=0;
        for(ItemStack held:stacks){
            if(held.isEmpty())continue;
            result+=radiation(held)*held.getCount();
        }
        return result;
    }
    private float radiation(ItemStack stack){
        var level=registry.evaluate(stack.copy()).firstLevel(HazardType.RADIATION);
        if(level.isEmpty())throw new HazardStateUnavailable("Nested inventory has unresolved radiation state");
        return (float)level.getAsDouble();
    }
    private static HazardEntry unresolved(String reason){return new HazardEntry(HazardType.RADIATION,0,List.of(),List.of(reason));}
    public static double squirt(double value){return Math.sqrt(value+1D/((value+2D)*(value+2D)))-1D/(value+2D);}
}
