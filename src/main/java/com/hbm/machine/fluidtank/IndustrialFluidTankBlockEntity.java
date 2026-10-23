package com.hbm.machine.fluidtank;

import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.fluid.*;
import com.hbm.fluid.network.FluidPorts;
import com.hbm.machine.barrel.BarrelBlockEntity;
import com.hbm.multiblock.*;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.recipe.generic.GenericIngredient;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Source256000mB industrial tank; damage, rate limits, leak traits and extinguishing. */
public final class IndustrialFluidTankBlockEntity extends BarrelBlockEntity implements NamedMultiblock,IRepairable,IOverpressurable {
    public boolean hasExploded,onFire;
    public Explosion lastExplosion;
    private List<BlockPos> currentPorts=List.of();
    public IndustrialFluidTankBlockEntity(BlockPos pos,BlockState state){super(IndustrialFluidTankContent.ENTITY.get(),pos,state,256000);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public static void tick(Level level,BlockPos pos,BlockState state,IndustrialFluidTankBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);for(var player:level.getEntitiesOfClass(Player.class,machine.ladderBounds()))LegacyFauxLadder.mark(player);}
    @Override public Component getDisplayName(){var original=super.getDisplayName();return original.equals(Component.translatable("container.barrel"))?Component.translatable("container.fluidtank"):original;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new IndustrialFluidTankMenu(id,inventory,this,worldPosition);}
    @Override public int trackingRange(){return 150;}
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.offset(-1,0,-1),core.offset(1,0,-1),core.offset(1,0,1),core.offset(-1,0,1)};}
    @Override protected void updatePorts(ServerLevel server){
        var loaded=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos)))loaded.add(pos);
        if(!currentPorts.equals(loaded)){disconnect();currentPorts=List.copyOf(loaded);var connections=new ArrayList<NodeConnection>();for(var pos:loaded){Direction x=pos.getX()<worldPosition.getX()?Direction.WEST:Direction.EAST,z=pos.getZ()<worldPosition.getZ()?Direction.NORTH:Direction.SOUTH;connections.add(new NodeConnection(pos.relative(x),x));connections.add(new NodeConnection(pos.relative(z),z));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}
        if(fluidPorts!=null)fluidPorts.update(server,this);
    }
    @Override protected void disconnect(){super.disconnect();currentPorts=List.of();}
    @Override protected void updateComparators(ServerLevel server){for(var pos:portPositions(worldPosition)){server.updateNeighbourForOutputSignal(pos,getBlockState().getBlock());for(var side:Direction.values())server.updateNeighborsAt(pos.relative(side),getBlockState().getBlock());}server.updateNeighbourForOutputSignal(worldPosition,getBlockState().getBlock());}
    public AABB ladderBounds(){Direction dir=DummyableBlock.facing(getBlockState()),rot=dir.getClockWise();return new AABB(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),worldPosition.getX()+1,worldPosition.getY()+2.875,worldPosition.getZ()+1).move(dir.getStepX()*.5-rot.getStepX()*2.25,0,dir.getStepZ()*.5-rot.getStepZ()*2.25);}
    @Override public long getReceiverSpeed(HbmFluidType type,int pressure){return hasExploded?0:mode==0||mode==1?Math.max(500,(tank.getMaxFill()-tank.getFill())/100):0;}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return hasExploded?0:mode==1||mode==2?Math.max(500,tank.getFill()/100):0;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return true;}
    @Override public void checkFluidInteraction(ServerLevel server){
        var type=tank.getTankType();if(type.isAntimatter()){antimatter(server);explode();tank.setFill(0);}
        if(type.isHighlyCorrosive())explode();
        if(hasExploded){int amount=type.isAntimatter()?tank.getFill():type.hasTrait("gaseous")||type.hasTrait("gaseous_art")?Math.min(tank.getFill(),tank.getMaxFill()/100):Math.min(tank.getFill(),tank.getMaxFill()/10000);updateLeak(server,amount);}
    }
    private void antimatter(ServerLevel server){new ExplosionVNT(server,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,5).makeAmat().setBlockAllocator(null).setBlockProcessor(null).explode();}
    public void explode(){hasExploded=true;onFire=tank.getTankType().hasTrait("flammable");setChanged();}
    @Override public void explode(Level level,BlockPos pos){if(hasExploded)return;explode();}
    public void updateLeak(ServerLevel server,int amount){
        if(!hasExploded||amount<=0)return;tank.setFill(Math.max(0,tank.getFill()-amount));setChanged();var type=tank.getTankType();double x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();
        // FluidType.onFluidRelease is empty in X5846; its trait callbacks below are the actual release path.
        if(type.isAntimatter())antimatter(server);
        else if(type.hasTrait("flammable")&&onFire){for(var entity:server.getEntitiesOfClass(Entity.class,new AABB(x-1.5,y,z-1.5,x+2.5,y+5,z+2.5)))entity.igniteForSeconds(5);var random=server.random;LegacyFluidEffects.gasFlame(server,x+random.nextDouble(),y+.5+random.nextDouble(),z+random.nextDouble(),random.nextGaussian()*.2,.1,random.nextGaussian()*.2);if(server.getGameTime()%5==0)type.release(server,worldPosition,tank,amount*5,FluidTraits.Release.BURN);}
        else if(type.hasTrait("gaseous")||type.hasTrait("gaseous_art")){if(server.getGameTime()%5==0){LegacyFluidEffects.tower(server,x+.5,y+1,z+.5,new LegacyFluidEffects.Tower(1,1,5,100+server.random.nextInt(20)).color(type.getColor()));type.release(server,worldPosition,tank,amount*5,FluidTraits.Release.SPILL);}}
    }
    @Override public boolean isDamaged(){return hasExploded;}
    @Override public List<GenericIngredient> getRepairMaterials(){return List.of(new GenericIngredient(Ingredient.of(TagKey.create(Registries.ITEM,ResourceLocation.parse("hbm:legacy_ore_dict/platesteel"))),6));}
    @Override public void repair(Player player){hasExploded=false;setChanged();}
    @Override public void tryExtinguish(Level level,BlockPos pos,EnumExtinguishType type){if(!hasExploded||!onFire)return;if(type==EnumExtinguishType.WATER){if(tank.getTankType().hasTrait("liquid")){level.explode(null,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,5,true,Level.ExplosionInteraction.BLOCK);}else{onFire=false;setChanged();return;}}if(type==EnumExtinguishType.FOAM||type==EnumExtinguishType.CO2){onFire=false;setChanged();}}
    @Override protected void saveState(CompoundTag tag){super.saveState(tag);tag.putBoolean("exploded",hasExploded);tag.putBoolean("onFire",onFire);}
    @Override protected void loadState(CompoundTag tag){super.loadState(tag);hasExploded=tag.getBoolean("exploded");onFire=tag.getBoolean("onFire");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.remove("onFire");return tag;}
    @Override public void writeItemData(CompoundTag tag){if(tank.getFill()==0&&!hasExploded)return;var data=new CompoundTag();tank.writeToNBT(data,"tank");data.putShort("mode",mode);data.putBoolean("hasExploded",hasExploded);data.putBoolean("onFire",onFire);tag.put("persistent",data);}
    @Override protected void readItemData(CompoundTag tag){super.readItemData(tag);hasExploded=tag.getBoolean("hasExploded");onFire=tag.getBoolean("onFire");}
}
