package com.hbm.client.weapon.fireext;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.machine.arcfurnace.ArcSandBlock;
import com.hbm.registry.HbmContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
/** ClientProxy vanillaExt/blockdust: true terrain particles, source velocity/.8grey and10..29ticklife. */
final class FireExtDust extends TerrainParticle {
 private FireExtDust(ClientLevel level,EntityBulletBaseMK4 bullet,BlockState state,double vx,double vy,double vz,boolean water){
  super(level,bullet.getX(),bullet.getY(),bullet.getZ(),vx,vy+.2,vz,state);
  xd=vx;yd=vy+.2;zd=vz;setColor(.8F,.8F,.8F);lifetime=10+level.random.nextInt(20);
  // Water is a liquid renderer in modern Minecraft, so its block model's particle icon is absent.
  // Use the actual native still-water atlas sprite, corresponding to the source water.getIcon(0,0).
  if(water)setSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.withDefaultNamespace("block/water_still")));
 }
 static void spawn(EntityBulletBaseMK4 bullet,int mode){
  var client=Minecraft.getInstance();if(client.level==null||client.level!=bullet.level())return;var random=client.level.random;var motion=bullet.getDeltaMovement();double spread=mode==0?.05:.1;
  double vx=motion.x+random.nextGaussian()*spread,vy=motion.y-.2+random.nextGaussian()*spread,vz=motion.z+random.nextGaussian()*spread;
  BlockState state;if(mode==0)state=Blocks.WATER.defaultBlockState();else if(mode==1)state=HbmContent.block("block_foam").defaultBlockState();else{var sand=HbmContent.block("sand_mix");if(!(sand instanceof ArcSandBlock actual))throw new IllegalStateException("Extinguisher dust requires true BlockNTMSand");state=actual.fromLegacy(0);}
  client.particleEngine.add(new FireExtDust(client.level,bullet,state,vx,vy,vz,mode==0));
 }
}
