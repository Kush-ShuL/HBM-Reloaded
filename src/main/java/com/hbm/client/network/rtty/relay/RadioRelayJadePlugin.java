package com.hbm.client.network.rtty.relay;

import com.hbm.network.rtty.relay.RadioRelayBlock;
import com.hbm.network.rtty.relay.RadioRelayEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public final class RadioRelayJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registry) { registry.registerBlockDataProvider(Provider.INSTANCE, RadioRelayEntity.class); }
    @Override public void registerClient(IWailaClientRegistration registry) { registry.registerBlockComponent(Provider.INSTANCE, RadioRelayBlock.class); }

    private enum Provider implements IServerDataProvider<BlockAccessor>, IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return ResourceLocation.parse("hbm:radio_relay_status"); }

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof RadioRelayEntity relay) {
                var data = new CompoundTag();
                data.putString("channel", relay.channel);
                data.putDouble("signal", relay.lastState);
                tag.put("hbm:radio_relay", data);
            }
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!accessor.getServerData().contains("hbm:radio_relay")) return;
            var data = accessor.getServerData().getCompound("hbm:radio_relay");
            if (!data.getString("channel").isEmpty()) tooltip.add(Component.translatable("jade.hbm.rtty.frequency", data.getString("channel")).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("jade.hbm.rtty.signal", data.getDouble("signal")).withStyle(ChatFormatting.RED));
        }
    }
}
