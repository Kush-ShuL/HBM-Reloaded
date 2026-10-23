package com.hbm.weapon.sedna.gun.chemthrower;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.MagazineFluid;
import com.hbm.weapon.sedna.gun.mods.*;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
/** Exact XFactoryFlamer chemthrower receiver; native gun controls/state/armor/damage/modifier pipeline remain unique. */
public final class ChemthrowerContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static BiConsumer<ItemStack,LambdaContext> CLIENT_ORCHESTRA=(stack,context)->{};
 public static final DeferredItem<ItemGunChemthrower> GUN=ITEMS.register("gun_chemthrower",()->new ItemGunChemthrower(WeaponQuality.A_SIDE,new GunConfig().dura(90000).draw(10).inspect(17).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE).rec(new Receiver(0).delay(1).spreadHipfire(0).auto(true).mag(new MagazineFluid(0,3000)).offset(.75,-.0625,-.25).canFire(ItemGunChemthrower.LAMBDA_CAN_FIRE).fire(ItemGunChemthrower.LAMBDA_FIRE)).pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).decider(GunStateDecider.LAMBDA_STANDARD_DECIDER).anim((stack,type)->type==GunAnimation.EQUIP?new BusAnimation().addBus("EQUIP",new BusAnimationSequence().addPos(-45,0,0,0).addPos(0,0,0,500,IType.SIN_DOWN)):null).orchestra((stack,context)->{if(context.entity.level().isClientSide)CLIENT_ORCHESTRA.accept(stack,context);})));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityChemical>> CHEMICAL=ENTITIES.register("entity_chemthrower_splash",()->EntityType.Builder.<EntityChemical>of(EntityChemical::new,MobCategory.MISC).sized(.25F,.25F).fireImmune().clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_chemthrower_splash"));
 public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->{SourceWeaponModBindings.definition("weapon_mod_generic",8).addMod(GUN.get(),SourceWeaponModBindings.handler(108,WeaponModGenericDamage.class,WeaponModGenericDamage::new));SourceWeaponModBindings.definition("weapon_mod_generic",9).addMod(GUN.get(),SourceWeaponModBindings.handler(109,WeaponModGenericDurability.class,WeaponModGenericDurability::new));}));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(GUN.get())):List.of();}private ChemthrowerContent(){}
}
