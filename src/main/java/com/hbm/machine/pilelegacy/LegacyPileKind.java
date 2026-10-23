package com.hbm.machine.pilelegacy;

/** Eight real legacy channel registrations; the solid graphite moderator is already ported separately. */
public enum LegacyPileKind {
    DRILLED("drilled",null),FUEL("fuel","uranium"),PLUTONIUM("plutonium","plutonium"),ROD("rod","boron"),
    SOURCE("source","source"),LITHIUM("lithium","lithium"),TRITIUM("tritium",null),DETECTOR("detector","detector");
    public final String suffix,rod;
    LegacyPileKind(String suffix,String rod){this.suffix=suffix;this.rod=rod;}
    public String id(){return "block_graphite_"+suffix;}
    public boolean hasEntity(){return this==FUEL||this==PLUTONIUM||this==SOURCE||this==LITHIUM||this==DETECTOR;}
    public boolean isStreamBase(){return this==FUEL||this==PLUTONIUM||this==SOURCE||this==LITHIUM;}
}
