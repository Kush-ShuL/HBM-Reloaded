package com.hbm.fluid.interop;

import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.util.*;
import java.util.function.Supplier;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/** A live native endpoint/network view. No standard tank or independent fluid buffer exists. */
final class NativeFluidHandler implements IFluidHandler {
    private final Supplier<List<NativeFluidEndpoint>> endpoints;
    private final Supplier<Set<HbmFluidType>> permitted;
    NativeFluidHandler(Supplier<List<NativeFluidEndpoint>> endpoints,Supplier<Set<HbmFluidType>> permitted){this.endpoints=endpoints;this.permitted=permitted;}
    private boolean enabled(){return FluidInteropConfig.enabled();}
    private List<NativeFluidEndpoint> live(){if(!enabled())return List.of();return endpoints.get();}
    private boolean allows(HbmFluidType type){return type!=null&&type!=HbmFluids.NONE&&permitted.get().contains(type);}
    private List<FluidTank> tanks(){
        var seen=Collections.newSetFromMap(new IdentityHashMap<FluidTank,Boolean>());var result=new ArrayList<FluidTank>();
        for(var endpoint:live())for(var tank:endpoint.owner().getAllTanks())if(tank.getPressure()==0&&allows(tank.getTankType())&&endpoint.valid(tank.getTankType())&&seen.add(tank))result.add(tank);return result;
    }
    @Override public int getTanks(){return tanks().size();}
    @Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int index){var tanks=tanks();if(index<0||index>=tanks.size())return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var tank=tanks.get(index);return FluidInteropMappings.stack(tank.getTankType(),tank.getFill());}
    @Override public int getTankCapacity(int index){var tanks=tanks();return index>=0&&index<tanks.size()?tanks.get(index).getMaxFill():0;}
    @Override public boolean isFluidValid(int index,net.neoforged.neoforge.fluids.FluidStack resource){var type=FluidInteropMappings.fromNeo(resource);var tanks=tanks();return allows(type)&&index>=0&&index<tanks.size()&&tanks.get(index).getTankType()==type;}
    @Override public int fill(net.neoforged.neoforge.fluids.FluidStack resource,FluidAction action){
        if(!enabled()||resource.isEmpty())return 0;var type=FluidInteropMappings.fromNeo(resource);if(!allows(type))return 0;int left=resource.getAmount();
        var visited=Collections.newSetFromMap(new IdentityHashMap<FluidUser,Boolean>());var receiving=new ArrayList<NativeFluidEndpoint>();
        for(var endpoint:live())if(endpoint.valid(type)&&endpoint.owner() instanceof FluidReceiver receiver&&zeroPressure(receiver.getReceivingPressureRange(type))&&visited.add(endpoint.owner()))receiving.add(endpoint);
        receiving.sort(Comparator.comparingInt((NativeFluidEndpoint endpoint)->((FluidReceiver)endpoint.owner()).getFluidPriority().ordinal()).reversed());
        for(var endpoint:receiving){var receiver=(FluidReceiver)endpoint.owner();int amount=(int)Math.min(left,Math.max(0,Math.min(receiver.getDemand(type,0),FluidBridgeBudget.receiving(receiver,type,0))));if(amount<=0)continue;
            int accepted=amount;if(action.execute()){long rejected=receiver.transferFluid(type,0,amount);accepted=(int)Math.clamp(amount-rejected,0,amount);FluidBridgeBudget.received(receiver,type,0,accepted);changed(receiver);}left-=accepted;if(left==0)break;
        }return resource.getAmount()-left;
    }
    @Override public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack resource,FluidAction action){
        if(!enabled()||resource.isEmpty())return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var type=FluidInteropMappings.fromNeo(resource);if(type==null)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var result=drain(type,resource.getAmount(),action);return result.isEmpty()?result:resource.copyWithAmount(result.getAmount());
    }
    @Override public net.neoforged.neoforge.fluids.FluidStack drain(int maximum,FluidAction action){if(!enabled()||maximum<=0)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;for(var tank:tanks()){var result=drain(tank.getTankType(),maximum,action);if(!result.isEmpty())return result;}return net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
    private net.neoforged.neoforge.fluids.FluidStack drain(HbmFluidType type,int maximum,FluidAction action){
        if(!allows(type)||maximum<=0)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;int left=maximum;var visited=Collections.newSetFromMap(new IdentityHashMap<FluidUser,Boolean>());
        for(var endpoint:live())if(endpoint.valid(type)&&endpoint.owner() instanceof FluidProvider provider&&zeroPressure(provider.getProvidingPressureRange(type))&&visited.add(endpoint.owner())){
            int amount=(int)Math.min(left,Math.max(0,Math.min(provider.getFluidAvailable(type,0),FluidBridgeBudget.providing(provider,type,0))));if(amount<=0)continue;if(action.execute()){provider.useUpFluid(type,0,amount);FluidBridgeBudget.provided(provider,type,0,amount);changed(provider);}left-=amount;if(left==0)break;
        }return FluidInteropMappings.stack(type,maximum-left);
    }
    private static boolean zeroPressure(int[] range){return range.length>=2&&range[0]<=0&&range[1]>=0;}
    private static void changed(FluidUser owner){if(owner instanceof net.minecraft.world.level.block.entity.BlockEntity block)block.setChanged();}
}
