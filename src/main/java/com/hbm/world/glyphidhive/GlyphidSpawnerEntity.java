package com.hbm.world.glyphidhive;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.entity.mob.glyphid.hive.EntityGlyphidScout;
import com.hbm.entity.mob.glyphid.special.*;
import com.hbm.pollution.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Actual TileEntityGlpyhidSpawner (source spelling), including its independent new Random swarm. */
public final class GlyphidSpawnerEntity extends BlockEntity {
    public boolean initialSpawn=true;
    private record Spawn(Function<Level,EntityGlyphid> create,String chance){}
    private static final List<Spawn> SPAWNS=List.of(
        new Spawn(world->new EntityGlyphid(GlyphidContent.GRUNT.get(),world),"12.GC01_glyphidChance"),
        new Spawn(world->new EntityGlyphidBombardier(GlyphidContent.BOMBARDIER.get(),world),"12.GC03_bombardierChance"),
        new Spawn(world->new EntityGlyphidBrawler(GlyphidContent.BRAWLER.get(),world),"12.GC02_brawlerChance"),
        new Spawn(EntityGlyphidDigger::new,"12.GC05_diggerChance"),
        new Spawn(world->new EntityGlyphidBlaster(GlyphidContent.BLASTER.get(),world),"12.GC04_blasterChance"),
        new Spawn(world->new EntityGlyphidBehemoth(GlyphidContent.BEHEMOTH.get(),world),"12.GC06_behemothChance"),
        new Spawn(world->new EntityGlyphidBrenda(GlyphidContent.BRENDA.get(),world),"12.GC07_brendaChance"),
        new Spawn(EntityGlyphidNuclear::new,"12.GC08_johnsonChance"));
    public GlyphidSpawnerEntity(BlockPos pos,BlockState state){super(GlyphidHiveContent.SPAWNER_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,GlyphidSpawnerEntity self){if(level instanceof ServerLevel world)self.tick(world);}
    private void tick(ServerLevel world){
        if(world.getDifficulty()==Difficulty.PEACEFUL)return;
        if(!initialSpawn&&world.getGameTime()%(GlyphidConfig.COOLDOWN_SECONDS.get()*20L)!=0)return;
        initialSpawn=false;setChanged(); // Retain source timing; make the changed real NBT durable in the modern save lifecycle.
        int count=0;for(var entity:world.getAllEntities())if(entity instanceof EntityGlyphid&&++count>=GlyphidConfig.SPAWN_MAX.get())return;
        var pos=worldPosition;var near=world.getEntitiesOfClass(EntityGlyphid.class,new AABB(pos.getX()-5,pos.getY()+1,pos.getZ()-5,pos.getX()+6,pos.getY()+7,pos.getZ()+6));float soot=PollutionSystem.get(world,pos,PollutionType.SOOT);int subtype=getBlockState().getValue(GlyphidSpawnerBlock.META);
        if(near.size()<=3||subtype==EntityGlyphid.TYPE_RADIOACTIVE){for(var bug:createSwarm(soot,subtype))trySpawnEntity(bug);
            // Source clears initialSpawn above; its separate scoutInitialSpawn config was not consulted here.
            if(!initialSpawn&&world.random.nextInt(GlyphidConfig.scoutChance()+1)==0&&soot>=GlyphidConfig.scoutThreshold()&&subtype!=EntityGlyphid.TYPE_RADIOACTIVE){var scout=new EntityGlyphidScout(world);if(subtype==1)scout.setSubtype(EntityGlyphid.TYPE_INFECTED);trySpawnEntity(scout);}
        }
    }
    public void trySpawnEntity(EntityGlyphid bug){var world=(ServerLevel)level;double x=bug.getRandom().nextGaussian()*3,z=bug.getRandom().nextGaussian()*3;for(int i=0;i<7;i++){bug.moveTo(worldPosition.getX()+.5+x,worldPosition.getY()-2+i,worldPosition.getZ()+.5+z,world.random.nextFloat()*360,0);if(bug.checkSpawnObstruction(world)){world.addFreshEntity(bug);return;}}}
    public ArrayList<EntityGlyphid> createSwarm(float soot,int meta){Random random=new Random();var result=new ArrayList<EntityGlyphid>();int amount=(int)Math.min(GlyphidConfig.SWARM_SIZE.get()*Math.max(GlyphidConfig.SWARM_SCALING.get()*(soot/GlyphidConfig.SOOT_STEP.get()),1),10),cap=100;while(result.size()<=amount&&cap>=0){for(var spawn:SPAWNS){int[] chance=GlyphidConfig.chance(spawn.chance);int adjusted=(int)(chance[0]+(chance[1]-chance[1]/Math.max((soot+1)/3,1)));if(soot>=chance[2]&&random.nextInt(100)<=adjusted){var bug=spawn.create.apply(level);if(meta==1)bug.setSubtype(EntityGlyphid.TYPE_INFECTED);if(meta==2)bug.setSubtype(EntityGlyphid.TYPE_RADIOACTIVE);result.add(bug);}}cap--;}return result;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("initialSpawn",initialSpawn);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);initialSpawn=tag.getBoolean("initialSpawn");}
}
