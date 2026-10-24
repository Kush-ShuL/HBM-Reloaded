package com.hbm.world.glyphidhive;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Three fixed source material items share one live block and the original common name. */
public final class GlyphidHiveMaterialItem extends BlockItem {
    public enum Kind {
        STANDARD(0), INFESTED(1), RADIOACTIVE(2);
        private final int stateValue;
        Kind(int stateValue){this.stateValue=stateValue;}
        public int stateValue(){return stateValue;}
    }
    private final Kind kind;
    public GlyphidHiveMaterialItem(GlyphidHiveMaterialBlock block,Kind kind){super(block,new Properties());this.kind=Objects.requireNonNull(kind);}
    public Kind kind(){return kind;}
    @Override public void registerBlocks(Map<Block,Item> blocks,Item item){if(kind==Kind.STANDARD)super.registerBlocks(blocks,item);}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(GlyphidHiveMaterialBlock.META,kind.stateValue());}
    @Override public Component getName(ItemStack stack){return Component.translatable("tile.glyphid_base.name");}
}
