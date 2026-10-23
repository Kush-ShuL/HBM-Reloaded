package com.hbm.machine.generator.stirling;
public enum StirlingKind {NORMAL("machine_stirling",0),STEEL("machine_stirling_steel",1),CREATIVE("machine_stirling_creative",2);public final String id;public final int gear;StirlingKind(String id,int gear){this.id=id;this.gear=gear;}public int maxHeat(){return this==NORMAL?300:1500;}}
