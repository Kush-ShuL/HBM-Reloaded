package com.hbm.entity.mob.maskman;
/** Live GunNPCFactory configurations for the actual migrated MaskMan, BOTPrime and UFO callers and inherited source standardBullet/Grenade defaults. Other retired NPC configurations remain unported, never masquerade as one of these. */
public enum LegacyNpcBullets {
 TURBINE(1,1,0,200,100,150,false,0,0,0,0,false,false,15,0,0,true,0,"",false),
 ORB(2,.25F,0,60,100,100,false,0,0,0,1,false,false,12,1,1.5F,true,0,"",false),
 BOLT(3,5,0,100,15,20,true,5,2,95,.8,true,true,4,0,.5F,true,0,"reddust",true),
 ROCKET(4,1,.1,300,15,20,false,0,0,0,1,false,false,6,0,5,false,0,"smoke",false),
 TRACER(5,5,0,100,15,20,true,5,2,95,.8,true,true,4,1,0,true,0,"reddust",true),
 METEOR(6,1,.1,300,20,30,false,0,0,0,1,false,false,13,0,2.5F,false,3,"smoke",false),
 WORM_BOLT(7,5,0,60,15,25,false,5,2,95,.8,true,true,4,4,0,true,0,"",true),
 WORM_LASER(8,5,0,100,35,60,false,5,2,95,.8,true,true,4,2,0,true,0,"",true),
 UFO_ROCKET(9,2,.005,300,10,15,true,10,2,100,.8,false,false,6,0,0,false,0,"reddust",false);
 public final int sourceId,life,style,trail,higherChance,lowerChance,incendiary;public final float velocity,minDamage,maxDamage,explosive;public final double gravity,angle,bounce;public final boolean ricochet,penetrate,breakGlass,blockDamage,laser;public final String particles;
 LegacyNpcBullets(int id,float v,double g,int age,float min,float max,boolean ric,double a,int hc,int lc,double b,boolean pen,boolean glass,int s,int t,float e,boolean blocks,int fire,String p,boolean l){sourceId=id;velocity=v;gravity=g;life=age;minDamage=min;maxDamage=max;ricochet=ric;angle=a;higherChance=hc;lowerChance=lc;bounce=b;penetrate=pen;breakGlass=glass;style=s;trail=t;explosive=e;blockDamage=blocks;incendiary=fire;particles=p;laser=l;}
 public static LegacyNpcBullets byId(int id){for(var kind:values())if(kind.sourceId==id)return kind;return null;}
}
