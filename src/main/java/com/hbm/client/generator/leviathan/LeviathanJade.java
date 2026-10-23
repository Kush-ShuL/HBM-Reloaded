package com.hbm.client.generator.leviathan;
import com.hbm.fluid.*;
import com.hbm.machine.generator.leviathan.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Source MachineChungus look overlay uses realcoreinput/outputfluidandgeneratedHE, withnativeJadeoptionlocalization. */
@WailaPlugin
public final class LeviathanJade implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,LeviathanBlock.class);}@Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,LeviathanBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:leviathan_status");}
  @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){var block=(LeviathanBlock)accessor.getBlockState().getBlock();var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof LeviathanEntity turbine){for(int i=0;i<2;i++)turbine.tanks[i].writeToNBT(data,"hbmLeviathan"+i);data.putString("hbmLeviathanHE",turbine.getPowerExact().toString());data.putInt("hbmLeviathanIn",turbine.inputConsumed());data.putInt("hbmLeviathanOut",turbine.outputProduced());data.putString("hbmLeviathanGenerated",turbine.generatedPower().toString());}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var data=accessor.getServerData();if(!data.contains("hbmLeviathanHE"))return;var input=new FluidTank(HbmFluids.NONE,1);var output=new FluidTank(HbmFluids.NONE,1);input.readFromNBT(data,"hbmLeviathan0");output.readFromNBT(data,"hbmLeviathan1");var trait=input.getTankType().getTrait(FluidTraits.Coolable.class);tooltip.add(Component.literal("\u00a7a-> \u00a7r").append(input.getTankType().displayName()).append(": "+String.format(java.util.Locale.US,"%,d/%,dmB",input.getFill(),input.getMaxFill())));tooltip.add(Component.literal("\u00a7c<- \u00a7r").append((trait==null?HbmFluids.NONE:trait.coolsTo()).displayName()).append(": "+String.format(java.util.Locale.US,"%,d/%,dmB",output.getFill(),output.getMaxFill())));tooltip.add(Component.literal("\u00a7c<- \u00a7r"+data.getString("hbmLeviathanGenerated")+" HE/t"));}
 }
}
