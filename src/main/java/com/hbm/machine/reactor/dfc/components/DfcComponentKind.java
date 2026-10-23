package com.hbm.machine.reactor.dfc.components;
public enum DfcComponentKind {
 EMITTER("dfc_emitter","container.dfcEmitter",0,1_000_000_000L,250),
 INJECTOR("dfc_injector","container.dfcInjector",4,0,250),
 RECEIVER("dfc_receiver","container.dfcReceiver",0,0,50),
 STABILIZER("dfc_stabilizer","container.dfcStabilizer",1,2_500_000_000L,250);
 public final String id,title;public final int slots,range;public final long capacity;
 DfcComponentKind(String id,String title,int slots,long capacity,int range){this.id=id;this.title=title;this.slots=slots;this.capacity=capacity;this.range=range;}
}
