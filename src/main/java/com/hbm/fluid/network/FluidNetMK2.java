package com.hbm.fluid.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.interop.FluidBridgeBudget;



import com.hbm.energy.ConnectionPriority;

public class FluidNetMK2 {

    public static final java.util.Random rand = new java.util.Random();
    private boolean valid = true;
    public final java.util.Set<FluidNode> links = new java.util.LinkedHashSet<>();
    public final java.util.Set<FluidProvider> providerEntries = new java.util.HashSet<>();
    public final java.util.Set<FluidReceiver> receiverEntries = new java.util.HashSet<>();
    public boolean isValid() { return valid; }
    public boolean isProvider(FluidProvider value) { return providerEntries.contains(value); }
    public boolean isSubscribed(FluidReceiver value) { return receiverEntries.contains(value); }
    public void addProvider(FluidProvider value) { providerEntries.add(value); }
    public void removeProvider(FluidProvider value) { providerEntries.remove(value); }
    public void addReceiver(FluidReceiver value) { receiverEntries.add(value); }
    public void removeReceiver(FluidReceiver value) { receiverEntries.remove(value); }
    void addNode(FluidNode node) {
        links.add(node); node.setNetwork(this);
        providerEntries.addAll(node.providers()); receiverEntries.addAll(node.receivers());
    }
    public void invalidate() {
        valid = false;
        for (var node : links) if (node.network() == this) node.setNetwork(null);
        links.clear(); providerEntries.clear(); receiverEntries.clear();
    }
    private static boolean isBadLink(FluidUser user) { return !user.isLoaded(); }
    private record Entry<K,V>(K getKey, V getValue) {}


	public long fluidTracker = 0L;
	
	protected static int timeout = 3_000;
	protected HbmFluidType type;
	
	public FluidNetMK2(HbmFluidType type) {
		this.type = type;
		for(int i = 0; i < FluidUser.HIGHEST_VALID_PRESSURE + 1; i++) providers[i] = new ArrayList();
		for(int i = 0; i < FluidUser.HIGHEST_VALID_PRESSURE + 1; i++) for(int j = 0; j < ConnectionPriority.values().length; j++) receivers[i][j] = new ArrayList();
	}
	
	public void resetTrackers() { this.fluidTracker = 0; }
	
	public void update() {
		
		if(providerEntries.isEmpty()) return;
		if(receiverEntries.isEmpty()) return;

		setupFluidProviders();
		setupFluidReceivers();
		transferFluid();
		
		cleanUp();
	}
	
	//this sucks ass, but it makes the code just a smidge more structured
	public long totalFluidInNetwork;
	public long[] fluidAvailable = new long[FluidUser.HIGHEST_VALID_PRESSURE + 1];
	public List<Entry<FluidProvider, Long>>[] providers = new ArrayList[FluidUser.HIGHEST_VALID_PRESSURE + 1];
	public long[][] fluidDemand = new long[FluidUser.HIGHEST_VALID_PRESSURE + 1][ConnectionPriority.values().length];
	public List<Entry<FluidReceiver, Long>>[][] receivers = new ArrayList[FluidUser.HIGHEST_VALID_PRESSURE + 1][ConnectionPriority.values().length];
	public long[] transfered = new long[FluidUser.HIGHEST_VALID_PRESSURE + 1];
	
	public void setupFluidProviders() {
		
		totalFluidInNetwork = 0;
		
		Iterator<FluidProvider> iterator = providerEntries.iterator();
		
		while(iterator.hasNext()) {
			FluidProvider provider = iterator.next();
			if(isBadLink(provider)) { iterator.remove(); continue; }
			int[] pressureRange = provider.getProvidingPressureRange(type);
			for(int p = pressureRange[0]; p <= pressureRange[1]; p++) {
				long available = Math.min(provider.getFluidAvailable(type, p), FluidBridgeBudget.providing(provider,type,p));
				providers[p].add(new Entry(provider, available));
				fluidAvailable[p] += available;
				totalFluidInNetwork += provider.getFluidAvailable(type, p);
			}
		}
	}
	
