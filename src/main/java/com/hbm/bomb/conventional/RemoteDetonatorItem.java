package com.hbm.bomb.conventional;

import com.hbm.interfaces.IBomb;
import com.hbm.interfaces.IBomb.BombReturnCode;
import com.hbm.registry.HbmSounds;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.joml.Vector3f;

/** The source single/multi/laser detonators and the two ItemDrop dead-man variants. */
public final class RemoteDetonatorItem extends Item {
    public enum Kind { SINGLE, MULTI, LASER, DEAD_MAN, DEAD_EXPLOSIVE }
    private final Kind kind;
    public RemoteDetonatorItem(Kind kind) { super(new Properties().stacksTo(1)); this.kind = kind; }
    public Kind kind() { return kind; }
    private static CompoundTag data(ItemStack stack) { var data = stack.get(DataComponents.CUSTOM_DATA); return data == null ? null : data.copyTag(); }
    private static void save(ItemStack stack, CompoundTag tag) { stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag)); }
    private static void sound(Level level, Player player, boolean success, float volume) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(), HbmSounds.get(success ? "item.techBleep" : "item.techBoop"), SoundSource.PLAYERS, volume, 1F);
    }
    private net.minecraft.network.chat.MutableComponent prefix(ItemStack stack) { return Component.literal("[").append(stack.getHoverName()).append("] ").withStyle(ChatFormatting.DARK_AQUA); }
    private void message(Player player, ItemStack stack, Component text, ChatFormatting color, boolean overlay) { player.displayClientMessage(prefix(stack).append(text.copy().withStyle(color)), overlay); }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (kind == Kind.LASER || kind == Kind.DEAD_EXPLOSIVE) return InteractionResult.PASS;
        var stack = context.getItemInHand(); var player = context.getPlayer(); var level = context.getLevel();
        if (player == null) return InteractionResult.PASS;
        var tag = data(stack); if (tag == null) { tag = new CompoundTag(); save(stack, tag); }
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos();
        if (kind == Kind.MULTI) {
            for (String axis : List.of("x", "y", "z")) {
                String key = axis + "Values"; int[] old = tag.getIntArray(key); int[] next = Arrays.copyOf(old, old.length + 1);
                next[old.length] = axis.equals("x") ? pos.getX() : axis.equals("y") ? pos.getY() : pos.getZ(); tag.putIntArray(key, next);
            }
        } else { tag.putInt("x", pos.getX()); tag.putInt("y", pos.getY()); tag.putInt("z", pos.getZ()); }
        save(stack, tag);
        if (kind == Kind.DEAD_MAN) { if (level.isClientSide) player.displayClientMessage(Component.translatable("hbm.explosives.position.set"), false); }
        else if (!level.isClientSide) message(player, stack, Component.translatable(kind == Kind.MULTI ? "hbm.explosives.position.added" : "hbm.explosives.position.set"), ChatFormatting.GREEN, false);
        sound(level, player, false, 2F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    public static BombReturnCode trigger(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof IBomb bomb ? bomb.explode(level, pos.getX(), pos.getY(), pos.getZ()) : BombReturnCode.ERROR_NO_BOMB;
    }
    /** Source logs every actual IBomb attempt, including unsuccessful BombReturnCode results. */
    private static BombReturnCode triggerByPlayer(Level level, BlockPos pos, Player player) {
        boolean bomb = level.getBlockState(pos).getBlock() instanceof IBomb;
        var result = trigger(level, pos);
        if (bomb) com.hbm.logging.SourceActionLog.info("[DET] Tried to detonate block at {} / {} / {} by {}!",
                pos.getX(), pos.getY(), pos.getZ(), player.getDisplayName().getString());
        return result;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (kind == Kind.DEAD_MAN || kind == Kind.DEAD_EXPLOSIVE) return InteractionResultHolder.pass(stack);
        if (kind == Kind.LASER) { laser(level, player, stack); return InteractionResultHolder.sidedSuccess(stack, level.isClientSide); }
        var tag = data(stack);
        if (tag == null || kind == Kind.MULTI && tag.getIntArray("xValues").length == 0) {
            if (!level.isClientSide) message(player, stack, Component.translatable("hbm.explosives.position.none"), ChatFormatting.RED, false);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        if (kind == Kind.MULTI) {
            if (player.isShiftKeyDown()) {
                for (String axis : List.of("x", "y", "z")) tag.putIntArray(axis + "Values", new int[0]); save(stack, tag);
                sound(level, player, false, 2F);
                if (!level.isClientSide) message(player, stack, Component.translatable("hbm.explosives.position.cleared"), ChatFormatting.RED, false);
            } else {
                int[] xs = tag.getIntArray("xValues"), ys = tag.getIntArray("yValues"), zs = tag.getIntArray("zValues"); int successful = 0;
                if (!level.isClientSide) for (int i = 0; i < xs.length; i++) if (triggerByPlayer(level, new BlockPos(xs[i], ys[i], zs[i]), player).wasSuccessful()) successful++;
                sound(level, player, true, 1F);
                if (!level.isClientSide) message(player, stack, Component.translatable("hbm.explosives.position.triggered",successful,xs.length), ChatFormatting.YELLOW, false);
            }
        } else {
            var pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            if (level.getBlockState(pos).getBlock() instanceof IBomb) sound(level, player, true, 1F);
            if (!level.isClientSide) { var result = triggerByPlayer(level, pos, player); message(player, stack, Component.translatable(result.getUnlocalizedMessage()), result.wasSuccessful() ? ChatFormatting.YELLOW : ChatFormatting.RED, false); }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    private void laser(Level level, Player player, ItemStack stack) {
        Vec3 eye = player.getEyePosition(), end = eye.add(player.getViewVector(1).scale(500));
        var hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        var pos = hit.getBlockPos();
        if (!level.isClientSide) {
            var result = triggerByPlayer(level, pos, player); level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(result!=BombReturnCode.ERROR_NO_BOMB?"item.techBleep":"item.techBoop"),SoundSource.PLAYERS,1,1);
            // Source PlayerInformPacket uses ID_DETONATOR=8 in the shared top-left information box.
            DetonatorNotice.send(player,result);
        } else {
            Vec3 vector = Vec3.atCenterOf(pos).subtract(player.position()); double length = Math.min(vector.length(), 15); vector = vector.normalize();
            for (int i = 0; i < length; i++) { double distance = level.random.nextDouble() * length + 3; var particle = player.position().add(vector.scale(distance)); level.addParticle(new DustParticleOptions(new Vector3f(1, 0, 0), 1), particle.x, particle.y, particle.z, 0, 0, 0); }
        }
    }
    /** Source LOWEST death path runs before inventory drops, including keepInventory deaths. */
    static void playerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (!(stack.getItem() instanceof RemoteDetonatorItem detonator) || detonator.kind != Kind.DEAD_MAN) continue;
            var tag = data(stack);
            if (tag == null) continue;
            var pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            if (!player.level().isClientSide && player.level().getBlockState(pos).getBlock() instanceof IBomb) {
                trigger(player.level(), pos);
                com.hbm.logging.SourceActionLog.info("[DET] Tried to detonate block at {} / {} / {} by dead man's switch from {}!",
                        pos.getX(), pos.getY(), pos.getZ(), player.getDisplayName().getString());
            }
            inventory.setItem(slot, ItemStack.EMPTY);
        }
    }
    @Override public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (kind == Kind.DEAD_MAN && !entity.level().isClientSide) {
            var tag = data(stack); if (tag != null) {
                var pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
                boolean bomb = entity.level().getBlockState(pos).getBlock() instanceof IBomb;
                trigger(entity.level(), pos);
                if (bomb) com.hbm.logging.SourceActionLog.info("[DET] Tried to detonate block at {} / {} / {} by dead man's switch!", pos.getX(), pos.getY(), pos.getZ());
            }
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 0, Level.ExplosionInteraction.TNT); entity.discard();
        }
        if (kind == Kind.DEAD_EXPLOSIVE) {
            if (!entity.level().isClientSide && ConventionalContent.DEAD_EXPLOSIVE_ENABLED.getAsBoolean()) {
                entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 15, Level.ExplosionInteraction.TNT);
                com.hbm.logging.SourceActionLog.info("[DET] Detonated dead man's explosive at {} / {} / {}!", (int)entity.getX(), (int)entity.getY(), (int)entity.getZ());
            }
            entity.discard();
        }
        // Source returns true once the dropped item touches ground; it is gone regardless of config.
        return (kind == Kind.DEAD_MAN || kind == Kind.DEAD_EXPLOSIVE) && entity.onGround();
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        var tag = data(stack);
        if (kind == Kind.LASER) { text.add(Component.translatable("hbm.explosives.detonator.aim")); return; }
        if (kind == Kind.DEAD_EXPLOSIVE) text.add(Component.translatable("hbm.explosives.detonator.drop_explode"));
        else {
            text.add(Component.translatable(kind == Kind.MULTI ? "hbm.explosives.detonator.add" : "hbm.explosives.detonator.set"));
            text.add(Component.translatable(kind == Kind.DEAD_MAN ? "hbm.explosives.detonator.drop" : "hbm.explosives.detonator.trigger"));
            if (kind == Kind.MULTI) text.add(Component.translatable("hbm.explosives.detonator.clear"));
            if (tag == null || kind == Kind.MULTI && tag.getIntArray("xValues").length == 0) text.add(Component.translatable("hbm.explosives.position.none").withStyle(kind == Kind.DEAD_MAN ? ChatFormatting.WHITE : ChatFormatting.RED));
            else if (kind == Kind.MULTI) {
                int[] xs = tag.getIntArray("xValues"), ys = tag.getIntArray("yValues"), zs = tag.getIntArray("zValues");
                for (int i = 0; i < xs.length; i++) text.add(Component.literal(xs[i] + " / " + ys[i] + " / " + zs[i]).withStyle(ChatFormatting.YELLOW));
            } else text.add(Component.translatable(kind == Kind.DEAD_MAN ? "hbm.explosives.position.target" : "hbm.explosives.position.linked",tag.getInt("x"),tag.getInt("y"),tag.getInt("z")).withStyle(kind == Kind.DEAD_MAN ? ChatFormatting.WHITE : ChatFormatting.YELLOW));
        }
        if (kind == Kind.DEAD_MAN || kind == Kind.DEAD_EXPLOSIVE) text.add(Component.literal("[").append(Component.translatable("trait.drop")).append("]").withStyle(ChatFormatting.RED));
    }
}
