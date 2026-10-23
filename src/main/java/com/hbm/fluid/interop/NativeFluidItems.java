package com.hbm.fluid.interop;

import com.hbm.fluid.*;
import com.hbm.item.fluid.InfiniteFluidItem;
import com.hbm.item.tool.BlowtorchItem;
import com.hbm.machine.barrel.BarrelItem;
import com.hbm.machine.fluidtank.IndustrialFluidTankItem;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.fluids.capability.*;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/** Standard item operations mutate only the original item components and native tank NBT. */
final class NativeFluidItems {
    static IFluidHandlerItem create(ItemStack stack){
        if(!FluidInteropConfig.enabled()||stack.isEmpty())return null;
        if(stack.getItem() instanceof FillableItem item)return new Fillable(stack,item);
        if(stack.getItem() instanceof InfiniteFluidItem item)return new Infinite(stack,item);
        if(stack.getItem() instanceof BarrelItem||stack.getItem() instanceof IndustrialFluidTankItem)return new CarriedTank(stack);
        for(var row:FluidContainerRegistry.allContainers)if(FluidContainerRegistry.sameItem(row.fullContainer(),stack)||FluidContainerRegistry.sameItem(row.emptyContainer(),stack))return new Fixed(stack);
        return null;
    }
    private abstract static class ItemHandler implements IFluidHandlerItem {
        ItemStack container;
        ItemHandler(ItemStack stack){container=stack;}
        boolean enabled(){return FluidInteropConfig.enabled()&&!container.isEmpty();}
        boolean single(){return enabled()&&container.getCount()==1;}
        @Override public ItemStack getContainer(){return container;}
        @Override public int getTanks(){return enabled()?1:0;}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack request,FluidAction action){
            var type=FluidInteropMappings.fromNeo(request);if(type==null)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            var stored=getFluidInTank(0);return !stored.isEmpty()&&FluidInteropMappings.fromNeo(stored)==type?alias(request,drain(request.getAmount(),action)):net.neoforged.neoforge.fluids.FluidStack.EMPTY;
        }
        static net.neoforged.neoforge.fluids.FluidStack alias(net.neoforged.neoforge.fluids.FluidStack requested,net.neoforged.neoforge.fluids.FluidStack actual){return actual.isEmpty()?actual:requested.copyWithAmount(actual.getAmount());}
        void replace(ItemStack stack){var name=container.get(DataComponents.CUSTOM_NAME);container=stack.copy();if(name!=null&&!container.isEmpty())container.set(DataComponents.CUSTOM_NAME,name);}
    }
    private static final class Fixed extends ItemHandler {
        Fixed(ItemStack stack){super(stack);}
        FluidContainerRegistry.Container full(){for(var row:FluidContainerRegistry.allContainers)if(FluidContainerRegistry.sameItem(row.fullContainer(),container))return row;return null;}
        @Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int index){var row=enabled()&&index==0?full():null;return row==null?net.neoforged.neoforge.fluids.FluidStack.EMPTY:FluidInteropMappings.stack(row.type(),row.content());}
        @Override public int getTankCapacity(int index){if(!enabled()||index!=0)return 0;var full=full();if(full!=null)return full.content();int max=0;for(var row:FluidContainerRegistry.allContainers)if(FluidContainerRegistry.sameItem(row.emptyContainer(),container))max=Math.max(max,row.content());return max;}
        @Override public boolean isFluidValid(int index,net.neoforged.neoforge.fluids.FluidStack resource){if(!enabled()||index!=0)return false;var type=FluidInteropMappings.fromNeo(resource);return type!=null&&FluidContainerRegistry.getContainer(type,container)!=null;}
        @Override public int fill(net.neoforged.neoforge.fluids.FluidStack resource,FluidAction action){if(!single()||full()!=null)return 0;var type=FluidInteropMappings.fromNeo(resource);var row=type==null?null:FluidContainerRegistry.getContainer(type,container);if(row==null||row.content()<=0||resource.getAmount()<row.content())return 0;if(action.execute())replace(row.fullContainer());return row.content();}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(int maximum,FluidAction action){if(!single())return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var row=full();if(row==null||row.content()<=0||maximum<row.content())return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var result=FluidInteropMappings.stack(row.type(),row.content());if(!result.isEmpty()&&action.execute())replace(row.emptyContainer());return result;}
    }
    private static final class Fillable extends ItemHandler {
        private final FillableItem item;
        Fillable(ItemStack stack,FillableItem item){super(stack);this.item=item;}
        List<HbmFluidType> types(){if(item instanceof BlowtorchItem torch)return torch.acetylene?List.of(HbmFluids.UNSATURATEDS,HbmFluids.OXYGEN):List.of(HbmFluids.GAS);var result=new ArrayList<HbmFluidType>();var copy=container.copy();for(var type:HbmFluids.getAll())if(type!=HbmFluids.NONE&&(item.acceptsFluid(type,copy)||item.providesFluid(type,copy)))result.add(type);return result;}
        int fill(HbmFluidType type){return item instanceof BlowtorchItem torch?torch.getFill(container.copy(),type):item.getFirstFluidType(container.copy())==type?Math.max(0,item.getFill(container.copy())):0;}
        @Override public int getTanks(){return enabled()?types().size():0;}
        @Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int index){var types=types();return enabled()&&index>=0&&index<types.size()?FluidInteropMappings.stack(types.get(index),fill(types.get(index))):net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
        @Override public int getTankCapacity(int index){var types=types();if(!enabled()||index<0||index>=types.size())return 0;var type=types.get(index);if(item instanceof BlowtorchItem torch)return torch.getMaxFill(type);var copy=container.copy();return (int)Math.min(Integer.MAX_VALUE,(long)fill(type)+Math.max(0,(long)Integer.MAX_VALUE-item.tryFill(type,Integer.MAX_VALUE,copy)));}
        @Override public boolean isFluidValid(int index,net.neoforged.neoforge.fluids.FluidStack resource){var type=FluidInteropMappings.fromNeo(resource);var types=types();return enabled()&&type!=null&&index>=0&&index<types.size()&&types.get(index)==type&&item.acceptsFluid(type,container.copy());}
        @Override public int fill(net.neoforged.neoforge.fluids.FluidStack resource,FluidAction action){if(!single())return 0;var type=FluidInteropMappings.fromNeo(resource);if(type==null||!item.acceptsFluid(type,container.copy()))return 0;var copy=container.copy();int amount=(int)Math.clamp((long)resource.getAmount()-item.tryFill(type,resource.getAmount(),copy),0,resource.getAmount());if(amount>0&&action.execute())container=copy;return amount;}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(int maximum,FluidAction action){if(!single()||maximum<=0)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;for(var type:types())if(item.providesFluid(type,container.copy())){var copy=container.copy();int amount=Math.clamp(item.tryEmpty(type,maximum,copy),0,maximum);var result=FluidInteropMappings.stack(type,amount);if(!result.isEmpty()){if(action.execute())container=copy;return result;}}return net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack request,FluidAction action){if(!single())return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var type=FluidInteropMappings.fromNeo(request);if(type==null||!item.providesFluid(type,container.copy()))return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var copy=container.copy();int amount=Math.clamp(item.tryEmpty(type,request.getAmount(),copy),0,request.getAmount());var result=request.copyWithAmount(amount);if(!result.isEmpty()&&action.execute())container=copy;return result;}
    }
    private static final class CarriedTank extends ItemHandler {
        CarriedTank(ItemStack stack){super(stack);}
        CompoundTag data(){var custom=container.get(DataComponents.CUSTOM_DATA);return custom==null?new CompoundTag():custom.copyTag().getCompound("persistent");}
        FluidTank tank(){var result=new FluidTank(HbmFluids.NONE,0);result.readFromNBT(data(),"tank");return result;}
        boolean active(FluidTank tank){return enabled()&&tank.getTankType()!=HbmFluids.NONE&&tank.getPressure()==0&&!data().getBoolean("hasExploded");}
        void save(FluidTank tank){var custom=container.get(DataComponents.CUSTOM_DATA);var tag=custom==null?new CompoundTag():custom.copyTag();var persistent=tag.getCompound("persistent");tank.writeToNBT(persistent,"tank");tag.put("persistent",persistent);container=container.copy();container.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
        int rate(FluidTank tank,boolean input){return container.getItem() instanceof IndustrialFluidTankItem?Math.max(500,(input?tank.getMaxFill()-tank.getFill():tank.getFill())/100):Integer.MAX_VALUE;}
        @Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int index){var tank=tank();return enabled()&&index==0&&tank.getPressure()==0?FluidInteropMappings.stack(tank.getTankType(),tank.getFill()):net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
        @Override public int getTankCapacity(int index){return enabled()&&index==0?tank().getMaxFill():0;}
        @Override public boolean isFluidValid(int index,net.neoforged.neoforge.fluids.FluidStack resource){var tank=tank();return active(tank)&&index==0&&FluidInteropMappings.fromNeo(resource)==tank.getTankType();}
        @Override public int fill(net.neoforged.neoforge.fluids.FluidStack resource,FluidAction action){if(!single())return 0;var tank=tank();int mode=data().getShort("mode");if(!active(tank)||mode==2||mode==3||FluidInteropMappings.fromNeo(resource)!=tank.getTankType())return 0;int amount=Math.max(0,Math.min(resource.getAmount(),Math.min(tank.getMaxFill()-tank.getFill(),rate(tank,true))));if(amount>0&&action.execute()){tank.setFill(tank.getFill()+amount);save(tank);}return amount;}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(int maximum,FluidAction action){if(!single()||maximum<=0)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;var tank=tank();int mode=data().getShort("mode");if(!active(tank)||mode==0||mode==3)return net.neoforged.neoforge.fluids.FluidStack.EMPTY;int amount=Math.min(maximum,Math.min(tank.getFill(),rate(tank,false)));var result=FluidInteropMappings.stack(tank.getTankType(),amount);if(!result.isEmpty()&&action.execute()){tank.setFill(tank.getFill()-amount);save(tank);}return result;}
    }
    private static final class Infinite extends ItemHandler {
        private final InfiniteFluidItem item;
        Infinite(ItemStack stack,InfiniteFluidItem item){super(stack);this.item=item;}
        boolean matches(HbmFluidType type){return type!=null&&(item.getType()==null||item.getType()==type);}
        boolean chance(FluidAction action){return action.simulate()||item.getChance()<=1||java.util.concurrent.ThreadLocalRandom.current().nextInt(item.getChance())==0;}
        @Override public int getTankCapacity(int index){return enabled()&&index==0?item.getAmount():0;}
        @Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int index){return enabled()&&index==0?FluidInteropMappings.stack(item.getType(),item.getAmount()):net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
        @Override public boolean isFluidValid(int index,net.neoforged.neoforge.fluids.FluidStack resource){return enabled()&&index==0&&matches(FluidInteropMappings.fromNeo(resource));}
        @Override public int fill(net.neoforged.neoforge.fluids.FluidStack resource,FluidAction action){return single()&&matches(FluidInteropMappings.fromNeo(resource))&&chance(action)?Math.min(resource.getAmount(),item.getAmount()):0;}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(int maximum,FluidAction action){return single()&&maximum>0&&item.getType()!=null&&chance(action)?FluidInteropMappings.stack(item.getType(),Math.min(maximum,item.getAmount())):net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
        @Override public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack request,FluidAction action){return single()&&matches(FluidInteropMappings.fromNeo(request))&&chance(action)?request.copyWithAmount(Math.min(request.getAmount(),item.getAmount())):net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
    }
    private NativeFluidItems(){}
}
