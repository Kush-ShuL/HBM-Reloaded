package com.hbm.item.meteorgavel;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.item.legacysword.LegacySwordBlocking;
import com.hbm.item.medicine.MedicineEffects;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/** Real WeaponSpecial lead/diamond branches and ItemSwordAbility Mese branch; unrelated special weapons remain pending. */
public final class LegacyGavelItem extends SwordItem implements LegacySwordBlocking,SourceGavelAppearance {
    public enum Kind { LEAD, DIAMOND, MESE }
    private final Kind kind;
    private final GavelAbilities abilities;
    public LegacyGavelItem(Kind kind) {
        super(material(kind), new GavelProperties().attributes(attributes(kind)));
        this.kind = kind; abilities = kind == Kind.MESE ? new GavelAbilities() : null;
    }
    private static Tier material(Kind kind) { return switch (kind) { case LEAD -> GavelMaterials.STEEL; case DIAMOND -> Tiers.DIAMOND; case MESE -> GavelMaterials.MESE; }; }
    private static final class GavelProperties extends Item.Properties {
        // Old maxDamage0 means not damageable. Modern MAX_DAMAGE has a POSITIVE_INT codec;
        // keep the component absent instead of installing an invalid max_damage=0 default.
        @Override public Item.Properties durability(int uses) { return uses == 0 ? stacksTo(1) : super.durability(uses); }
    }
    private static ItemAttributeModifiers attributes(Kind kind) {
        var attributes = SwordItem.createAttributes(material(kind), kind == Kind.MESE ? 250F : 4F, -2.4F);
        return kind != Kind.MESE ? attributes : attributes.withModifierAdded(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm", "mese_gavel_movement"), 1.5D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }
    public Kind kind() { return kind; }
    @Override public String sourceGavelTexture(){return kind.name().toLowerCase(java.util.Locale.ROOT);}
    @Override public boolean sourceLargeGavel(){return kind==Kind.MESE;}
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + switch (kind) { case LEAD -> "lead_gavel"; case DIAMOND -> "diamond_gavel"; case MESE -> "mese_gavel"; } + ".name"); }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BLOCK; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResultHolder.consume(player.getItemInHand(hand)); }
    @Override public boolean canPerformAction(ItemStack stack, ItemAbility action) { return !action.equals(ItemAbilities.SWORD_SWEEP) && !action.equals(ItemAbilities.SHIELD_BLOCK) && super.canPerformAction(stack, action); }
    @Override public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) return 15F;
        return switch (LegacyMaterials.of(state).name()) { case "plants", "vine", "coral", "leaves", "gourd" -> 1.5F; default -> 1F; };
    }
    @Override public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) { return state.is(Blocks.COBWEB); }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (kind == Kind.MESE) {
            if (!attacker.level().isClientSide && attacker instanceof Player player) { sound(victim); abilities.onHit(player, victim); }
            // Source hitEntity returns true, then damageItem(1) has no effect on maxDamage0.
            return true;
        }
        if (kind == Kind.LEAD) { sound(victim); victim.addEffect(new MobEffectInstance(MedicineEffects.LEAD, 15 * 20, 4)); }
        else { victim.setHealth(victim.getHealth() - victim.getMaxHealth() / 3F); sound(victim); }
        // WeaponSpecial does not call super.hitEntity and returnsfalse: no hit durability or extra branch is invented.
        return false;
    }
    @Override public void postHurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) { }
    private static void sound(LivingEntity victim) {
        var level = victim.level(); var sound = HbmSounds.get("weapon.whack");
        if (level.isClientSide) level.playLocalSound(victim.getX(), victim.getY(), victim.getZ(), sound, SoundSource.PLAYERS, 3F, 1F, false);
        else level.playSound(null, victim.getX(), victim.getY(), victim.getZ(), sound, SoundSource.PLAYERS, 3F, 1F);
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity holder, int slot, boolean selected) {
        if (kind == Kind.MESE || !(holder instanceof ServerPlayer player)) return;
        // WeaponSpecial.onUpdate has no this== check. Its ArmorUtil conditions require another weapon in main hand.
        if (equipped(player, "jackt", "shimmer_sledge")) awardExisting(player, "fiend");
        else if (equipped(player, "jackt2", "shimmer_axe")) awardExisting(player, "fiend2");
    }
    private static boolean equipped(Player player, String chest, String held) {
        var registry = net.minecraft.core.registries.BuiltInRegistries.ITEM;
        var armor = registry.getOptional(ResourceLocation.fromNamespaceAndPath("hbm", chest));
        var weapon = registry.getOptional(ResourceLocation.fromNamespaceAndPath("hbm", held));
        return armor.isPresent() && weapon.isPresent() && player.getItemBySlot(EquipmentSlot.CHEST).is(armor.get()) && player.getMainHandItem().is(weapon.get());
    }
    private static void awardExisting(ServerPlayer player, String id) {
        var advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("hbm", id));
        if (advancement != null) for (String criterion : advancement.value().criteria().keySet()) player.getAdvancements().award(advancement, criterion);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        switch (kind) {
            case LEAD -> tooltip.add(Component.translatable("tooltip.hbm.gavel.lead"));
            case DIAMOND -> { tooltip.add(Component.translatable("tooltip.hbm.gavel.diamond_joke")); tooltip.add(Component.empty()); tooltip.add(Component.translatable("tooltip.hbm.gavel.diamond_damage").withStyle(ChatFormatting.BLUE)); }
            case MESE -> abilities.tooltip(tooltip);
        }
    }

    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,net.minecraft.core.BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,2,miner);return true;}
}
