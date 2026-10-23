package com.hbm.world.structure.templates.materials;

import com.hbm.block.ore.*;
import java.util.List;

/** Source BlockEnumMulti's metadata/drop family; the original per-face textures are generated without pixel changes. */
public final class TemplateEnumBlock extends LegacyOreBlock {
    public TemplateEnumBlock(String id,String original,float hardness,float resistance,boolean canSpawn,List<String> variants){super(new OreSpec(id,original,"rock","stone",hardness,resistance,0,true,canSpawn,null,null,0,1,0,"NONE",0,variants,"COMMON",false,0,false,false));}
}
