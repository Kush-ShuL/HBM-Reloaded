package com.hbm.client.network.cargo;
import com.hbm.network.cargo.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class CargoDroneJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration r){r.registerBlockDataProvider(Provider.INSTANCE,DroneWaypointEntity.class);r.registerBlockDataProvider(Provider.INSTANCE,DroneCrateEntity.class);}@Override public void registerClient(IWailaClientRegistration r){r.registerBlockComponent(Provider.INSTANCE,DroneWaypointBlock.class);r.registerBlockComponent(Provider.INSTANCE,DroneCrateBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:drone_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor a){var tile=a.getBlockEntity();var data=new CompoundTag();if(tile instanceof DroneWaypointEntity w){data.putInt("height",w.height);data.putIntArray("target",new int[]{w.nextX,w.nextY,w.nextZ});}else if(tile instanceof DroneCrateEntity c)data.putIntArray("target",new int[]{c.nextX,c.nextY,c.nextZ});else return;tag.put("hbm:drone",data);}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor a,IPluginConfig config){if(!a.getServerData().contains("hbm:drone"))return;var data=a.getServerData().getCompound("hbm:drone");if(data.contains("height"))tooltip.add(Component.translatable("jade.hbm.drone.distance",data.getInt("height")));var target=data.getIntArray("target");if(target.length>=3&&target[1]!=-1)tooltip.add(Component.translatable("jade.hbm.drone.next",target[0],target[1],target[2]));}}
}
