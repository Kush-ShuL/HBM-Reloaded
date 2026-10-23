package com.hbm.client.weapon.pistols;
import java.util.*;
import com.hbm.client.animation.bus.*;
import net.minecraft.resources.ResourceLocation;
/** Original animator JSONs and actual X5846 loader; never substitutes canned animations. */
public final class PistolAnimationAssets {
 private static final Map<String,Map<String,BusAnimation>> LOADED=new HashMap<>();
 public static void clear(){LOADED.clear();}
 public static BusAnimation get(String model,String animation){var map=LOADED.computeIfAbsent(model,key->Objects.requireNonNull(AnimationLoader.load(ResourceLocation.parse("hbm:models/weapons/animations/"+key+".json")),"Source gun animation "+key));return Objects.requireNonNull(map.get(animation),"Source gun animation "+model+"/"+animation);}
}
