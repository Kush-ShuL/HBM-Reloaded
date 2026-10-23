package com.hbm.block.structurebasic;
import com.hbm.block.ore.*;
import java.util.List;
import net.minecraft.world.level.block.SoundType;
/** BlockEnumMulti(TileType): LARGE0/SMALL1, raw4bit placement, modulo2 ordinary drops and raw silk drops. */
public final class SourceVinylTileBlock extends LegacyOreBlock {
 private static OreSpec source(){return new OreSpec("vinyl_tile","BlockEnumMulti","rock","stone",10,36,0,true,true,null,null,0,1,0,"NONE",0,List.of("large","small"),"COMMON",false,0,false,false);}
 public SourceVinylTileBlock(){this(source());}private SourceVinylTileBlock(OreSpec source){super(source,source.properties().sound(SoundType.GLASS));}
}
