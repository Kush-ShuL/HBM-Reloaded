package com.hbm.client.armor;

import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.GasMask;
import com.hbm.armor.GasMaskFilters;
import com.hbm.item.settings.client.LegacyInfoSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Source client START warning, refreshed through the existing keyed notice inbox. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class EmptyMaskWarning {
    private static final int FILTER_NOTICE_ID=1;

    @SubscribeEvent public static void tick(ClientTickEvent.Pre event){
        var client=Minecraft.getInstance();
        if(client.level==null||client.player==null||!wearingEmptyMask(client.player))return;
        LegacyInfoSystem.push(Component.translatable("tooltip.hbm.gasmask.no_filter").withStyle(ChatFormatting.RED),FILTER_NOTICE_ID);
    }

    private static boolean wearingEmptyMask(Player player){
        var helmet=player.getItemBySlot(EquipmentSlot.HEAD);
        if(helmet.isEmpty())return false;
        var lookup=player.level().registryAccess();
        if(helmet.getItem() instanceof GasMask)return GasMaskFilters.filter(helmet,lookup).isEmpty();
        var attachment=ArmorModStorage.pryMod(helmet,ArmorModStorage.HELMET,lookup);
        return attachment.getItem() instanceof GasMask&&GasMaskFilters.filter(attachment,lookup).isEmpty();
    }

    private EmptyMaskWarning(){}
}
