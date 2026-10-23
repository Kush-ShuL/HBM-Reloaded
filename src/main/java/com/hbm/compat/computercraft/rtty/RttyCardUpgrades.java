package com.hbm.compat.computercraft.rtty;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeType;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Resolved only after the common caller verifies that CC:Tweaked is present. */
public final class RttyCardUpgrades {
    private static final DeferredRegister<UpgradeType<? extends IPocketUpgrade>> POCKET_TYPES = DeferredRegister.create(IPocketUpgrade.typeRegistry(), "hbm");
    private static final DeferredRegister<UpgradeType<? extends ITurtleUpgrade>> TURTLE_TYPES = DeferredRegister.create(ITurtleUpgrade.typeRegistry(), "hbm");
    public static final Supplier<UpgradeType<Pocket>> POCKET = POCKET_TYPES.register("rtty_card", () -> UpgradeType.simpleWithCustomItem(Pocket::new));
    public static final Supplier<UpgradeType<Turtle>> TURTLE = TURTLE_TYPES.register("rtty_card", () -> UpgradeType.simpleWithCustomItem(Turtle::new));
    public static void register(IEventBus bus) {
        POCKET_TYPES.register(bus);
        TURTLE_TYPES.register(bus);
        if (FMLEnvironment.dist == Dist.CLIENT) com.hbm.client.rttycard.RttyCardClient.register(bus);
    }
    public static final class Pocket extends AbstractPocketUpgrade implements RttyCardData {
        public Pocket(ItemStack item) { super(Component.translatable("upgrade.hbm.rtty_card.adjective"), item); }
        @Override public UpgradeType<Pocket> getType() { return POCKET.get(); }
        @Override public IPeripheral createPeripheral(IPocketAccess access) {
            return new RttyCardPeripheral(new RttyCardHost() {
                public ServerLevel level() { return access.getLevel(); }
                public ItemStack card() { var upgrade = access.getUpgrade(); return upgrade == null || upgrade.upgrade() != Pocket.this ? ItemStack.EMPTY : upgrade.getUpgradeItem(); }
                public void save(ItemStack card) { access.setUpgradeData(card.getComponentsPatch()); }
            });
        }
        @Override public void update(IPocketAccess access, IPeripheral peripheral) {
            if (peripheral instanceof RttyCardPeripheral card) card.tick();
        }
    }
    public static final class Turtle extends AbstractTurtleUpgrade implements RttyCardData {
        public Turtle(ItemStack item) { super(TurtleUpgradeType.PERIPHERAL, Component.translatable("upgrade.hbm.rtty_card.adjective"), item); }
        @Override public UpgradeType<Turtle> getType() { return TURTLE.get(); }
        @Override public IPeripheral createPeripheral(ITurtleAccess access, TurtleSide side) {
            if (!(access.getLevel() instanceof ServerLevel)) return null;
            return new RttyCardPeripheral(new RttyCardHost() {
                public ServerLevel level() { return access.getLevel() instanceof ServerLevel level && !access.isRemoved() ? level : null; }
                public ItemStack card() { var upgrade = access.getUpgradeWithData(side); return access.isRemoved() || upgrade == null || upgrade.upgrade() != Turtle.this ? ItemStack.EMPTY : upgrade.getUpgradeItem(); }
                public void save(ItemStack card) { access.setUpgradeData(side, card.getComponentsPatch()); }
            });
        }
        @Override public void update(ITurtleAccess access, TurtleSide side) {
            if (access.getPeripheral(side) instanceof RttyCardPeripheral card) card.tick();
        }
    }
    private RttyCardUpgrades() { }
}
