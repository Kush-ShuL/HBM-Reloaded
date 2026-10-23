package com.hbm.client.turret.himars;
import com.hbm.machine.turret.himars.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class HimarsTurretJadeClient {
    public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,HimarsTurretBlock.class);}
    private enum Data implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return HimarsTurretJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(HimarsTurretJadePlugin.DATA);if(tag.isEmpty())return;tooltip.add(Component.translatable("jade.hbm.turret.active",Component.translatable(tag.getBoolean("active")?"turret.on":"turret.off")));tooltip.add(Component.translatable("jade.hbm.turret.tally",tag.getInt("kills")));tooltip.add(Component.translatable("jade.hbm.himars.mode",Component.translatable("turret.arty."+(tag.getShort("mode")==0?"artillery_rocket":"manual_rocket")).getString().split("\\$",-1)[0]));tooltip.add(Component.translatable("jade.hbm.himars.queued",tag.getInt("queued")));tooltip.add(Component.translatable("jade.hbm.himars.ammo",tag.getInt("ammo")));if(tag.contains("target")){var name=Component.Serializer.fromJson(tag.getString("target"),accessor.getLevel().registryAccess());if(name!=null)tooltip.add(Component.translatable("jade.hbm.turret.target",name));}}}
    private HimarsTurretJadeClient(){}
}
