package com.hbm.client.coker;
import com.hbm.machine.coker.*;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin public final class CokerJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration r){r.registerBlockDataProvider(Provider.INSTANCE,CokerEntity.class);r.registerBlockDataProvider(Provider.INSTANCE,CokerPort.class);}
 @Override public void registerClient(IWailaClientRegistration r){r.registerBlockComponent(Provider.INSTANCE,CokerBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:coker_heat");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor a){var be=a.getBlockEntity();if(be instanceof CokerPort p)be=p.core();if(be instanceof CokerEntity m)tag.putInt("hbm:coker_heat",m.heat);}
  @Override public void appendTooltip(ITooltip tip,BlockAccessor a,IPluginConfig c){if(a.getServerData().contains("hbm:coker_heat"))tip.add(Component.literal(String.format(Locale.US,"%,d TU",a.getServerData().getInt("hbm:coker_heat"))));}
 }
}
