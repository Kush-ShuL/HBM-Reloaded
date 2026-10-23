package com.hbm.railway.cart;
import java.util.Locale;
public enum SourceCartKind {
    EMPTY(1,2,3),CRATE(0),DESTROYER(2,3),POWDER(1,2,3),SEMTEX(1,2,3);
    public final int bases;
    SourceCartKind(int... bases){int mask=0;for(int base:bases)mask|=1<<base;this.bases=mask;}
    public boolean supports(Base base){return (bases&(1<<base.ordinal()))!=0;}
    public String key(){return name().toLowerCase(Locale.ROOT);}
    public static SourceCartKind safe(int value){return value>=0&&value<values().length?values()[value]:EMPTY;}
    public enum Base {VANILLA,WOOD,STEEL,PAINTED;public static Base safe(int value){return value>=0&&value<values().length?values()[value]:VANILLA;}}
}
