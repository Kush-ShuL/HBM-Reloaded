package com.hbm.client.weapontable;
import com.hbm.machine.weapontable.WeaponTableBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class WeaponTableJadePlugin implements IWailaPlugin {
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,WeaponTableBlock.class);}
    private enum Provider implements IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:weapon_table");}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){tooltip.add(Component.translatable("gui.hbm.weapon_table.slots",7));}
    }
}
