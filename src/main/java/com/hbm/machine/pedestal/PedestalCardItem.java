package com.hbm.machine.pedestal;
import com.hbm.armor.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
/** Actual ItemModCard. AoS uses the already-authoritative GunArmorPerks ammo path; QoS cancels the original random one-in-three incoming hits. */
public final class PedestalCardItem extends ArmorModItem {
    private final boolean queen;
    public PedestalCardItem(boolean queen){super(ArmorModStorage.HELMET,true,true,false,false);this.queen=queen;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable(queen?"tooltip.hbm.pedestal_card.power":"tooltip.hbm.pedestal_card.top").withStyle(ChatFormatting.RED));lines.add(Component.translatable(queen?"tooltip.hbm.pedestal_card.damage":"tooltip.hbm.pedestal_card.ammo").withStyle(ChatFormatting.RED));lines.add(Component.empty());super.appendHoverText(stack,context,lines,flags);}
    public Component installedDescription(ItemStack stack){return stack.getHoverName().copy().withStyle(ChatFormatting.RED);}
    private static final Map<Player,Long> PLINK=new WeakHashMap<>();
    public static void register(){NeoForge.EVENT_BUS.addListener(PedestalCardItem::damage);}
    private static void damage(LivingIncomingDamageEvent event){
        if(!(event.getEntity() instanceof Player player)||player.level().isClientSide)return;
        // Source ModEventHandler calls every installed ItemArmorMod on every equipped piece.
        for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD})for(var card:ArmorModStorage.pryMods(player.getItemBySlot(slot),player.level().registryAccess())){
            if(!(card.getItem() instanceof PedestalCardItem item)||!item.queen||player.getRandom().nextInt(3)!=0)continue;
            float pitch=1+player.getRandom().nextFloat()*.5F;long time=player.level().getGameTime();if(time-PLINK.getOrDefault(player,Long.MIN_VALUE/2)>=10){player.level().playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.ITEM_BREAK,SoundSource.PLAYERS,.5F,pitch);PLINK.put(player,time);}
            event.setAmount(0);event.setCanceled(true);
        }
    }
}
