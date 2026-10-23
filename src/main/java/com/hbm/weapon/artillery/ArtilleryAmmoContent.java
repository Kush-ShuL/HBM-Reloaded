package com.hbm.weapon.artillery;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.registries.*;
/** Actual source ammo item, actual shell entity and the actual cargo-handler serializer; no turret or missile registry is duplicated. */
public final class ArtilleryAmmoContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> RECIPES=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredItem<ArtilleryAmmoItem> AMMO=ITEMS.register("ammo_arty",ArtilleryAmmoItem::new);
    public static final DeferredHolder<EntityType<?>,EntityType<ArtilleryShellEntity>> SHELL=ENTITIES.register("entity_artillery_shell",()->EntityType.Builder.<ArtilleryShellEntity>of(ArtilleryShellEntity::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_artillery_shell"));
    public static final DeferredHolder<RecipeSerializer<?>,SimpleCraftingRecipeSerializer<CargoShellRecipe>> CARGO_RECIPE=RECIPES.register("artillery_cargo",()->new SimpleCraftingRecipeSerializer<>(CargoShellRecipe::new));
    public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);RECIPES.register(bus);bus.addListener((RegisterTicketControllersEvent event)->event.register(ArtilleryShellEntity.TICKETS));bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->{for(int type=0;type<12;type++)ArtyShellTypes.casing(type);com.hbm.machine.turret.TurretTargeting.machines.add(ArtilleryShellEntity.class);}));}
    public static List<ItemStack> creativeStacks(String tab){return "weapon".equals(tab)?ArtilleryAmmoItem.creative():List.of();}
    private ArtilleryAmmoContent(){}
}
