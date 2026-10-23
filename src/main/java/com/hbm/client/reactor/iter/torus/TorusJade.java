package com.hbm.client.reactor.iter.torus;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.reactor.iter.torus.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Actual server core thermal/fusion state and source tool-conversion look overlay. */
@WailaPlugin
public final class TorusJade implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,TorusBlock.class);}
 @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,TorusBlock.class);registration.registerBlockComponent(Provider.INSTANCE,FusionComponent.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:torus_status");}
  @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof TorusBlock block))return;var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof TorusEntity torus){data.putFloat("hbmTorusTemperature",torus.temperature);data.putLong("hbmTorusTU",torus.plasmaEnergy);data.putBoolean("hbmTorusActive",torus.didProcess);}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var data=accessor.getServerData();if(data.contains("hbmTorusTemperature")){tooltip.add(Component.translatable("jade.hbm.iter.temperature",(int)Math.ceil(data.getFloat("hbmTorusTemperature"))));tooltip.add(Component.translatable("jade.hbm.iter.plasma",data.getLong("hbmTorusTU")));}if(!(accessor.getBlockState().getBlock() instanceof FusionComponent)||accessor.getBlockState().getValue(FusionComponent.META)!=0)return;var player=accessor.getPlayer();if(player==null||ToolableBlock.ToolType.getType(player.getMainHandItem())!=ToolableBlock.ToolType.TORCH)return;tooltip.add(Component.translatable("jade.hbm.iter.requires").withStyle(ChatFormatting.GOLD));var tools=TorusTooling.tools();if(!tools.isEmpty())tooltip.add(Component.literal("- ").append(tools.get((int)(System.currentTimeMillis()/1000%tools.size())).getHoverName()).withStyle(ChatFormatting.BLUE));var plates=TorusTooling.bolts();if(!plates.isEmpty())tooltip.add(Component.literal("- ").append(plates.get((int)(System.currentTimeMillis()/1000%plates.size())).getHoverName()).append(" x1"));}
 }
}
