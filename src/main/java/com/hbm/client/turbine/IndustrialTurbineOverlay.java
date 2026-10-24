package com.hbm.client.turbine;

import com.hbm.fluid.FluidTank;
import com.hbm.fluid.FluidTraits;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.generator.leviathan.LeviathanBlock;
import com.hbm.machine.generator.leviathan.LeviathanEntity;
import com.hbm.machine.turbine.IndustrialTurbineBlock;
import com.hbm.machine.turbine.IndustrialTurbineBlockEntity;
import java.math.BigInteger;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Original industrial/Leviathan look information beside the crosshair, also when Jade is installed. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class IndustrialTurbineOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        var mc=Minecraft.getInstance();
        if(mc.options.hideGui||mc.screen!=null||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit))return;
        var block=mc.level.getBlockState(hit.getBlockPos()).getBlock();
        if(block instanceof IndustrialTurbineBlock industrial){
            var core=industrial.findCore(mc.level,hit.getBlockPos());
            if(core!=null&&mc.level.getBlockEntity(core) instanceof IndustrialTurbineBlockEntity turbine)
                draw(event.getGuiGraphics(),Component.translatable("block.hbm.machine_industrial_turbine"),turbine.tanks,turbine.getPowerExact(),turbine.spin);
        }else if(block instanceof LeviathanBlock leviathan){
            var core=leviathan.findCore(mc.level,hit.getBlockPos());
            if(core!=null&&mc.level.getBlockEntity(core) instanceof LeviathanEntity turbine)
                draw(event.getGuiGraphics(),Component.translatable("block.hbm.machine_chungus"),turbine.tanks,turbine.getPowerExact(),null);
        }
    }
    private static void draw(GuiGraphics graphics,Component title,FluidTank[] tanks,BigInteger power,Double spin){
        var font=Minecraft.getInstance().font;int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2-10;
        graphics.drawString(font,title,x,y,0xffff00,true);y+=12;
        var coolant=tanks[0].getTankType().getTrait(FluidTraits.Coolable.class);
        for(int i=0;i<2;i++){
            var tank=tanks[i];var type=i==0?tank.getTankType():coolant==null?HbmFluids.NONE:coolant.coolsTo();
            var label=Component.literal(i==0?"\u00a7a-> \u00a7r":"\u00a7c<- \u00a7r").append(type.displayName()).append(String.format(Locale.US," %,d/%,d mB",tank.getFill(),tank.getMaxFill()));
            graphics.drawString(font,label,x+2,y+1,0xffffff,true);y+=14;
        }
        graphics.drawString(font,"\u00a7c<- \u00a7r"+power+" HE"+(spin==null?"":"  "+Math.round(spin*100)+"%"),x+2,y+1,0xffffff,true);
    }
    private IndustrialTurbineOverlay(){}
}
