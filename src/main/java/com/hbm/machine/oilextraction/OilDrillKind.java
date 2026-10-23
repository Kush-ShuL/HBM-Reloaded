package com.hbm.machine.oilextraction;
public enum OilDrillKind {
 WELL("machine_well","derrick","container.oilWell",100000,100,50,500,100,500,.05),PUMPJACK("machine_pumpjack","pumpjack","container.pumpjack",250000,200,25,750,50,250,.025);
 public final String id,config,title;public final int power,consumption,delay,oil,gasMin,gasMax;public final double drain;
 OilDrillKind(String id,String config,String title,int power,int consumption,int delay,int oil,int gasMin,int gasMax,double drain){this.id=id;this.config=config;this.title=title;this.power=power;this.consumption=consumption;this.delay=delay;this.oil=oil;this.gasMin=gasMin;this.gasMax=gasMax;this.drain=drain;}
}
