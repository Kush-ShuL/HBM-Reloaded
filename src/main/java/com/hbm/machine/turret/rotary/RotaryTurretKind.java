package com.hbm.machine.turret.rotary;
public enum RotaryTurretKind {
    CHEKHOV("turret_chekhov","container.turretChekhov",2),FRIENDLY("turret_friendly","container.turretFriendly",5);
    public final String id,title;public final int delay;
    RotaryTurretKind(String id,String title,int delay){this.id=id;this.title=title;this.delay=delay;}
}
