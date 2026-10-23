package com.hbm.weapon.b92;
import java.util.List;import net.minecraft.core.registries.Registries;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.entity.*;import net.minecraft.world.item.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.fml.ModContainer;import net.neoforged.neoforge.registries.*;
public final class B92Content {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredItem<B92Gun> GUN=ITEMS.register("gun_b92",B92Gun::new);public static final DeferredItem<B92Cell> CELL=ITEMS.register("gun_b92_ammo",B92Cell::new);public static final DeferredItem<B92WeaponizedCell> WEAPONIZED=ITEMS.register("weaponized_starblaster_cell",B92WeaponizedCell::new);
 public static final DeferredHolder<EntityType<?>,EntityType<EntityB92Beam>> BEAM=ENTITIES.register("entity_beam_bomb",()->EntityType.Builder.<EntityB92Beam>of(EntityB92Beam::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_beam_bomb"));
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static void register(IEventBus bus,ModContainer container){ITEMS.register(bus);ENTITIES.register(bus);B92Settings.register(container);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(GUN.get()),new ItemStack(CELL.get()),new ItemStack(WEAPONIZED.get())):List.of();}
 private B92Content(){}
}
