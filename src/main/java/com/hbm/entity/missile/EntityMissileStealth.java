package com.hbm.entity.missile;
import com.hbm.hazard.HbmHazards;
import com.hbm.missile.completion.MissileContent;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import java.util.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
/** Source radar-invisible stealth missile, not merely a differently skinned strong missile. */
public class EntityMissileStealth extends EntityMissileBaseNT implements com.hbm.radar.StealthRadarExempt {
 public EntityMissileStealth(EntityType<?> type,Level level){super(type,level);}public EntityMissileStealth(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_stealth"),level);initialize(x,y,z,a,b);}
 @Override public List<ItemStack> getDebris(){var bolt=new ItemStack(MissileContent.item("bolt"),4);bolt.set(HbmHazards.LEGACY_METADATA.get(),30);return new ArrayList<>(List.of(bolt));}
 @Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_stealth"));}@Override public boolean canBeSeenBy(Object radar){return false;}
 @Override public void onMissileImpact(HitResult hit){explodeStandard(20F,24,false);LargeConventionalEffects.standard(level(),getX(),getY(),getZ());}
 @Override public ItemStack getDebrisRareDrop(){return com.hbm.item.ash.FlatAshContent.stack(2,1);}
}
