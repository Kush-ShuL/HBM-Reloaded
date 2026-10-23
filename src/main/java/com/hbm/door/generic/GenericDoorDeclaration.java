package com.hbm.door.generic;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** The actual admitted source DoorDecls, sharing original generic-door state/geometry contracts. */
public enum GenericDoorDeclaration {
    SILO_HATCH("silo_hatch",10,60,new int[]{0,0,2,2,2,2},2,new int[][]{{1,0,1,-3,3,0},{0,0,1,-3,3,0},{-1,0,1,-3,3,0}},true,0,"door.wgh_big_start","door.wgh_big_stop"),
    SILO_HATCH_LARGE("silo_hatch_large",10,60,new int[]{0,0,3,3,3,3},3,new int[][]{{2,0,1,-3,3,0},{1,0,2,-5,3,0},{0,0,2,-5,3,0},{-1,0,2,-5,3,0},{-2,0,1,-3,3,0}},true,0,"door.wgh_big_start","door.wgh_big_stop"),
    CARGO_DOOR("cargo_door",5,30,new int[]{2,0,0,0,1,1},0,new int[][]{{-1,-1,0,3,3,1}},false,1,"door.garage_move","door.garage_stop"),
    FIRE_DOOR("fire_door",10,600,new int[]{2,0,0,0,2,1},0,new int[][]{{-1,0,0,3,4,1}},false,5,"door.wghStart","door.wghStop"),
    SLIDE_DOOR("sliding_blast_door",10,450,new int[]{3,0,0,0,3,3},0,new int[][]{{-2,0,0,4,5,1}},false,2,"door.sliding_door_opening","door.sliding_door_opened"),
    LARGE_VEHICLE_DOOR("large_vehicle_door",10,600,new int[]{5,0,0,0,3,3},0,new int[][]{{0,0,0,-4,6,2},{0,0,0,4,6,2}},false,4,"door.garage_move","door.garage_stop"),
    SECURE_ACCESS_DOOR("secure_access_door",20,1200,new int[]{4,0,0,0,2,2},0,new int[][]{{-2,1,0,4,5,1}},false,4,"door.garage_move","door.garage_stop"),
    ROUND_AIRLOCK_DOOR("round_airlock_door",10,600,new int[]{3,0,0,0,2,1},0,new int[][]{{0,0,0,-2,4,2},{0,0,0,3,4,2}},false,3,"door.garage_move","door.garage_stop"),
    QE_CONTAINMENT("qe_containment",10,600,new int[]{2,0,0,0,1,1},0,new int[][]{{-1,0,0,3,3,1}},false,3,"door.wgh_start","door.wgh_stop"),
    QE_SLIDING("qe_sliding_door",10,600,new int[]{1,0,0,0,1,0},0,new int[][]{{0,0,0,2,2,2}},false,5,"door.qe_sliding_opening","door.qe_sliding_opened"),
    SLIDING_SEAL_DOOR("sliding_seal_door",10,600,new int[]{1,0,0,0,0,0},0,new int[][]{{0,0,0,1,2,2}},false,3,null,"door.sliding_seal_stop"),
    WATER_DOOR("water_door",5,30,new int[]{2,0,0,0,1,1},0,new int[][]{{1,0,0,-3,3,2}},false,2,"door.wgh_big_start","door.wgh_big_stop"),
    VAULT_DOOR("vault_door",10,600,new int[]{4,0,0,0,2,2},0,new int[][]{{-1,1,0,3,3,2}},false,7,null,null),
    TRANSITION_SEAL("transition_seal",10,600,new int[]{23,0,0,0,13,12},0,new int[][]{{-9,2,0,20,20,1}},false,0,null,null);
    public final String id,loop,end;public final float hardness,resistance;private final int[] dimensions;private final int[][] ranges;public final int offset,skins;public final boolean remoteControllable;
    GenericDoorDeclaration(String id,float hardness,float resistance,int[] dimensions,int offset,int[][] ranges,boolean remote,int skins,String loop,String end){this.id=id;this.hardness=hardness;this.resistance=resistance;this.dimensions=dimensions;this.offset=offset;this.ranges=ranges;remoteControllable=remote;this.skins=skins;this.loop=loop;this.end=end;}
    public int[] dimensions(){return dimensions.clone();}public int[][] ranges(){return ranges;}public int timeToOpen(){return switch(this){case TRANSITION_SEAL->480;case SECURE_ACCESS_DOOR,VAULT_DOOR->120;case QE_CONTAINMENT,FIRE_DOOR->160;case QE_SLIDING->10;case SLIDING_SEAL_DOOR->20;case SLIDE_DOOR->24;default->60;};}
    public boolean family247(){return switch(this){case LARGE_VEHICLE_DOOR,SECURE_ACCESS_DOOR,ROUND_AIRLOCK_DOOR,QE_CONTAINMENT,QE_SLIDING,SLIDING_SEAL_DOOR,WATER_DOOR->true;default->false;};}
    public float soundVolume(){return this==TRANSITION_SEAL?6F:this==VAULT_DOOR?1F:2F;}
    public boolean neverDemandRedstone(){return this==WATER_DOOR;}
    public String startSound(boolean opening){return this==TRANSITION_SEAL?"door.TransitionSealOpen":this==SLIDING_SEAL_DOOR?"door.sliding_seal_open":this==WATER_DOOR&&opening?"door.lever":null;}
    public boolean siloHatch(){return this==SILO_HATCH||this==SILO_HATCH_LARGE;}
    public float hatchPivotZ(){return this==SILO_HATCH_LARGE?-2.875F:-1.875F;}
    public float rangeTime(int ticks,int index){return this==WATER_DOOR?norm(ticks,35,40):siloHatch()?norm(ticks,20,20):norm(ticks,0,timeToOpen());}
    public String secondaryLoop(){return this==SLIDE_DOOR?"door.sliding_door_opening":this==FIRE_DOOR?"door.alarm6":null;}
    public String endSound(boolean opening){return !opening&&this==WATER_DOOR?"door.lever":!opening&&this==QE_SLIDING?"door.qe_sliding_shut":this==SLIDE_DOOR&&!opening?"door.sliding_door_shut":end;}
    // Preserve the source's deliberate floating-point max==min behavior, including its tick20 NaN.
    public static float norm(float ticks,float min,float max){return Mth.clamp((ticks-min)/(max-min),0F,1F);}
    public static float smooth(float t){return t*t*(3F-2F*t);}
    public AABB bounds(int x,int y,int z,boolean open,boolean collision){
        if(this==VAULT_DOOR)return !open||y==0?new AABB(0,0,0,1,1,1):new AABB(0,0,0,0,0,0);
        if(this==SLIDING_SEAL_DOOR)return collision&&open?new AABB(0,0,0,0,0,0):new AABB(0,0,.75,1,1,1);
        if(this==QE_SLIDING){if(collision&&open)return z==0?new AABB(.875,0,.8125,1,1,1):new AABB(0,0,.8125,.125,1,1);return new AABB(0,0,.8125,1,1,1);}
        if(this==QE_CONTAINMENT){if(!open)return new AABB(0,0,.5,1,1,1);if(y>1)return new AABB(0,.25,.5,1,1,1);if(y==0)return new AABB(0,0,.5,1,collision?0:.125,1);}
        if(this==SECURE_ACCESS_DOOR){if(!open)return y>0?new AABB(0,0,.375,1,1,.625):new AABB(0,0,0,1,1,1);if(y==1)return new AABB(0,0,0,1,collision?0:.0625,1);if(y==4)return new AABB(0,.5,.15,1,1,.85);if(y==0)return new AABB(0,0,0,1,1,1);}
        if(this==ROUND_AIRLOCK_DOOR&&open){if(z==1)return new AABB(.4,0,0,1,1,1);if(z==-2)return new AABB(0,0,0,.6,1,1);if(y==3)return new AABB(0,.5,0,1,1,1);if(y==0)return new AABB(0,0,0,1,collision?0:.0625,1);}
        if(this==LARGE_VEHICLE_DOOR&&open){if(z==3)return new AABB(.4,0,0,1,1,1);if(z==-3)return new AABB(0,0,0,.6,1,1);if(y==0)return new AABB(0,0,0,1,collision?0:.0625,1);}
        if(this==WATER_DOOR){if(!open)return new AABB(0,0,.75,1,1,1);if(y>1)return new AABB(0,.85,.75,1,1,1);if(y==0)return new AABB(0,0,.75,1,collision?0:.15,1);}

        if(this==SLIDE_DOOR&&open){if(y==3)return new AABB(0,.5,0,1,1,1);else if(y==0)return new AABB(0,0,0,1,collision?0:.08,1);}
        if(this==FIRE_DOOR&&open){if(z==1)return new AABB(.5,0,0,1,1,1);else if(z==-2)return new AABB(0,0,0,.5,1,1);else if(y>1)return new AABB(0,.75,0,1,1,1);else if(y==0)return new AABB(0,0,0,1,collision?0:.1,1);}
        if(this==CARGO_DOOR){if(!open)return new AABB(0,0,.375,1,1,.625);if(y>1)return new AABB(0,.25,.375,1,1,.625);if(y==0)return new AABB(0,0,.375,1,collision?0:.125,.625);}
        return open?new AABB(0,0,0,0,0,0):new AABB(0,0,0,1,1,1);
    }
}
