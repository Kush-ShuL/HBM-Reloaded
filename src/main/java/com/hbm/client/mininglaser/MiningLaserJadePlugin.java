package com.hbm.client.mininglaser;
import com.hbm.machine.mininglaser.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Storage/tank/progress remain the sole MachineView integration; this provider adds actual run/redstone state only. */
@WailaPlugin public final class MiningLaserJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Status.INSTANCE,MiningLaserEntity.class);event.registerBlockDataProvider(Status.INSTANCE,MiningLaserPort.class);}
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Status.INSTANCE,MiningLaserBlock.class);}
    private enum Status implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:mining_laser_status");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var pos=accessor.getPosition();if(!(accessor.getBlock() instanceof MiningLaserBlock block))return;var core=block.findCore(accessor.getLevel(),pos);if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof MiningLaserEntity be){tag.putBoolean("hbm:mining_laser_on",be.isOn);tag.putBoolean("hbm:mining_laser_redstone",be.isRedstonePowered());}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.getBoolean("hbm:mining_laser_redstone"))tooltip.add(Component.translatable("gui.hbm.mining_laser.redstone").withStyle(ChatFormatting.RED));else tooltip.add(Component.translatable(tag.getBoolean("hbm:mining_laser_on")?"gui.hbm.mining_laser.enabled":"gui.hbm.mining_laser.disabled"));}
    }
}
