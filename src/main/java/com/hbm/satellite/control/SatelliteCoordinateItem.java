package com.hbm.satellite.control;
import com.hbm.satellite.*;import com.hbm.satellite.item.SatelliteChipItem;import com.hbm.machine.storage.CrateStackData;import java.util.function.Consumer;import net.minecraft.world.*;import net.minecraft.world.entity.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.*;import net.minecraft.world.level.Level;
/** Actual ItemSatInterface: connected flag updates only while this real stack is held; coordinate GUI is client-only. */
public final class SatelliteCoordinateItem extends SatelliteChipItem {
 public static final String KEY_NBT_CONNECTED="connected";public static Consumer<InteractionHand> OPEN=hand->{};
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){if(level.isClientSide)OPEN.accept(hand);return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);}
 @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(!(level instanceof net.minecraft.server.level.ServerLevel server)||!(entity instanceof Player player)||(player.getMainHandItem()!=stack&&player.getOffhandItem()!=stack))return;boolean connected=SatelliteSavedData.getData(server).getSatFromFreq(getFreq(stack))!=null;var tag=CrateStackData.data(stack);if(!tag.contains(KEY_NBT_CONNECTED)||tag.getBoolean(KEY_NBT_CONNECTED)!=connected){tag.putBoolean(KEY_NBT_CONNECTED,connected);CrateStackData.data(stack,tag);}}
 public void receiveControl(Player player,ItemStack stack,int x,Integer y,int z){if(!(player.level() instanceof net.minecraft.server.level.ServerLevel level))return;var sat=SatelliteSavedData.getData(level).getSatFromFreq(getFreq(stack));if(sat!=null)sat.onCoordAction(level,player,x,y==null?-1:y,z);}
}
