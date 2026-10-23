package com.hbm.machine.reactor.dfc;

import com.hbm.bomb.conventional.MistExposure;
import com.hbm.bomb.nuclear.*;
import com.hbm.bomb.nuclear.programmable.NuclearRainbowCloud;
import com.hbm.damage.HbmDamageSources;
import com.hbm.fluid.*;
import com.hbm.item.dfc.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.radiation.HbmRadiation;
import com.hbm.satellite.SatelliteRayEvents;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Source DFC burn, fuel multipliers, decaying restriction field, radiation and real antitime-gated meltdown. */
public final class DfcCoreEntity extends ThermalMachine {
    public int field,heat,color,consumption,prevConsumption;
    public boolean meltdownTick;
    private boolean lastTickValid;
    private int displayHeat;
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("DEUTERIUM"),128000),new FluidTank(HbmFluids.fromName("TRITIUM"),128000)};
    public DfcCoreEntity(BlockPos pos,BlockState state){super(DfcCoreContent.ENTITY.get(),pos,state,3);}
    @Override public boolean isValidBlockState(BlockState state){return state.is(DfcCoreContent.BLOCK.get());}
    @Override protected String title(){return "container.dfcCore";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new DfcCoreMenu(id,inventory,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,DfcCoreEntity core){if(level instanceof ServerLevel server)core.serverTick(server);}
    private void serverTick(ServerLevel server) {
        prevConsumption=consumption;consumption=0;meltdownTick=false;
        int cx=worldPosition.getX()>>4,cz=worldPosition.getZ()>>4;var chunks=server.getChunkSource();
        lastTickValid=chunks.hasChunk(cx,cz)&&chunks.hasChunk(cx+1,cz+1)&&chunks.hasChunk(cx+1,cz-1)&&chunks.hasChunk(cx-1,cz+1)&&chunks.hasChunk(cx-1,cz-1);
        if(lastTickValid&&heat>0&&heat>=field)meltdown(server);
        color=slots.get(0).getItem()instanceof DfcCatalystItem first&&slots.get(2).getItem()instanceof DfcCatalystItem second?average(first.getColor(),second.getColor()):0;
        if(heat>0){radiation(server);if(server.getGameTime()%100==0)SatelliteRayEvents.reportEvent(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),SatelliteRayEvents.RayEvent.INFO_PARTICLE,200);}
        // The original packet captures this tick's heat before resetting the accumulator.
        displayHeat=heat;sync(250);heat=0;if(lastTickValid&&field>0)field--;setChanged();
    }
    private void meltdown(ServerLevel server) {
        int fill=tanks[0].getFill()+tanks[1].getFill(),max=tanks[0].getMaxFill()+tanks[1].getMaxFill(),mod=heat*10;
        int size=Math.max(Math.min(fill*mod/max,1000),50);
        boolean canExplode=true;
        synchronized(NuclearExplosionMK3.at){var iterator=NuclearExplosionMK3.at.entrySet().iterator();while(iterator.hasNext()){
            var entry=iterator.next();if(entry.getValue()<server.getGameTime()){iterator.remove();continue;}
            var position=entry.getKey();if(position.dimension!=server.dimension())continue;
            if(Vec3.atCenterOf(worldPosition).distanceTo(new Vec3(position.x,position.y,position.z))<300){canExplode=false;break;}
        }}
        if(canExplode){
            var blast=new NuclearExplosionMK3(NuclearContent.MK3.get(),server);blast.setPos(Vec3.atCenterOf(worldPosition));blast.destructionRange=size;blast.speed=NuclearConfig.BLAST_SPEED.get();blast.coefficient=1;blast.waste=false;blast.loadChunk(worldPosition.getX()>>4,worldPosition.getZ()>>4);server.addFreshEntity(blast);
            server.playSound(null,worldPosition,SoundEvents.GENERIC_EXPLODE.value(),SoundSource.BLOCKS,100000F,1);
            var cloud=new NuclearRainbowCloud(server,size);cloud.setPos(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());server.addFreshEntity(cloud);
        }else{meltdownTick=true;HbmRadiation.incrementRad(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),100);}
    }
    private void radiation(ServerLevel server) {
        double scale=meltdownTick?5:3,range=meltdownTick?50:10;var center=Vec3.atCenterOf(worldPosition);
        var source=center.add(0,6,0);
        for(var entity:server.getEntities((Entity)null,new AABB(center,center).inflate(range))){
            if(entity instanceof Player player&&(player.isSpectator()||MistExposure.checkForHazmat(player)))continue;
            var target=new Vec3(entity.getX(),entity.getY()+entity.getEyeHeight(),entity.getZ());
            if(server.clip(new ClipContext(source,target,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,entity)).getType()==HitResult.Type.MISS){entity.hurt(HbmDamageSources.source(server,HbmDamageSources.ams),1000);entity.igniteForSeconds(3);}
        }
        for(var entity:server.getEntities((Entity)null,new AABB(center,center).inflate(scale))){
            if(entity instanceof Player player&&(player.isSpectator()||DfcProtection.checkForHaz2(player)))continue;
            entity.hurt(HbmDamageSources.source(server,HbmDamageSources.amsCore),10000);
        }
    }
    public boolean isReady(){return lastTickValid&&getCore()!=0&&color!=0&&getFuelEfficiency(tanks[0].getTankType())>0&&getFuelEfficiency(tanks[1].getTankType())>0;}
    public long burn(long joules) {
        if(!isReady())return joules;
        int demand=(int)Math.ceil((double)joules/1000D);if(tanks[0].getFill()<demand||tanks[1].getFill()<demand)return joules;
        consumption+=demand;heat+=(int)Math.ceil((double)joules/10000D);tanks[0].setFill(tanks[0].getFill()-demand);tanks[1].setFill(tanks[1].getFill()-demand);setChanged();
        return (long)(joules*getCore()*getFuelEfficiency(tanks[0].getTankType())*getFuelEfficiency(tanks[1].getTankType()));
    }
    public float getFuelEfficiency(HbmFluidType type){return switch(type.getName()){case "HYDROGEN"->1F;case "DEUTERIUM","XENON"->1.5F;case "TRITIUM"->1.7F;case "OXYGEN"->1.2F;case "PEROXIDE"->1.4F;case "SAS3"->2F;case "BALEFIRE"->2.5F;case "AMAT"->2.2F;case "ASCHRAB"->2.7F;default->0F;};}
    public int getCore(){if(!(slots.get(1).getItem()instanceof DfcCoreItem core))return 0;return switch(core.data.id()){case "ams_core_sing"->500;case "ams_core_wormhole"->650;case "ams_core_eyeofharmony"->800;case "ams_core_thingy"->2500;default->0;};}
    private static int average(int first,int second){int r=((first>>16&255)+(second>>16&255))/2,g=((first>>8&255)+(second>>8&255))/2,b=((first&255)+(second&255))/2;return r<<16|g<<8|b;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(Vec3.atCenterOf(worldPosition))<=144;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.dfc.field",field,100,0x4040ff),new ProgressView("jade.hbm.dfc.heat",level!=null&&level.isClientSide?heat:displayHeat,100,0xff8000));}
    @Override protected void saveMachine(CompoundTag tag){for(int i=0;i<2;i++)tanks[i].writeToNBT(tag,"fuel"+(i+1));tag.putInt("field",field);}
    @Override protected void loadMachine(CompoundTag tag){for(int i=0;i<2;i++)tanks[i].readFromNBT(tag,"fuel"+(i+1));field=tag.getInt("field");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("heat",heat);tag.putInt("color",color);tag.putBoolean("meltdownTick",meltdownTick);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);heat=tag.getInt("heat");color=tag.getInt("color");meltdownTick=tag.getBoolean("meltdownTick");}
}
