package com.hbm.item.redroom;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
/** Original HbmPlayerProps shield/maxShield saved and cloned, lastDamage transient. */
public final class ShieldData implements INBTSerializable<CompoundTag> {
 public float shield,maxShield;public int lastDamage;
 @Override public CompoundTag serializeNBT(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putFloat("shield",shield);tag.putFloat("maxShield",maxShield);return tag;}
 @Override public void deserializeNBT(HolderLookup.Provider lookup,CompoundTag tag){shield=tag.getFloat("shield");maxShield=tag.getFloat("maxShield");lastDamage=0;}
}
