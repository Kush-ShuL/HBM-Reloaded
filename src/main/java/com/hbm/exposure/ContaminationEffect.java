package com.hbm.exposure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/** Original HbmLivingProps.ContaminationEffect; each contact remains an independent decaying dose. */
public final class ContaminationEffect {
    public float maxRad;
    public int maxTime;
    public int time;
    public boolean ignoreArmor;
    public ContaminationEffect(float maxRad,int duration,boolean ignoreArmor) {
        if(!Float.isFinite(maxRad)||duration<=0)throw new IllegalArgumentException("Finite radiation and positive contamination duration required");
        this.maxRad=maxRad;this.maxTime=this.time=duration;this.ignoreArmor=ignoreArmor;
    }
    public float getRad() {return maxRad*((float)time/(float)maxTime);}
    public void write(FriendlyByteBuf buffer) {
        buffer.writeFloat(maxRad);buffer.writeInt(maxTime);buffer.writeInt(time);buffer.writeBoolean(ignoreArmor);
    }
    public static ContaminationEffect read(FriendlyByteBuf buffer) {
        float maxRad=buffer.readFloat();int maxTime=buffer.readInt();int time=buffer.readInt();boolean ignoreArmor=buffer.readBoolean();
        var effect=new ContaminationEffect(maxRad,maxTime,ignoreArmor);effect.time=time;return effect;
    }
    public void save(CompoundTag tag,int index) {
        var child=new CompoundTag();child.putFloat("maxRad",maxRad);child.putInt("maxTime",maxTime);child.putInt("time",time);child.putBoolean("ignoreArmor",ignoreArmor);tag.put("cont_"+index,child);
    }
    public static ContaminationEffect load(CompoundTag tag,int index) {
        var child=tag.getCompound("cont_"+index);float maxRad=child.getFloat("maxRad");int maxTime=child.getInt("maxTime"),time=child.getInt("time");
        // Fix X5846's erroneous parent-tag reads: valid saves must retain all four child fields.
        if(!Float.isFinite(maxRad)||maxTime<=0||time<=0)return null;
        var effect=new ContaminationEffect(maxRad,maxTime,child.getBoolean("ignoreArmor"));effect.time=time;return effect;
    }
}
