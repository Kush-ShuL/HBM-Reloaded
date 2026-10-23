package com.hbm.equipment.boostmurk;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.equipment.jetpack.JetpackRuntime;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Genuine WingsMurk, both variants: armor modification or standalone chest item, never a fueled item. */
public final class SourceWingsItem extends ArmorModItem {
    public final boolean limp;
    public SourceWingsItem(boolean limp){super(ArmorModStorage.CHEST,false,true,false,false);this.limp=limp;}
    @Override public EquipmentSlot getEquipmentSlot(ItemStack stack){return EquipmentSlot.CHEST;}
    @Override public boolean canEquip(ItemStack stack,EquipmentSlot slot,LivingEntity entity){return slot==EquipmentSlot.CHEST;}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(entity instanceof Player player&&player.getItemBySlot(EquipmentSlot.CHEST)==stack)armorTick(player);}
    @Override public void modUpdate(LivingEntity entity,ItemStack armor){
        if(!(entity instanceof Player player))return;
        var wings=ArmorModStorage.pryMod(armor,ArmorModStorage.CHEST,player.level().registryAccess());
        if(wings.getItem()!=this)return;armorTick(player);ArmorModStorage.applyMod(armor,wings,player.level().registryAccess());
    }
    private void armorTick(Player player){
        if(player.onGround())return;
        JetpackRuntime.resetFlight(player);if(player.fallDistance>0)player.fallDistance=0;
        var velocity=player.getDeltaMovement();
        if(limp){
            if(velocity.y<-.4D)velocity=new Vec3(velocity.x,-.4D,velocity.z);
            if(player.isShiftKeyDown()&&velocity.y<-.08D){double motion=velocity.y*-.2D;velocity=velocity.add(0,motion,0);var look=player.getLookAngle();velocity=velocity.add(look.x*motion,look.y*motion,look.z*motion);}
        }else{
            boolean enabled=JetpackNetwork.enabled(player),active=enabled&&JetpackNetwork.jump(player);double y=velocity.y;
            if(active){
                if(player.isShiftKeyDown()){
                    if(y< -1)y+=.4D;else if(y<-.1D)y+=.2D;else if(y<0)y=0;else if(y>1)y-=.4D;else if(y>.1D)y-=.2D;else if(y>0)y=0;
                }else{if(y<.6D)y+=.2D;else y=.8D;}
            }else if(enabled&&!player.isShiftKeyDown()){if(y< -1)y+=.4D;else if(y<-.1D)y+=.2D;else if(y<0)y=0;}
            velocity=new Vec3(velocity.x,y,velocity.z);
            if(enabled){
                var original=player.getLookAngle();var look=LegacyVector.createVectorHelper(original.x,0,original.z).normalize();double mod=player.isSprinting()?1D:.25D;
                if(player.zza!=0)velocity=velocity.add(look.xCoord*.35D*player.zza*mod,0,look.zCoord*.35D*player.zza*mod);
                if(player.xxa!=0){look.rotateAroundY((float)Math.PI*.5F);velocity=velocity.add(look.xCoord*.15D*player.xxa*mod,0,look.zCoord*.15D*player.xxa*mod);}
            }
        }
        player.setDeltaMovement(velocity);
    }
    @Override public Component installedDescription(ItemStack stack){return Component.literal("  ").append(stack.getHoverName()).withStyle(ChatFormatting.RED);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags){super.appendHoverText(stack,context,tooltip,flags);tooltip.add(Component.translatable("tooltip.hbm.jetpack.wearable").withStyle(ChatFormatting.GOLD));}
}
