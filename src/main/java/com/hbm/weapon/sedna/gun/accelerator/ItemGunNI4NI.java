package com.hbm.weapon.sedna.gun.accelerator;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original coin regeneration/custom colors; all source fields live in native gun custom_data. */
public final class ItemGunNI4NI extends ItemGunBaseNT implements CustomizableWeapon {
    public ItemGunNI4NI(WeaponQuality quality,GunConfig... configs){super(quality,configs);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean held){
        super.inventoryTick(stack,level,entity,slot,held);
        if(level.isClientSide)return;
        int maximum=4;if(XWeaponModManager.hasUpgrade(stack,0,220))maximum+=2;if(XWeaponModManager.hasUpgrade(stack,0,221))maximum+=2;
        if(getCoinCount(stack)<maximum){setCoinCharge(stack,getCoinCharge(stack)+1);if(getCoinCharge(stack)>=80){setCoinCharge(stack,0);int count=getCoinCount(stack)+1;setCoinCount(stack,count);
            if(held||entity instanceof net.minecraft.world.entity.player.Player player&&stack==player.getOffhandItem()&&GunHands.operable(player,net.minecraft.world.InteractionHand.OFF_HAND))level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),com.hbm.registry.HbmSounds.get("item.techBoop"),net.minecraft.sounds.SoundSource.NEUTRAL,1F,1F+count/(float)maximum);
        }}
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        lines.add(Component.translatable("tooltip.hbm.ni4ni.line1"));
        lines.add(Component.translatable("tooltip.hbm.ni4ni.line2.prefix").append(Component.translatable("tooltip.hbm.ni4ni.line2.emphasis").withStyle(ChatFormatting.RED)).append(Component.translatable("tooltip.hbm.ni4ni.line2.suffix").withStyle(ChatFormatting.GRAY)));
        lines.add(Component.translatable("tooltip.hbm.ni4ni.line3"));super.appendHoverText(stack,context,lines,flag);
    }
    @Override public void customize(ServerPlayer player,ItemStack stack,String... arguments){
        if(arguments.length==0){resetColors(stack);player.sendSystemMessage(Component.translatable("message.hbm.customize.reset").withStyle(ChatFormatting.GREEN));return;}
        if(arguments.length!=3){resetColors(stack);player.sendSystemMessage(Component.translatable("message.hbm.customize.three_colors").withStyle(ChatFormatting.RED));return;}
        try{
            int dark=Integer.parseInt(arguments[0],16),light=Integer.parseInt(arguments[1],16),grip=Integer.parseInt(arguments[2],16);
            if(dark<0||dark>0xffffff||light<0||light>0xffffff||grip<0||grip>0xffffff){player.sendSystemMessage(Component.translatable("message.hbm.customize.range").withStyle(ChatFormatting.RED));return;}
            setColors(stack,dark,light,grip);player.sendSystemMessage(Component.translatable("message.hbm.customize.set").withStyle(ChatFormatting.GREEN));
        }catch(NumberFormatException error){player.sendSystemMessage(Component.translatable("message.hbm.customize.invalid",error.getLocalizedMessage()).withStyle(ChatFormatting.RED));}
    }
    public static void resetColors(ItemStack stack){if(GunStackData.hasData(stack))GunStackData.update(stack,tag->tag.remove("colors"));}
    public static void setColors(ItemStack stack,int dark,int light,int grip){GunStackData.update(stack,tag->tag.putIntArray("colors",new int[]{dark,light,grip}));}
    public static int[] getColors(ItemStack stack){int[] colors=GunStackData.data(stack).getIntArray("colors");return colors.length==3?colors:null;}
    public static final String KEY_COIN_COUNT="coincount",KEY_COIN_CHARGE="coincharge";
    public static int getCoinCount(ItemStack stack){return getValueInt(stack,KEY_COIN_COUNT);}
    public static void setCoinCount(ItemStack stack,int count){setValueInt(stack,KEY_COIN_COUNT,count);}
    public static int getCoinCharge(ItemStack stack){return getValueInt(stack,KEY_COIN_CHARGE);}
    public static void setCoinCharge(ItemStack stack,int count){setValueInt(stack,KEY_COIN_CHARGE,count);}
}
