package com.hbm.bomb.conventional;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.*;

/** Source-backed mine registrations; nuclear mine enters after its original radiation and visual effects are ready. */
public final class LandmineContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<ResourceLocation> STATS=DeferredRegister.create(Registries.CUSTOM_STAT,"hbm");
    public static final DeferredBlock<LandmineBlock> AP=BLOCKS.register("mine_ap",()->new LandmineBlock(LandmineBlock.Kind.AP)),HE=BLOCKS.register("mine_he",()->new LandmineBlock(LandmineBlock.Kind.HE));
    public static final DeferredItem<BlockItem> AP_ITEM=ITEMS.register("mine_ap",()->new BlockItem(AP.get(),new Item.Properties())),HE_ITEM=ITEMS.register("mine_he",()->new BlockItem(HE.get(),new Item.Properties()));
    public static final DeferredBlock<LandmineBlock> SHRAP=BLOCKS.register("mine_shrap",()->new LandmineBlock(LandmineBlock.Kind.SHRAP)),NAVAL=BLOCKS.register("mine_naval",()->new LandmineBlock(LandmineBlock.Kind.NAVAL));
    public static final DeferredItem<BlockItem> SHRAP_ITEM=ITEMS.register("mine_shrap",()->new BlockItem(SHRAP.get(),new Item.Properties())),NAVAL_ITEM=ITEMS.register("mine_naval",()->new BlockItem(NAVAL.get(),new Item.Properties()));
    public static final DeferredBlock<LandmineBlock> FAT=BLOCKS.register("mine_fat",()->new LandmineBlock(LandmineBlock.Kind.FAT));
    public static final DeferredItem<BlockItem> FAT_ITEM=ITEMS.register("mine_fat",()->new BlockItem(FAT.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LandmineBlockEntity>> ENTITY=ENTITIES.register("landmine",()->BlockEntityType.Builder.of(LandmineBlockEntity::new,AP.get(),HE.get(),SHRAP.get(),NAVAL.get(),FAT.get()).build(null));
    public static final DeferredHolder<ResourceLocation,ResourceLocation> MINES_STAT=STATS.register("ntm_mines",()->ResourceLocation.parse("hbm:ntm_mines"));
    private static final ModConfigSpec CONFIG;public static final ModConfigSpec.ConfigValue<Double> AP_DAMAGE,HE_DAMAGE,SHRAP_DAMAGE,NAVAL_DAMAGE,NUKE_DAMAGE;
    static{var config=new ModConfigSpec.Builder();AP_DAMAGE=config.comment("反步兵地雷的伤害参数。").define(java.util.List.of("damage","antiPersonnel"),10D);HE_DAMAGE=config.comment("高爆地雷的伤害参数。").define(java.util.List.of("damage","highExplosive"),35D);SHRAP_DAMAGE=config.comment("破片地雷的伤害参数。").define(java.util.List.of("damage","shrapnel"),7.5D);NAVAL_DAMAGE=config.comment("水雷的伤害参数。").define(java.util.List.of("damage","naval"),60D);NUKE_DAMAGE=config.comment("核地雷的伤害参数。").define(java.util.List.of("damage","nuclear"),100D);CONFIG=config.build();}
    private LandmineContent(){}
    public static void register(IEventBus bus,ModContainer container){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);STATS.register(bus);container.registerConfig(ModConfig.Type.COMMON,CONFIG,"hbm-mines.toml");bus.addListener(LandmineContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->Stats.CUSTOM.get(MINES_STAT.get(),StatFormatter.DEFAULT));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(AP_ITEM.get()),new ItemStack(SHRAP_ITEM.get()),new ItemStack(HE_ITEM.get()),new ItemStack(FAT_ITEM.get()),new ItemStack(NAVAL_ITEM.get())):List.of();}
}
