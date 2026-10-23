package com.hbm.block.light;
public enum SpotlightKind {
 INCANDESCENT("incandescent","cage_lamp","CageLamp",2),FLUORESCENT("fluoro","fluorescent_lamp","FluoroSingle",8),HALOGEN("halogen","flood_lamp","FloodLamp",32);
 public final String suffix,texture,part;public final int beamLength;
 SpotlightKind(String suffix,String texture,String part,int distance){this.suffix=suffix;this.texture=texture;this.part=part;beamLength=distance;}
 public String id(boolean on){return "spotlight_"+suffix+(on?"":"_off");}
 public String part(int connections){return this==FLUORESCENT?connections==0?"FluoroSingle":connections==1?"FluoroCap":"FluoroMid":part;}
}
