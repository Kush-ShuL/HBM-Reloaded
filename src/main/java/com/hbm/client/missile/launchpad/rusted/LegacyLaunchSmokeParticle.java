package com.hbm.client.missile.launchpad.rusted;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.bomb.nuclear.SourceSmokeGeometry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
/** Source ParticleSmokePlume six seeded quads,80..99 ticks, expanding rise, original collision rebound and.925 friction. */
public final class LegacyLaunchSmokeParticle extends Particle implements SourceSmokeGeometry {
    private static final AtomicInteger IDS=new AtomicInteger();private final int seed=IDS.incrementAndGet();private float scale=.25F;private boolean verticalCollision;
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/particle/contrail.png");
    private static final ParticleRenderType TYPE=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/particle/contrail.png"),false);
    public LegacyLaunchSmokeParticle(ClientLevel level,double x,double y,double z,double mx,double my,double mz){super(level,x,y,z);random.nextFloat();random.nextFloat();lifetime=80+random.nextInt(20);xd=mx;yd=my;zd=mz;}
    @Override public void move(double dx,double dy,double dz){Vec3 actual=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());verticalCollision=dy!=actual.y;onGround=verticalCollision&&dy<0;setPos(x+actual.x,y+actual.y,z+actual.z);if(dx!=actual.x)xd=0;if(dy!=actual.y)yd=0;if(dz!=actual.z)zd=0;}
    @Override public void tick(){xo=x;yo=y;zo=z;alpha=1-(float)age/lifetime;float old=scale;scale=.25F+(float)age/lifetime*2;age++;if(age==lifetime)remove();double speed=(float)Math.sqrt(xd*xd+yd*yd+zd*zd);move(xd,yd+scale-old,zd);if(verticalCollision)yd=speed;xd*=.925;yd*=.925;zd*=.925;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    @Override public ParticleRenderType smokeMaterial(){return TYPE;}
    @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;emitSmokeGeometry(MukeRenderTypes.begin(smokeMaterial()),camera,partial);}
    @Override public void emitSmokeGeometry(VertexConsumer out,Camera camera,float partial){var eye=camera.getPosition();Random random=new Random(seed);for(int i=0;i<6;i++){float color=random.nextFloat()*.75F+.1F,px=(float)(xo+(x-xo)*partial-eye.x+random.nextGaussian()*.5*scale),py=(float)(yo+(y-yo)*partial-eye.y+random.nextGaussian()*.5*scale),pz=(float)(zo+(z-zo)*partial-eye.z+random.nextGaussian()*.5*scale);var corners=new Vector3f[]{new Vector3f(-1,-1,0),new Vector3f(-1,1,0),new Vector3f(1,1,0),new Vector3f(1,-1,0)};float[] us={1,1,0,0},vs={1,0,0,1};for(int j=0;j<4;j++){var point=corners[j].rotate(camera.rotation()).mul(scale).add(px,py,pz);out.addVertex(point.x,point.y,point.z).setUv(us[j],vs[j]).setColor(color,color,color,alpha).setLight(240);}}}
}
