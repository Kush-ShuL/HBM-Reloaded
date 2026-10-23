package com.hbm.world.structure.generated;
import com.hbm.config.WorldgenControls;
import com.hbm.entity.mob.glyphid.GlyphidConfig;
import com.hbm.world.glyphidhive.GlyphidHivePlacement;
import com.hbm.world.structure.generated.compat.World;
import java.util.*;
/** Source HbmWorldGen191..201, before Antenna, in the sole existing persistent plan. */
public final class SourceGlyphidHive {
    public static void generate(World world,Random random,int i,int j,Set<String> selected){
        if(!GlyphidConfig.ENABLE_HIVES.get()||!WorldgenControls.feature("glyphid_hives"))return;
        var rule=WorldgenControls.rule("glyphid_hives","small_hive");int frequency=rule.chance(GlyphidConfig.HIVE_SPAWN.get());if(!rule.enabled()||frequency<=0||random.nextInt(frequency)!=0)return;
        int x=i+random.nextInt(16)+8,z=j+random.nextInt(16)+8,y=world.getHeightValue(x,z);
        for(int k=3;k>=-1;k--)if(world.getBlock(x,y-1+k,z).isNormalCube()){
            GeneratedDependencies.forNames(List.of("old:GlyphidHive")).requireRegistryAndHooks();
            GlyphidHivePlacement.generateSmall(world,x,y+k,z,random,random.nextInt(10)==0,true);selected.add("old:GlyphidHive");break;
        }
    }
    private SourceGlyphidHive(){}
}
