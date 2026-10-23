package com.hbm.hazard;

import com.google.gson.JsonObject;
import java.util.OptionalDouble;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Source Fuel/RBMK/RTG formulas, including fresh-stack NBT initialization order. */
public final class HazardStateModifiers {
    private HazardStateModifiers(){}
    public static HazardModifier decode(JsonObject spec,JsonObject profile,float xenon135,float nugget){
        String type=spec.get("type").getAsString();
        float target=spec.has("target")?spec.get("target").getAsFloat():0;
        boolean linear=spec.has("linear")&&spec.get("linear").getAsBoolean();
        return (stack,level)->{
            if(type.equals("mode_528"))return HazardRuntime.settings().mode528()?target:level;
            if(profile==null||profile.get("kind").getAsString().equals("unresolved"))throw new HazardStateUnavailable("Missing source fuel state profile for "+stack.item());
            String kind=profile.get("kind").getAsString();
            if(type.equals("fuel_radiation")){
                double depletion=Math.pow(depletion(stack,profile),.4D);
                return (float)(level+(target-level)*depletion);
            }
            if(type.equals("rtg_radiation"))return kind.equals("rtg_pellet")?(float)(level+(target-level)*depletion(stack,profile)):level;
            if(type.equals("rbmk_hot")){
                if(!kind.equals("rbmk_rod"))return 0;
                return (int)Math.min(Math.ceil((stateValue(stack,profile,"hull")-100)/10D),60);
            }
            if(type.equals("rbmk_radiation")){
                if(kind.equals("rbmk_rod")){
                    double enrichment=enrichment(stack,profile);
                    double depleted=linear?1D-enrichment:1D-Math.pow(enrichment,2);
                    double xenon=stateValue(stack,profile,"xenon")/100D;
                    float result=(float)(level+(target-level)*depleted);
                    result+=xenon135*xenon;return result;
                }
                if(kind.equals("rbmk_pellet")){
                    int metadata=Math.abs(stack.metadata())%10;
                    float result=level+(target-level)*((metadata%5)/4F);
                    if(metadata>=5)result+=xenon135*nugget;
                    return result;
                }
                return level;
            }
            throw new HazardStateUnavailable("Unsupported source hazard modifier "+type);
        };
    }
    private static OptionalDouble override(HazardStack snapshot,String field){
        if(snapshot.original()==null||!(snapshot.original().getItem() instanceof HazardItemState item))return OptionalDouble.empty();
        return switch(field){
            case "depletion"->item.hazardDepletion(snapshot.original());
            case "enrichment"->item.hazardEnrichment(snapshot.original());
            case "xenon"->item.hazardXenon(snapshot.original());
            case "hull"->item.hazardHullHeat(snapshot.original());
            default->OptionalDouble.empty();
        };
    }
    private static double enrichment(HazardStack stack,JsonObject profile){
        var override=override(stack,"enrichment");
        return override.isPresent()?override.getAsDouble():stateValue(stack,profile,"yield")/profile.get("maximumYield").getAsDouble();
    }
    private static double depletion(HazardStack stack,JsonObject profile){
        var override=override(stack,"depletion");if(override.isPresent())return override.getAsDouble();
        String kind=profile.get("kind").getAsString();
        if(kind.equals("rbmk_rod"))return 1-enrichment(stack,profile);
        CompoundTag data=data(stack,profile);
        if(kind.equals("life_fuel"))return (double)data.getInt("life")/profile.get("maximumLife").getAsDouble();
        if(kind.equals("zirnox_fuel")){
            var lives=profile.getAsJsonArray("maximumLifeByMetadata");
            return (double)data.getInt("life")/lives.get(Math.abs(stack.metadata()%lives.size())).getAsDouble();
        }
        if(kind.equals("rtg_pellet"))return 1D-(double)data.getLong("PELLET_DEPLETION")/profile.get("maximumLife").getAsDouble();
        throw new HazardStateUnavailable("Unknown fuel durability class "+profile.get("sourceClass").getAsString());
    }
    private static double stateValue(HazardStack stack,JsonObject profile,String field){
        var override=override(stack,field);if(override.isPresent())return field.equals("xenon")?override.getAsDouble()*100:override.getAsDouble();
        return data(stack,profile).getDouble(field);
    }
    private static CompoundTag data(HazardStack stack,JsonObject profile){
        ItemStack original=stack.original();
        if(original==null)throw new HazardStateUnavailable("Fuel state requires an actual ItemStack");
        CustomData existing=original.get(DataComponents.CUSTOM_DATA);
        if(existing!=null)return existing.copyTag();
        CompoundTag data=new CompoundTag();
        switch(profile.get("kind").getAsString()){
            case "rbmk_rod"->{data.putDouble("yield",profile.get("maximumYield").getAsDouble());data.putDouble("core",20);data.putDouble("hull",20);}
            case "rtg_pellet"->data.putLong("PELLET_DEPLETION",profile.get("maximumLife").getAsLong());
            default->{}
        }
        original.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
        return data;
    }
}
