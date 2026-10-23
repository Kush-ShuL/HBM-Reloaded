package com.hbm.world.structure.generated;
import com.hbm.world.structure.generated.compat.World;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
/** Explicit source BE mutations, bound only to real implementations; unsupported dependencies fail before placement. */
public final class GeneratedAttachments {
    public enum BobbleType {NONE,STRENGTH,PERCEPTION,ENDURANCE,CHARISMA,INTELLIGENCE,AGILITY,LUCK,BOB,FRIZZLE,PU238,VT,DOC,BLUEHAT,PHEO,ADAM29,UFFR,VAER,NOS,DRILLGON,CIRNO,MICROWAVE,PEEP,MELLOW,ABEL}
    public interface Backend {boolean supports(String sourceKind);void apply(String sourceKind,WorldGenLevel level,BlockPos position,CompoundTag values);}
    private static Backend backend;
    public static void install(Backend actual){backend=Objects.requireNonNull(actual);}
    public static void require(Set<String> kinds){for(var kind:kinds)if(backend==null||!backend.supports(kind))throw new IllegalStateException("Real generated structure BE dependency is not ready: "+kind);}
    private static void apply(String kind,World world,int x,int y,int z,CompoundTag data){var pos=new BlockPos(x,y,z);if(!world.contains(pos))return;require(Set.of(kind));if(world.isPlanning())world.planAttachment(kind,pos,data);else backend.apply(kind,world.level(),pos,data);}
    public static void applySource(String kind,World world,BlockPos pos,CompoundTag data){apply(kind,world,pos.getX(),pos.getY(),pos.getZ(),data);}
    public static void applyNative(String kind,WorldGenLevel level,BlockPos pos,CompoundTag data){require(Set.of(kind));backend.apply(kind,level,pos,data);}
    public static void radio(World world,int x,int y,int z,int frequency){var data=new CompoundTag();data.putString("channel",String.valueOf(frequency));data.putInt("lastState",0);apply("radio_torch",world,x,y,z,data);}
    public static void mine(World world,int x,int y,int z){var data=new CompoundTag();data.putBoolean("waitingForPlayer",true);apply("landmine",world,x,y,z,data);}
    public static void launchpad(World world,int x,int y,int z){var data=new CompoundTag();data.putBoolean("missileLoaded",true);apply("launch_pad_rusted",world,x,y,z,data);}
    public static boolean isBobble(BlockEntity tile){return tile!=null&&net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(tile.getBlockState().getBlock()).toString().equals("hbm:bobblehead");}
    public static Bobble bobble(World world,BlockEntity tile){if(tile==null)return null;if(!isBobble(tile))throw new IllegalStateException("Source bobble expected actual hbm:bobblehead, got "+tile);return new Bobble(world,tile);}
    public static final class Bobble {
        private final World world;private final BlockEntity tile;public BobbleType type=BobbleType.NONE;
        Bobble(World world,BlockEntity tile){this.world=Objects.requireNonNull(world);this.tile=tile;}
        public void markDirty(){
            var data=new CompoundTag();data.putInt("type",type.ordinal());
            // ProtoChunk entities may not have a Level yet; use the caller's actual planning/placement context.
            applySource("bobblehead",world,tile.getBlockPos(),data);tile.setChanged();
        }
    }
    private GeneratedAttachments(){}
}
