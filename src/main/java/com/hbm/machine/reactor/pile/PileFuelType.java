package com.hbm.machine.reactor.pile;

import java.util.Locale;

/** Exact ItemPileRodMK2.EnumPileRod, independent of the older deprecated rods and research plates. */
public enum PileFuelType {
    RA226BE(1D),PO210BE(1D),ZR(0,0,0,2),NU(1,25000,.25,4),PU239(1,500,.5,5),RGP(1,1000,.5,6),WASTE(1,0,1.5,6),THORIUM(1,35000,.25,8),THORIUM_FUEL(1,2000,.5,6);
    public final double reactionMult,life,heatMult,neutronSource;public final int turnsInto;
    PileFuelType(double source){reactionMult=0;life=0;heatMult=0;neutronSource=source;turnsInto=0;}
    PileFuelType(double reaction,double life,double heat,int turnsInto){reactionMult=reaction;this.life=life;heatMult=heat;neutronSource=0;this.turnsInto=turnsInto;}
    public String serializedName(){return name().toLowerCase(Locale.US);}
    public static PileFuelType from(int metadata){return values()[Math.abs(metadata%values().length)];}
}
