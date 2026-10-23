package com.hbm.machine.pa.cooled;
public enum PaKind{
    SOURCE("pa_source","container.paSource",5,10000000,1,new int[]{1,1,1,1,4,4}),RFC("pa_rfc","container.paRFC",1,1000000,1,new int[]{1,1,1,1,4,4}),QUADRUPOLE("pa_quadrupole","container.paQuadrupole",2,2500000,1,new int[]{1,1,1,1,1,1}),DIPOLE("pa_dipole","container.paDipole",2,2500000,1,new int[]{1,1,1,1,1,1}),DETECTOR("pa_detector","container.paDetector",5,1000000,2,new int[]{2,2,2,2,4,4});
    public final String id,title;public final int slots,height;public final long capacity;private final int[] dimensions;PaKind(String id,String title,int slots,long capacity,int height,int[] dimensions){this.id=id;this.title=title;this.slots=slots;this.capacity=capacity;this.height=height;this.dimensions=dimensions;}public int[] dimensions(){return dimensions.clone();}
}
