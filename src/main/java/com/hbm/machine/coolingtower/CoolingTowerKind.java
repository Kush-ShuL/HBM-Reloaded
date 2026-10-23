package com.hbm.machine.coolingtower;
public enum CoolingTowerKind {
 CONDENSER("machine_condenser","condenser",100),SMALL("machine_tower_small","condenserTowerSmall",1000),LARGE("machine_tower_large","condenserTowerLarge",10000);
 public final String id,config;public final int capacity;CoolingTowerKind(String id,String config,int capacity){this.id=id;this.config=config;this.capacity=capacity;}
 public int radius(){return this==SMALL?2:4;}
}
