package com.hbm.client.turret;
import com.hbm.machine.turret.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class TurretJadeClient {
    public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,SentryBlock.class);}
    private enum Data implements IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return TurretJadePlugin.UID;}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(TurretJadePlugin.DATA);if(tag.isEmpty())return;tooltip.add(Component.translatable("jade.hbm.turret.active",Component.translatable(tag.getBoolean("active")?"turret.on":"turret.off")));if(tag.getBoolean("damaged"))tooltip.add(Component.translatable("jade.hbm.turret.autonomous"));else tooltip.add(Component.translatable("jade.hbm.turret.ammo",tag.getInt("ammo")));tooltip.add(Component.translatable("jade.hbm.turret.tally",tag.getInt("kills")));if(tag.contains("target")){var target=Component.Serializer.fromJson(tag.getString("target"),accessor.getLevel().registryAccess());if(target!=null)tooltip.add(Component.translatable("jade.hbm.turret.target",target));}}
    }
    private TurretJadeClient(){}
}
