package com.hbm.bomb.nuclear.programmable;

/** Original post-gating seven float fields and source adjusted-yield functions. */
public record CustomBombPayload(float tnt,float nuke,float hydro,float amat,float dirty,float schrab,float euph) {
 public static final CustomBombPayload ZERO=new CustomBombPayload(0,0,0,0,0,0,0);
 public static final int MAX_TNT=150,MAX_NUKE=200,MAX_HYDRO=350,MAX_AMAT=350,MAX_SCHRAB=250;
 public float nukeAdjusted(){return nuke==0?0:Math.min(nuke+tnt/2,MAX_NUKE);}
 public float hydroAdjusted(){return hydro==0?0:Math.min(hydro+nuke/2+tnt/4,MAX_HYDRO);}
 public float amatAdjusted(){return amat==0?0:Math.min(amat+hydro/2+nuke/4+tnt/8,MAX_AMAT);}
 public float schrabAdjusted(){return schrab==0?0:Math.min(schrab+amat/2+hydro/4+nuke/8+tnt/16,MAX_SCHRAB);}
 public void save(net.minecraft.nbt.CompoundTag tag){tag.putFloat("tnt",tnt);tag.putFloat("nuke",nuke);tag.putFloat("hydro",hydro);tag.putFloat("amat",amat);tag.putFloat("dirty",dirty);tag.putFloat("schrab",schrab);tag.putFloat("euph",euph);}
 public static CustomBombPayload load(net.minecraft.nbt.CompoundTag tag){return new CustomBombPayload(tag.getFloat("tnt"),tag.getFloat("nuke"),tag.getFloat("hydro"),tag.getFloat("amat"),tag.getFloat("dirty"),tag.getFloat("schrab"),tag.getFloat("euph"));}
}
