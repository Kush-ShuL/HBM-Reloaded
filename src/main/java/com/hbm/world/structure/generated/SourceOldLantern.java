package com.hbm.world.structure.generated;

import com.hbm.config.WorldgenControls;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.oldlantern.*;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.World;
import com.hbm.world.structure.loot.*;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** HbmWorldGen292..315, called once at its exact AP-mine / Bosnia-mine position. */
public final class SourceOldLantern {
    public static void generate(World world,Random random,int i,int j,Set<String> selected){
        if(!WorldgenControls.feature("old_lantern"))return;
        var rule=WorldgenControls.rule("old_lantern","lantern_behemoth");int frequency=rule.chance(2000);
        if(!rule.enabled()||frequency<=0||random.nextInt(frequency)!=0)return;
        int x=i+random.nextInt(16),z=j+random.nextInt(16),y=world.getHeightValue(x,z);
        if(!world.getBlock(x,y-1,z).canPlaceTorchOnTop(world,x,y-1,z)||!world.stateAt(x,y,z).canBeReplaced())return;
        GeneratedDependencies.forNames(List.of("old:OldLantern")).requireRegistryAndHooks();
        var block=OldLanternContent.BLOCK.get();
        world.setBlock(x,y,z,Block.observed(block.defaultBlockState().setValue(DummyableBlock.LEGACY_META,12)),12,3);
        // MultiblockHandlerXR.fillSpace({4,0,0,0,0,0},NORTH): upward cells
        // all point down by source metadata1, and no pre-clear/canPlace check is added.
        for(int height=1;height<=4;height++)world.setBlock(x,y+height,z,Block.observed(block.defaultBlockState().setValue(DummyableBlock.LEGACY_META,1)),1,3);
        if(world.getTileEntity(x,y,z) instanceof OldLanternEntity lantern){lantern.isBroken=true;lantern.setChanged();}
        if(random.nextInt(2)==0){
            var state=StructureLootContent.PILE.get().defaultBlockState();world.setBlock(x,y,z-2,Block.observed(state),0,3);
            // Source LootGenerator.lootBooklet consumes no RNG and only adds to an empty true pile.
            // The native planning BE intentionally has no Level; its complete NBT is persisted by GeneratedPlanPiece.
            if(world.getTileEntity(x,y,z-2) instanceof LootPileBlockEntity pile&&pile.items().isEmpty())pile.add(LoreBookItem.create("beacon",12,0x404040,0xd637b3),0,0,0);
        }
        selected.add("old:OldLantern");
    }
    private SourceOldLantern(){}
}
