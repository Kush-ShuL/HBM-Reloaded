package com.hbm.machine.reactor.rbmk.fluid;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.boiler.RbmkBoilerContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
/** Actual RBMK ModulePortManFluidAdaptive unified groups, reusable by true later irradiation/heatex columns. */
public final class RbmkAdaptivePorts {
    private final BlockPos core;private record Shape(List<BlockPos> positions,List<NodeConnection> connections){}private Shape last;private final Map<HbmFluidType,FluidNode> nodes=new IdentityHashMap<>();
    public RbmkAdaptivePorts(BlockPos core){this.core=core.immutable();}
    public int loaderMode(Level level){if(level.getBlockState(core.below(2)).is(RbmkBoilerContent.LOADER.get()))return 2;return level.getBlockState(core.below()).is(RbmkBoilerContent.LOADER.get())?1:0;}
    private Shape shape(Level level){int mode=loaderMode(level);var top=core.above(RbmkDials.getColumnHeight(level));var positions=new ArrayList<BlockPos>();var connections=new ArrayList<NodeConnection>();positions.add(top);connections.add(new NodeConnection(top.above(),Direction.UP));if(mode!=1){positions.add(core);connections.add(new NodeConnection(core.below(),Direction.DOWN));}if(mode!=0){var loader=core.below(mode);positions.add(loader);for(var direction:Direction.values())connections.add(new NodeConnection(loader.relative(direction),direction));}return new Shape(List.copyOf(positions),List.copyOf(connections));}
    public void update(ServerLevel level,FluidUser owner){var shape=shape(level);if(!shape.equals(last)||nodes.values().stream().anyMatch(FluidNode::isExpired)){remove(level);last=shape;}var active=Collections.newSetFromMap(new IdentityHashMap<HbmFluidType,Boolean>());if(owner instanceof StandardFluidReceiver input)for(var tank:input.getReceivingTanks())active.add(tank.getTankType());if(owner instanceof StandardFluidSender output)for(var tank:output.getSendingTanks())active.add(tank.getTankType());active.remove(HbmFluids.NONE);for(var entry:List.copyOf(nodes.entrySet()))if(!active.contains(entry.getKey())){FluidNetworks.nodespace(level,entry.getKey()).destroyNode(entry.getValue());nodes.remove(entry.getKey());}for(var type:active){var node=nodes.get(type);if(node==null){node=new FluidNode(shape.positions.toArray(BlockPos[]::new)).setConnections(shape.connections.toArray(NodeConnection[]::new));FluidNetworks.nodespace(level,type).createNode(node);nodes.put(type,node);}if(node.hasValidNet()){if(owner instanceof StandardFluidReceiver input&&Arrays.stream(input.getReceivingTanks()).anyMatch(tank->tank.getTankType()==type))node.addReceiver(input);if(owner instanceof StandardFluidSender output&&Arrays.stream(output.getSendingTanks()).anyMatch(tank->tank.getTankType()==type))node.addProvider(output);}}}
    public void remove(ServerLevel level){nodes.forEach((type,node)->FluidNetworks.nodespace(level,type).destroyNode(node));nodes.clear();last=null;}
}
