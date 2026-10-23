package com.hbm.block.dungeonspawner;

import com.hbm.block.skeletonholder.SkeletonHolderEntity;
import com.hbm.entity.mob.undead.UndeadSoldierContent;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.pedestal.PedestalContent;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** X5846 DungeonSpawner.TileEntityDungeonSpawner and its only actual ABERRATOR sequence. */
public final class DungeonSpawnerEntity extends BlockEntity {
    public enum SpawnerType { ABERRATOR }
    public int phase=0,timer=0;
    public SpawnerType type=SpawnerType.ABERRATOR;
    public DungeonSpawnerEntity(BlockPos pos,BlockState state){super(DungeonSpawnerContent.ENTITY.get(),pos,state);}

    public void tickServer(){
        if(!(level instanceof ServerLevel server))return;
        // Exact source order: even peaceful suppresses the condition only; it
        // does not suppress the phase action or completed-stage reward.
        phaseAction(server);
        if(phaseCondition(server)){phase++;timer=0;}else timer++;
    }
    private boolean phaseCondition(ServerLevel server){
        if(server.getDifficulty()==Difficulty.PEACEFUL)return false;
        int x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();
        if(phase==0){
            if(server.getGameTime()%20!=0)return false;
            // Original 1.7 AABB is not auto-sorted: (y,y-2).expand(20,10,20).
            // Construct its final bounds, not a modern pre-sorted starting box.
            return !server.getEntitiesOfClass(Player.class,new AABB(x-20,y-10,z-20,x+21,y+8,z+21),player->true).isEmpty();
        }
        if(phase<3){
            if(server.getGameTime()%20!=0||timer<60)return false;
            // Original x,x-2 inversion likewise yields maxX=x+48, not x+51.
            return server.getEntitiesOfClass(com.hbm.entity.mob.undead.UndeadSoldier.class,new AABB(x-50,y-20,z-50,x+48,y+21,z+51),mob->true).isEmpty();
        }
        return false;
    }
    private void phaseAction(ServerLevel server){
        int x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();
        if((phase==1||phase==2)&&timer==0){
            double vx=10,vz=0;
            double angle=36D/180D*Math.PI,cos=Math.cos(angle),sin=Math.sin(angle);
            for(int i=0;i<10;i++){
                var mob=UndeadSoldierContent.SOLDIER.get().create(server);
                if(mob==null)throw new IllegalStateException("Registered source UndeadSoldier factory returned null");
                // The original retries the identical position seven times on
                // the same entity. Do not invent an offset/search or fewer attempts.
                for(int j=0;j<7;j++){
                    mob.moveTo(x+.5+vx,y-5,z+.5+vz,i*36F,0);
                    if(mob.sourceCanSpawnHere()){
                        mob.finalizeSpawn(server,server.getCurrentDifficultyAt(mob.blockPosition()),MobSpawnType.SPAWNER,null);
                        server.addFreshEntity(mob);
                        break;
                    }
                }
                // Exact Vec3NT.rotateAroundYDeg recurrence, retaining accumulated rounding.
                double nextX=vx*cos+vz*sin;
                vz=vz*cos-vx*sin;vx=nextX;
            }
        }
        if(phase>2){
            if(server.getBlockEntity(worldPosition.above(18)) instanceof SkeletonHolderEntity holder){
                ItemStack reward;
                // This RNG draw occurs only if the real holder exists.
                if(server.random.nextInt(5)==0){
                    reward=new ItemStack(HbmContent.item("item_secret"));
                    reward.set(HbmHazards.LEGACY_METADATA.get(),3); // EnumSecretType.ABERRATOR
                }else{
                    reward=new ItemStack(PedestalContent.TABLET.get());
                    reward.set(HbmHazards.LEGACY_METADATA.get(),1);
                }
                holder.setItem(reward); // Actual holder performs source markDirty + block update.
            }
            server.setBlock(worldPosition,Blocks.OBSIDIAN.defaultBlockState(),Block.UPDATE_ALL);
        }
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("phase",phase);tag.putByte("type",(byte)type.ordinal());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);phase=tag.getInt("phase");
        var values=SpawnerType.values();type=values[Math.abs(tag.getByte("type")%values.length)];
        // No timer field is read: a fresh entity naturally starts at zero, just
        // as in the original, while loading into an existing entity leaves it alone.
    }
}
