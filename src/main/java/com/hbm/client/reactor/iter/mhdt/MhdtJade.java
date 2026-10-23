package com.hbm.client.reactor.iter.mhdt;
import com.hbm.machine.reactor.iter.mhdt.*;
import java.math.BigInteger;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Source look overlay: receivedTU/threshold, nominalHErate, lowpower/cooling warnings; realprivatefluids remain MachineView. */
@WailaPlugin
public final class MhdtJade implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,MhdtBlock.class);}@Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,MhdtBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:fusion_mhdt_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var block=(MhdtBlock)accessor.getBlockState().getBlock();var pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos!=null&&accessor.getLevel().getBlockEntity(pos) instanceof MhdtEntity machine){tag.putLong("hbmMHDHeat",machine.plasmaEnergySync);tag.putLong("hbmMHDMinimum",machine.minimumPlasma);tag.putBoolean("hbmMHDCooled",machine.isCool());}}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(!tag.contains("hbmMHDHeat"))return;long heat=tag.getLong("hbmMHDHeat"),min=tag.getLong("hbmMHDMinimum");boolean full=heat>=min,cool=tag.getBoolean("hbmMHDCooled");tooltip.add(Component.translatable("jade.hbm.fusion_mhdt.heat",heat,min).withStyle(full?ChatFormatting.GREEN:ChatFormatting.GOLD));tooltip.add(Component.translatable("jade.hbm.fusion_mhdt.power",cool?MhdtEntity.generation(heat,full).toString():"0"));if(heat>0&&!full)tooltip.add(Component.translatable("jade.hbm.fusion_mhdt.low_power").withStyle(ChatFormatting.GOLD));if(!cool)tooltip.add(Component.translatable("jade.hbm.fusion_mhdt.no_cooling").withStyle(ChatFormatting.RED));}}
}
