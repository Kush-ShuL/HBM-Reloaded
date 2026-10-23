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
    private final Set<EnergyProvider> providers = new LinkedHashSet<>();
    private final Set<EnergyReceiver> receivers = new LinkedHashSet<>();
    private final Set<PowerLink> nodes = new LinkedHashSet<>();
    private BigInteger transferred = BigInteger.ZERO;
    private boolean valid = true;
    private int remainderCursor;

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
    public long getEnergyTracker() { return transferred.min(LONG_MAX).longValueExact(); }

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
        List<Offer<EnergyProvider>> sources = new ArrayList<>();
        BigInteger available = BigInteger.ZERO;
        for (EnergyProvider provider : providers) {
            BigInteger offer = provider.getPowerExact().min(provider.getProviderSpeedExact()).max(BigInteger.ZERO);
            if (offer.signum() > 0) {
                sources.add(new Offer<>(provider, offer));
                available = available.add(offer);
            }
        }
        if (sources.isEmpty()) return;
        var demands = new EnumMap<ConnectionPriority, List<Offer<EnergyReceiver>>>(ConnectionPriority.class);
        for (ConnectionPriority priority : ConnectionPriority.values()) demands.put(priority, new ArrayList<>());
        for (EnergyReceiver receiver : receivers) {
            BigInteger stored = receiver.getPowerExact();
            BigInteger capacity = receiver.getMaxPowerExact();
            if (capacity.signum() < 0 || capacity.compareTo(stored) < 0) throw new IllegalStateException("Invalid receiver HE storage");
            // Original machines can pay their last running tick from a positive
            // balance smaller than the tick cost (e.g. shredder: 1 - 5 = -4).
            // Repay that deficit as well, without integer narrowing or wrapping.
            BigInteger demand = capacity.subtract(stored).min(receiver.getReceiverSpeedExact().max(BigInteger.ZERO));
            if (demand.signum() > 0) demands.get(receiver.getPriority()).add(new Offer<>(receiver, demand));
        }
        BigInteger consumed = BigInteger.ZERO;
        var priorities = ConnectionPriority.values();
        for (int i = priorities.length - 1; i >= 0 && available.signum() > 0; i--) {
            var targets = demands.get(priorities[i]);
            BigInteger totalDemand = sum(targets);
            if (totalDemand.signum() == 0) continue;
            BigInteger[] shares = allocate(available.min(totalDemand), targets, totalDemand);
            BigInteger groupConsumed = BigInteger.ZERO;
            for (int j = 0; j < targets.size(); j++) {
                BigInteger sent = shares[j];
                if (sent.signum() == 0) continue;
                BigInteger rejected = targets.get(j).participant().transferPowerExact(sent);
                if (rejected.signum() < 0 || rejected.compareTo(sent) > 0) throw new IllegalStateException("Receiver returned invalid rejected HE");
                groupConsumed = groupConsumed.add(sent.subtract(rejected));
            }
            available = available.subtract(groupConsumed);
            consumed = consumed.add(groupConsumed);
        }
        if (consumed.signum() > 0) {
            BigInteger[] debits = allocate(consumed, sources, sum(sources));
            for (int i = 0; i < sources.size(); i++) {
                if (debits[i].signum() > 0) sources.get(i).participant().usePowerExact(debits[i]);
            }
            transferred = transferred.add(consumed);
        }
        remainderCursor = remainderCursor == Integer.MAX_VALUE ? 0 : remainderCursor + 1;
    }

    private <T> BigInteger[] allocate(BigInteger budget, List<Offer<T>> entries, BigInteger total) {
        BigInteger[] shares = new BigInteger[entries.size()];
        BigInteger used = BigInteger.ZERO;
        for (int i = 0; i < entries.size(); i++) {
            shares[i] = budget.multiply(entries.get(i).amount()).divide(total);
            used = used.add(shares[i]);
        }
        int remainder = budget.subtract(used).intValueExact();
        for (int j = 0; j < entries.size() && remainder > 0; j++) {
            int i = (int) (((long) remainderCursor + j) % entries.size());
            if (shares[i].compareTo(entries.get(i).amount()) < 0) {
                shares[i] = shares[i].add(BigInteger.ONE);
                remainder--;
            }
        }
        if (remainder != 0) throw new IllegalStateException("Unallocated proportional HE remainder");
        return shares;
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
