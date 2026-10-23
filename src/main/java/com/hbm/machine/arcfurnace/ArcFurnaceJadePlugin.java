package com.hbm.machine.arcfurnace;
import com.hbm.client.arcfurnace.ArcFurnaceJadeClient;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
/** Molten quanta remain original Mats, not falsely labelled standard-fluid millibuckets. */
@WailaPlugin
public final class ArcFurnaceJadePlugin implements IWailaPlugin {
 public static final ResourceLocation UID=ResourceLocation.parse("hbm:arc_furnace");public static final String DATA="hbm:arc_furnace";
 @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,ArcFurnaceBlockEntity.class);event.registerBlockDataProvider(Data.INSTANCE,ArcFurnacePort.class);}
 @Override public void registerClient(IWailaClientRegistration event){ArcFurnaceJadeClient.register(event);}
 private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public void appendServerData(CompoundTag root,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof ArcFurnaceBlock block))return;var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core==null||!(accessor.getLevel().getBlockEntity(core) instanceof ArcFurnaceBlockEntity arc))return;var tag=new CompoundTag();tag.putBoolean("liquid",arc.liquidMode);var list=new ListTag();for(var stack:arc.liquids){var row=new CompoundTag();row.putInt("material",stack.material.id());row.putInt("amount",stack.amount);list.add(row);}tag.put("molten",list);root.put(DATA,tag);}}
}
