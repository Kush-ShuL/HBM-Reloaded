package com.hbm.bomb.conventional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConventionalContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    private static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final net.neoforged.neoforge.registries.DeferredBlock<TimedChargeBlock> CHARGE_DYNAMITE=BLOCKS.register("charge_dynamite",()->new TimedChargeBlock(false)), CHARGE_MINER=BLOCKS.register("charge_miner",()->new TimedChargeBlock(true));
    public static final net.neoforged.neoforge.registries.DeferredBlock<TimedChargeBlock> CHARGE_C4=BLOCKS.register("charge_c4",()->new TimedChargeBlock(TimedChargeBlock.Kind.C4)), CHARGE_SEMTEX=BLOCKS.register("charge_semtex",()->new TimedChargeBlock(TimedChargeBlock.Kind.SEMTEX));
    public static final DeferredHolder<net.minecraft.world.level.block.entity.BlockEntityType<?>,net.minecraft.world.level.block.entity.BlockEntityType<TimedChargeBlockEntity>> CHARGE_ENTITY=BLOCK_ENTITIES.register("explosive_charge",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(TimedChargeBlockEntity::new,CHARGE_DYNAMITE.get(),CHARGE_MINER.get(),CHARGE_C4.get(),CHARGE_SEMTEX.get()).build(null));
    public static final DeferredHolder<EntityType<?>, EntityType<PrimedExplosive>> PRIMED = ENTITIES.register("tnt_primed_base", () -> EntityType.Builder.<PrimedExplosive>of(PrimedExplosive::new, MobCategory.MISC).sized(.98F, .98F).clientTrackingRange(63).updateInterval(1).build("hbm:tnt_primed_base"));
    private static final Map<String, DeferredItem<? extends Item>> BY_ID = new LinkedHashMap<>();
    private static final ModConfigSpec CONFIG;
    public static final BooleanSupplier DEAD_EXPLOSIVE_ENABLED,EXTENDED_LOGGING;
    static {
        var config = new ModConfigSpec.Builder();
        DEAD_EXPLOSIVE_ENABLED = config.comment("掉落型自爆引爆器是否触发自身爆炸；关闭后掉落物品仍被消耗。").define("dropDead", true);
        EXTENDED_LOGGING=config.comment("记录扩展诊断日志，默认关闭。").define("enableExtendedLogging",false);
        CONFIG = config.build();
        BY_ID.put("charge_dynamite",ITEMS.register("charge_dynamite",()->new BlockItem(CHARGE_DYNAMITE.get(),new Item.Properties())));
        BY_ID.put("charge_c4",ITEMS.register("charge_c4",()->new BlockItem(CHARGE_C4.get(),new Item.Properties())));
        BY_ID.put("charge_semtex",ITEMS.register("charge_semtex",()->new BlockItem(CHARGE_SEMTEX.get(),new Item.Properties())));
        BY_ID.put("charge_miner",ITEMS.register("charge_miner",()->new BlockItem(CHARGE_MINER.get(),new Item.Properties())));
        block("flame_war",FlameWarBombBlock::new);
        block("therm_endo",()->new SpecialBombBlock(SpecialBombBlock.Kind.ENDO));block("therm_exo",()->new SpecialBombBlock(SpecialBombBlock.Kind.EXO));block("float_bomb",()->new SpecialBombBlock(SpecialBombBlock.Kind.FLOAT));block("emp_bomb",()->new SpecialBombBlock(SpecialBombBlock.Kind.EMP));
        block("det_charge",()->new DetonationChargeBlock(false));block("det_miner",()->new DetonationChargeBlock(true));block("det_nuke",()->new DetonationChargeBlock(DetonationChargeBlock.Kind.NUCLEAR));
        block("det_cord", DetCordBlock::new);
        block("block_semtex", PlasticExplosiveBlock::new); block("block_c4", PlasticExplosiveBlock::new);
        tnt("dynamite", 8); tnt("tnt_ntm", 10); tnt("semtex", 12); tnt("c4", 15);
        remote("detonator", RemoteDetonatorItem.Kind.SINGLE); remote("detonator_multi", RemoteDetonatorItem.Kind.MULTI);
        remote("detonator_laser", RemoteDetonatorItem.Kind.LASER); remote("detonator_deadman", RemoteDetonatorItem.Kind.DEAD_MAN); remote("detonator_de", RemoteDetonatorItem.Kind.DEAD_EXPLOSIVE);
    }
    private ConventionalContent() {}
    private static void block(String id, java.util.function.Supplier<? extends Block> factory) {
        var block=BLOCKS.register(id,factory); BY_ID.put(id,ITEMS.register(id,()->new BlockItem(block.get(),new Item.Properties())));
    }
    private static void tnt(String id, float radius) {
        var block = BLOCKS.register(id, () -> new TntExplosiveBlock(radius));
        BY_ID.put(id, ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties())));
    }
    private static void remote(String id, RemoteDetonatorItem.Kind kind) { BY_ID.put(id, ITEMS.register(id, () -> new RemoteDetonatorItem(kind))); }
    public static void register(IEventBus bus, ModContainer container) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); BLOCK_ENTITIES.register(bus); bus.addListener(ConventionalEffects::register); bus.addListener(LargeConventionalEffects::register); bus.addListener(SmokeBurstEffects::register); bus.addListener(TinyExplosionEffects::register);
        container.registerConfig(ModConfig.Type.COMMON, CONFIG, "hbm-conventional.toml");
        LandmineContent.register(bus,container);
        ConventionalProjectiles.register(bus);MultiBombContent.register(bus);HazardousBarrels.register(bus);com.hbm.bomb.conventional.grenade.GrenadeContent.register(bus);
    }
    public static List<ItemStack> creativeStacks(String tab) {
        if(tab.equals("weapon"))return com.hbm.bomb.conventional.grenade.GrenadeContent.creativeStacks();
        if (!tab.equals("nuke")&&!tab.equals("block")) return List.of();
        var result = new ArrayList<ItemStack>();BY_ID.forEach((id,item)->{boolean construction=id.equals("block_semtex")||id.equals("block_c4");if(tab.equals(construction?"block":"nuke"))result.add(new ItemStack(item.get()));});result.addAll(LandmineContent.creativeStacks(tab));result.addAll(MultiBombContent.creativeStacks(tab));result.addAll(HazardousBarrels.creativeStacks(tab));return result;
    }
    public static Item item(String id) { return BY_ID.get(id).get(); }
}
