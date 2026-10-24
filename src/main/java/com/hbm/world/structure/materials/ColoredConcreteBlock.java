package com.hbm.world.structure.materials;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import java.util.List;
import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.item.DyeColor;

/** Original BlockConcreteColored's sixteen textures, raw metadata, no-spawn flag and material. */
public final class ColoredConcreteBlock extends LegacyOreBlock {
    public static final List<String> COLORS=List.of("white","orange","magenta","lightBlue","yellow","lime","pink","gray","silver","cyan","purple","blue","brown","green","red","black");
    private static OreSpec specification(){return new OreSpec("concrete_colored","BlockConcreteColored","rock","stone",15,84,0,true,false,null,null,0,1,0,"NONE",0,COLORS,"COMMON",false,0,false,false);}
    public ColoredConcreteBlock(){this(specification());}
    private ColoredConcreteBlock(OreSpec spec){super(spec,spec.properties().mapColor(state->DyeColor.byId(state.getValue(LEGACY_META)).getMapColor()));}
    @Override protected ItemStack blockStack(BlockState state){return new ItemStack(MeteorMaterialContent.coloredItem(state.getValue(LEGACY_META)));}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        return super.getDrops(state,context).stream().map(stack->{
            if(!stack.is(asItem()))return stack;
            var fixed=stack.transmuteCopy(MeteorMaterialContent.coloredItem(state.getValue(LEGACY_META)));
            fixed.remove(HbmHazards.LEGACY_METADATA.get());return fixed;
        }).toList();
    }
}
