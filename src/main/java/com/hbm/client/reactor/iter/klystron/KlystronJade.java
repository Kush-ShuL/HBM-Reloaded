package com.hbm.client.reactor.iter.klystron;
import com.hbm.machine.reactor.iter.klystron.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class KlystronJade implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,KlystronBlock.class);}@Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,KlystronBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:klystron_output");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var block=(KlystronBlock)accessor.getBlockState().getBlock();var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof KlystronEntity machine){tag.putLong("hbmKyU",machine.output);tag.putLong("hbmKyUTarget",machine.outputTarget);}}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbmKyU"))tooltip.add(Component.translatable("jade.hbm.klystron.output",tag.getLong("hbmKyU"),tag.getLong("hbmKyUTarget")));}}
}
