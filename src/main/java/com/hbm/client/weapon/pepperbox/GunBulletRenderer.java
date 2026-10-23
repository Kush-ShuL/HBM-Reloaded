package com.hbm.client.weapon.pepperbox;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.mojang.blaze3d.vertex.*;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.*;

/** Source LegoClient.renderBulletStandard: six untextured tapered quads and the exact dark/light endpoint colors. */
public final class GunBulletRenderer extends RenderType {
    private GunBulletRenderer(){super("hbm_sedna_bullet",DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,VertexFormat.Mode.QUADS,1536,false,false,()->{},()->{});}
    private static final RenderType TYPE=com.hbm.client.render.WorldEffectMeshes.register(RenderType.create("hbm_sedna_bullet",DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,VertexFormat.Mode.QUADS,1536,false,false,
        CompositeState.builder().setShaderState(POSITION_COLOR_LIGHTMAP_SHADER).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false)),"Sedna+Aberrator+Accelerator/OriginalOpaqueTaper");
    public static final BiConsumer<EntityBulletBaseMK4,Float> STANDARD=(bullet,partial)->render(bullet,partial,0xFFBF00,0xFFFFFF,false);
    public static final BiConsumer<EntityBulletBaseMK4,Float> AP=(bullet,partial)->render(bullet,partial,0xFF6A00,0xFFE28D,false);
    public static final BiConsumer<EntityBulletBaseMK4,Float> EXPRESS=(bullet,partial)->render(bullet,partial,0x9E082E,0xFF8A79,false);
    private static void render(EntityBulletBaseMK4 bullet,float partial,int dark,int light,boolean fullbright){double length=bullet.prevVelocity+(bullet.velocity-bullet.prevVelocity)*partial;if(length>0)renderBulletStandard(dark,light,length,.03125D,.03125D*.25D,fullbright);}
    public static void renderBulletStandard(int dark,int light,double length,double front,double back,boolean fullbright){
        var context=GunBulletRenderContext.current();var out=context.buffers().getBuffer(TYPE);int packed=fullbright?LightTexture.FULL_BRIGHT:context.light();
        double[][] vertices={{length,back,-back},{length,back,back},{0,front,front},{0,front,-front},{length,-back,-back},{length,-back,back},{0,-front,front},{0,-front,-front},{length,-back,back},{length,back,back},{0,front,front},{0,-front,front},{length,-back,-back},{length,back,-back},{0,front,-front},{0,-front,-front},{length,back,back},{length,back,-back},{length,-back,-back},{length,-back,back},{0,front,front},{0,front,-front},{0,-front,-front},{0,-front,front}};
        for(int i=0;i<vertices.length;i++){var vertex=vertices[i];int rgb=i<16?(i%4<2?dark:light):i<20?dark:light;out.addVertex(context.poses().last(),(float)vertex[0],(float)vertex[1],(float)vertex[2]).setColor(0xFF000000|rgb).setLight(packed);}
    }
}
