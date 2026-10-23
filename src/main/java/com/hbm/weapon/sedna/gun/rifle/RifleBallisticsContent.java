package com.hbm.weapon.sedna.gun.rifle;
import com.hbm.weapon.sedna.gun.mods.SourceWeaponModBindings;
import com.hbm.weapon.sedna.gun.pistols.PistolGuns;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
/** Actual cross-caliber upgrade routes, using all source ammunition arrays. */
public final class RifleBallisticsContent {
 private RifleBallisticsContent(){}
 public static void register(IEventBus bus){RifleBallistics.initialize();bus.addListener(RifleBallisticsContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{RifleBallistics.initializeCasings();
  SourceWeaponModBindings.caliber(5,PistolGuns.gun_henry,350,10,10F,RifleBallistics.r556_sp,RifleBallistics.r556_fmj,RifleBallistics.r556_jhp,RifleBallistics.r556_ap);
  SourceWeaponModBindings.caliber(6,PistolGuns.gun_henry,360,8,10F,RifleBallistics.r762_sp,RifleBallistics.r762_fmj,RifleBallistics.r762_jhp,RifleBallistics.r762_ap,RifleBallistics.r762_du,RifleBallistics.r762_he);
  SourceWeaponModBindings.caliber(7,PistolGuns.gun_henry,370,5,10F,RifleBallistics.bmg50_sp,RifleBallistics.bmg50_fmj,RifleBallistics.bmg50_jhp,RifleBallistics.bmg50_ap,RifleBallistics.bmg50_du,RifleBallistics.bmg50_he);
 });}
}
