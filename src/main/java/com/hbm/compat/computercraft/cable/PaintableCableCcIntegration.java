package com.hbm.compat.computercraft.cable;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.network.wired.*;
import java.util.EnumMap;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
/** Optional real CC wired-node adapter. All packet routing/peripheral visibility stays the single native CC backend. */
final class PaintableCableCcIntegration {
    static void register(IEventBus bus){PaintableComputerCableEntity.NETWORK_FACTORY=Host::new;bus.addListener(PaintableCableCcIntegration::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(WiredElementCapability.get(),PaintableComputerCableContent.ENTITY.get(),(cable,side)->{
        if(side==null||!provided(cable,side))return null;var network=cable.network();return network instanceof Host host&&!host.closed?host:null;
    });}
    private static boolean full(Level level,BlockPos pos){return level instanceof ServerLevel server&&server.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
    private static boolean provided(PaintableComputerCableEntity cable,Direction side){
        var level=cable.getLevel();var pos=cable.getBlockPos().relative(side);if(level==null||cable.isRemoved()||!full(level,pos))return false;
        var neighbor=level.getBlockEntity(pos);return !(neighbor instanceof PaintableComputerCableEntity colored)||colored.color()==cable.color();
    }
    private static final class Host implements WiredElement,PaintableCableNetwork {
        private final PaintableComputerCableEntity cable;private final WiredNode node;
        private final EnumMap<Direction,WiredNode> links=new EnumMap<>(Direction.class);
        private boolean dirty=true,closed;
        Host(PaintableComputerCableEntity cable){this.cable=cable;node=ComputerCraftAPI.createWiredNodeForElement(this);}
        @Override public Level getLevel(){return cable.getLevel();}
        @Override public Vec3 getPosition(){return cable.getBlockPos().getCenter();}
        @Override public String getSenderID(){return "hbm:oc_cable_paintable@"+cable.getBlockPos().toShortString();}
        @Override public WiredNode getNode(){return node;}
        @Override public void changed(){dirty=true;}
        @Override public void tick(){
            if(closed||!(cable.getLevel() instanceof ServerLevel level)||cable.isRemoved())return;
            if(!dirty&&level.getGameTime()%20!=0)return;dirty=false;
            for(var side:Direction.values()){
                var position=cable.getBlockPos().relative(side);WiredNode next=null;
                if(!full(level,position))dirty=true;
                else if(provided(cable,side)){
                    var other=level.getCapability(WiredElementCapability.get(),position,side.getOpposite());
                    if(other!=null)next=other.getNode();
                }
                var previous=links.get(side);if(previous!=next){if(previous!=null)node.disconnectFrom(previous);if(next==null)links.remove(side);else links.put(side,next);}
                // Reconfirm only this real native edge. No private packet graph, modem, address or peripheral registry.
                if(next!=null)node.connectTo(next);
            }
        }
        @Override public void close(){if(closed)return;closed=true;node.remove();links.clear();}
    }
    private PaintableCableCcIntegration(){}
}
