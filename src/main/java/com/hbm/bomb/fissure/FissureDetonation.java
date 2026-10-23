package com.hbm.bomb.fissure;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.bomb.nuclear.NuclearChunkloadingEntity;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.explosion.ExplosionNuclearExempt;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.registry.HbmContent;
import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Only waits for the original operation's real access footprint; it contains no explosion algorithm.
 * The sole SmallNuke medium implementation then runs once, followed by the original i/j/k1331 cells.
 */
public final class FissureDetonation extends NuclearChunkloadingEntity implements ExplosionNuclearExempt {
    private final NuclearChunkAccess[] access={new NuclearChunkAccess(),new NuclearChunkAccess()};
    private final LinkedHashSet<Long> demand=new LinkedHashSet<>();
    private int splitX;
    private boolean initialized,executing;
    public FissureDetonation(EntityType<? extends FissureDetonation> type,Level level){super(type,level);noPhysics=true;}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
    @Override public void tick(){
        if(!(level() instanceof ServerLevel server))return;
        try{
            loadChunk(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));
            if(!initialized)initializeFootprint();
            // Source getEntities only returns already loaded entities. Plan their actual LOS paths,
            // not an extra generated55-radius disk. Add new real requirements without discarding work.
            addLoadedTargetPaths(server);
            boolean ready=true;
            for(long key:demand){int x=ChunkPos.getX(key),z=ChunkPos.getZ(key);if(access[x<splitX?0:1].getWithNeighbors(server,x,z)==null)ready=false;}
            if(!ready)return;
            executing=true;
            try{
                // Original EntityTNTPrimedBase discards first. Keep all neighbour leases until finally.
                discard();
                ExplosionNukeSmall.explode(server,getX(),getY(),getZ(),ExplosionNukeSmall.PARAMS_MEDIUM);
                var biome=server.getBiome(BlockPos.containing(getX(),getY(),getZ()));
                boolean crater=biome.is(com.hbm.bomb.nuclear.NuclearCraterBiomes.CRATER)||biome.is(com.hbm.bomb.nuclear.NuclearCraterBiomes.INNER)||biome.is(com.hbm.bomb.nuclear.NuclearCraterBiomes.OUTER);
                var bedrockOre=HbmContent.block("ore_bedrock");var oilOre=HbmContent.block("ore_bedrock_oil");
                var fissure=HbmContent.block("ore_volcano").defaultBlockState().setValue(LegacyOreBlock.LEGACY_META,crater?1:0);
                for(int i=-5;i<=5;i++)for(int j=-5;j<=5;j++)for(int k=-5;k<=5;k++){
                    var pos=BlockPos.containing(getX()+i,getY()+j,getZ()+k);
                    var state=server.getBlockState(pos);
                    if(state.is(bedrockOre))server.setBlock(pos,fissure,3);
                    else if(state.is(oilOre))server.setBlock(pos,Blocks.BEDROCK.defaultBlockState(),3);
                }
            }finally{executing=false;closeAccess();}
        }catch(RuntimeException|Error failure){closeAccess();throw failure;}
    }
    private void initializeFootprint(){
        var params=ExplosionNukeSmall.PARAMS_MEDIUM;
        // Existing ExplosionNT: maximum power=size*1.3; every air step costs.3*.75,
        // moves.3; one extra step bounds float roundoff and the sampled block coordinate.
        double terrainRadius=Math.max(5,Math.ceil(params.blastRadius*1.3D/(.3D*.75D))*.3D+1D);
        double bound=Math.max(terrainRadius,params.killRadius);
        int boundMin=Mth.floor((getX()-bound)/16D),boundMax=Mth.floor((getX()+bound)/16D);
        splitX=Math.floorDiv(boundMin+boundMax+1,2);
        int minX=Mth.floor((getX()-terrainRadius)/16D),maxX=Mth.floor((getX()+terrainRadius)/16D);
        int minZ=Mth.floor((getZ()-terrainRadius)/16D),maxZ=Mth.floor((getZ()+terrainRadius)/16D);
        for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++){
            double dx=Math.max(Math.max(x*16D-getX(),0),getX()-(x*16D+16D));
            double dz=Math.max(Math.max(z*16D-getZ(),0),getZ()-(z*16D+16D));
            if(dx*dx+dz*dz<=terrainRadius*terrainRadius)demand.add(ChunkPos.asLong(x,z));
        }
        initialized=true;
    }
    private void addLoadedTargetPaths(ServerLevel server){
        double radius=ExplosionNukeSmall.PARAMS_MEDIUM.killRadius;
        Vec3 from=position();
        for(Entity target:server.getEntities((Entity)null,new AABB(getX(),getY(),getZ(),getX(),getY(),getZ()).inflate(radius))){
            double distance=(double)(float)Math.sqrt(target.distanceToSqr(getX(),getY(),getZ()));
            if(distance>radius||target instanceof Ocelot||target instanceof com.hbm.explosion.ExplosionKnockbackExempt||target instanceof ExplosionNuclearExempt||target instanceof ServerPlayer player&&player.isCreative())continue;
            // Original LOS's native DDA and202 sample limit, but only record chunk keys: no block,
            // shape or RNG lookup. The actual damage/target query is still the sole source method.
            int[] samples={0};
            BlockGetter.traverseBlocks(from,new Vec3(target.getX(),target.getEyeY(),target.getZ()),demand,(needed,pos)->{
                if(samples[0]++>=202)return Boolean.FALSE;
                if(!server.isOutsideBuildHeight(pos))needed.add(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));
                return (Boolean)null;
            },needed->Boolean.FALSE);
        }
    }
    private void closeAccess(){for(var lease:access)lease.close();}
    @Override public void remove(RemovalReason reason){if(!executing)closeAccess();super.remove(reason);}
    @Override protected void addAdditionalSaveData(CompoundTag tag){saveTicket(tag);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){loadTicket(tag);initialized=false;demand.clear();closeAccess();}
}
