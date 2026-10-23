package com.hbm.geiger;

import com.hbm.exposure.HbmExposure;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmSounds;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** ItemGeigerCounter emits clicks while anywhere in the inventory; it reads the real radBuf. */
public final class GeigerCounterItem extends Item {
    private final Random random=new Random();
    public GeigerCounterItem() { super(new Properties().stacksTo(1)); }
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected) {
        if(!(entity instanceof LivingEntity living)||!(level instanceof ServerLevel)||level.getGameTime()%5!=0) return;
        int sound=GeigerClicks.handheld(HbmExposure.data(living).radBuffer(),random::nextInt);
        if(sound>0) level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("item.geiger"+sound),SoundSource.PLAYERS,1,1);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        if(!level.isClientSide) { level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.techBoop"),SoundSource.PLAYERS,1,1);GeigerReadings.print(player); }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);
    }
    public static int check(ServerLevel level,int x,int y,int z) { return (int)Math.ceil(HbmRadiation.getRadiation(level,x,y,z)); }
    public static void setFloat(ItemStack stack,float value,String name) { var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putFloat(name,value);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag)); }
    public static float getFloat(ItemStack stack,String name) { return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getFloat(name); }
}
