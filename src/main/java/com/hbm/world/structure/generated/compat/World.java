package com.hbm.world.structure.generated.compat;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.NaturalStructures;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.*;
/** Bounded native WorldGenLevel writes; source algorithms retain their own exact integer loops and RNG. */
public final class World implements AutoCloseable {
    private static final ThreadLocal<World> PLANNING=new ThreadLocal<>();
    private final WorldGenLevel level;private final Structure.GenerationContext context;private final StructureBoundingBox clip;private final Map<BlockPos,Integer> placedMetadata=new HashMap<>();private int safeRemoval;
    private final Map<BlockPos,net.minecraft.world.level.block.state.BlockState> plannedStates=new HashMap<>();
    private final Map<BlockPos,Block> placedSources=new HashMap<>();
    private final Map<Long,Integer> highestTouched=new HashMap<>();
    private final Map<Long,net.minecraft.world.level.NoiseColumn> predictionColumns=new HashMap<>();
    private final Map<BlockPos,BlockEntity> plannedTiles=new HashMap<>();
    private final List<PlannedWrite> plannedWrites=new ArrayList<>();private final List<PlannedAttachment> plannedAttachments=new ArrayList<>();private final ArrayDeque<Runnable> deferred=new ArrayDeque<>();
    public record PlannedWrite(BlockPos position,net.minecraft.world.level.block.state.BlockState state,int flags){}
    public record PlannedAttachment(String kind,BlockPos position,net.minecraft.nbt.CompoundTag data){}
    /** Independent source bodies require actual surfaced/carved terrain supplied by the decoration phase. */
    public static World plan(Structure.GenerationContext context,Random random,WorldGenLevel terrain){if(PLANNING.get()!=null)throw new IllegalStateException("Nested source world planning");var world=new World(context,random,terrain);PLANNING.set(world);return world;}
    public static World planning(){return PLANNING.get();}
    public boolean isPlanning(){return context!=null;}
    public net.minecraft.core.HolderLookup.Provider lookup(){return context!=null?context.registryAccess():level().registryAccess();}
    public List<PlannedWrite> plannedWrites(){return List.copyOf(plannedWrites);}public Map<BlockPos,BlockEntity> plannedTiles(){return Map.copyOf(plannedTiles);}public List<PlannedAttachment> plannedAttachments(){return List.copyOf(plannedAttachments);}
    public void planAttachment(String kind,BlockPos pos,net.minecraft.nbt.CompoundTag data){if(context==null)throw new IllegalStateException("Not planning");plannedAttachments.add(new PlannedAttachment(kind,pos.immutable(),data.copy()));}
    public void defer(Runnable job){if(context==null)throw new IllegalStateException("Deferred source room jobs require native planning");deferred.add(job);}
    public void finishDeferred(){while(!deferred.isEmpty())deferred.remove().run();}
    @Override public void close(){if(PLANNING.get()==this)PLANNING.remove();predictionColumns.clear();}
    public net.minecraft.world.level.block.state.BlockState stateAt(int x,int y,int z){var position=new BlockPos(x,y,z);var planned=plannedStates.get(position);if(planned!=null)return planned;if(level!=null)return level.getBlockState(position);return predictionColumn(x,z).getBlock(y);}
    /** Prediction-only input for the existing weighted start geometry; never used by plan(...,terrain). */
    private net.minecraft.world.level.NoiseColumn predictionColumn(int x,int z){
        if(level!=null)throw new IllegalStateException("Actual source terrain cannot fall back to a noise prediction");
        return predictionColumns.computeIfAbsent(net.minecraft.world.level.ChunkPos.asLong(x,z),unused->
            context.chunkGenerator().getBaseColumn(x,z,context.heightAccessor(),context.randomState()));
    }
    public final Random rand;public final Provider provider=new Provider();
    /** Existing weighted structure start prediction only; independent decoration uses plan(...,terrain). */
    public World(Structure.GenerationContext context,Random random){this.context=context;this.rand=random;level=null;clip=null;}
    private World(Structure.GenerationContext context,Random random,WorldGenLevel terrain){this.context=Objects.requireNonNull(context);this.rand=Objects.requireNonNull(random);this.level=Objects.requireNonNull(terrain);clip=null;}
    public World(WorldGenLevel level,StructureBoundingBox clip,Random random){this.level=level;this.clip=clip;this.rand=random;context=null;}
    public WorldGenLevel level(){if(level==null)throw new IllegalStateException("Source start attempted a block write before native piece placement");return level;}
    public final class Provider {public int getAverageGroundLevel(){return context!=null?context.chunkGenerator() instanceof FlatLevelSource?context.heightAccessor().getMinBuildHeight()+4:64:level().getLevel().getChunkSource().getGenerator() instanceof FlatLevelSource?level().getMinBuildHeight()+4:64;}}
    public record Biome(float heightVariation,float temperature,float rainfall,boolean mesa,boolean lightning,String biome){}
    public Biome getBiomeGenForCoords(int x,int z){var biome=level!=null?com.hbm.world.structure.StructureSurfaceBiomes.sample(level,x,z):com.hbm.world.structure.StructureSurfaceBiomes.sample(context,x,z);var profile=NaturalStructures.profile(biome);if(profile==null)throw new IllegalStateException("No actual source biome profile for "+biome);return new Biome(profile.variation(),profile.temperature(),profile.rainfall(),profile.mesa(),profile.lightning(),profile.biome());}
    public int getSeaLevel(){return context!=null?context.chunkGenerator().getSeaLevel():level().getSeaLevel();}
    public int getHeightValue(int x,int z){
        int minimum=level!=null?level.getMinBuildHeight():context.heightAccessor().getMinBuildHeight();
        int y=level!=null?level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)-1:context.chunkGenerator().getFirstFreeHeight(x,z,Heightmap.Types.WORLD_SURFACE_WG,context.heightAccessor(),context.randomState())-1;
        y=Math.max(y,highestTouched.getOrDefault(BlockPos.asLong(x,0,z),y));
        for(;y>=minimum;y--){var state=stateAt(x,y,z);if(level!=null?state.getLightBlock(level,new BlockPos(x,y,z))>0:!state.isAir())return y+1;}
        return minimum;
    }
    /** Real terrain and prior source writes when decorating; weighted start prediction remains explicit. */
    public int getTerrainTopSolidOrLiquidBlock(int x,int z){
        net.minecraft.world.level.LevelHeightAccessor height=level!=null?level:context.heightAccessor();
        int y=height.getMaxBuildHeight()-1;
        for(;y>=height.getMinBuildHeight();y--){var state=stateAt(x,y,z);if(state.blocksMotion()&&!state.is(BlockTags.LEAVES))return y+1;}
        return height.getMinBuildHeight();
    }
    public int getTopSolidOrLiquidBlock(int x,int z){int minimum=level!=null?level.getMinBuildHeight():context.heightAccessor().getMinBuildHeight(),y=getHeightValue(x,z);for(;y>=minimum;y--){var state=stateAt(x,y,z);if(state.blocksMotion()&&!state.is(BlockTags.LEAVES))return y+1;}return minimum;}
    public Block getBlock(int x,int y,int z){return Block.observed(stateAt(x,y,z));}
    public boolean sameBlock(int x,int y,int z,Block block){return stateAt(x,y,z).getBlock()==block.state(0).getBlock();}
    public boolean isAirBlock(int x,int y,int z){return stateAt(x,y,z).isAir();}
    public boolean doesBlockHaveSolidTopSurface(World ignored,int x,int y,int z){var pos=new BlockPos(x,y,z);return stateAt(x,y,z).isFaceSturdy(level(),pos,Direction.UP);}
    public boolean contains(BlockPos pos){return context!=null?!(level!=null?level:context.heightAccessor()).isOutsideBuildHeight(pos):clip!=null&&clip.isVecInside(pos.getX(),pos.getY(),pos.getZ());}
    public BlockEntity getTileEntity(int x,int y,int z){var pos=new BlockPos(x,y,z);return context!=null?plannedTiles.get(pos):contains(pos)?level().getBlockEntity(pos):null;}
    public int getBlockMetadata(int x,int y,int z){var pos=new BlockPos(x,y,z);var saved=placedMetadata.get(pos);if(saved!=null)return saved;var state=stateAt(x,y,z);var property=state.getBlock().getStateDefinition().getProperty("legacy_meta");if(property!=null&&state.getValue(property) instanceof Integer value)return value;if(state.getBlock() instanceof net.minecraft.world.level.block.ChestBlock)return state.getValue(net.minecraft.world.level.block.ChestBlock.FACING).get3DDataValue();return 0;}
    public boolean setBlock(int x,int y,int z,Block block){return setBlock(x,y,z,block,0,3);}
    public boolean setBlock(int x,int y,int z,Block block,int metadata,int flags){var pos=new BlockPos(x,y,z);if(!contains(pos))return false;return setSourceState(pos,block,metadata,flags,block.state(metadata));}
    private boolean setSourceState(BlockPos pos,Block block,int metadata,int flags,net.minecraft.world.level.block.state.BlockState state){
        int x=pos.getX(),y=pos.getY(),z=pos.getZ();placedMetadata.put(pos,metadata);placedSources.put(pos,block);
        if(context!=null){
            var previous=stateAt(x,y,z);highestTouched.merge(BlockPos.asLong(x,0,z),y,Math::max);plannedStates.put(pos,state);plannedWrites.add(new PlannedWrite(pos,state,flags));
            if(previous.getBlock()!=state.getBlock()){
                plannedTiles.remove(pos);
                if(state.getBlock() instanceof net.minecraft.world.level.block.EntityBlock entity){var tile=entity.newBlockEntity(pos,state);if(tile!=null)plannedTiles.put(pos,tile);}
            }else if(plannedTiles.get(pos)!=null)plannedTiles.get(pos).setBlockState(state);
            else if(level!=null&&state.getBlock() instanceof net.minecraft.world.level.block.EntityBlock entity){
                // Original setBlock preserves a same-block inventory. Copy it into
                // this plan so source loot draws cannot mutate live terrain early.
                var existing=level.getBlockEntity(pos);
                if(existing!=null){var tile=entity.newBlockEntity(pos,state);if(tile!=null){tile.loadWithComponents(existing.saveWithFullMetadata(lookup()),lookup());plannedTiles.put(pos,tile);}}
            }
            return true;
        }
        if(level().isOutsideBuildHeight(pos))return false;
        if(safeRemoval>0){boolean[] changed={false};DummyableBlock.safely(()->changed[0]=level().setBlock(pos,state,flags));return changed[0];}
        return level().setBlock(pos,state,flags);
    }
    public boolean setBlockToAir(int x,int y,int z){return setBlock(x,y,z,Blocks.air);}
    public boolean setBlockMetadataWithNotify(int x,int y,int z,int metadata,int flags){var source=placedSources.get(new BlockPos(x,y,z));if(source==null)throw new IllegalStateException("Original metadata mutation has no preceding source placement");return setBlock(x,y,z,source,metadata,flags);}
    public void beginSafeRemoval(){safeRemoval++;}public void endSafeRemoval(){safeRemoval=Math.max(0,safeRemoval-1);}
    /** Original BlockChest.func_149954_e automatic facing, called by the vanilla source chest helper. */
    public void sourceChestFacing(int x,int y,int z){
        boolean north=sameBlock(x,y,z-1,Blocks.chest),south=sameBlock(x,y,z+1,Blocks.chest),west=sameBlock(x-1,y,z,Blocks.chest),east=sameBlock(x+1,y,z,Blocks.chest);
        boolean n=getBlock(x,y,z-1).isOpaqueCube(),s=getBlock(x,y,z+1).isOpaqueCube(),w=getBlock(x-1,y,z).isOpaqueCube(),e=getBlock(x+1,y,z).isOpaqueCube();
        int metadata;
        if(north||south){int nz=north?z-1:z+1;boolean wn=getBlock(x-1,y,nz).isOpaqueCube(),en=getBlock(x+1,y,nz).isOpaqueCube();metadata=getBlockMetadata(x,y,nz)==4?4:5;if((w||wn)&&!e&&!en)metadata=5;if((e||en)&&!w&&!wn)metadata=4;}
        else if(west||east){int nx=west?x-1:x+1;boolean nn=getBlock(nx,y,z-1).isOpaqueCube(),sn=getBlock(nx,y,z+1).isOpaqueCube();metadata=getBlockMetadata(nx,y,z)==2?2:3;if((n||nn)&&!s&&!sn)metadata=3;if((s||sn)&&!n&&!nn)metadata=2;}
        else{metadata=3;if(n&&!s)metadata=3;if(s&&!n)metadata=2;if(w&&!e)metadata=5;if(e&&!w)metadata=4;}
        setBlock(x,y,z,Blocks.chest,metadata,3);
    }
    public static int[] rotateDimensions(int[] dimensions,ForgeDirection direction){return DummyableBlock.rotate(dimensions,direction.nativeDirection());}
    /** Flatten the source's lower direction/open bits and upper hinge bit into BOTH native states before publishing either cell. */
    public static void placeDoorHalves(World world,int x,int y,int z,Block door,int lowerMetadata,int upperMetadata,int flags){
        var lower=door.state(lowerMetadata);var upper=door.state(upperMetadata);
        if(!(lower.getBlock() instanceof net.minecraft.world.level.block.DoorBlock)||!upper.is(lower.getBlock())
            ||lower.getValue(net.minecraft.world.level.block.DoorBlock.HALF)!=net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER
            ||upper.getValue(net.minecraft.world.level.block.DoorBlock.HALF)!=net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER)
            throw new IllegalStateException("Original split-door helper requires matching lower/upper states: "+door.sourceId);
        lower=com.hbm.block.door.SourceDoorBlock.joinedHalf(lower,upper,net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER);
        upper=lower.setValue(net.minecraft.world.level.block.DoorBlock.HALF,net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER);
        var pos=new BlockPos(x,y,z);
        if(world.contains(pos))world.setSourceState(pos,door,lowerMetadata,flags,lower);
        if(world.contains(pos.above()))world.setSourceState(pos.above(),door,upperMetadata,flags,upper);
    }
    /** Original ItemDoor automatic double-door hinge selection. */
    public static void placeDoorBlock(World world,int x,int y,int z,int facing,Block door){int dx=0,dz=0;switch(facing&3){case 0->dz=1;case 1->dx=-1;case 2->dz=-1;case 3->dx=1;}int left=(world.getBlock(x-dx,y,z-dz).isNormalCube()?1:0)+(world.getBlock(x-dx,y+1,z-dz).isNormalCube()?1:0),right=(world.getBlock(x+dx,y,z+dz).isNormalCube()?1:0)+(world.getBlock(x+dx,y+1,z+dz).isNormalCube()?1:0);boolean neighborLeft=world.sameBlock(x-dx,y,z-dz,door)||world.sameBlock(x-dx,y+1,z-dz,door),neighborRight=world.sameBlock(x+dx,y,z+dz,door)||world.sameBlock(x+dx,y+1,z+dz,door);boolean hinge=neighborLeft&&!neighborRight||right>left;var lower=door.state(facing);if(lower.getBlock() instanceof net.minecraft.world.level.block.DoorBlock){
        lower=lower.setValue(net.minecraft.world.level.block.DoorBlock.HINGE,hinge?net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT:net.minecraft.world.level.block.state.properties.DoorHingeSide.LEFT);
        var lowerPos=new BlockPos(x,y,z);if(world.contains(lowerPos))world.setSourceState(lowerPos,door,facing,2,lower);
        var upperPos=lowerPos.above();if(world.contains(upperPos))world.setSourceState(upperPos,door,8|(hinge?1:0),2,lower.setValue(net.minecraft.world.level.block.DoorBlock.HALF,net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER));
    }else throw new IllegalStateException("Original ItemDoor helper requires an actual door: "+door.sourceId);}
}
