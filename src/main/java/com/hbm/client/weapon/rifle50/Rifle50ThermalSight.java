package com.hbm.client.weapon.rifle50;
import com.hbm.weapon.sedna.gun.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.Set;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
/** Actual RenderOverhead.renderThermalSight: original 64m, entity classes/colors, 12 AABB lines and depth-disabled display. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class Rifle50ThermalSight {
 private static final TagKey<EntityType<?>> BOSSES=TagKey.create(Registries.ENTITY_TYPE,ResourceLocation.parse("c:bosses"));
 // Exact registered HBM implementations of the old IBossDisplayData; no name-substring guessing.
 private static final Set<String> LEGACY_BOSSES=Set.of("hbm:entity_balls_o_tron","hbm:entity_ntm_ufo","hbm:entity_mob_hunter_chopper","hbm:entity_mob_mask_man","hbm:entity_elder_one");
 private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(32768));
 private static BooleanSupplier hudEnabled=()->true; // Original HbmPlayerProps.enableHUD defaults true.
 public static void installHudGate(BooleanSupplier actualPlayerPropsGate){hudEnabled=java.util.Objects.requireNonNull(actualPlayerPropsGate);}
 @SubscribeEvent public static void render(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_LEVEL||!hudEnabled.getAsBoolean())return;var mc=Minecraft.getInstance();var player=mc.player;if(player==null||mc.level==null)return;boolean thermal=player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof GunArmorPerks.FullSetArmor armor&&armor.fsbThermal()&&com.hbm.armor.t51.T51Runtime.fullSet(player,true);var held=GunHands.viewStack(player);if(ItemGunBaseNT.aimingProgress==1&&held.getItem() instanceof ItemGunBaseNT gun)for(int i=0;i<gun.getConfigCount();i++)if(gun.getConfig(held,i).hasThermalSights(held))thermal=true;if(!thermal)return;
  // AFTER_LEVEL has no world PoseStack. Use its explicit world matrices rather
  // than the identity fallback or whichever matrices a previous renderer left.
  var eye=event.getCamera().getPosition();var matrix=new Matrix4f(event.getModelViewMatrix());
  var projection=new Matrix4f(RenderSystem.getProjectionMatrix());var sorting=RenderSystem.getVertexSorting();
  var modelView=RenderSystem.getModelViewStack();modelView.pushMatrix();
  try {
   modelView.identity();RenderSystem.applyModelViewMatrix();
   RenderSystem.setProjectionMatrix(new Matrix4f(event.getProjectionMatrix()),VertexSorting.DISTANCE_TO_ORIGIN);
   var out=BUFFERS.getBuffer(Lines.TYPE);
   for(var entity:mc.level.entitiesForRendering()){
    if(entity==player||entity.distanceToSqr(player)>4096)continue;
    int color=color(entity,player);if(color==0)continue;
    if(entity instanceof LivingEntity living&&living.getHealth()<=0)color=0xff000000;
    float partial=event.getPartialTick().getGameTimeDeltaPartialTick(!mc.level.tickRateManager().isEntityFrozen(entity));
    var b=entity.getBoundingBox().move(Mth.lerp(partial,entity.xOld,entity.getX())-entity.getX()-eye.x,
      Mth.lerp(partial,entity.yOld,entity.getY())-entity.getY()-eye.y,
      Mth.lerp(partial,entity.zOld,entity.getZ())-entity.getZ()-eye.z);
    box(out,matrix,b,color);
   }
   BUFFERS.endBatch(Lines.TYPE);
  } finally {
   modelView.popMatrix();RenderSystem.applyModelViewMatrix();RenderSystem.setProjectionMatrix(projection,sorting);
  }
 }
 private static int color(Entity entity,Player player){if(entity instanceof WitherBoss||entity instanceof EnderDragon||entity.getType().is(BOSSES)||LEGACY_BOSSES.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))return 0xffff8000;if(entity instanceof Enemy)return 0xffff0000;if(entity instanceof Player)return 0xffff00ff;if(entity instanceof Mob)return 0xff00ff00;if(entity instanceof ItemEntity)return 0xffffff80;if(entity instanceof ExperienceOrb)return player.tickCount%10<5?0xffffff80:0xff80ff80;return 0;}
 private static final int[][] EDGES={{0,2},{2,3},{3,1},{0,1},{1,5},{7,3},{7,5},{2,6},{6,4},{6,7},{4,5},{0,4}};
 private static void box(VertexConsumer out,org.joml.Matrix4f matrix,AABB box,int color){for(var edge:EDGES)for(int corner:edge)out.addVertex(matrix,(float)((corner&1)==0?box.minX:box.maxX),(float)((corner&2)==0?box.minY:box.maxY),(float)((corner&4)==0?box.minZ:box.maxZ)).setColor(color);}
 private static final class Lines extends RenderType {
  Lines(){super("hbm_source_thermal_unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.DEBUG_LINES,256,false,false,()->{},()->{});}
  static final RenderType TYPE=create("hbm_source_thermal",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.DEBUG_LINES,32768,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionColorShader)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).createCompositeState(false));
 }
 private Rifle50ThermalSight(){}
}
