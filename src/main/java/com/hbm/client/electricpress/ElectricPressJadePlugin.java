package com.hbm.client.electricpress;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.jade.HbmJadeBars;
import com.hbm.machine.electricpress.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** The source50range wire snapshot is authoritative also when looking at a no-BE dummy; no guessed inventory lookup or new network is used. */
@WailaPlugin public final class ElectricPressJadePlugin implements IWailaPlugin {
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,ElectricPressBlock.class);event.registerBlockComponent(Provider.INSTANCE,ConveyorPressBlock.class);}
    private enum Provider implements IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:electric_press_state");}
        @Override public void appendTooltip(ITooltip tip,BlockAccessor accessor,IPluginConfig config){if(!(accessor.getBlockState().getBlock() instanceof DummyableBlock block))return;var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core==null||!(accessor.getLevel().getBlockEntity(core) instanceof PoweredPressEntity machine))return;
            // Generic MachineView already supplies these values on core hits. Middle dummy has no BE by real source design.
            if(!accessor.getServerData().contains("hbm:machine")){HbmJadeBars.add(tip,machine.getPowerExact(),machine.getMaxPowerExact(),Component.translatable("jade.hbm.energy",machine.getPower(),50000),0xf2c94c);for(var progress:machine.progressBars())HbmJadeBars.add(tip,progress.value(),progress.maximum(),Component.translatable(progress.name()),progress.color());}
            if(machine instanceof ConveyorPressEntity conveyor)tip.add(ElectricPressClient.stamp(conveyor.renderedStamp));
        }
    }
}
