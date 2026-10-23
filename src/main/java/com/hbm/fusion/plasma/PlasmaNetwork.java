package com.hbm.fusion.plasma;
import java.util.*;
/** Source PlasmaNetwork/NodeNet has no transfer loop; fusion machines directly distribute over these subscriptions. */
public final class PlasmaNetwork {
    public final LinkedHashSet<FusionPowerReceiver> receivers=new LinkedHashSet<>();
    public final LinkedHashSet<Object> providers=new LinkedHashSet<>();
    private final Set<PlasmaNode> nodes=new LinkedHashSet<>();
    private boolean valid=true;
    public boolean isValid(){return valid;}
    void addNode(PlasmaNode node){nodes.add(node);node.network=this;receivers.addAll(node.receivers);providers.addAll(node.providers);}
    public void addReceiver(FusionPowerReceiver receiver){if(valid)receivers.add(receiver);}
    public void addProvider(Object provider){if(valid)providers.add(provider);}
    public void invalidate(){valid=false;for(var node:nodes)if(node.network==this)node.network=null;nodes.clear();receivers.clear();providers.clear();}
}
