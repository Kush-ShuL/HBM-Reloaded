package com.hbm.client.launchpadsoyuz;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** The original HFR mesh, source smooth normals and UV, independently moving named parts. */
public final class LaunchpadSoyuzModels {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/launchpad_soyuz.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/launchpad_soyuz.png");
    public static void part(String part,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.HFR,part);}
    public static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.scale(.25F,.25F,.25F);part("Launchpad",pose,buffers,light,overlay);pose.translate(0,0,3.25);for(int i=1;i<=5;i++)part("Strut"+i,pose,buffers,light,overlay);}
    private LaunchpadSoyuzModels(){}
}
