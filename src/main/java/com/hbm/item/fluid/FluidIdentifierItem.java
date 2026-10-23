package com.hbm.item.fluid;

import com.hbm.HbmReloaded;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original dual fluid identifier, persistent bookmarks, swap and selection control. */
public final class FluidIdentifierItem extends Item implements FluidIdentifier {
    public FluidIdentifierItem() { super(new Properties().stacksTo(1)); }
    public List<ItemStack> creativeStacks() {
        var result = new ArrayList<ItemStack>();
        var order = HbmFluids.getInNiceOrder();
        for (int i = 1; i < order.length; i++) if (!order[i].hasNoID()) {
            var stack = FluidItemContent.stack(this, order[i]); setType(stack, order[i], true); result.add(stack);
        }
        return result;
    }
    public static HbmFluidType getType(ItemStack stack, boolean primary) {
        return HbmFluids.fromID(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(primary ? "fluid1" : "fluid2"));
    }
    public static void setType(ItemStack stack, HbmFluidType type, boolean primary) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(primary ? "fluid1" : "fluid2", type.getID()));
    }
    public static void updateMeta(ItemStack stack) { FluidItemContent.setType(stack, getType(stack, true)); }
    public static int[] bookmarks(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getIntArray("bookmarks"); }
    @Override public HbmFluidType getType(Level level, BlockPos position, ItemStack stack) { return getType(stack, true); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!level.isClientSide && !player.isShiftKeyDown()) {
            var primary = getType(stack, true); var secondary = getType(stack, false);
            setType(stack, secondary, true); setType(stack, primary, false); updateMeta(stack);
            level.playSound(null, player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.25F, 1.25F);
            player.displayClientMessage(secondary.displayName(), true);
        }
        if (level.isClientSide && player.isShiftKeyDown()) com.hbm.item.fluid.client.FluidIdentifierClient.open(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.level.LevelReader level, BlockPos position, Player player) { return true; }
    @Override public boolean hasCraftingRemainingItem(ItemStack stack) { return true; }
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copy(); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.fluid_identifier_multi.info"));
        tooltip.add(Component.literal("   ").append(getType(stack, true).displayName()));
        tooltip.add(Component.translatable("item.fluid_identifier_multi.info2"));
        tooltip.add(Component.literal("   ").append(getType(stack, false).displayName()));
    }
    static void registerPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Control.TYPE, Control.CODEC, (payload, context) -> context.enqueueWork(() -> {
            ItemStack held = context.player().getItemInHand(payload.hand());
            if (!(held.getItem() instanceof FluidIdentifierItem)) return;
            CompoundTag data = payload.data();
            if (data.contains("primary")) setType(held, HbmFluids.fromID(data.getInt("primary")), true);
            if (data.contains("secondary")) setType(held, HbmFluids.fromID(data.getInt("secondary")), false);
            if (data.contains("bookmarks")) CustomData.update(DataComponents.CUSTOM_DATA, held, tag -> tag.putIntArray("bookmarks", Arrays.stream(data.getIntArray("bookmarks")).limit(18).toArray()));
            updateMeta(held);
        }));
    }
    public record Control(InteractionHand hand, CompoundTag data) implements CustomPacketPayload {
        public static final Type<Control> TYPE = new Type<>(HbmReloaded.id("fluid_identifier_control"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Control> CODEC = StreamCodec.of(
                (buffer, payload) -> { buffer.writeEnum(payload.hand()); buffer.writeNbt(payload.data()); },
                buffer -> new Control(buffer.readEnum(InteractionHand.class), java.util.Objects.requireNonNull(buffer.readNbt())));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
}
