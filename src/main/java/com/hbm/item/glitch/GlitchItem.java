package com.hbm.item.glitch;

import com.hbm.LegacyStartup;
import com.hbm.armor.LegacyArmorDurabilityRules;
import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.entity.specialvehicle.EntityBoxcar;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.item.battery.HbmBattery;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.gun.kinetic.KineticAmmo;
import com.hbm.world.meteor.MeteorEntity;
import java.util.List;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.EventHooks;

/** Actual ItemGlitch: consume before all31 equally selected source branches, including its deliberate no-effect battery setters. */
public final class GlitchItem extends Item implements HbmBattery {
    private final Random itemRandom = new Random();
    public GlitchItem() { super(new Properties().durability(1)); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            wear(server, stack, player, hand);
            int choice = itemRandom.nextInt(31);
            switch (choice) {
                case 0, 1, 16, 17, 19, 20, 22, 23 -> message(player, choice);
                case 2 -> player.hurt(HbmDamageSources.source(level, HbmDamageSources.radiation), 1000);
                case 3 -> player.hurt(HbmDamageSources.source(level, HbmDamageSources.boxcar), 1000);
                case 4 -> player.hurt(HbmDamageSources.source(level, HbmDamageSources.blackhole), 1000);
                // Source BlockMeteoriteTreasure does not override getItemDropped: these are block items, not its getDrops loot pool.
                case 5, 6, 7 -> { int count = choice == 5 ? 1 : choice == 6 ? 3 : 10; for (int i = 0; i < count; i++) give(player, "block_meteor_treasure", 1); }
                case 8 -> { player.getInventory().add(new ItemStack(com.hbm.world.structure.loot.StructureLootContent.AMMO_STANDARD.get(), 10)); message(player, choice); }
                case 9 -> give(player, "nuke_advanced_kit", 1);
                case 10 -> give(player, "nuke_starter_kit", 1);
                case 11 -> { var boxcar = new EntityBoxcar(level); boxcar.setPos(player.getX(), player.getY() + 50, player.getZ()); level.addFreshEntity(boxcar); }
                case 12 -> {
                    for (int i = 0; i < 10; i++) {
                        var boxcar = new EntityBoxcar(level);
                        boxcar.setPos(player.getX() + itemRandom.nextGaussian() * 25, player.getY() + 50, player.getZ() + itemRandom.nextGaussian() * 25);
                        level.addFreshEntity(boxcar);
                    }
                }
                case 13 -> { give(player, "gun_heavy_revolver_lilmac", 1); give(player, "bottle_sparkle", 1); give(player, "geiger_counter", 1); message(player, choice); }
                case 14 -> { player.getInventory().dropAll(); ExplosionChaos.igniteAllBlocks(level, (int) player.getX(), (int) player.getY(), (int) player.getZ(), 5); }
                case 15 -> { for (int i = 0; i < 36; i++) player.getInventory().add(new ItemStack(Blocks.DIRT, 64)); }
                case 18 -> { give(player, "gun_maresleg", 1); var ammo = KineticAmmo.G12.toStack(); ammo.setCount(12); player.getInventory().add(ammo); message(player, choice); }
                case 21 -> { give(player, "missile_nuclear", 1); message(player, choice); }
                case 24 -> { player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60 * 20, 9)); message(player, choice); }
                case 25 -> { player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * 20, 9)); message(player, choice); }
                case 26 -> { player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60 * 20, 9)); message(player, choice); }
                case 27 -> { var vortex = new EntityVortex(level, 2.5F); vortex.setPos(player.getX(), player.getY() - 15, player.getZ()); level.addFreshEntity(vortex); }
                case 28 -> { var meteor = new MeteorEntity(level); meteor.setPos(player.getX(), player.getY() + 100, player.getZ()); level.addFreshEntity(meteor); message(player, choice); }
                case 29 -> { ExplosionLarge.spawnBurst(level, player.getX(), player.getY(), player.getZ(), 27, 3); message(player, choice); }
                case 30 -> { give(player, "plate_saturnite", 1); message(player, choice); }
                default -> throw new AssertionError(choice);
            }
        }
        player.inventoryMenu.broadcastChanges();
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    private void wear(ServerLevel level, ItemStack stack, Player player, InteractionHand hand) {
        if (player.getAbilities().instabuild || !stack.isDamageableItem()) return;
        int damage = stack.getDamageValue() + LegacyArmorDurabilityRules.damageAfterUnbreaking(level, stack, 5, player.getRandom());
        if (damage > getMaxDamage(stack)) {
            ItemStack before = stack.copy();
            player.onEquippedItemBroken(this, LivingEntity.getSlotForHand(hand));
            stack.shrink(1);
            EventHooks.onPlayerDestroyItem(player, before, hand);
            stack.setDamageValue(0);
        } else stack.setDamageValue(damage);
    }
    private static void give(Player player, String id, int count) { player.getInventory().add(new ItemStack(HbmContent.item(id), count)); }
    private static void message(Player player, int choice) { player.displayClientMessage(Component.translatable("item.glitch.message." + choice), false); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        text.add(Component.translatable("item.glitch.desc"));
        text.add(Component.empty());
        if (LegacyStartup.polaroidId >= 1 && LegacyStartup.polaroidId <= 18) text.add(Component.translatable("item.glitch.fate." + LegacyStartup.polaroidId));
    }
    @Override public void chargeBattery(ItemStack stack, long amount) { }
    @Override public void setCharge(ItemStack stack, long amount) { }
    @Override public void dischargeBattery(ItemStack stack, long amount) { }
    @Override public long getCharge(ItemStack stack) { return 200; }
    @Override public long getMaxCharge(ItemStack stack) { return 200; }
    @Override public long getChargeRate(ItemStack stack) { return 0; }
    @Override public long getDischargeRate(ItemStack stack) { return 200; }
}
