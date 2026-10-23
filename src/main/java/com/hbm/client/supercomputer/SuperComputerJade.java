package com.hbm.client.supercomputer;
import com.hbm.machine.supercomputer.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Native realprocessorprogress; commonMachineViewalreadyexposesexactHE/privatefluidtanks. */
@WailaPlugin
public final class SuperComputerJade implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,SuperComputerBlock.class);}@Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,SuperComputerBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:supercomputer_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var block=(SuperComputerBlock)accessor.getBlockState().getBlock();var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof SuperComputerEntity computer){tag.putDouble("hbmComputerProgress",computer.computerModule.progress);tag.putBoolean("hbmComputerActive",computer.didProcess);}}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbmComputerProgress"))tooltip.add(Component.translatable("jade.hbm.supercomputer.progress",(int)Math.round(tag.getDouble("hbmComputerProgress")*100)));}}
}
