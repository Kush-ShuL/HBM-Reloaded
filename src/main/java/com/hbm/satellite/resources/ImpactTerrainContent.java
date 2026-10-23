package com.hbm.satellite.resources;

import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.registries.*;

/** Only the two actually missing terrain hosts used by the true Tom aftermath, not copies of NuclearTerrain. */
public final class ImpactTerrainContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<BurningEarth> BURNING=BLOCKS.register("burning_earth",BurningEarth::new);
    public static final DeferredBlock<ImpactDirt> DIRT=BLOCKS.register("impact_dirt",ImpactDirt::new);
    public static final DeferredItem<BlockItem> BURNING_ITEM=ITEMS.registerSimpleBlockItem("burning_earth",BURNING),DIRT_ITEM=ITEMS.registerSimpleBlockItem("impact_dirt",DIRT);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(BURNING_ITEM.get()),new ItemStack(DIRT_ITEM.get())):List.of();}
    private static List<ItemStack> drops(Block block,LootParams.Builder context){var tool=context.getOptionalParameter(LootContextParams.TOOL);boolean silk=tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),tool)>0;return List.of(new ItemStack(silk?block:Blocks.DIRT));}
    private static boolean named(BlockState state,String symbol){var id=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm",symbol);return net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)&&state.is(net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(id));}
    public static final class ImpactDirt extends Block {
        ImpactDirt(){super(Properties.of().mapColor(MapColor.DIRT).strength(.5F).sound(SoundType.GRAVEL).randomTicks());}
        @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return drops(this,context);}
        @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moved){for(int x=-1;x<2;x++)for(int y=-1;y<2;y++)for(int z=-1;z<2;z++)if(level.getBlockState(pos.offset(x,y,z)).is(Blocks.GRASS_BLOCK))level.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);}
        @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){var data=com.hbm.saveddata.TomSaveData.forWorld(level);int light=Math.max(level.getBrightness(LightLayer.BLOCK,pos.above()),(int)(level.getMaxLocalRawBrightness(pos.above())*(1-data.dust)));if(light>=9&&data.fire==0)level.setBlock(pos,Blocks.GRASS_BLOCK.defaultBlockState(),3);}
    }
    public static final class BurningEarth extends Block {
        BurningEarth(){super(Properties.of().mapColor(MapColor.DIRT).strength(.6F).sound(SoundType.GRASS).randomTicks());}
        @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return drops(this,context);}
        @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){if(entity instanceof LivingEntity)entity.igniteForSeconds(5);}
        @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){level.addParticle(ParticleTypes.FLAME,pos.getX()+random.nextFloat(),pos.getY()+1.1F,pos.getZ()+random.nextFloat(),0,0,0);level.addParticle(ParticleTypes.SMOKE,pos.getX()+random.nextFloat(),pos.getY()+1.1F,pos.getZ()+random.nextFloat(),0,0,0);}
        @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
            if(random.nextInt(5)==0)for(int x=-1;x<2;x++)for(int y=-1;y<2;y++)for(int z=-1;z<2;z++){var at=pos.offset(x,y,z);if(!level.hasChunkAt(at))continue;var base=level.getBlockState(at);var top=level.getBlockState(at.above());
                if(!top.isSolidRender(level,at.above())&&(base.is(Blocks.GRASS_BLOCK)||base.is(Blocks.MYCELIUM)||named(base,"waste_earth")||named(base,"frozen_grass")||named(base,"waste_mycelium"))&&!level.isRainingAt(pos))level.setBlock(at,defaultBlockState(),3);
                if(base.is(BlockTags.LEAVES)||base.getBlock() instanceof BushBlock)level.removeBlock(at,false);
                if(named(base,"frozen_dirt"))level.setBlock(at,Blocks.DIRT.defaultBlockState(),3);
                // Source flammability is queried at the central position and fire is set centrally, not at each neighbour.
                if(top.isFlammable(level,pos,Direction.UP)&&!top.is(BlockTags.LEAVES)&&!(top.getBlock() instanceof BushBlock)&&level.getBlockState(pos.above()).is(Blocks.AIR))level.setBlock(pos.above(),Blocks.FIRE.defaultBlockState(),3);
            }
            level.setBlock(pos,DIRT.get().defaultBlockState(),3);
        }
        @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moved){var above=level.getBlockState(pos.above());if(!above.getFluidState().isEmpty()||above.isRedstoneConductor(level,pos.above()))level.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);}
        @Override public TriState canSustainPlant(BlockState state,BlockGetter level,BlockPos pos,Direction face,BlockState plant){return TriState.FALSE;}
    }
    private ImpactTerrainContent(){}
}
