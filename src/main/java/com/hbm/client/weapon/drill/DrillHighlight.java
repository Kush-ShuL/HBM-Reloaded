package com.hbm.client.weapon.drill;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.weapon.sedna.gun.drill.*;
import com.mojang.blaze3d.vertex.*;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
/** Original center plus full cubic AoE outline; sneaking shows only the source red center. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DrillHighlight extends RenderType {
 private DrillHighlight(){super("hbm_drill_highlight",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,()->{},()->{});}
 private static final RenderType LINES=create("hbm_drill_highlight",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(2))).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
 @SubscribeEvent public static void render(RenderHighlightEvent.Block event){var mc=Minecraft.getInstance();var player=mc.player;if(player==null||!(player.getMainHandItem().getItem() instanceof ItemGunDrill))return;event.setCanceled(true);var stack=player.getMainHandItem();var hit=LegacyEntityDamage.getMouseOver(player,XFactoryDrill.getModdableReach(stack,5));if(!(hit instanceof BlockHitResult block)||hit.getType()!=HitResult.Type.BLOCK)return;
  int area=player.isShiftKeyDown()?0:XFactoryDrill.getModdableAoE(stack,1);var pos=block.getBlockPos();var eye=event.getCamera().getPosition();var pose=event.getPoseStack();var buffers=mc.renderBuffers().bufferSource();var out=buffers.getBuffer(LINES);pose.pushPose();try{pose.translate(pos.getX()-eye.x,pos.getY()-eye.y,pos.getZ()-eye.z);LevelRenderer.renderLineBox(pose,out,new AABB(0,0,0,1,1,1).inflate(.002),area>0?0:.5F,0,0,area>0?.4F:1);if(area>0)LevelRenderer.renderLineBox(pose,out,new AABB(-area,-area,-area,1+area,1+area,1+area).inflate(.002),.5F,0,0,1);buffers.endBatch(LINES);}finally{pose.popPose();}}
}
