package com.hbm.fusion.klystron;
import java.util.*;
/** Source KlystronNetwork/NodeNet has no transfer loop; fusion machines directly distribute over these subscriptions. */
public final class KlystronNetwork {
    public final LinkedHashSet<KlystronReceiver> receivers=new LinkedHashSet<>();
    public final LinkedHashSet<Object> providers=new LinkedHashSet<>();
    private final Set<KlystronNode> nodes=new LinkedHashSet<>();
    private boolean valid=true;
    public boolean isValid(){return valid;}
    void addNode(KlystronNode node){nodes.add(node);node.network=this;receivers.addAll(node.receivers);providers.addAll(node.providers);}
    public void addReceiver(KlystronReceiver receiver){if(valid)receivers.add(receiver);}
    public void addProvider(Object provider){if(valid)providers.add(provider);}
    public void invalidate(){valid=false;for(var node:nodes)if(node.network==this)node.network=null;nodes.clear();receivers.clear();providers.clear();}
}
