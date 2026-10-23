package com.hbm.machine.turret.howard;
public enum HowardTurretKind { HOWARD("turret_howard","container.turretHoward",false),DAMAGED("turret_howard_damaged","container.turretHoward",true);public final String id,title;public final boolean damaged;HowardTurretKind(String id,String title,boolean damaged){this.id=id;this.title=title;this.damaged=damaged;}}
