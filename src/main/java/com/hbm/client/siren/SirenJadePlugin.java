package com.hbm.client.siren;
import com.hbm.machine.siren.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin public final class SirenJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,SirenBlockEntity.class);}
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,SirenBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:siren106");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof SirenBlockEntity machine){var data=new CompoundTag();data.putInt("track",machine.track().ordinal());data.putBoolean("powered",accessor.getLevel().hasNeighborSignal(accessor.getPosition()));tag.put("hbm:siren106",data);}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:siren106"))return;var data=accessor.getServerData().getCompound("hbm:siren106");var track=SirenTrack.getEnum(data.getInt("track"));if(track!=SirenTrack.NULL){tooltip.add(track.title().copy().withColor(track.color));tooltip.add(Component.translatable("gui.hbm.siren.type",track.typeName()));tooltip.add(Component.translatable("gui.hbm.siren.volume",track.volume));}tooltip.add(Component.translatable("jade.hbm.siren.powered."+data.getBoolean("powered")));}
    }
}
