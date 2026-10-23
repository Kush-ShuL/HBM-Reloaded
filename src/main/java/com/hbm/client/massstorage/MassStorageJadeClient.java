package com.hbm.client.massstorage;

import com.hbm.machine.massstorage.*;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public final class MassStorageJadeClient {
    public static void register(IWailaClientRegistration event){event.registerBlockComponent(Data.INSTANCE,MassStorageBlock.class);}
    private enum Data implements IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return MassStorageJadePlugin.UID;}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData().getCompound(MassStorageJadePlugin.DATA);if(tag.isEmpty())return;var filter=ItemStack.parseOptional(accessor.getLevel().registryAccess(),tag.getCompound("filter"));tooltip.add(filter.isEmpty()?Component.translatable("hud.hbm.mass_storage.empty"):filter.getHoverName());tooltip.add(Component.literal(String.format(Locale.US,"%,d / %,d",tag.getInt("stack"),tag.getInt("capacity"))));tooltip.add(Component.translatable(tag.getBoolean("output")?"jade.hbm.mass_storage.output_enabled":"jade.hbm.mass_storage.output_disabled"));}
    }
    private MassStorageJadeClient(){}
}
