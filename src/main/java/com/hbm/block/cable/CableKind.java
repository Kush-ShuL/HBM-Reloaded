package com.hbm.block.cable;

/** X5846's directly adjacent HE conductors; overhead pylons are a separate system. */
public enum CableKind {
    COATED("red_wire_coated"), NEO("red_cable"), CLASSIC("red_cable_classic"),
    PAINTABLE("red_cable_paintable"), GAUGE("red_cable_gauge"), BOX("red_cable_box"),
    SWITCH("cable_switch"), DETECTOR("cable_detector"), DIODE("cable_diode");

    private final String id;
    CableKind(String id) { this.id = id; }
    public String id() { return id; }
    public boolean switched() { return this == SWITCH || this == DETECTOR; }
    public boolean shaped() { return this == NEO || this == CLASSIC || this == BOX; }
}
