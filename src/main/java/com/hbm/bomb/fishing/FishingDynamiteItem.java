package com.hbm.bomb.fishing;
import com.hbm.bomb.conventional.grenade.DynamiteGrenadeItem;
import com.hbm.bomb.conventional.grenade.GenericGrenade;
import net.minecraft.world.entity.LivingEntity;
/** ItemGrenadeFishing(3): use/stack16/60-tick fuse/.5 bounce are the sole source generic grenade. */
public final class FishingDynamiteItem extends DynamiteGrenadeItem {
 @Override public void explode(GenericGrenade grenade,LivingEntity owner){
  if(grenade.level().isClientSide)return;
  var pending=new FishingDetonation(FishingDynamiteContent.PENDING.get(),grenade.level());
  pending.setPos(grenade.position());grenade.level().addFreshEntity(pending);
 }
}
