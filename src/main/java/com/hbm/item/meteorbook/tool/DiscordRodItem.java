package com.hbm.item.meteorbook.tool;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** ItemDiscord's real 100-block ray teleport; no charge, cooldown, obstruction search or safe-position substitution. */
public final class DiscordRodItem extends Item {
    public DiscordRodItem() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.rod_of_discord.name"); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Vec3 eye = player.getEyePosition(1F);
        // Original EntityPlayer.getLook(1): preserve its float sine-table products before the double ray scale.
        float cosYaw = Mth.cos(-player.getYRot() * .017453292F - (float)Math.PI);
        float sinYaw = Mth.sin(-player.getYRot() * .017453292F - (float)Math.PI);
        float horizontal = -Mth.cos(-player.getXRot() * .017453292F);
        float vertical = Mth.sin(-player.getXRot() * .017453292F);
        Vec3 end = eye.add((double)(sinYaw * horizontal) * 100D, (double)vertical * 100D, (double)(cosYaw * horizontal) * 100D);
        // Library.rayTrace flags false,false,true: ignore liquids, retain non-colliding block outlines, distinguish MISS.
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);
        if (!level.isClientSide) {
            if (player.isPassenger()) player.stopRiding();
            sound(level, player);
            var direction = hit.getDirection(); var point = hit.getLocation();
            double x = point.x + direction.getStepX(), y = point.y + direction.getStepY() - 1D, z = point.z + direction.getStepZ();
            // Source setPositionAndUpdate directly calls the absolute-position network teleport and retains view angles.
            if (player instanceof ServerPlayer server) server.connection.teleport(x, y, z, player.getYRot(), player.getXRot());
            else player.teleportTo(x, y, z);
            sound(level, player);
            player.fallDistance = 0F;
        }
        for (int i = 0; i < 32; i++) level.addParticle(ParticleTypes.PORTAL,
            player.getX(), player.getY() + player.getRandom().nextDouble() * 2D, player.getZ(),
            player.getRandom().nextGaussian(), 0D, player.getRandom().nextGaussian());
        // The original unchanged ItemStack return starts no use pose or swing. Consume only to avoid a second offhand use.
        return InteractionResultHolder.consume(stack);
    }
    private static void sound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1F, 1F);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        line(tooltip, 0, ChatFormatting.YELLOW); line(tooltip, 1, ChatFormatting.YELLOW); tooltip.add(Component.empty());
        line(tooltip, 2, ChatFormatting.RED); line(tooltip, 3, ChatFormatting.RED); tooltip.add(Component.empty());
        line(tooltip, 4, ChatFormatting.YELLOW); line(tooltip, 5, ChatFormatting.YELLOW);
    }
    private static void line(List<Component> tooltip, int line, ChatFormatting color) {
        tooltip.add(Component.translatable("tooltip.hbm.discord_rod." + line).withStyle(color, ChatFormatting.ITALIC));
    }
}
