package com.hbm.entity.mob.glyphid;

import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.entity.projectile.EntityAcidBomb;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

/** Six actual source mobs and their task marker/acid projectile. No fake special variant or natural spawner. */
public final class GlyphidContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphid>> GRUNT=mob("entity_glyphid",EntityGlyphid::new,1.75F,1,false);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphidBrawler>> BRAWLER=mob("entity_glyphid_brawler",EntityGlyphidBrawler::new,2,1.125F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphidBombardier>> BOMBARDIER=mob("entity_glyphid_bombardier",EntityGlyphidBombardier::new,1.75F,1,false);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphidBlaster>> BLASTER=mob("entity_glyphid_blaster",EntityGlyphidBlaster::new,2,1.125F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphidBehemoth>> BEHEMOTH=mob("entity_glyphid_behemoth",EntityGlyphidBehemoth::new,2.5F,1.5F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphidBrenda>> BRENDA=mob("entity_glyphid_brenda",EntityGlyphidBrenda::new,2.5F,1.75F,true);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityWaypoint>> WAYPOINT=ENTITIES.register("entity_waypoint",()->EntityType.Builder.<EntityWaypoint>of(EntityWaypoint::new,MobCategory.MISC).sized(.6F,1.8F).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_waypoint"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityAcidBomb>> ACID=ENTITIES.register("entity_acid_bomb",()->EntityType.Builder.<EntityAcidBomb>of(EntityAcidBomb::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_acid_bomb"));
    public static final DeferredItem<DeferredSpawnEggItem> GRUNT_EGG=egg("glyphid_spawn_egg",GRUNT,0x724A21,0xD2BB72);
    public static final DeferredItem<DeferredSpawnEggItem> BRAWLER_EGG=egg("glyphid_brawler_spawn_egg",BRAWLER,0x273038,0xD2BB72);
    public static final DeferredItem<DeferredSpawnEggItem> BOMBARDIER_EGG=egg("glyphid_bombardier_spawn_egg",BOMBARDIER,0xDDD919,0xDBB79D);
    public static final DeferredItem<DeferredSpawnEggItem> BLASTER_EGG=egg("glyphid_blaster_spawn_egg",BLASTER,0xD83737,0xDBB79D);
    public static final DeferredItem<DeferredSpawnEggItem> BEHEMOTH_EGG=egg("glyphid_behemoth_spawn_egg",BEHEMOTH,0x267F00,0xD2BB72);
    public static final DeferredItem<DeferredSpawnEggItem> BRENDA_EGG=egg("glyphid_brenda_spawn_egg",BRENDA,0x4FC0C0,0xA0A0A0);
    private static <T extends EntityGlyphid> DeferredHolder<EntityType<?>,EntityType<T>> mob(String id,EntityType.EntityFactory<T> factory,float width,float height,boolean immune){return ENTITIES.register(id,()->{var builder=EntityType.Builder.of(factory,MobCategory.MONSTER).sized(width,height).eyeHeight(height*.85F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true);if(immune)builder.fireImmune();return builder.build("hbm:"+id);});}
    private static <T extends EntityGlyphid> DeferredItem<DeferredSpawnEggItem> egg(String id,DeferredHolder<EntityType<?>,EntityType<T>> type,int primary,int secondary){return ITEMS.register(id,()->new DeferredSpawnEggItem(type,primary,secondary,new Item.Properties()));}
    public static void register(IEventBus bus,ModContainer container){ENTITIES.register(bus);ITEMS.register(bus);bus.addListener(GlyphidContent::attributes);bus.addListener(GlyphidContent::creative);container.registerConfig(ModConfig.Type.COMMON,GlyphidConfig.SPEC,"hbm-glyphids.toml");}
    private static void attributes(EntityAttributeCreationEvent event){var stats=GlyphidStats.getStats();event.put(GRUNT.get(),EntityGlyphid.attributes(stats.getGrunt()).build());event.put(BRAWLER.get(),EntityGlyphid.attributes(stats.getBrawler()).build());event.put(BOMBARDIER.get(),EntityGlyphid.attributes(stats.getBombardier()).build());event.put(BLASTER.get(),EntityGlyphid.attributes(stats.getBlaster()).build());event.put(BEHEMOTH.get(),EntityGlyphid.attributes(stats.getBehemoth()).build());event.put(BRENDA.get(),EntityGlyphid.attributes(stats.getBrenda()).build());}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)for(var stack:indexedItems())event.accept(stack);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(GRUNT_EGG.get()),new ItemStack(BRAWLER_EGG.get()),new ItemStack(BOMBARDIER_EGG.get()),new ItemStack(BLASTER_EGG.get()),new ItemStack(BEHEMOTH_EGG.get()),new ItemStack(BRENDA_EGG.get()));}
    private GlyphidContent(){}
}
