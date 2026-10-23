package com.hbm.item.rttyhandheld;

import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

/** Two actual source handheld RTTY items, sharing the one installed broadcast/listen network and freq NBT. */
public final class RttyHandheldItem extends Item {
    public enum Kind { PAGER, DESIGNATOR }
    public static final String KEY_CHANNEL = "chan";
    public static BiConsumer<InteractionHand, Kind> CLIENT_OPEN = (hand, kind) -> { };
    public final Kind kind;
    public RttyHandheldItem(Kind kind) { super(new Properties().stacksTo(1)); this.kind = kind; }
    public static String channel(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(KEY_CHANNEL); }
    private static boolean configured(ItemStack stack) { return stack.has(DataComponents.CUSTOM_DATA) && stack.get(DataComponents.CUSTOM_DATA).copyTag().contains(KEY_CHANNEL); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.rtty_" + (kind == Kind.PAGER ? "pager" : "designator") + ".name"); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (kind == Kind.PAGER || player.isShiftKeyDown()) {
            if (level.isClientSide) CLIENT_OPEN.accept(hand, kind);
        } else if (!level.isClientSide && configured(stack)) {
            var eye = player.getEyePosition(1); var end = eye.add(player.getViewVector(1).scale(300));
            var hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            // Source mopOnMiss=true still sends its returned position+face; do not introduce a BLOCK-only guard.
            var target = hit.getBlockPos().relative(hit.getDirection());
            String coords = target.getX() + IRORInteractive.PARAM_SEPARATOR + target.getY() + IRORInteractive.PARAM_SEPARATOR + target.getZ();
            RTTYSystem.broadcast(level, channel(stack), coords);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (kind != Kind.PAGER || !configured(stack) || !(entity instanceof ServerPlayer player)) return;
        String channel = channel(stack); var signal = RTTYSystem.listen(level, channel);
        if (signal == null || signal.timeStamp < level.getGameTime() - 1) return;
        if ("selfdestruct".equals(String.valueOf(signal.signal))) {
            new ExplosionVNT(level, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 5, null)
                .setEntityProcessor(new EntityProcessorCrossSmooth(1, 50).setupPiercing(5F, .5F))
                .setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F)).explode();
            stack.shrink(1); // Source consumption happens after the real explosion, including creative.
            return;
        }
        String message = ChatFormatting.GOLD + "[ " + channel + " (" + entity.tickCount % 1000 + ") ] " + ChatFormatting.YELLOW + signal.signal;
        RttyPagerNotice.send(player, message, 1000 + slot);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        String channel = channel(stack);
        lines.add(channel.isEmpty() ? Component.translatable("tooltip.hbm.rtty_handheld.no_channel").withStyle(ChatFormatting.RED)
            : Component.translatable("tooltip.hbm.rtty_handheld.channel", channel).withStyle(ChatFormatting.YELLOW));
    }
}
