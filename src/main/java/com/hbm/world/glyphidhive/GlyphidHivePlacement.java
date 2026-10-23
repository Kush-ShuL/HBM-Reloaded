package com.hbm.world.glyphidhive;
import com.hbm.world.structure.generated.GlyphidHiveLayout;
import com.hbm.world.structure.generated.SourceRandom;
import com.hbm.world.structure.generated.compat.World;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.StructureBoundingBox;
import com.hbm.world.structure.loot.*;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
/** One real backend for both native persistent natural planning and live Scout construction. */
public final class GlyphidHivePlacement {
    public static void requireReady(World world,boolean loot){if(!StructureLootPresets.ready(world.lookup(),"LOOT_BONES")||loot&&!StructureLootPresets.ready(world.lookup(),"LOOT_GLYPHID_HIVE"))throw new IllegalStateException("Original Glyphid hive loot has unbound real dependencies");}
    public static void generateSmall(ServerLevel level,int x,int y,int z,Random sourceRandom,boolean infected,boolean loot){try(var world=new World(level,new StructureBoundingBox(x-5,y-2,z-5,x+5,y+2,z+5),new SourceRandom(level.random))){generateSmall(world,x,y,z,sourceRandom,infected,loot);}}
    public static void generateSmall(World world,int x,int y,int z,Random random,boolean infected,boolean loot){
        requireReady(world,loot);
        GlyphidHiveLayout.generateSmall(new GlyphidHiveLayout.Target(){
            @Override public void base(int x,int y,int z,int metadata){var state=GlyphidHiveMaterialContent.BASE.get().defaultBlockState().setValue(GlyphidHiveMaterialBlock.META,metadata);world.setBlock(x,y,z,Block.observed(state),metadata,2);}
            @Override public void spawner(int x,int y,int z,int metadata){var state=GlyphidHiveContent.SPAWNER.get().defaultBlockState().setValue(GlyphidSpawnerBlock.META,metadata);world.setBlock(x,y,z,Block.observed(state),metadata,2);}
            @Override public boolean skull(int x,int y,int z){world.setBlock(x,y,z,Block.observed(Blocks.SKELETON_SKULL.defaultBlockState()),1,3);return world.getTileEntity(x,y,z) instanceof SkullBlockEntity;}
            @Override public void skullRotation(int x,int y,int z,int rotation){if(world.getTileEntity(x,y,z) instanceof SkullBlockEntity){var state=world.stateAt(x,y,z).setValue(SkullBlock.ROTATION,rotation);world.setBlock(x,y,z,Block.observed(state),1,2);}}
            private void pile(int x,int y,int z,String preset){var state=StructureLootContent.PILE.get().defaultBlockState();world.setBlock(x,y,z,Block.observed(state),0,2);if(world.getTileEntity(x,y,z) instanceof LootPileBlockEntity pile){var rng=new LegacyLootRandom(world.rand);if(world.isPlanning())StructureLootPresets.planSimple(world.lookup(),pile,preset,rng);else StructureLootPresets.preset(world.level(),new BlockPos(x,y,z),preset,rng);}}
            @Override public void bones(int x,int y,int z){pile(x,y,z,"LOOT_BONES");}
            @Override public void hiveLoot(int x,int y,int z){pile(x,y,z,"LOOT_GLYPHID_HIVE");}
        },x,y,z,random,infected,loot);
    }
    private GlyphidHivePlacement(){}
}
