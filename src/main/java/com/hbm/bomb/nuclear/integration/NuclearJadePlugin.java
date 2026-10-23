package com.hbm.bomb.nuclear.integration;

import com.hbm.bomb.nuclear.NuclearBombBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Authoritative bomb assembly readiness; discovered only by the optional Jade integration. */
@WailaPlugin
public final class NuclearJadePlugin implements IWailaPlugin {
 public static final ResourceLocation UID=ResourceLocation.fromNamespaceAndPath("hbm","nuclear_assembly");
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(new Data(),NuclearBombBlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registration){com.hbm.client.bomb.nuclear.NuclearJadeClient.register(registration);}
 private static final class Data implements IServerDataProvider<BlockAccessor>{
  @Override public ResourceLocation getUid(){return UID;}
  @Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockEntity() instanceof NuclearBombBlockEntity;}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof NuclearBombBlockEntity bomb){var state=new CompoundTag();state.putBoolean("ready",bomb.assembly().isReady()||bomb.assembly().isFilled());tag.put("hbm:nuclear_assembly",state);}}
 }
}
