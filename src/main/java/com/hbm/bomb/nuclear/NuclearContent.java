package com.hbm.bomb.nuclear;

import com.hbm.item.BasicBlockItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** A group is admitted only with its original assembly, renderer, components and actual blast/fallout path. */
public final class NuclearContent {
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 private static final DeferredRegister<MenuType<?>> MENU_TYPES=DeferredRegister.create(Registries.MENU,"hbm");
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final List<String> ADMITTED=List.of("gadget","boy","man","mike","tsar","n2","fleija","solinium","prototype");
 public static final Map<String,net.neoforged.neoforge.registries.DeferredBlock<NuclearBombBlock>> BOMBS=new LinkedHashMap<>();
 public static final Map<String,DeferredHolder<MenuType<?>,MenuType<NuclearAssemblyMenu>>> MENUS=new LinkedHashMap<>();
 private static final List<java.util.function.Supplier<? extends Item>> CREATIVE=new ArrayList<>();
 static{for(String kind:ADMITTED){var block=BLOCKS.register("nuke_"+kind,()->new NuclearBombBlock(kind));BOMBS.put(kind,block);CREATIVE.add(ITEMS.register("nuke_"+kind,()->new BasicBlockItem(block.get(),new Item.Properties(),"Nuke"+kind,"nuke_"+kind)));MENUS.put(kind,MENU_TYPES.register("nuke_"+kind,()->new MenuType<>((id,inventory)->new NuclearAssemblyMenu(MENUS.get(kind).get(),id,inventory,new NuclearAssembly(kind,()->{},player->true)),FeatureFlags.DEFAULT_FLAGS)));if(!kind.equals("n2"))CREATIVE.add(ITEMS.register(kind+"_kit",()->new NuclearStarterKit(kind+"_kit")));}}
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<NuclearBombBlockEntity>> BOMB_ENTITY=BLOCK_ENTITIES.register("nuclear_bomb",()->BlockEntityType.Builder.of(NuclearBombBlockEntity::new,BOMBS.values().stream().map(java.util.function.Supplier::get).toArray(net.minecraft.world.level.block.Block[]::new)).build(null));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearTorex>> TOREX=ENTITIES.register("nuke_torex",()->EntityType.Builder.<NuclearTorex>of(NuclearTorex::new,MobCategory.MISC).sized(1F,50F).fireImmune().noSave().clientTrackingRange(63).updateInterval(1).build("hbm:nuke_torex"));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearExplosionMK5>> MK5=ENTITIES.register("nuke_explosion_mk5",()->EntityType.Builder.<NuclearExplosionMK5>of(NuclearExplosionMK5::new,MobCategory.MISC).fireImmune().clientTrackingRange(0).updateInterval(20).build("hbm:nuke_explosion_mk5"));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearFalloutRain>> FALLOUT_RAIN=ENTITIES.register("fallout_rain",()->EntityType.Builder.<NuclearFalloutRain>of(NuclearFalloutRain::new,MobCategory.MISC).sized(4,20).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:fallout_rain"));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearFallingBlock>> FALLING_BLOCK=ENTITIES.register("falling_block_nt",()->EntityType.Builder.<NuclearFallingBlock>of(NuclearFallingBlock::new,MobCategory.MISC).sized(.98F,.98F).clientTrackingRange(10).updateInterval(20).build("hbm:falling_block_nt"));
 public static final net.neoforged.neoforge.registries.DeferredItem<NuclearBombPart> N2_CHARGE=ITEMS.register("n2_charge",()->new NuclearBombPart("n2_charge","n2",false));
 private static final List<net.neoforged.neoforge.registries.DeferredItem<NuclearBreedingRod>> BREEDING_RODS=registerBreedingRods();
 private static List<net.neoforged.neoforge.registries.DeferredItem<NuclearBreedingRod>> registerBreedingRods(){var rods=new ArrayList<net.neoforged.neoforge.registries.DeferredItem<NuclearBreedingRod>>();for(String family:List.of("rod","rod_dual","rod_quad"))for(int i=0;i<NuclearBreedingRod.VARIANTS.size();i++){int kind=i;rods.add(ITEMS.register(family+"_"+NuclearBreedingRod.VARIANTS.get(i),()->new NuclearBreedingRod(family,kind)));}return List.copyOf(rods);}
 public static List<ItemStack> breedingRodStacks(){return BREEDING_RODS.stream().map(rod->new ItemStack(rod.get())).toList();}
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearExplosionMK3>> MK3=ENTITIES.register("nuke_explosion_mk3",()->EntityType.Builder.<NuclearExplosionMK3>of(NuclearExplosionMK3::new,MobCategory.MISC).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:nuke_explosion_mk3"));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearFleijaCloud>> FLEIJA_CLOUD=ENTITIES.register("cloud_fleija",()->EntityType.Builder.<NuclearFleijaCloud>of(NuclearFleijaCloud::new,MobCategory.MISC).sized(1,4).fireImmune().clientTrackingRange(32).updateInterval(1).build("hbm:cloud_fleija"));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearSoliniumCloud>> SOLINIUM_CLOUD=ENTITIES.register("cloud_solinium",()->EntityType.Builder.<NuclearSoliniumCloud>of(NuclearSoliniumCloud::new,MobCategory.MISC).sized(1,4).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:cloud_solinium"));
 public static final Map<String,net.neoforged.neoforge.registries.DeferredItem<NuclearBombPart>> PARTS=new LinkedHashMap<>();
 static{for(String bomb:List.of("fleija","solinium"))for(String part:List.of("igniter","propellant","core")){String id=bomb+"_"+part;var item=ITEMS.register(id,()->new NuclearBombPart(id,bomb,id.equals("fleija_propellant")));PARTS.put(id,item);CREATIVE.add(item);}}
 private NuclearContent(){}
 public static void register(IEventBus bus,ModContainer container){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);MENU_TYPES.register(bus);ENTITIES.register(bus);NuclearTerrain.register(bus);NuclearSpecialContent.register(bus);com.hbm.bomb.nuclear.programmable.ProgrammableNuclearContent.register(bus);NuclearConfig.register(container);bus.addListener((RegisterTicketControllersEvent event)->event.register(NuclearChunkloadingEntity.TICKETS));FalloutRules.initialize();}
 public static List<ItemStack> creativeStacks(String tab){var base=tab.equals("nuke")?java.util.stream.Stream.concat(CREATIVE.stream(),java.util.stream.Stream.of(N2_CHARGE)).map(item->new ItemStack(item.get())).toList():NuclearTerrain.creativeStacks(tab);if(tab.equals("control"))base=breedingRodStacks();return java.util.stream.Stream.concat(base.stream(),java.util.stream.Stream.concat(NuclearSpecialContent.creativeStacks(tab).stream(),com.hbm.bomb.nuclear.programmable.ProgrammableNuclearContent.creativeStacks(tab).stream())).toList();}
}
