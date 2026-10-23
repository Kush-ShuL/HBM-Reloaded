package com.hbm.machine.pilelegacy;

import api.hbm.block.IPileNeutronReceiver;
import com.hbm.exposure.HbmExposure;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Actual NeutronHandler PILE stream branch, including its moving base position, first-null-TE return and zero-size exposure box. */
public final class LegacyPileNeutrons {
    private static final Map<ServerLevel,Field> WORLDS=new HashMap<>();
    private static boolean registered;
    public static void register(){if(registered)return;registered=true;NeoForge.EVENT_BUS.addListener(LegacyPileNeutrons::tick);NeoForge.EVENT_BUS.addListener((LevelEvent.Unload event)->{if(event.getLevel() instanceof ServerLevel level)WORLDS.remove(level);});NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event)->WORLDS.clear());}
    private static void tick(ServerTickEvent.Pre event){WORLDS.values().removeIf(field->field.streams.isEmpty());for(var entry:WORLDS.entrySet()){for(var stream:entry.getValue().streams)stream.run(entry.getKey(),entry.getValue());entry.getValue().streams.clear();}}
    public static void remove(Level level,BlockPos pos){if(level instanceof ServerLevel server){var field=WORLDS.get(server);if(field!=null)field.nodes.remove(pos);}}
    static void cast(LegacyPileEntity source,int flux){
        if(!(source.getLevel() instanceof ServerLevel level))return;
        if(flux==0){remove(level,source.getBlockPos());return;}
        var field=WORLDS.computeIfAbsent(level,ignored->new Field());var pos=source.getBlockPos().immutable();var node=field.nodes.computeIfAbsent(pos,ignored->source);
        double x=1,y=0,z=0;
        float angle=(float)(Math.PI*2D*level.random.nextDouble()),cos=Mth.cos(angle),sin=Mth.sin(angle);
        double nx=x*cos+y*sin,ny=y*cos-x*sin;x=nx;y=ny;
        angle=(float)(Math.PI*2D*level.random.nextDouble());cos=Mth.cos(angle);sin=Mth.sin(angle);
        nx=x*cos+z*sin;double nz=z*cos-x*sin;x=nx;z=nz;
        angle=(float)(Math.PI*2D*level.random.nextDouble());cos=Mth.cos(angle);sin=Mth.sin(angle);
        ny=y*cos+z*sin;nz=z*cos-y*sin;y=ny;z=nz;
        field.streams.add(new Stream(node,new Vec3(x,y,z),flux));
    }
    private static final class Field {final List<Stream> streams=new ArrayList<>();final Map<BlockPos,LegacyPileEntity> nodes=new HashMap<>();}
    private static final class Stream {
        final LegacyPileEntity origin;final Vec3 vector;double flux;
        Stream(LegacyPileEntity origin,Vec3 vector,double flux){this.origin=origin;this.vector=vector;this.flux=flux;}
        void run(ServerLevel world,Field field){
            var pos=origin.getBlockPos().mutable();
            for(float i=1;i<=5;i+=.5F){
                var next=BlockPos.containing(pos.getX()+.5+vector.x*i,pos.getY()+.5+vector.y*i,pos.getZ()+.5+vector.z*i);
                if(next.equals(pos))continue;
                // Source mutates the local base every step. Do not replace this with a conventional ray from origin.
                pos.set(next);
                net.minecraft.world.level.block.entity.BlockEntity tile=field.nodes.get(next);
                if(tile==null){tile=world.getBlockEntity(next);if(tile==null)return;if(tile instanceof LegacyPileEntity pile&&pile.streamBase())field.nodes.put(next,pile);}
                var block=tile.getBlockState().getBlock();int metadata=LegacyGraphiteBlock.metadata(tile.getBlockState());
                if(!(tile instanceof LegacyPileEntity pile&&pile.streamBase())){
                    String id=BuiltInRegistries.BLOCK.getKey(block).toString();
                    if(id.equals("hbm:block_boron"))return;
                    else if(switch(id){case "hbm:concrete","hbm:concrete_smooth","hbm:concrete_asbestos","hbm:concrete_colored","hbm:brick_concrete"->true;default->false;})flux*=.25;
                    if(block==LegacyPileContent.CHANNELS.get(LegacyPileKind.ROD).get()&&(metadata&8)==0)return;
                }
                if(tile instanceof IPileNeutronReceiver receiver){receiver.receiveNeutrons((int)Math.floor(flux));if(block!=LegacyPileContent.CHANNELS.get(LegacyPileKind.DETECTOR).get()||(metadata&8)==0)return;}
                int x=(int)(next.getX()+.5),y=(int)(next.getY()+.5),z=(int)(next.getZ()+.5);
                for(var entity:world.getEntitiesOfClass(LivingEntity.class,new AABB(x,y,z,x,y,z),entity->true))HbmExposure.contaminate(entity,HbmExposure.Contamination.CREATIVE,(float)(flux/4D));
            }
        }
    }
    private LegacyPileNeutrons(){}
}
