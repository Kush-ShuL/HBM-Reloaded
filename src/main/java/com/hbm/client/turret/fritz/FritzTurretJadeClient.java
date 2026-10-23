package com.hbm.client.turret.fritz;
import com.hbm.machine.turret.fritz.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class FritzTurretJadeClient {
    public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,FritzTurretBlock.class);}
    private enum Data implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return FritzTurretJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(FritzTurretJadePlugin.DATA);if(tag.isEmpty())return;tooltip.add(Component.translatable("jade.hbm.turret.active",Component.translatable(tag.getBoolean("active")?"turret.on":"turret.off")));tooltip.add(Component.translatable("jade.hbm.turret.tally",tag.getInt("kills")));if(tag.contains("target")){var name=Component.Serializer.fromJson(tag.getString("target"),accessor.getLevel().registryAccess());if(name!=null)tooltip.add(Component.translatable("jade.hbm.turret.target",name));}}}
    private FritzTurretJadeClient(){}
}
