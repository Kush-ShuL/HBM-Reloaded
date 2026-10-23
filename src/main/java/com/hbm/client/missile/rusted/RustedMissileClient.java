package com.hbm.client.missile.rusted;
import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.missile.rusted.RustedMissileContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RustedMissileClient {
    private static ShaderInstance rocketShader;static ShaderInstance rocketShader(){return rocketShader;}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:missile_rocket_flame"),DefaultVertexFormat.PARTICLE),shader->rocketShader=shader);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(RustedMissileContent.ENTITY.get(),RustedMissileRenderer::new);EntityMissileBaseNT.contrails=effect->{var mc=Minecraft.getInstance();if(!(effect.level() instanceof ClientLevel level)||mc.player==null||mc.player.distanceToSqr(effect.x(),effect.y(),effect.z())>350D*350D)return;mc.particleEngine.add(new LegacyRocketFlameParticle(level,effect.x(),effect.y(),effect.z(),effect.mx(),effect.my(),effect.mz(),effect.scale(),effect.life()));};}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RustedMissileItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RustedMissileItemRenderer();return renderer;}},RustedMissileContent.ITEM.get());}
}
