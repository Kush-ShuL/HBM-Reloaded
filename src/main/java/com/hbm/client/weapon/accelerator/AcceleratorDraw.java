package com.hbm.client.weapon.accelerator;

import com.hbm.client.model.*;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
/** Real source color, texture-enable, cull and fullbright state, consumed by actual OBJ draws. */
final class AcceleratorDraw {
    private static final ResourceLocation WHITE=ResourceLocation.parse("hbm:textures/misc/accelerator_untextured.png");
    private static final ThreadLocal<State> STATE=ThreadLocal.withInitial(State::new);
    private static final class State{int color=-1;boolean cull=true,textured=true,fullbright;float alphaCutoff=.1F;}
    static void reset(){STATE.set(new State());}
    static void cull(boolean enabled){STATE.get().cull=enabled;}
    static void textured(boolean enabled){STATE.get().textured=enabled;}
    static void alpha(int compare,float threshold){if(compare!=LegacyGunGL.GL_GREATER)throw new IllegalArgumentException("Source Accelerator alpha comparator");STATE.get().alphaCutoff=threshold;}
    static void color(float r,float g,float b){STATE.get().color=0xff000000|Math.round(r*255)<<16|Math.round(g*255)<<8|Math.round(b*255);}
    static float fr(int rgb){return (float)((rgb>>16)&255)/255F;}static float fg(int rgb){return (float)((rgb>>8)&255)/255F;}static float fb(int rgb){return (float)(rgb&255)/255F;}
    static void fullbright(boolean enabled){var state=STATE.get();if(enabled){LegacyGunGL.glPushMatrix();state.fullbright=true;state.cull=false;}else{state.fullbright=false;LegacyGunGL.glPopMatrix();}}
    static void mesh(ResourceLocation model,String part){
        var frame=LegacyGunGL.frame();var state=STATE.get();var texture=state.textured?java.util.Objects.requireNonNull(frame.texture()[0]):WHITE;
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);
        var out=frame.buffers().getBuffer(AcceleratorModelRenderTypes.model(texture,state.fullbright,state.cull,state.alphaCutoff));
        int light=state.fullbright?LightTexture.FULL_BRIGHT:frame.light();
        if(part==null)LegacyObjRenderer.renderAll(mesh,frame.pose(),out,light,frame.overlay(),state.color,LegacyObjRenderer.Convention.HFR);
        else LegacyObjRenderer.renderOnly(mesh,frame.pose(),out,light,frame.overlay(),state.color,LegacyObjRenderer.Convention.HFR,part);
    }
    private AcceleratorDraw(){}
}