	public void setupFluidReceivers() {
		Iterator<FluidReceiver> iterator = receiverEntries.iterator();
		
		while(iterator.hasNext()) {
			FluidReceiver receiver = iterator.next();
			if(isBadLink(receiver)) { iterator.remove(); continue; }
			int[] pressureRange = receiver.getReceivingPressureRange(type);
			for(int p = pressureRange[0]; p <= pressureRange[1]; p++) {
				long required = Math.min(receiver.getDemand(type, p), FluidBridgeBudget.receiving(receiver,type,p));
				int priority = receiver.getFluidPriority().ordinal();
				receivers[p][priority].add(new Entry(receiver, required));
				fluidDemand[p][priority] += required;
			}
		}
	}
	
	public void transferFluid() {

		long[] received = new long[FluidUser.HIGHEST_VALID_PRESSURE + 1];
		long[] notAccountedFor = new long[FluidUser.HIGHEST_VALID_PRESSURE + 1];
		
		for(int p = 0; p <= FluidUser.HIGHEST_VALID_PRESSURE; p++) { // if the pressure range were ever to increase, we might have to rethink this
			
			long totalAvailable = fluidAvailable[p];
			
			for(int i = ConnectionPriority.values().length - 1; i >= 0; i--) {
				
				long toTransfer = Math.min(fluidDemand[p][i], totalAvailable);
				if(toTransfer <= 0) continue;

				long priorityDemand = fluidDemand[p][i];
				
				for(Entry<FluidReceiver, Long> entry : receivers[p][i]) {
					double weight = (double) entry.getValue() / (double) (priorityDemand);
					long toSend = (long) Math.max(toTransfer * weight, 0D);
					toSend -= entry.getKey().transferFluid(type, p, toSend);
					FluidBridgeBudget.received(entry.getKey(),type,p,toSend);
					received[p] += toSend;
				}
				
				totalAvailable -= received[p];
			}
			
			notAccountedFor[p] = received[p];
		}
		
		for(int p = 0; p <= FluidUser.HIGHEST_VALID_PRESSURE; p++) {

			for(Entry<FluidProvider, Long> entry : providers[p]) {
				double weight = (double) entry.getValue() / (double) fluidAvailable[p];
				long toUse = (long) Math.max(received[p] * weight, 0D);
				entry.getKey().useUpFluid(type, p, toUse);
				FluidBridgeBudget.provided(entry.getKey(),type,p,toUse);
				notAccountedFor[p] -= toUse;
			}
		}
		
		for(int p = 0; p <= FluidUser.HIGHEST_VALID_PRESSURE; p++) {
			
			int iterationsLeft = 100;
			while(iterationsLeft > 0 && notAccountedFor[p] > 0 && providers[p].size() > 0) {
				iterationsLeft--;
				
				Entry<FluidProvider, Long> selected = providers[p].get(rand.nextInt(providers[p].size()));
				FluidProvider scapegoat = selected.getKey();
				
				long toUse = Math.min(notAccountedFor[p], scapegoat.getFluidAvailable(type, p));
				scapegoat.useUpFluid(type, p, toUse);
					FluidBridgeBudget.provided(scapegoat,type,p,toUse);
				notAccountedFor[p] -= toUse;
			}
		}
		
		Iterator<FluidProvider> iterator = providerEntries.iterator();
		long totalAfterTransfer = 0;
		
		while(iterator.hasNext()) {
			FluidProvider provider = iterator.next();
			for(int p = 0; p <= FluidUser.HIGHEST_VALID_PRESSURE; p++) {
				totalAfterTransfer += provider.getFluidAvailable(type, p);
			}
		}
		
		this.fluidTracker += this.totalFluidInNetwork - totalAfterTransfer;
	}
	
	public void cleanUp() {
		for(int i = 0; i < FluidUser.HIGHEST_VALID_PRESSURE + 1; i++) {
			fluidAvailable[i] = 0;
			providers[i].clear();
			transfered[i] = 0;
			
			for(int j = 0; j < ConnectionPriority.values().length; j++) {
				fluidDemand[i][j] = 0;
				receivers[i][j].clear();
			}
		}
	}
}
