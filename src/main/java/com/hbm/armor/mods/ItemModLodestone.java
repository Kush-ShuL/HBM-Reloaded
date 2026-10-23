package com.hbm.armor.mods;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original AABB magnet pull, separately applied for every installed magnet; no HE or LOS filter. */
public final class ItemModLodestone extends ArmorModItem {
    public final int range;
    private final String name;
    public ItemModLodestone(String name, int range) {
        super(ArmorModStorage.EXTRA, true, true, true, true); this.name = name; this.range = range;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public void modUpdate(LivingEntity wearer, ItemStack armor) {
        if (!(wearer.level() instanceof ServerLevel level)
                || wearer instanceof Player player && !MagnetNetwork.enabled(player)) return;
        for (var item : level.getEntitiesOfClass(ItemEntity.class, wearer.getBoundingBox().inflate(range))) {
            var direction = wearer.position().subtract(item.position()).normalize();
            var motion = item.getDeltaMovement().add(direction.scale(.75));
            if (direction.y > 0 && motion.y < .04) motion = motion.add(0, .2, 0);
            item.setDeltaMovement(motion);
            level.getChunkSource().broadcast(item, new ClientboundSetEntityMotionPacket(item));
        }
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.magnet.installed", stack.getHoverName(), range).withStyle(ChatFormatting.DARK_GRAY);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.magnet.attracts").withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.translatable("tooltip.hbm.magnet.range", range).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
