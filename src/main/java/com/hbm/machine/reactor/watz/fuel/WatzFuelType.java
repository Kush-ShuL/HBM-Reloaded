package com.hbm.machine.reactor.watz.fuel;

import java.util.Locale;

/** Source EnumWatzType order, exact500M yields, base emissions, response functions and half-mud factors. */
public enum WatzFuelType {
    SCHRABIDIUM(0x32ffff,0x005c5c,2000,20,.01,WatzFunction.linear(1.5),WatzFunction.sqrtFalling(10),null),
    HES(0x66dcd6,0x023933,1750,20,.005,WatzFunction.linear(1.25),WatzFunction.sqrtFalling(15),null),
    MES(0xcbeadf,0x28473c,1500,15,.0025,WatzFunction.linear(1.15),WatzFunction.sqrtFalling(15),null),
    LES(0xabb4a8,0x0c1105,1250,15,.00125,WatzFunction.linear(1),WatzFunction.sqrtFalling(20),null),
    HEN(0xa6b2a6,0x030f03,0,10,.0005,WatzFunction.sqrt(100),WatzFunction.sqrtFalling(10),null),
    MEU(0xc1c7bd,0x2b3227,0,10,.0005,WatzFunction.sqrt(75),WatzFunction.sqrtFalling(10),null),
    MEP(0x9aa3a0,0x111a17,0,15,.0005,WatzFunction.sqrt(150),WatzFunction.sqrtFalling(10),null),
    LEAD(0xa6a6b2,0x03030f,0,0,.0025,null,null,WatzFunction.sqrt(10)),
    BORON(0xbdc8d2,0x29343e,0,0,.0025,null,null,WatzFunction.linear(10)),
    DU(0xc1c7bd,0x2b3227,0,0,.0025,null,null,WatzFunction.quadratic(1,1).withDiv(100)),
    NQD(0x4b4b4b,0x121212,2000,20,.01,WatzFunction.linear(2),WatzFunction.sqrt(1D/25).withOff(25D*25),null),
    NQR(0x2d2d2d,0x0b0b0b,2500,30,.01,WatzFunction.linear(1.5),WatzFunction.sqrt(1D/25).withOff(25D*25),null);
    public final double yield=500000000;public final int colorLight,colorDark;public final double passive,heatEmission,mudContent;public final WatzFunction burnFunc,heatDiv,absorbFunc;
    WatzFuelType(int light,int dark,double passive,double heat,double mud,WatzFunction burn,WatzFunction thermal,WatzFunction absorb){colorLight=light;colorDark=dark;this.passive=passive;heatEmission=heat;mudContent=mud/2;burnFunc=burn;heatDiv=thermal;absorbFunc=absorb;}
    public String serialized(){return name().toLowerCase(Locale.US);}
}
