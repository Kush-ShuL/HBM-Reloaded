package com.hbm.client.arcfurnace;
import com.hbm.machine.arcfurnace.*;
import com.hbm.material.*;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class ArcFurnaceJadeClient {
 public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,ArcFurnaceBlock.class);}
 private enum Data implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ArcFurnaceJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(ArcFurnaceJadePlugin.DATA);if(tag.isEmpty())return;tooltip.add(Component.translatable(tag.getBoolean("liquid")?"jade.hbm.arc_furnace.liquid":"jade.hbm.arc_furnace.solid"));for(var value:tag.getList("molten",Tag.TAG_COMPOUND)){var row=(net.minecraft.nbt.CompoundTag)value;var material=HbmMaterials.byId(row.getInt("material"));if(material!=null)tooltip.add(material.displayName().copy().append(": ").append(HbmMaterials.formatAmount(row.getInt("amount"),false)));}}}
 private ArcFurnaceJadeClient(){}
}
