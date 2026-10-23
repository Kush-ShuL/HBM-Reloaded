package com.hbm.machine.pa.cooled;
import com.hbm.api.computer.*;
import com.hbm.advancement.HbmAdvancements;
import com.hbm.bomb.nuclear.SatelliteBursts;
import com.hbm.machine.pa.physics.*;
import com.hbm.machine.pa.recipe.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.satellite.SatelliteRayEvents;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Source detector consumes the particle even when its outputs are obstructed. */
public final class PaDetectorEntity extends PaCooledEntity implements PaParticleUser {
 public static final long USAGE=100000;public PaDetectorEntity(BlockPos pos,BlockState state){super(PaContent.ENTITY.get(),PaKind.DETECTOR,pos,state);}
 private ForgeDirection axis(){return ForgeDirection.getOrientation(DummyableBlock.facing(getBlockState()).get3DDataValue()).getRotation(ForgeDirection.DOWN);}
 @Override public boolean canParticleEnter(PaParticle particle,ForgeDirection dir,int x,int y,int z){var axis=axis();return x==worldPosition.getX()-axis.offsetX*4&&y==worldPosition.getY()&&z==worldPosition.getZ()-axis.offsetZ*4&&dir==axis;}
 @Override public void onEnter(PaParticle particle,ForgeDirection dir){particle.invalid=true;if(particle.defocus>0){particle.crash(PaState.CRASH_DEFOCUS);return;}if(!hasPower(USAGE)){particle.crash(PaState.CRASH_NOPOWER);return;}if(!isCool()){particle.crash(PaState.CRASH_NOCOOL);return;}usePower(USAGE);var recipe=PaRecipes.find(level.getRecipeManager(),particle.input1,particle.input2);if(recipe==null){particle.crash(PaState.CRASH_NORECIPE);return;}if(particle.momentum<recipe.momentum()){particle.crash(PaState.CRASH_UNDERSPEED);return;}ItemStack one=recipe.output1(),two=recipe.output2().orElse(ItemStack.EMPTY);if(checkSlot(one,1,3)&&checkSlot(two,2,4)){if(!one.getCraftingRemainingItem().isEmpty())removeItem(1,1);if(!two.isEmpty()&&!two.getCraftingRemainingItem().isEmpty())removeItem(2,1);addOutput(one,3);addOutput(two,4);}if(isDigamma(one)||isDigamma(two)){var bounds=new AABB(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5).inflate(100,50,100);for(var player:((ServerLevel)level).getPlayers(player->player.getBoundingBox().intersects(bounds)))HbmAdvancements.trigger(player,"achOmega12");}SatelliteBursts.reportEvent(level,SatelliteBursts.DURATION_MEDIUM,SatelliteBursts.BurstIntensity.MEDIUM,worldPosition.getX(),worldPosition.getZ());SatelliteRayEvents.reportEvent(level,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),SatelliteRayEvents.RayEvent.INFO_PARTICLE,600);particle.crash(PaState.SUCCESS);setChanged();}
 private static boolean isDigamma(ItemStack stack){return !stack.isEmpty()&&BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:particle_digamma");}
 private void addOutput(ItemStack result,int slot){if(result.isEmpty())return;if(slots[slot].isEmpty())slots[slot]=result.copy();else slots[slot].grow(result.getCount());}
 private static boolean sameSourceItem(ItemStack a,ItemStack b){return a.is(b.getItem())&&a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue())==b.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),b.getDamageValue());}
 public boolean checkSlot(ItemStack output,int containerSlot,int outputSlot){if(output.isEmpty())return true;if(!slots[outputSlot].isEmpty()&&(!sameSourceItem(slots[outputSlot],output)||slots[outputSlot].getCount()+output.getCount()>output.getMaxStackSize()))return false;var container=output.getCraftingRemainingItem();return container.isEmpty()||!slots[containerSlot].isEmpty()&&sameSourceItem(slots[containerSlot],container);}
 @Override public BlockPos getExitPos(PaParticle particle){return null;}
 @Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getCoolant","getCrafting","getInfo"};}@Override public Object[] callComputer(String name,ComputerArguments args){return switch(name){case "getCrafting"->craftingInfo();case "getInfo"->{var values=new ArrayList<>(Arrays.asList(basicInfo()));values.addAll(Arrays.asList(craftingInfo()));yield values.toArray();}default->super.callComputer(name,args);};}
}
