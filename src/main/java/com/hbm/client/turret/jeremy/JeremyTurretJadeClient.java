package com.hbm.client.turret.jeremy;
import com.hbm.machine.turret.jeremy.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class JeremyTurretJadeClient {
    public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,JeremyTurretBlock.class);}
    private enum Data implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return JeremyTurretJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(JeremyTurretJadePlugin.DATA);if(tag.isEmpty())return;tooltip.add(Component.translatable("jade.hbm.turret.active",Component.translatable(tag.getBoolean("active")?"turret.on":"turret.off")));tooltip.add(Component.translatable("jade.hbm.turret.tally",tag.getInt("kills")));if(tag.contains("target")){var name=Component.Serializer.fromJson(tag.getString("target"),accessor.getLevel().registryAccess());if(name!=null)tooltip.add(Component.translatable("jade.hbm.turret.target",name));}}}
    private JeremyTurretJadeClient(){}
}
