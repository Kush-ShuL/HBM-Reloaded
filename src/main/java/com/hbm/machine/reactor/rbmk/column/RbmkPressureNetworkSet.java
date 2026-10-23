package com.hbm.machine.reactor.rbmk.column;
import com.hbm.fluid.network.*;
import java.util.*;
/** Actual pre-melt network members survive modern synchronous node invalidation until the original pressure phase. */
final class RbmkPressureNetworkSet extends HashSet<FluidNetMK2> {
    private record Members(Set<FluidNode> links,Set<FluidReceiver> receivers){}
    private final Map<FluidNetMK2,Members> captured=new IdentityHashMap<>();
    @Override public boolean add(FluidNetMK2 network){boolean added=super.add(network);if(added)captured.put(network,new Members(new HashSet<>(network.links),new HashSet<>(network.receiverEntries)));return added;}
    Set<FluidNode> links(FluidNetMK2 network){return captured.get(network).links;}
    Set<FluidReceiver> receivers(FluidNetMK2 network){return captured.get(network).receivers;}
    @Override public void clear(){super.clear();captured.clear();}
}
