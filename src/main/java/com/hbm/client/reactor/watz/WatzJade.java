package com.hbm.client.reactor.watz;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.machine.reactor.watz.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Real core heat/flux/lock plus source ToolConversion look-overlay materials, never fake local energy values. */
@WailaPlugin
public final class WatzJade implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,WatzBlock.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,WatzBlock.class);registration.registerBlockComponent(Provider.INSTANCE,WatzCasing.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:watz_status");}
        @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof WatzBlock block))return;var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof WatzEntity watz){data.putInt("hbmWatzHeat",watz.heat);data.putDouble("hbmWatzFlux",watz.fluxLastBase+watz.fluxLastReaction);data.putBoolean("hbmWatzOn",watz.isOn);data.putBoolean("hbmWatzLocked",watz.isLocked);}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var data=accessor.getServerData();if(data.contains("hbmWatzHeat")){tooltip.add(Component.translatable("jade.hbm.watz.heat",data.getInt("hbmWatzHeat")));tooltip.add(Component.translatable("jade.hbm.watz.flux",String.format(java.util.Locale.US,"%,.1f",data.getDouble("hbmWatzFlux"))));}if(!(accessor.getBlockState().getBlock() instanceof WatzCasing)||accessor.getBlockState().getValue(WatzCasing.META)!=0)return;var player=accessor.getPlayer();if(player==null||ToolableBlock.ToolType.getType(player.getMainHandItem())!=ToolableBlock.ToolType.BOLT)return;tooltip.add(Component.translatable("jade.hbm.watz.requires").withStyle(ChatFormatting.GOLD));var tools=ToolableBlock.ToolType.BOLT.stacksForDisplay;if(!tools.isEmpty())tooltip.add(Component.literal("- ").append(tools.get((int)(Math.abs(System.currentTimeMillis()/1000)%tools.size())).getHoverName()).withStyle(ChatFormatting.BLUE));var bolt=HbmMaterials.bySymbol("MAT_DURA").make(AutogenContent.item("bolt"));tooltip.add(Component.literal("- ").append(bolt.getHoverName()).append(" x4"));}
    }
}
