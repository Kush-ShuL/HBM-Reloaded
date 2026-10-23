package com.hbm.entity.missile;
import com.hbm.bomb.nuclear.RbmkMushEffects;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.missile.completion.MissileContent;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
/** Source Shuttle's quiet64-resolution NT impact, RBMK mushroom and original Robin explosion sound. */
public class EntityMissileShuttle extends EntityMissileBaseNT {
 public EntityMissileShuttle(EntityType<?> type,Level level){super(type,level);}public EntityMissileShuttle(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_shuttle"),level);initialize(x,y,z,a,b);}
 @Override public void onMissileImpact(HitResult hit){var explosion=new ExplosionNT(level(),null,getX()+.5F,getY()+.5F,getZ()+.5F,20F).overrideResolution(64);explosion.atttributes.add(ExAttrib.NOSOUND);explosion.atttributes.add(ExAttrib.NOPARTICLE);explosion.explode();RbmkMushEffects.send(level(),getX()+.5,getY()+1,getZ()+.5,10);level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("weapon.robin_explosion"),SoundSource.BLOCKS,4F,(1F+(level().random.nextFloat()-level().random.nextFloat())*.2F)*.7F);}
 @Override public List<ItemStack> getDebris(){return new ArrayList<>(List.of(new ItemStack(MissileContent.item("plate_steel"),8),new ItemStack(MissileContent.item("thruster_medium"),2),new ItemStack(MissileContent.item("canister_empty")),new ItemStack(Items.GLASS_PANE,2)));}
 @Override public ItemStack getDebrisRareDrop(){return new ItemStack(MissileContent.item("missile_generic"));}@Override public String getUnlocalizedName(){return "radar.target.shuttle";}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_shuttle"));}
}
