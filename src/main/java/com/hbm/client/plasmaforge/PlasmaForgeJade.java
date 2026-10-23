package com.hbm.client.plasmaforge;

import com.hbm.machine.plasmaforge.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Plasma TU and isotope stores remain distinct from the shared private-HE/tank/task view. */
@WailaPlugin
public final class PlasmaForgeJade implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,PlasmaForgeBlockEntity.class);event.registerBlockDataProvider(Provider.INSTANCE,PlasmaForgePort.class);}
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,PlasmaForgeBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:plasma_forge_state");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var machine=accessor.getBlockEntity() instanceof PlasmaForgeBlockEntity core?core:accessor.getBlockEntity() instanceof PlasmaForgePort port?port.core():null;if(machine==null)return;var data=new CompoundTag();data.putLong("plasma",machine.plasmaEnergySync);var recipe=(PlasmaForgeRecipe)machine.module.getRecipe();data.putLong("ignition",recipe==null?0:recipe.ignitionTemp());data.putInt("booster",machine.booster);data.putInt("maxBooster",machine.maxBooster);tag.put("hbm:plasmaForge",data);}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(!tag.contains("hbm:plasmaForge"))return;var data=tag.getCompound("hbm:plasmaForge");com.hbm.compat.jade.HbmJadeBars.add(tooltip,data.getLong("plasma"),data.getLong("ignition"),Component.translatable("jade.hbm.plasmaforge.plasma",com.hbm.item.battery.BatteryItem.shortNumber(data.getLong("plasma")),com.hbm.item.battery.BatteryItem.shortNumber(data.getLong("ignition"))),0xe45aeb);if(data.getInt("maxBooster")>0)com.hbm.compat.jade.HbmJadeBars.add(tooltip,data.getInt("booster"),data.getInt("maxBooster"),Component.translatable("gui.hbm.plasmaforge.booster_remaining",data.getInt("booster"),data.getInt("maxBooster")),0xb3b43d);}
    }
}
