package com.hbm.railway.track;
/** Actual eleven hidden source registrations, not the four RailGeneric cart rails. */
public enum SourceRailKind {
NARROW_STRAIGHT("rail_narrow_straight","rail_narrow","rail_narrow_neo",new int[]{0,0,2,2,0,0},2,0),
NARROW_CURVE("rail_narrow_curve","rail_narrow_bend","rail_narrow_neo",new int[]{0,0,4,0,4,0},0,4),
STRAIGHT("rail_large_straight","rail_standard","rail_standard_straight",new int[]{0,0,2,2,1,0},2,0),
SHORT("rail_large_straight_short","rail_standard_short","rail_standard_straight",new int[]{0,0,0,0,1,0},0,0),
CURVE("rail_large_curve","rail_standard_bend","rail_standard_straight",new int[]{0,0,4,0,4,0},0,4),
CURVE7("rail_large_curve_7","rail_standard_bend_wide","rail_standard_straight",new int[]{0,0,6,0,6,0},0,6),
CURVE9("rail_large_curve_9","rail_standard_bend_wide9","rail_standard_straight",new int[]{0,0,8,0,8,0},0,8),
RAMP("rail_large_ramp","rail_standard_ramp","rail_standard_straight",new int[]{0,0,2,2,1,0},2,0),
BUFFER("rail_large_buffer","rail_standard_buffer","rail_standard_buffer",new int[]{0,0,2,2,1,0},2,0),
SWITCH("rail_large_switch","rail_standard_switch","rail_standard_straight",new int[]{0,0,7,7,1,0},7,0),
SWITCH_FLIPPED("rail_large_switch_flipped","rail_standard_switch_flipped","rail_standard_straight",new int[]{0,0,7,7,1,0},7,0);
public final String id,model,texture;public final int[] dimensions;public final int offset,width;
SourceRailKind(String id,String model,String texture,int[] dimensions,int offset,int width){this.id=id;this.model=model;this.texture=texture;this.dimensions=dimensions;this.offset=offset;this.width=width;}
public boolean isSwitch(){return this==SWITCH||this==SWITCH_FLIPPED;}
public IRailNTM.TrackGauge gauge(){return this==NARROW_STRAIGHT||this==NARROW_CURVE?IRailNTM.TrackGauge.NARROW:IRailNTM.TrackGauge.STANDARD;}
}
