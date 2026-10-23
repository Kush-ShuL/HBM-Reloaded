package com.hbm.machine.pneumaticstorage;

/** The five actual X5846 endpoints; pressure belongs only to the two real storage variants. */
public enum PneumaticKind {
    ACCESS("access",0,210,251),CLUTTER("clutter",54,200,235),MONO("mono",3,200,181),IMPORTER("importer",9,176,185),EXPORTER("exporter",18,176,185);
    public final String suffix;public final int slots,width,height;
    PneumaticKind(String suffix,int slots,int width,int height){this.suffix=suffix;this.slots=slots;this.width=width;this.height=height;}
    public String id(){return "pneumatic_storage_"+suffix;}
    public String title(){return "container.pneumoStorage"+switch(this){case ACCESS->"Access";case CLUTTER->"Clutter";case MONO->"Mono";case IMPORTER->"Importer";case EXPORTER->"Exporter";};}
}
