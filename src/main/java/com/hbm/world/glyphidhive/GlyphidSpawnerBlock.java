package com.hbm.world.glyphidhive;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** Genuine full-cube coral spawner, not a vanilla-spawner replacement or GUI shell. */
public final class GlyphidSpawnerBlock extends Block implements EntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final SoundType FLESH=new SoundType(.5F,1,SoundType.STONE.getBreakSound(),SoundType.STONE.getStepSound(),SoundType.STONE.getPlaceSound(),SoundType.STONE.getHitSound(),SoundType.STONE.getFallSound()){
        private SoundEvent flesh(){return HbmSounds.get("block.flesh");}@Override public SoundEvent getBreakSound(){return flesh();}@Override public SoundEvent getPlaceSound(){return flesh();}@Override public SoundEvent getStepSound(){return flesh();}@Override public SoundEvent getHitSound(){return flesh();}@Override public SoundEvent getFallSound(){return flesh();}};
    public GlyphidSpawnerBlock(){super(Properties.of().mapColor(MapColor.PLANT).strength(.5F).sound(FLESH));registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected com.mojang.serialization.MapCodec<? extends Block> codec(){return simpleCodec(ignored->new GlyphidSpawnerBlock());}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&15);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new GlyphidSpawnerEntity(pos,state);}
    @Override @SuppressWarnings("unchecked") public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world,BlockState state,BlockEntityType<T> type){return world.isClientSide||type!=GlyphidHiveContent.SPAWNER_ENTITY.get()?null:(BlockEntityTicker<T>)(BlockEntityTicker<GlyphidSpawnerEntity>)GlyphidSpawnerEntity::tick;}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return GlyphidSpawnerItem.stack(state.getValue(META),1);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){var world=params.getLevel();var tool=params.getOptionalParameter(LootContextParams.TOOL);int fortune=tool==null?0:EnchantmentHelper.getItemEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE),tool);int count=1+world.random.nextInt(3)+fortune;var egg=BuiltInRegistries.ITEM.get(ResourceLocation.parse("hbm:egg_glyphid"));if(egg==Items.AIR)throw new IllegalStateException("Real source egg_glyphid item is not admitted");var drops=new ArrayList<ItemStack>();for(int i=0;i<count;i++)drops.add(new ItemStack(egg));Float radius=params.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);if(radius!=null)drops.removeIf(stack->world.random.nextFloat()>1F/radius);return drops;}
}
