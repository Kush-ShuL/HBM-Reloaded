package com.hbm.client.generator.gasturbine;

import com.hbm.fluid.HbmFluids;
import com.hbm.machine.generator.gasturbine.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Actual source ILookOverlay flow labels for the five fluid ports / single HE port. */
@WailaPlugin
public final class GasTurbineJade implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,GasTurbineBlock.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,GasTurbineBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:gas_turbine_ports");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof GasTurbineBlock block))return;var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core==null||!(accessor.getLevel().getBlockEntity(core) instanceof GasTurbineBlockEntity machine))return;var facing=DummyableBlock.facing(machine.getBlockState());if(accessor.getPosition().equals(GasTurbineBlockEntity.powerPort(core,facing)))tag.putBoolean("hbmGasPowerOut",true);else for(var port:GasTurbineBlockEntity.fluidConnections(core,facing))if(accessor.getPosition().equals(port.origin())){tag.putIntArray("hbmGasFlow",new int[]{machine.tanks[0].getTankType().getID(),machine.tanks[1].getTankType().getID(),machine.tanks[2].getTankType().getID(),machine.tanks[3].getTankType().getID()});break;}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.getBoolean("hbmGasPowerOut"))tooltip.add(Component.literal("<- ").withStyle(ChatFormatting.RED).append(Component.translatable("jade.hbm.gas_turbine.power")));int[] types=tag.getIntArray("hbmGasFlow");for(int i=0;i<types.length;i++)tooltip.add(Component.literal(i<3?"-> ":"<- ").withStyle(i<3?ChatFormatting.GREEN:ChatFormatting.RED).append(HbmFluids.fromID(types[i]).displayName().copy().withStyle(ChatFormatting.RESET)));}
    }
}
