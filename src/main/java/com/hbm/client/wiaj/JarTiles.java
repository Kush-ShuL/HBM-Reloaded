package com.hbm.client.wiaj;

import com.hbm.material.*;
import com.hbm.machine.foundry.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Exact local tutorial-only faux tile state. It is never registered, saved or inserted in the game world. */
public final class JarTiles {
 /** Starts a local source foundry stream when this action is replayed, never in the real Level. */
 public static com.hbm.client.wiaj.actions.IJarAction foundryFlow(int actorId,double x,double y,double z,net.minecraft.core.Direction direction,double length,double base,double offset,NtmMaterial material,int duration,int period){
  return new com.hbm.client.wiaj.actions.IJarAction(){
   @Override public int getDuration(){return 0;}
   @Override public void act(WorldInAJar world,JarScene scene){
    var data=new net.minecraft.nbt.CompoundTag();data.putDouble("x",x);data.putDouble("y",y);data.putDouble("z",z);data.putInt("color",material.moltenColor());data.putInt("dir",direction.get3DDataValue());data.putDouble("len",length);data.putDouble("base",base);data.putDouble("off",offset);data.putInt("started",scene.script.ticksElapsed);data.putInt("duration",duration);data.putInt("period",period);
    scene.script.actors.put(actorId,new com.hbm.client.wiaj.actors.ActorTileEntity(new com.hbm.client.wiaj.actors.JarMachineActors.FoundryFlow(),data));
   }
  };
 }
 public static class Crucible {public int meta;public final List<MaterialStack> recipeStack=new ArrayList<>(),wasteStack=new ArrayList<>();}
 public static class Foundry {
  public final FoundryContent.Kind kind;public NtmMaterial type;public int amount;public final ItemStack[] slots={ItemStack.EMPTY,ItemStack.EMPTY};private FoundryBlockEntity renderTile;
  protected Foundry(FoundryContent.Kind kind){this.kind=kind;}
  public FoundryBlockEntity renderTile(BlockPos pos,BlockState state){if(renderTile==null||!renderTile.getBlockPos().equals(pos)||renderTile.getBlockState()!=state)renderTile=new FoundryBlockEntity(pos,state);renderTile.type=type;renderTile.amount=amount;renderTile.slots.set(0,slots[0]);renderTile.slots.set(1,slots[1]);return renderTile;}
 }
 public static class Basin extends Foundry {public Basin(){super(FoundryContent.Kind.BASIN);}}
 public static class Mold extends Foundry {public Mold(){super(FoundryContent.Kind.MOLD);}}
 public static class Channel extends Foundry {public Channel(){super(FoundryContent.Kind.CHANNEL);}}
 public static class Tank extends Foundry {public Tank(){super(FoundryContent.Kind.TANK);}}
 public static class Outlet extends Foundry {public boolean isClosed;public Outlet(){super(FoundryContent.Kind.OUTLET);}}
 public static class Pipe {
  private com.hbm.fluid.HbmFluidType type=com.hbm.fluid.HbmFluids.NONE;private final Map<BlockPos,com.hbm.block.pipe.NativePipeBlockEntity> renderTiles=new HashMap<>();
  public void setType(com.hbm.fluid.HbmFluidType type){this.type=type;}
  public com.hbm.block.pipe.NativePipeBlockEntity renderTile(BlockPos pos,BlockState state){var tile=renderTiles.computeIfAbsent(pos.immutable(),key->new com.hbm.block.pipe.NativePipeBlockEntity(key,state));if(tile.getFluidType()!=type)tile.setType(type);return tile;}
 }
 private JarTiles(){}
}
