package com.hbm.entity.mob.glyphid;
/** Source getStats() always returns NT; the unused source70K numeric bundles are retained separately. */
public final class GlyphidStats {
    public record StatBundle(double health,double speed,double damage,float divisor,float damageThreshold,float thresholdMultForArmor,float resistanceMult){}
    private final StatBundle[] bundles;
    private GlyphidStats(StatBundle... rows){bundles=rows;}
    public static final GlyphidStats GLYPHID_STATS_NT=new GlyphidStats(
        new StatBundle(20,1,2,.25F,0,1,.1F),new StatBundle(15,1,2,.25F,0,1,.1F),new StatBundle(35,1,10,.5F,.5F,2,.15F),new StatBundle(50,1,10,.5F,.5F,3,.20F),new StatBundle(35,1,10,.5F,.5F,2,.15F),new StatBundle(125,.8,25,1.5F,2,5,.35F),new StatBundle(250,1.2,50,2.5F,5,10,.5F),new StatBundle(100,.8,50,2.5F,5,10,.5F),new StatBundle(20,1.5,5,.5F,0,.5F,.5F));
    public static final GlyphidStats GLYPHID_STATS_70K=new GlyphidStats(
        old(30,1,5,1,.5F),old(20,1,5,1,.5F),old(50,1,10,3,1),old(50,1,5,1,.5F),old(50,1,10,2,1),old(130,.8,25,4,2.5F),old(250,1.2,50,5,10),old(100,.8,50,5,10),old(20,1.5,2,1,.5F));
    private static StatBundle old(double health,double speed,double damage,float divisor,float threshold){return new StatBundle(health,speed,damage,divisor,threshold,0,0);}
    public static GlyphidStats getStats(){return GLYPHID_STATS_NT;}
    public StatBundle getGrunt(){return bundles[0];}public StatBundle getBombardier(){return bundles[1];}public StatBundle getBrawler(){return bundles[2];}public StatBundle getDigger(){return bundles[3];}public StatBundle getBlaster(){return bundles[4];}public StatBundle getBehemoth(){return bundles[5];}public StatBundle getBrenda(){return bundles[6];}public StatBundle getNuclear(){return bundles[7];}public StatBundle getScout(){return bundles[8];}
}
