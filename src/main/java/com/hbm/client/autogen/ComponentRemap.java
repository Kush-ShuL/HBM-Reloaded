package com.hbm.client.autogen;

/** Exact RGBMutatorInterpolatedComponentRemap math: double interpolation, Java int truncation, byte wrap. */
public final class ComponentRemap {
    private ComponentRemap() {}
    public static int remapArgb(int pixel,int light,int dark) {
        int result=pixel&0xff000000;
        for(int shift:new int[]{16,8,0}) {
            int component=pixel>>>shift&255, lighter=light>>>shift&255, darker=dark>>>shift&255;
            double position=(component-255D)/(80D-255D);
            int value=(int)(lighter+position*(darker-lighter));
            result|=(value&255)<<shift;
        }
        return result;
    }
    public static int remapAbgr(int pixel,int light,int dark) {
        int argb=(pixel&0xff00ff00)|(pixel&255)<<16|(pixel>>>16&255);
        int remapped=remapArgb(argb,light,dark);
        return (remapped&0xff00ff00)|(remapped&255)<<16|(remapped>>>16&255);
    }
}
