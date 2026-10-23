package com.hbm.client.precisionassembler;
import com.hbm.machine.precisionassembler.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Shared MachineView supplies true HE/fluid/task bars; this provider adds only the actual selected source recipe. */
@WailaPlugin
public final class PrecisionAssemblerJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,PrecisionAssemblerBlockEntity.class);event.registerBlockDataProvider(Provider.INSTANCE,PrecisionAssemblerPort.class);}
 @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,PrecisionAssemblerBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:precision_assembler_state");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var tile=accessor.getBlockEntity();if(tile instanceof PrecisionAssemblerPort&&accessor.getBlockState().getBlock() instanceof PrecisionAssemblerBlock block){var core=block.findCore(accessor.getLevel(),accessor.getPosition());tile=core==null?null:accessor.getLevel().getBlockEntity(core);}if(tile instanceof PrecisionAssemblerBlockEntity machine){var recipe=machine.module.getRecipe();if(recipe!=null)tag.putString("hbm:precision_recipe",Component.Serializer.toJson(recipe.displayName(),accessor.getLevel().registryAccess()));}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbm:precision_recipe")){var name=Component.Serializer.fromJson(tag.getString("hbm:precision_recipe"),accessor.getLevel().registryAccess());if(name!=null)tooltip.add(Component.translatable("jade.hbm.precass.recipe",name));}}
 }
}
