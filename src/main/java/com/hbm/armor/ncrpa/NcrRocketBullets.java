package com.hbm.armor.ncrpa;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.panzerschreck.PanzerschreckBullets;
import com.hbm.weapon.sedna.gun.quadro.QuadroGuidance;
/** Original5 makeRPZB and5 makeNCR clones, independently named for real MK4 spawn/save identity. */
public final class NcrRocketBullets {
 public static final BulletConfig[] PLAIN=new BulletConfig[5],STEER=new BulletConfig[5];
 static{String[] names={"he","heat","demo","inc","phosphorus"};for(int index=0;index<5;index++){
  var original=PanzerschreckBullets.ROCKETS[index];PLAIN[index]=copy("hbm:rocket_ncrpa/"+names[index],original);STEER[index]=copy("hbm:rocket_ncrpa_steer/"+names[index],original).setLife(400).setOnUpdate(entity->QuadroGuidance.steeringAccelerate(entity,false));
 }}
 private static BulletConfig copy(String name,BulletConfig source){
  var target=new BulletConfig(name);
  target.ammo=source.ammo;target.casingItem=source.casingItem;target.casingAmount=source.casingAmount;target.ammoReloadCount=source.ammoReloadCount;target.casing=source.casing;target.renderer=source.renderer;
  target.pType=source.pType;target.onImpactBeam=source.onImpactBeam;target.rendererBeam=source.rendererBeam;
  target.velocity=source.velocity;target.spread=source.spread;target.wear=source.wear;target.damageMult=source.damageMult;target.armorThresholdNegation=source.armorThresholdNegation;target.armorPiercingPercent=source.armorPiercingPercent;target.knockbackMult=source.knockbackMult;target.headshotMult=source.headshotMult;target.ricochetAngle=source.ricochetAngle;
  target.projectilesMin=source.projectilesMin;target.projectilesMax=source.projectilesMax;target.maxRicochetCount=source.maxRicochetCount;target.expires=source.expires;target.selfDamageDelay=source.selfDamageDelay;
  target.damageFalloffByPen=source.damageFalloffByPen;target.impactsEntities=source.impactsEntities;target.doesPenetrate=source.doesPenetrate;target.isSpectral=source.isSpectral;target.blackPowder=source.blackPowder;target.renderRotations=source.renderRotations;target.clientKeepsRotation=source.clientKeepsRotation;
  target.gravity=source.gravity;target.dmgClass=source.dmgClass;target.onUpdate=source.onUpdate;target.onImpact=source.onImpact;target.onRicochet=source.onRicochet;target.onEntityHit=source.onEntityHit;return target;
 }
 public static void init(){}
 private NcrRocketBullets(){}
}
