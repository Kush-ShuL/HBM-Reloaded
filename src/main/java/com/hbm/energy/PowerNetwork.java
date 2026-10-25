package com.hbm.energy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Rewritten X5846 PowerNetMK2: priority demand, proportional supply and exact HE
 * accounting. Storage, rates, individual transfers and aggregate totals are
 * arbitrary-precision integers; original long machines use exact adapters.
 */
public final class PowerNetwork {
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    // Both source networks inherited the same NodeNet random for remainder compensation.
    private static final java.util.Random SOURCE_RANDOM = com.hbm.fluid.network.FluidNetMK2.rand;
    private final Set<EnergyProvider> providers = new LinkedHashSet<>();
    private final Set<EnergyReceiver> receivers = new LinkedHashSet<>();
    private final Set<PowerLink> nodes = new LinkedHashSet<>();
    private BigInteger transferred = BigInteger.ZERO;
    private boolean valid = true;

    public void addProvider(EnergyProvider provider) { requireValid(); providers.add(Objects.requireNonNull(provider)); }
    public void removeProvider(EnergyProvider provider) { providers.remove(provider); }
    public boolean isProvider(EnergyProvider provider) { return providers.contains(provider); }
    public void addReceiver(EnergyReceiver receiver) { requireValid(); receivers.add(Objects.requireNonNull(receiver)); }
    public void removeReceiver(EnergyReceiver receiver) { receivers.remove(receiver); }
    public boolean isSubscribed(EnergyReceiver receiver) { return receivers.contains(receiver); }
    public boolean isValid() { return valid; }
    public Set<PowerLink> nodes() { return Collections.unmodifiableSet(nodes); }
    /** Read-only original receiverEntries view for a network separator demand survey. */
    public Set<EnergyReceiver> receivers() { return Collections.unmodifiableSet(receivers); }
    /** Read-only original providerEntries view for the real cable-network diagnostic tool. */
    public Set<EnergyProvider> providers() { return Collections.unmodifiableSet(providers); }
    public void resetTrackers() { transferred = BigInteger.ZERO; }
    public BigInteger getTransferredExact() { return transferred; }

    /** A long telemetry view saturates; stored and transferred HE never saturate. */
    public long getEnergyTracker() { return transferred.max(LONG_MIN).min(LONG_MAX).longValueExact(); }

    void addNode(PowerLink node) {
        requireValid();
        nodes.add(node);
        node.setNetwork(this);
        providers.addAll(node.providers());
        receivers.addAll(node.receivers());
    }

    public void invalidate() {
        valid = false;
        for (PowerLink node : nodes) if (node.network() == this) node.setNetwork(null);
        nodes.clear();
        providers.clear();
        receivers.clear();
    }

    public void update() {
        requireValid();
        providers.removeIf(provider -> !provider.isLoaded());
        receivers.removeIf(receiver -> !receiver.isLoaded());
        if (providers.isEmpty() || receivers.isEmpty()) return;
        List<Offer<EnergyProvider>> sources = new ArrayList<>();
        BigInteger available = BigInteger.ZERO;
        BigInteger powerBefore = BigInteger.ZERO;
        for (EnergyProvider provider : providers) {
            BigInteger stored = provider.getPowerExact();
            BigInteger offer = stored.min(provider.getProviderSpeedExact()).max(BigInteger.ZERO);
            if (offer.signum() > 0) {
                sources.add(new Offer<>(provider, offer));
                available = available.add(offer);
                powerBefore = powerBefore.add(stored);
            }
        }
        var demands = new EnumMap<ConnectionPriority, List<Offer<EnergyReceiver>>>(ConnectionPriority.class);
        for (ConnectionPriority priority : ConnectionPriority.values()) demands.put(priority, new ArrayList<>());
        BigInteger totalDemand = BigInteger.ZERO;
        for (EnergyReceiver receiver : receivers) {
            BigInteger stored = receiver.getPowerExact();
            BigInteger capacity = receiver.getMaxPowerExact();
            if (capacity.signum() < 0 || capacity.compareTo(stored) < 0) throw new IllegalStateException("Invalid receiver HE storage");
            // Repay original machines' final-tick deficits without narrowing their HE.
            BigInteger demand = capacity.subtract(stored).min(receiver.getReceiverSpeedExact().max(BigInteger.ZERO));
            if (demand.signum() > 0) {
                demands.get(receiver.getPriority()).add(new Offer<>(receiver, demand));
                totalDemand = totalDemand.add(demand);
            }
        }
        BigInteger toTransfer = available.min(totalDemand);
        BigInteger consumed = BigInteger.ZERO;
        var priorities = ConnectionPriority.values();
        for (int i = priorities.length - 1; i >= 0; i--) {
            var targets = demands.get(priorities[i]);
            BigInteger priorityDemand = sum(targets);
            for (var target : targets) {
                // Source receivers get proportional floors, without a remainder round.
                BigInteger sent = toTransfer.max(BigInteger.ZERO).multiply(target.amount()).divide(priorityDemand).min(target.amount());
                BigInteger rejected = target.participant().transferPowerExact(sent);
                if (rejected.signum() < 0 || rejected.compareTo(sent) > 0) throw new IllegalStateException("Receiver returned invalid rejected HE");
                consumed = consumed.add(sent.subtract(rejected));
            }
            // This is the source cumulative subtraction, including empty priorities.
            toTransfer = toTransfer.subtract(consumed);
        }
        BigInteger leftover = consumed;
        for (var source : sources) {
            BigInteger debit = consumed.multiply(source.amount()).divide(available);
            source.participant().usePowerExact(debit);
            leftover = leftover.subtract(debit);
        }
        for (int attempts = 100; attempts > 0 && leftover.signum() > 0 && !sources.isEmpty(); attempts--) {
            var provider = sources.get(SOURCE_RANDOM.nextInt(sources.size())).participant();
            BigInteger debit = leftover.min(provider.getPowerExact().max(BigInteger.ZERO));
            provider.usePowerExact(debit);
            leftover = leftover.subtract(debit);
        }
        BigInteger powerAfter = BigInteger.ZERO;
        for (var provider : providers) powerAfter = powerAfter.add(provider.getPowerExact().max(BigInteger.ZERO));
        // Source tracker observes provider balances, rather than assuming all debit calls succeeded.
        transferred = transferred.add(powerBefore.subtract(powerAfter));
    }

    private static <T> BigInteger sum(List<Offer<T>> entries) {
        BigInteger result = BigInteger.ZERO;
        for (var entry : entries) result = result.add(entry.amount());
        return result;
    }

    private void requireValid() {
        if (!valid) throw new IllegalStateException("Expired HE network");
    }

    private record Offer<T>(T participant, BigInteger amount) {}
}
