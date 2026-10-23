package com.hbm.exposure;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/** Shared living-entity dose, retaining the original persisted field names. */
public final class ExposureData implements INBTSerializable<CompoundTag> {
    public static final int MAX_ASBESTOS = 60 * 60 * 20;
    public static final int MAX_BLACK_LUNG = 2 * 60 * 60 * 20;
    float radiation;
    float digamma;
    int asbestos;
    int blackLung;
    final java.util.List<ContaminationEffect> contamination=new java.util.ArrayList<>();
    float radEnvironment;
    float radBuffer;
    public float radiation() { return radiation; }
    public float digamma() { return digamma; }
    public int asbestos() { return asbestos; }
    public int blackLung() { return blackLung; }
    public java.util.List<ContaminationEffect> contaminationEffects() { return contamination; }
    public float radEnvironment() { return radEnvironment; }
    public float radBuffer() { return radBuffer; }
    @Override public CompoundTag serializeNBT(HolderLookup.Provider lookup) {
        var tag = new CompoundTag();
        tag.putFloat("hfr_radiation",radiation);
        tag.putFloat("hfr_digamma",digamma);
        tag.putInt("hfr_asbestos",asbestos);
        tag.putInt("hfr_blacklung",blackLung);
        tag.putInt("hfr_cont_count",contamination.size());
        for(int i=0;i<contamination.size();i++)contamination.get(i).save(tag,i);
        return tag;
    }
    @Override public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag tag) {
        radiation = Math.max(0,Math.min(2500,tag.getFloat("hfr_radiation")));
        if (!Float.isFinite(radiation)) radiation=0;
        digamma = Math.max(0,Math.min(10,tag.getFloat("hfr_digamma")));
        if (!Float.isFinite(digamma)) digamma=0;
        asbestos = Math.max(0,tag.getInt("hfr_asbestos"));
        blackLung = Math.max(0,tag.getInt("hfr_blacklung"));
        contamination.clear();
        for(int i=0;i<tag.getInt("hfr_cont_count");i++) {
            var effect=ContaminationEffect.load(tag,i);if(effect!=null)contamination.add(effect);
        }
        // radEnv/radBuf were transient in HbmLivingProps.
    }
}
