package com.hbm.bomb.conventional.grenade;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class GrenadeContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<GrenadeAssemblyRecipe>> ASSEMBLY=SERIALIZERS.register("grenade_assembly",()->new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(GrenadeAssemblyRecipe::new));
    public static final DeferredItem<UniversalGrenadeItem> UNIVERSAL=ITEMS.register("grenade_universal",UniversalGrenadeItem::new);
    public static final DeferredItem<com.hbm.item.EnumMultiItem> SHELL=component("grenade_shell",GrenadeParts.Shell.values()),FILLING=component("grenade_filling",GrenadeParts.Filling.values()),FUZE=component("grenade_fuze",GrenadeParts.Fuze.values()),EXTRA=component("grenade_extra",GrenadeParts.Extra.values());
    public static final DeferredHolder<EntityType<?>,EntityType<UniversalGrenade>> UNIVERSAL_ENTITY=ENTITIES.register("entity_grenade_universal",()->EntityType.Builder.<UniversalGrenade>of(UniversalGrenade::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_grenade_universal"));
    public static final DeferredItem<DynamiteGrenadeItem> DYNAMITE=ITEMS.register("stick_dynamite",DynamiteGrenadeItem::new);
    public static final DeferredHolder<EntityType<?>,EntityType<GenericGrenade>> GENERIC=ENTITIES.register("entity_grenade_bouncy_generic",()->EntityType.Builder.<GenericGrenade>of(GenericGrenade::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(16).updateInterval(1).build("hbm:entity_grenade_bouncy_generic"));
    public static final DeferredHolder<EntityType<?>,EntityType<LingeringFire>> LINGERING=ENTITIES.register("entity_fire_lingering",()->EntityType.Builder.<LingeringFire>of(LingeringFire::new,MobCategory.MISC).sized(.6F,1.8F).noSave().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_fire_lingering"));
    private GrenadeContent(){}
    public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);FireStatus.register(bus);SERIALIZERS.register(bus);GrenadeDeployment.register(bus);bus.addListener(GrenadeParticleEffects::register);GrenadeEffects.init();}
    private static DeferredItem<com.hbm.item.EnumMultiItem> component(String id,Enum<?>[] values){var names=java.util.Arrays.stream(values).map(value->value.name().toLowerCase(java.util.Locale.ROOT)).toList();var order=java.util.stream.IntStream.range(0,values.length).boxed().toList();return ITEMS.register(id,()->new com.hbm.item.EnumMultiItem(id,64,true,names,order));}
    public static List<net.minecraft.world.item.Item> components(){return List.of(SHELL.get(),FILLING.get(),FUZE.get(),EXTRA.get());}
    public static List<ItemStack> creativeStacks(){var result=new java.util.ArrayList<ItemStack>();result.add(new ItemStack(DYNAMITE.get()));for(var item:components())result.addAll(((com.hbm.item.EnumMultiItem)item).creativeStacks());for(var recipe:GrenadeRecipes.all())result.add(recipe.output());return result;}
}
