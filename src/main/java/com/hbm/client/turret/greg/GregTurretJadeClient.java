package com.hbm.client.turret.greg;
import com.hbm.machine.turret.greg.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class GregTurretJadeClient {
    public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,GregTurretBlock.class);}
    private enum Data implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return GregTurretJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(GregTurretJadePlugin.DATA);if(tag.isEmpty())return;tooltip.add(Component.translatable("jade.hbm.turret.active",Component.translatable(tag.getBoolean("active")?"turret.on":"turret.off")));tooltip.add(Component.translatable("jade.hbm.turret.tally",tag.getInt("kills")));tooltip.add(Component.translatable("jade.hbm.greg.mode",Component.translatable("turret.arty."+(tag.getShort("mode")==0?"artillery":tag.getShort("mode")==1?"cannon":"manual")).getString().split("\\$",-1)[0]));tooltip.add(Component.translatable("jade.hbm.greg.queued",tag.getInt("queued")));if(tag.contains("target")){var name=Component.Serializer.fromJson(tag.getString("target"),accessor.getLevel().registryAccess());if(name!=null)tooltip.add(Component.translatable("jade.hbm.turret.target",name));}}}
    private GregTurretJadeClient(){}
}
