package com.hbm.item.holotape;

import com.hbm.LegacyStartup;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source health<10, Resistance III for10ticks anywhere in inventory; global startup fate is not a second item/world state. */
public final class PolaroidItem extends Item {
    public PolaroidItem(){super(new Properties().stacksTo(1));}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(entity instanceof Player player&&player.getHealth()<10F)player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,10,2));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.polaroid.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.polaroid.fate"));lines.add(Component.empty());int fate=LegacyStartup.polaroidId;if(fate>=1&&fate<=18)lines.add(Component.translatable("tooltip.hbm.polaroid."+fate));if(fate==18){lines.add(Component.empty());lines.add(Component.translatable("tooltip.hbm.polaroid.18.ps"));lines.add(Component.translatable("tooltip.hbm.polaroid.18.reply"));}}
}
