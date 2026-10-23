package com.hbm.client.bomb.nuclear.programmable;

import com.hbm.bomb.nuclear.programmable.*;
import com.hbm.client.bomb.nuclear.MukeClient;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.model.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Source Body/Balefire mesh, two original texture-matrix glint passes and 0.04-scale old clock font. */
public final class TimedBalefireRenderer implements BlockEntityRenderer<TimedBalefireBlockEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/bombs/fstbmb.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/bombs/fstbmb.png");
 private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(131072));
 private static final RenderType BODY=RenderType.entityCutoutNoCull(TEXTURE),GLINT=GlintType.sourceGlint();
 public TimedBalefireRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 public static void draw(PoseStack pose,MultiBufferSource buffers,int light,int overlay){draw(pose,buffers,light,overlay,false);}
 public static void draw(PoseStack pose,MultiBufferSource buffers,int light,int overlay,boolean item){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(item?RenderType.entityCutout(TEXTURE):BODY),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Body","Balefire");}
 @Override public void render(TimedBalefireBlockEntity bomb,float partial,PoseStack pose,MultiBufferSource unused,int light,int overlay){pose.pushPose();pose.translate(.5,0,.5);int meta=bomb.getBlockState().getValue(ProgrammableBombBlock.META);pose.mulPose(Axis.YP.rotationDegrees(switch(meta){case 2->90;case 4->180;case 3->270;default->0;}));draw(pose,BUFFERS,light,overlay);BUFFERS.endBatch();if(bomb.loaded){glint(pose,partial);pose.translate(.815F,.9275F,.5F);pose.scale(.04F,-.04F,.04F);pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,1,0);MultiBufferSource text=type->BUFFERS.getBuffer(GlintType.noDepthWrite(type));LegacyFonts.get().drawInBatch(TimedBalefireBlockEntity.clock(bomb.timer),0,0,0xff0000,false,pose.last().pose(),text,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);BUFFERS.endBatch();}pose.popPose();}
 private static void glint(PoseStack pose,float partial){var mc=Minecraft.getInstance();float offset=(mc.player==null?0:mc.player.tickCount)+partial;var mesh=mesh();for(int pass=0;pass<2;pass++){float movement=offset*(.001F+pass*.003F)*5,angle=(float)Math.toRadians(30-pass*60);float cos=(float)Math.cos(angle),sin=(float)Math.sin(angle);var out=BUFFERS.getBuffer(GLINT);for(var triangle:mesh.triangles()){var face=mesh.faces().get(triangle.face());if(!face.legacyPart().equalsIgnoreCase("Balefire"))continue;int[] corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};for(int corner:corners){var vertex=face.corners().get(corner);var position=mesh.positions().get(vertex.position());var uv=mesh.textureCoordinates().get(vertex.texture());float u=uv.u(),v=1-uv.v()+movement;out.addVertex(pose.last().pose(),position.x(),position.y(),position.z()).setColor(0F,.8F*.76F,.15F*.76F,1F).setUv(2*(cos*u-sin*v),2*(sin*u+cos*v));}}BUFFERS.endBatch(GLINT);}}
 @Override public boolean shouldRenderOffScreen(TimedBalefireBlockEntity bomb){return true;}
 @Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(TimedBalefireBlockEntity bomb){return AABB.INFINITE;}
 private static final class GlintType extends RenderType {
  private static final Map<RenderType,RenderType> TEXT=new IdentityHashMap<>();
  private GlintType(RenderType base){super("hbm_source_clock",base.format(),base.mode(),base.bufferSize(),false,false,()->{base.setupRenderState();RenderSystem.depthMask(false);},()->{base.clearRenderState();RenderSystem.depthMask(true);});}
  private GlintType(String n,VertexFormat f,VertexFormat.Mode m,int size,boolean c,boolean s,Runnable set,Runnable clear){super(n,f,m,size,c,s,set,clear);}
  static RenderType noDepthWrite(RenderType base){return TEXT.computeIfAbsent(base,GlintType::new);}
  static RenderType sourceGlint(){var blend=new TransparencyStateShard("hbm_source_bf_glint",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});return create("hbm_source_bf_glint",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,131072,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(MukeClient::additiveShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/misc/glintbf.png"),false,false)).setTransparencyState(blend).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));}
 }
}
