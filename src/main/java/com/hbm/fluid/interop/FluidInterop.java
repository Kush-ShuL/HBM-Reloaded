package com.hbm.fluid.interop;

import com.hbm.block.fluid.LegacyFluidBucket;
import com.hbm.block.pipe.NativePipeBlock;
import com.hbm.block.pipe.NativePipeBlockEntity;
import com.hbm.block.pipe.PipeKind;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.*;

/** Optional outside API; native storage, fluid identity and native network remain authoritative. */
public final class FluidInterop {
    public record Identity(HbmFluidType nativeType,Supplier<? extends FluidType> type,ResourceLocation sprite){}
    private static final List<Identity> IDENTITIES=new ArrayList<>();
    private static boolean registered;
    public static List<Identity> identities(){return List.copyOf(IDENTITIES);}
    public static void register(IEventBus bus){
        if(registered||!FluidInteropConfig.enabled())return;registered=true;
        var fluids=DeferredRegister.create(Registries.FLUID,"hbm");
        var types=DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES,"hbm");
        for(var nativeType:HbmFluids.getAll()){
            if(nativeType==HbmFluids.NONE||nativeType==HbmFluids.WATER||nativeType==HbmFluids.LAVA)continue;
            String name=nativeType.getName().toLowerCase(Locale.ROOT);String id="interop/"+name;
            var type=types.register(id,()->new FluidType(FluidType.Properties.create().descriptionId(nativeType.getUnlocalizedName())
                    .density(nativeType.hasTrait("gaseous")||nativeType.hasTrait("gaseous_art")?-100:1000).viscosity(1000)
                    .temperature((int)Math.clamp((long)nativeType.temperature+273,0,Integer.MAX_VALUE)).canConvertToSource(false)){
                @Override public Component getDescription(){return nativeType.displayName();}
                @Override public Component getDescription(net.neoforged.neoforge.fluids.FluidStack stack){return nativeType.displayName();}
            });
            fluids.register(id,()->{var fluid=new InteropFluid(type);FluidInteropMappings.fallback(nativeType,fluid);return fluid;});
            IDENTITIES.add(new Identity(nativeType,type,ResourceLocation.fromNamespaceAndPath("hbm","block/interop/"+name)));
        }
        types.register(bus);fluids.register(bus);bus.addListener(FluidInterop::capabilities);bus.addListener(FluidInterop::loaded);
    }
    private static void loaded(FMLLoadCompleteEvent event){event.enqueueWork(()->{
        // Use the actual source fixed-container table: bucket_mud is WATZ, not a name-derived MUD alias.
        for(var row:FluidContainerRegistry.allContainers)if(row.content()==1000&&row.fullContainer().getItem() instanceof LegacyFluidBucket bucket)
            FluidInteropMappings.register(row.type(),bucket.nativeWorldFluid());
    });}
    private static void capabilities(RegisterCapabilitiesEvent event){
        if(!FluidInteropConfig.enabled())return;
        BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet().forEach(entry->{if(entry.getKey().location().getNamespace().equals("hbm"))
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,entry.getValue(),FluidInterop::blockHandler);});
        var bottles=Set.of(Items.BUCKET,Items.WATER_BUCKET,Items.LAVA_BUCKET,Items.GLASS_BOTTLE,Items.POTION,Items.EXPERIENCE_BOTTLE);
        BuiltInRegistries.ITEM.entrySet().forEach(entry->{var item=entry.getValue();if(entry.getKey().location().getNamespace().equals("hbm")||bottles.contains(item))
            event.registerItem(Capabilities.FluidHandler.ITEM,(stack,context)->NativeFluidItems.create(stack),item);});
    }
    private static IFluidHandler blockHandler(BlockEntity block,Direction side){
        if(!FluidInteropConfig.enabled()||side==null||block.isRemoved()||block.getLevel()==null)return null;
        if(block instanceof NativePipeBlockEntity pipe)return new NativeFluidHandler(()->pipeEndpoints(pipe,side),()->pipeTypes(pipe,side));
        var endpoint=NativeFluidEndpoint.at(block,side);if(endpoint==null)return null;
        return new NativeFluidHandler(()->List.of(endpoint),()->Set.of(HbmFluids.getAll()));
    }
    private static Set<HbmFluidType> pipeTypes(NativePipeBlockEntity pipe,Direction side){
        if(pipe.isRemoved()||!FluidInteropConfig.enabled())return Set.of();var kind=pipe.kind();
        if((kind==PipeKind.VALVE||kind==PipeKind.COUNTER)&&!pipe.getBlockState().getValue(NativePipeBlock.OPEN))return Set.of();
        var result=Collections.newSetFromMap(new IdentityHashMap<HbmFluidType,Boolean>());
        for(var type:pipe.networkTypes())if(type!=HbmFluids.NONE&&pipe.canConnect(type,side))result.add(type);return result;
    }
    private static List<NativeFluidEndpoint> pipeEndpoints(NativePipeBlockEntity pipe,Direction side){
        if(!(pipe.getLevel() instanceof ServerLevel level))return List.of();var result=new ArrayList<NativeFluidEndpoint>();
        for(var type:pipeTypes(pipe,side)){var node=FluidNetworks.nodeAt(level,type,pipe.getBlockPos());if(node==null||node.isExpired()||!node.hasValidNet())continue;
            for(var provider:node.network().providerEntries)result.add(NativeFluidEndpoint.network(provider,type));
            for(var receiver:node.network().receiverEntries)result.add(NativeFluidEndpoint.network(receiver,type));
        }return result;
    }
    private FluidInterop(){}
}
