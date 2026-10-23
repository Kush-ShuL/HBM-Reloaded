package com.hbm.world.structure;

import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;

/** Native template placement with one persisted, source-window height anchor per start. */
public final class NativeTemplatePiece extends StructurePiece {
    private final String sourceName;
    private final ResourceLocation location;
    private final StructureTemplate template;
    private final int turns,expectedY;
    private volatile int baseY=Integer.MIN_VALUE;
    NativeTemplatePiece(NaturalStructures.Spawn spawn,ResourceLocation location,StructureTemplate template,int turns,LevelHeightAccessor height,BlockPos corner){
        super(NativeTemplateStructures.PIECE.get(),0,new BoundingBox(corner.getX(),height.getMinBuildHeight(),corner.getZ(),corner.getX()+((turns&1)==0?template.getSize().getX():template.getSize().getZ())-1,height.getMaxBuildHeight()-1,corner.getZ()+((turns&1)==0?template.getSize().getZ():template.getSize().getX())-1));
        sourceName=spawn.name();this.location=location;this.template=template;this.turns=turns;expectedY=corner.getY();if(!spawn.conform())baseY=corner.getY();
    }
    NativeTemplatePiece(StructurePieceSerializationContext context,CompoundTag tag){
        super(NativeTemplateStructures.PIECE.get(),tag);sourceName=tag.getString("source");location=ResourceLocation.parse(tag.getString("template"));template=NativeTemplateStructures.template(context.structureTemplateManager(),location);turns=tag.getInt("turns");expectedY=tag.getInt("expectedY");baseY=tag.contains("baseY")?tag.getInt("baseY"):Integer.MIN_VALUE;
    }
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context,CompoundTag tag){tag.putString("source",sourceName);tag.putString("template",location.toString());tag.putInt("turns",turns);tag.putInt("expectedY",expectedY);if(baseY!=Integer.MIN_VALUE)tag.putInt("baseY",baseY);}
    @Override public BlockPos getLocatorPosition(){return new BlockPos(boundingBox.minX(),baseY==Integer.MIN_VALUE?expectedY:baseY,boundingBox.minZ());}
    @Override public void postProcess(WorldGenLevel level,StructureManager manager,ChunkGenerator generator,RandomSource random,BoundingBox clipping,ChunkPos chunk,BlockPos pivot){
        if(!NaturalStructures.enabled()||level.getLevel().dimension()!=Level.OVERWORLD)return;
        var spawn=NaturalStructures.spawn(sourceName);
        if(!spawn.conform()&&baseY==Integer.MIN_VALUE)synchronized(this){if(baseY==Integer.MIN_VALUE){
            // Old unresolved saves use the same source population window and immutable
            // generator as a new start, never whichever clipping chunk happened first.
            baseY=anchoredHeight(spawn,generator,level.getLevel().getChunkSource().randomState(),level,boundingBox.minX(),boundingBox.minZ());
        }}
        var settings=new StructurePlaceSettings().setRotation(rotation()).setBoundingBox(clipping).setKnownShape(false).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING).setIgnoreEntities(false).setFinalizeEntities(true);
        var zero=template.getBoundingBox(settings,BlockPos.ZERO);
        var origin=new BlockPos(boundingBox.minX()-zero.minX(),spawn.conform()?0:baseY,boundingBox.minZ()-zero.minZ());
        if(spawn.conform())settings.addProcessor(new NativeTemplateHeightProcessor(origin.getY(),spawn.heightOffset(),clipping));
        NativeTemplateStructures.sourceProcessors(settings);
        var nativeHousekeeping=settings.getRandom(origin);var sourceRandom=level.getRandom();settings.setRandom(sourceRandom);
        try(var source=com.hbm.world.structure.tandem.TandemSourceScope.using(sourceName,sourceRandom)){template.placeInWorld(level,origin,origin,settings,nativeHousekeeping,2);}
    }
    /** NBTStructure.getAverageHeight sampled the entire old population window,
     * not the template intersection. Fix its start-owned +8..+23 window once. */
    static int anchoredHeight(NaturalStructures.Spawn spawn,ChunkGenerator generator,RandomState randomState,LevelHeightAccessor height,int startX,int startZ){
        long total=0;
        for(int z=startZ+8;z<=startZ+23;z++)for(int x=startX+8;x<=startX+23;x++)total+=terrainTop(generator,randomState,height,x,z);
        int average=(int)(total/256)+spawn.heightOffset();
        // Unlike procedural Component, source NBT averaging had no provider floor.
        return generator instanceof FlatLevelSource?average:Math.clamp(average,spawn.minHeight(),spawn.maxHeight());
    }
    private static int terrainTop(ChunkGenerator generator,RandomState randomState,LevelHeightAccessor height,int x,int z){
        int y=height.getMaxBuildHeight()-1;
        var column=generator.getBaseColumn(x,z,height,randomState);
        for(;y>height.getMinBuildHeight();y--){
            var state=column.getBlock(y);var material=LegacyMaterials.of(state);var block=state.getBlock();
            // Source World.topSolid: blocksMovement, not material.leaves, not
            // Forge foliage. Retain logs/wood as ground; mapped leaf/bush/vine
            // families supply the modern counterpart of that removed API.
            if(material.blocksMovement()&&!material.name().equals("leaves")&&!state.is(BlockTags.LEAVES)
                    &&!(block instanceof LeavesBlock)&&!(block instanceof BushBlock)&&!(block instanceof VineBlock))return y+1;
        }
        return height.getMinBuildHeight()-1;
    }
    private Rotation rotation(){return switch(turns&3){case 1->Rotation.CLOCKWISE_90;case 2->Rotation.CLOCKWISE_180;case 3->Rotation.COUNTERCLOCKWISE_90;default->Rotation.NONE;};}
    @SuppressWarnings("deprecation") static int topSolid(LevelReader level,int x,int z){
        int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
        for(;y>level.getMinBuildHeight();y--){var state=level.getBlockState(new BlockPos(x,y,z));if(state.blocksMotion()&&!state.is(BlockTags.LEAVES))return y+1;}
        return level.getMinBuildHeight()-1;
    }
}
