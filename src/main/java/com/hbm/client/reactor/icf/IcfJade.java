package com.hbm.client.reactor.icf;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.machine.reactor.icf.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Real original BlockToolConversion held-tool requirements; dynamic members come from the sole native dictionary. */
@WailaPlugin
public final class IcfJade implements IWailaPlugin{
 @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,IcfReactorBlock.class);}
 @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,IcfMaterialBlock.class);event.registerBlockComponent(Provider.INSTANCE,IcfReactorBlock.class);}
 private enum Provider implements IBlockComponentProvider,IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:icf_tooling");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof IcfReactorBlock block))return;var pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos!=null&&accessor.getLevel().getBlockEntity(pos) instanceof IcfReactorEntity reactor){tag.putBoolean("hbmIcfActive",reactor.heatup>0);tag.putInt("hbmIcfConsumption",reactor.consumption);tag.putInt("hbmIcfOutput",reactor.output);}}
 @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbmIcfActive")){tooltip.add(Component.translatable("jade.hbm.icf.active",tag.getBoolean("hbmIcfActive")));tooltip.add(Component.translatable("jade.hbm.icf.consumption",tag.getInt("hbmIcfConsumption")));tooltip.add(Component.translatable("jade.hbm.icf.output",tag.getInt("hbmIcfOutput")));}var player=accessor.getPlayer();if(player==null||!(accessor.getBlockState().getBlock() instanceof IcfMaterialBlock block))return;var tool=ToolType.getType(player.getMainHandItem());if(tool==null)return;var needs=block.requirements(accessor.getBlockState(),tool);if(needs.isEmpty())return;tooltip.add(Component.translatable("gui.hbm.icf.requires").withStyle(ChatFormatting.GOLD));if(!tool.stacksForDisplay.isEmpty())tooltip.add(Component.literal("- ").append(tool.stacksForDisplay.get((int)(Math.abs(System.currentTimeMillis()/1000)%tool.stacksForDisplay.size())).getHoverName()).withStyle(ChatFormatting.BLUE));for(var need:needs){var materials=need.ingredient().getItems().toList();if(materials.isEmpty())tooltip.add(Component.translatable("gui.hbm.icf.missing_material").withStyle(ChatFormatting.RED));else tooltip.add(Component.literal("- ").append(materials.get((int)(Math.abs(System.currentTimeMillis()/1000)%materials.size())).getHoverName()).append(" x"+need.count()));}}}
}
