package com.hbm.world.structure.tandem;

import com.hbm.world.structure.meteor.MeteorConnectorPolicy;
import com.hbm.world.structure.meteor.MeteorTemplateAccess;
import com.hbm.world.structure.meteor.mixin.MeteorTemplatePoolAccess;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** The source's sole registered pooled SpawnCondition and its real native pools, not a second pool registry. */
public final class TandemSourcePools {
    public static final String CONDITION="meteor_dungeon";
    private static final List<String> NAMES=List.of("start","spike","default","10room","3x3loot","headloot","fallback","roomback","headback");
    private static ResourceLocation id(String name){return ResourceLocation.tryParse("hbm:meteor/"+name);}
    public record Selected(StructurePoolElement element,BlockPos origin,Rotation rotation){}

    public static Selected select(ServerLevel level,BlockPos anchor,Direction direction,String condition,String poolName,String target){
        if(!CONDITION.equals(condition))return null; // Other real source SpawnConditions have no pools.
        var key=id(poolName);if(key==null)return null;
        var pool=level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL).get(key);
        if(pool==null||pool.size()==0)return null;
        // Exactly the original clone.get's ONE weighted draw. No fallback pass or list shuffle.
        var element=pool.getRandomTemplate(level.random);
        if(element==EmptyPoolElement.INSTANCE)return null;
        if(!(element instanceof MeteorTemplateAccess))throw new IllegalStateException("Source tandem requires its real native single-template element");
        var tag=new CompoundTag();tag.putString("target","hbm:meteor/"+target);
        // Original Tandem stores roll in its editor but DOES NOT use it in tryGenerate.
        // The existing source connector policy's aligned/rotationNONE branch keeps vertical RNG/rotation0.
        tag.putString("joint","aligned");
        var orientation=switch(direction){case DOWN->FrontAndTop.DOWN_NORTH;case UP->FrontAndTop.UP_NORTH;case NORTH->FrontAndTop.NORTH_UP;case SOUTH->FrontAndTop.SOUTH_UP;case WEST->FrontAndTop.WEST_UP;case EAST->FrontAndTop.EAST_UP;};
        var from=new StructureBlockInfo(anchor,Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION,orientation),tag);
        var incoming=MeteorConnectorPolicy.incoming(from,Rotation.NONE,element,level.getStructureManager(),level.random);
        if(incoming==null)return null;
        var origin=anchor.relative(direction).subtract(incoming.connector().pos().rotate(incoming.rotation()));
        return new Selected(element,origin,incoming.rotation());
    }

    /** Exact registry element identity determines the source condition; template-name heuristics do not. */
    public static String conditionFor(ServerLevelAccessor level,StructurePoolElement element){
        var pools=level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        for(String name:NAMES){var pool=pools.get(id(name));if(pool==null)continue;
            for(var row:((MeteorTemplatePoolAccess)pool).hbm$rawTemplates())if(row.getFirst()==element)return CONDITION;
        }
        return null;
    }
    private TandemSourcePools(){}
}
