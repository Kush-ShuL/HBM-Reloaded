package com.hbm.client.refueler;

import com.hbm.machine.refueler.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RefuelerClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RefuelerContent.ENTITY.get(),RefuelerRenderer::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RefuelerEntity.CLIENT_TICK=RefuelerClient::effect);}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:refueler_fluid"),DefaultVertexFormat.NEW_ENTITY),shader->RefuelerRenderTypes.shader=shader);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RefuelerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RefuelerItemRenderer();return renderer;}},RefuelerContent.ITEM.get());}
    private static void effect(RefuelerEntity station){
        var mc=Minecraft.getInstance();if(!station.operating||mc.level==null)return;var rand=mc.level.random;int meta=station.getBlockState().getValue(RefuelerBlock.META);Direction dir=meta<=5?Direction.from3DDataValue(meta).getOpposite():null,rot=dir==null?null:dir.getAxis().isHorizontal()?dir.getClockWise():dir;
        int dx=dir==null?0:dir.getStepX(),dz=dir==null?0:dir.getStepZ(),rx=rot==null?0:rot.getStepX(),rz=rot==null?0:rot.getStepZ();var pos=station.getBlockPos();
        double x=pos.getX()+.5+rand.nextDouble()*.0625+dx*.5+rx*.25,z=pos.getZ()+.5+rand.nextDouble()*.0625+dz*.5+rz*.25,mx=-dx+rand.nextGaussian()*.1,mz=-dz+rand.nextGaussian()*.1;
        // EntityCritFX.nextTextureIndexX is the native enchanted-hit sprite; source tint overrides all RGB.
        var particle=mc.particleEngine.createParticle(ParticleTypes.ENCHANTED_HIT,x,pos.getY()+.375,z,mx,0,mz);int color=station.tank.getTankType().getColor();if(particle!=null)particle.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F);
    }
    private RefuelerClient(){}
}
