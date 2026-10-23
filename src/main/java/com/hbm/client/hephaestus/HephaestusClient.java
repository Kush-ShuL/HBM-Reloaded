package com.hbm.client.hephaestus;
import com.hbm.machine.hephaestus.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HephaestusClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(HephaestusContent.ENTITY.get(),HephaestusRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private HephaestusItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new HephaestusItemRenderer()):renderer;}},HephaestusContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->{HephaestusEntity.clientTick=HephaestusClient::tick;HephaestusEntity.clientRemoved=HephaestusSound::removed;});}
    private static void tick(HephaestusEntity machine){
        var level=machine.getLevel();
        if(level!=null&&machine.bufferedHeat>0&&level.random.nextInt(7)==0){
            double x=level.random.nextGaussian()*2,y=level.random.nextGaussian()*3,z=level.random.nextGaussian()*2;
            var p=machine.getBlockPos();
            level.addParticle(ParticleTypes.CLOUD,p.getX()+.5+x,p.getY()+6+y,p.getZ()+.5+z,0,0,0);
        }
        HephaestusSound.tick(machine);
    }
}
