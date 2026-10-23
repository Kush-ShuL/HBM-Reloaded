package com.hbm.world.structure.materials;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import java.util.List;
import net.minecraft.world.item.DyeColor;

/** Original BlockConcreteColored's sixteen textures, raw metadata, no-spawn flag and material. */
public final class ColoredConcreteBlock extends LegacyOreBlock {
    public static final List<String> COLORS=List.of("white","orange","magenta","lightBlue","yellow","lime","pink","gray","silver","cyan","purple","blue","brown","green","red","black");
    private static OreSpec specification(){return new OreSpec("concrete_colored","BlockConcreteColored","rock","stone",15,84,0,true,false,null,null,0,1,0,"NONE",0,COLORS,"COMMON",false,0,false,false);}
    public ColoredConcreteBlock(){this(specification());}
    private ColoredConcreteBlock(OreSpec spec){super(spec,spec.properties().mapColor(state->DyeColor.byId(state.getValue(LEGACY_META)).getMapColor()));}
}
