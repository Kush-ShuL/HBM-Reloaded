package com.hbm.world.glyphidhive;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

/** Actual BlockGlyphid: ordinary full cube, no BE, no drops, three source families; unknown raw metas use standard icons. */
public final class GlyphidHiveMaterialBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final SoundType FLESH=new SoundType(.5F,1,SoundType.STONE.getBreakSound(),SoundType.STONE.getStepSound(),SoundType.STONE.getPlaceSound(),SoundType.STONE.getHitSound(),SoundType.STONE.getFallSound()){
        private SoundEvent flesh(){return HbmSounds.get("block.flesh");}
        @Override public SoundEvent getBreakSound(){return flesh();}
        @Override public SoundEvent getPlaceSound(){return flesh();}
        @Override public SoundEvent getStepSound(){return flesh();}
        @Override public SoundEvent getHitSound(){return flesh();}
        @Override public SoundEvent getFallSound(){return flesh();}
    };
    private static final MapCodec<GlyphidHiveMaterialBlock> CODEC=simpleCodec(ignored->new GlyphidHiveMaterialBlock());
    public GlyphidHiveMaterialBlock(){super(Properties.of().mapColor(MapColor.PLANT).strength(.5F).sound(FLESH));registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getItemInHand().getItem() instanceof GlyphidHiveMaterialItem item?item.kind().stateValue():0);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of();}
    public static int family(int metadata){return metadata==1?1:metadata==2?2:0;}
}
