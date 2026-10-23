package com.hbm.block.pylon;

/** Original registration order, wire family and mounting range. */
public enum PylonKind {
    CONNECTOR("red_connector", 1, 10), SUPER_CONNECTOR("red_connector_super", 1, 100),
    WOOD("red_pylon", 1, 25), STEEL("red_pylon_steel", 1, 25),
    MEDIUM_WOOD("red_pylon_medium_wood", 3, 45), MEDIUM_WOOD_TRANSFORMER("red_pylon_medium_wood_transformer", 3, 45),
    MEDIUM_STEEL("red_pylon_medium_steel", 3, 45), MEDIUM_STEEL_TRANSFORMER("red_pylon_medium_steel_transformer", 3, 45),
    LARGE("red_pylon_large", 4, 100), SUBSTATION("substation", 4, 20);
    public final String id;
    public final int wires;
    public final double range;
    PylonKind(String id, int wires, double range) { this.id=id; this.wires=wires; this.range=range; }
    public boolean connector() { return this==CONNECTOR || this==SUPER_CONNECTOR; }
    public boolean medium() { return wires==3; }
    public boolean transformer() { return this==MEDIUM_WOOD_TRANSFORMER || this==MEDIUM_STEEL_TRANSFORMER; }
    public boolean steel() { return this==STEEL || this==MEDIUM_STEEL || this==MEDIUM_STEEL_TRANSFORMER; }
    public int[] dimensions() { return this==LARGE?new int[]{13,0,1,1,1,1}:this==SUBSTATION?new int[]{4,0,1,1,2,2}:medium()?new int[]{6,0,0,0,0,0}:new int[]{4,0,0,0,0,0}; }
}
