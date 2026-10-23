package com.hbm.entity.projectile;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.folly.FollyContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketController;

/** Actual original MK4CL: one current chunk, moved after the original bullet update, released on death. */
public class EntityBulletBaseMK4CL extends EntityBulletBaseMK4 {
    public static final TicketController TICKETS=new TicketController(ResourceLocation.parse("hbm:sedna_bullet"));
    private ChunkPos loaded;
    public EntityBulletBaseMK4CL(EntityType<? extends EntityBulletBaseMK4CL> type,Level world){super(type,world);}
    public EntityBulletBaseMK4CL(Level world){this(FollyContent.BULLET_CL.get(),world);}
    public EntityBulletBaseMK4CL(LivingEntity owner,BulletConfig config,float damage,float spread,double side,double height,double forward){super(FollyContent.BULLET_CL.get(),owner,config,damage,spread,side,height,forward);}
    @Override public void onAddedToLevel(){super.onAddedToLevel();loadNeighboringChunks(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));}
    @Override public void tick(){super.tick();if(!isRemoved())loadNeighboringChunks(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));}
    public void loadNeighboringChunks(int x,int z){if(!(level() instanceof ServerLevel world)||isRemoved())return;var next=new ChunkPos(x,z);if(next.equals(loaded))return;if(loaded!=null)TICKETS.forceChunk(world,this,loaded.x,loaded.z,false,true);TICKETS.forceChunk(world,this,x,z,true,true);loaded=next;}
    public void clearChunkLoader(){if(level() instanceof ServerLevel world&&loaded!=null)TICKETS.forceChunk(world,this,loaded.x,loaded.z,false,true);loaded=null;}
    @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)clearChunkLoader();super.remove(reason);}
}
