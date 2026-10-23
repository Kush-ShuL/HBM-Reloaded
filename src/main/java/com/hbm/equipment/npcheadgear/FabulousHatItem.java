package com.hbm.equipment.npcheadgear;

import com.hbm.armor.LegacyArmorDamageHandler;
import com.hbm.armor.alloy.AlloyContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

/** Actual ArmorHat: alloy armor, source attack cancellation, and dropped-entity deletion. */
public final class FabulousHatItem extends NpcHeadgearItem implements LegacyArmorDamageHandler {
    public FabulousHatItem() { super(AlloyContent.MATERIAL, 440, "nossy_hat", "textures/models/capes/capeunknown.png"); }
    @Override public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) { entity.discard(); return true; }
    @Override public boolean sourceCancelAttack(Player player, ItemStack armor, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) || amount > 2) return false;
        player.level().playSound(null, player, SoundEvents.ITEM_BREAK, net.minecraft.sounds.SoundSource.PLAYERS,
                5, 1 + player.getRandom().nextFloat() * .5F);
        return true;
    }
    @Override public float sourceHurt(Player player, ItemStack armor, DamageSource source, float amount) {
        return source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : Math.max(0, amount - 2);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.npc_headgear.dt", "+2").withStyle(ChatFormatting.BLUE));
    }
}
