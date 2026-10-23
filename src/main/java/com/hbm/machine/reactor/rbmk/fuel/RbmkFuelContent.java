package com.hbm.machine.reactor.rbmk.fuel;
import java.util.*;import com.hbm.machine.reactor.rbmk.*;import com.hbm.machine.reactor.rbmk.fuel.RbmkFuelRodItem.*;import net.minecraft.ChatFormatting;import net.minecraft.core.registries.Registries;import net.minecraft.world.item.*;import net.minecraft.world.item.crafting.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
/** Generated directly from the active X5846 ModItems builder chains. */
public final class RbmkFuelContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 private static final int tintUranium=0x868D82;
 private static final int tintNeptunium=0x757E73;
 private static final int tintPlutonium=0x656E6B;
 private static final int tintAmericium=0xA88A8F;
 private static final int tintThorium=0x665448;
 private static final int tintZirconium=0xAAA36A;
 private static final int tintSchrabidium=0x2D9A94;
 private static final int tintPolonium=0x563A26;
 private static final int tintRadium=0xB3B6AD;
 private static final int tintAustralium=0xFFEE00;
 private static final int tintFlashgold=0xDC9613;
 private static final int tintFlashlead=0x7B7B87;
 private static final int tintBalefire=0xB2FF1B;
 private static final int tintDRX=0xD77276;
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_ueu=ITEMS.register("rbmk_pellet_ueu",()->new RbmkPelletItem("ueu","Unenriched Uranium"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_meu=ITEMS.register("rbmk_pellet_meu",()->new RbmkPelletItem("meu","Medium Enriched Uranium-235"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_heu233=ITEMS.register("rbmk_pellet_heu233",()->new RbmkPelletItem("heu233","Highly Enriched Uranium-233"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_heu235=ITEMS.register("rbmk_pellet_heu235",()->new RbmkPelletItem("heu235","Highly Enriched Uranium-235"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_uzh=ITEMS.register("rbmk_pellet_uzh",()->new RbmkPelletItem("uzh","Uranium Zirconium Hydride"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_thmeu=ITEMS.register("rbmk_pellet_thmeu",()->new RbmkPelletItem("thmeu","Thorium with MEU Driver Fuel"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_lep=ITEMS.register("rbmk_pellet_lep",()->new RbmkPelletItem("lep","Low Enriched Plutonium-239"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_mep=ITEMS.register("rbmk_pellet_mep",()->new RbmkPelletItem("mep","Medium Enriched Plutonium-239"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_hep239=ITEMS.register("rbmk_pellet_hep239",()->new RbmkPelletItem("hep239","Highly Enriched Plutonium-239"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_hep241=ITEMS.register("rbmk_pellet_hep241",()->new RbmkPelletItem("hep241","Highly Enriched Plutonium-241"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_lea=ITEMS.register("rbmk_pellet_lea",()->new RbmkPelletItem("lea","Low Enriched Americium-242"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_mea=ITEMS.register("rbmk_pellet_mea",()->new RbmkPelletItem("mea","Medium Enriched Americium-242"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_hea241=ITEMS.register("rbmk_pellet_hea241",()->new RbmkPelletItem("hea241","Highly Enriched Americium-241"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_hea242=ITEMS.register("rbmk_pellet_hea242",()->new RbmkPelletItem("hea242","Highly Enriched Americium-242"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_men=ITEMS.register("rbmk_pellet_men",()->new RbmkPelletItem("men","Medium Enriched Neptunium-237"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_hen=ITEMS.register("rbmk_pellet_hen",()->new RbmkPelletItem("hen","Highly Enriched Neptunium-237"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_mox=ITEMS.register("rbmk_pellet_mox",()->new RbmkPelletItem("mox","Mixed MEU & LEP Oxide"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_les=ITEMS.register("rbmk_pellet_les",()->new RbmkPelletItem("les","Low Enriched Schrabidium-326"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_mes=ITEMS.register("rbmk_pellet_mes",()->new RbmkPelletItem("mes","Medium Enriched Schrabidium-326"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_hes=ITEMS.register("rbmk_pellet_hes",()->new RbmkPelletItem("hes","Highly Enriched Schrabidium-326"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_leaus=ITEMS.register("rbmk_pellet_leaus",()->new RbmkPelletItem("leaus","Low Enriched Australium (Tasmanite)"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_heaus=ITEMS.register("rbmk_pellet_heaus",()->new RbmkPelletItem("heaus","Highly Enriched Australium (Ayerite)"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_po210be=ITEMS.register("rbmk_pellet_po210be",()->new RbmkPelletItem("po210be","Polonium-210 & Beryllium Neutron Source").disableXenon());
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_ra226be=ITEMS.register("rbmk_pellet_ra226be",()->new RbmkPelletItem("ra226be","Radium-226 & Beryllium Neutron Source").disableXenon());
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_pu238be=ITEMS.register("rbmk_pellet_pu238be",()->new RbmkPelletItem("pu238be","Plutonium-238 & Beryllium Neutron Source"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_balefire_gold=ITEMS.register("rbmk_pellet_balefire_gold",()->new RbmkPelletItem("balefire_gold","Antihydrogen in a Magnetized Gold-198 Lattice").disableXenon());
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_flashlead=ITEMS.register("rbmk_pellet_flashlead",()->new RbmkPelletItem("flashlead","Antihydrogen confined by a Magnetized Gold-198 and Lead-209 Lattice").disableXenon());
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_balefire=ITEMS.register("rbmk_pellet_balefire",()->new RbmkPelletItem("balefire","Draconic Flames").disableXenon());
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_zfb_bismuth=ITEMS.register("rbmk_pellet_zfb_bismuth",()->new RbmkPelletItem("zfb_bismuth","Zirconium Fast Breeder - LEU/HEP-241#Bi"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_zfb_pu241=ITEMS.register("rbmk_pellet_zfb_pu241",()->new RbmkPelletItem("zfb_pu241","Zirconium Fast Breeder - HEU-235/HEP-240#Pu-241"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_zfb_am_mix=ITEMS.register("rbmk_pellet_zfb_am_mix",()->new RbmkPelletItem("zfb_am_mix","Zirconium Fast Breeder - HEP-241#MEA"));
 public static final DeferredItem<RbmkPelletItem> rbmk_pellet_drx=ITEMS.register("rbmk_pellet_drx",()->new RbmkPelletItem("drx",ChatFormatting.OBFUSCATED + "can't you hear, can't you hear the thunder?"));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_ueu=ITEMS.register("rbmk_fuel_ueu",()->new RbmkFuelRodItem(rbmk_pellet_ueu.get())
				.setYield(100_000_000D)
				.setStats(15)
				.setFunction(EnumBurnFunc.LOG_TEN)
				.setDepletionFunction(EnumDepleteFunc.RAISING_SLOPE)
				.setHeat(0.65)                                                                                         
				.setMeltingPoint(2865)
				.setTint(tintUranium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_meu=ITEMS.register("rbmk_fuel_meu",()->new RbmkFuelRodItem(rbmk_pellet_meu.get())
				.setYield(100_000_000D)
				.setStats(20)
				.setFunction(EnumBurnFunc.LOG_TEN)
				.setDepletionFunction(EnumDepleteFunc.RAISING_SLOPE)
				.setHeat(0.65)                             
				.setMeltingPoint(2865)
				.setTint(tintUranium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_heu233=ITEMS.register("rbmk_fuel_heu233",()->new RbmkFuelRodItem(rbmk_pellet_heu233.get())
				.setYield(100_000_000D)
				.setStats(27.5D)
				.setFunction(EnumBurnFunc.LINEAR)
				.setHeat(1.25D)
				.setMeltingPoint(2865)
				.setTint(tintUranium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_heu235=ITEMS.register("rbmk_fuel_heu235",()->new RbmkFuelRodItem(rbmk_pellet_heu235.get())
				.setYield(100_000_000D)
				.setStats(50)                                                                                   
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setMeltingPoint(2865)
				.setTint(tintUranium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_uzh=ITEMS.register("rbmk_fuel_uzh",()->new RbmkFuelRodItem(rbmk_pellet_uzh.get())
				.setYield(50_000_000D)
				.setStats(30)
				.setFunction(EnumBurnFunc.LOG_TEN)
				.setDepletionFunction(EnumDepleteFunc.GENTLE_SLOPE)
				.setHeat(0.75)
				.setHeatCoeff(1_000D, 500D)
				.setDiffusion(0.1D)
				.setMeltingPoint(1845)
				.setTint(0x7077AF));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_thmeu=ITEMS.register("rbmk_fuel_thmeu",()->new RbmkFuelRodItem(rbmk_pellet_thmeu.get())
				.setYield(100000000D)
				.setStats(20)
				.setFunction(EnumBurnFunc.PLATEU)
				.setDepletionFunction(EnumDepleteFunc.BOOSTED_SLOPE)
				.setHeat(0.65D)                       
				.setMeltingPoint(3350)
				.setTint(tintThorium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_lep=ITEMS.register("rbmk_fuel_lep",()->new RbmkFuelRodItem(rbmk_pellet_lep.get())
				.setYield(100000000D)
				.setStats(35)
				.setFunction(EnumBurnFunc.LOG_TEN)
				.setDepletionFunction(EnumDepleteFunc.RAISING_SLOPE)
				.setHeat(0.75D)
				.setMeltingPoint(2744)
				.setTint(tintPlutonium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_mep=ITEMS.register("rbmk_fuel_mep",()->new RbmkFuelRodItem(rbmk_pellet_mep.get())
				.setYield(100000000D)
				.setStats(35)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setMeltingPoint(2744)
				.setTint(tintPlutonium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_hep239=ITEMS.register("rbmk_fuel_hep",()->new RbmkFuelRodItem(rbmk_pellet_hep239.get())
				.setYield(100000000D)
				.setStats(30)
				.setFunction(EnumBurnFunc.LINEAR)
				.setHeat(1.25D)
				.setMeltingPoint(2744)
				.setTint(tintPlutonium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_hep241=ITEMS.register("rbmk_fuel_hep241",()->new RbmkFuelRodItem(rbmk_pellet_hep241.get())
				.setYield(100000000D)
				.setStats(40)
				.setFunction(EnumBurnFunc.LINEAR)
				.setHeat(1.75D)
				.setMeltingPoint(2744)
				.setTint(tintPlutonium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_lea=ITEMS.register("rbmk_fuel_lea",()->new RbmkFuelRodItem(rbmk_pellet_lea.get())
				.setYield(100000000D)
				.setStats(60, 10)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setDepletionFunction(EnumDepleteFunc.RAISING_SLOPE)
				.setHeat(1.5D)
				.setMeltingPoint(2386)
				.setTint(tintAmericium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_mea=ITEMS.register("rbmk_fuel_mea",()->new RbmkFuelRodItem(rbmk_pellet_mea.get())
				.setYield(100000000D)
				.setStats(35D, 20)
				.setFunction(EnumBurnFunc.ARCH)
				.setHeat(1.75D)
				.setMeltingPoint(2386)
				.setTint(tintAmericium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_hea241=ITEMS.register("rbmk_fuel_hea241",()->new RbmkFuelRodItem(rbmk_pellet_hea241.get())
				.setYield(100000000D)
				.setStats(65, 15)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setHeat(1.85D)
				.setMeltingPoint(2386)
				.setNeutronTypes(NeutronType.FAST, NeutronType.FAST)
				.setTint(tintAmericium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_hea242=ITEMS.register("rbmk_fuel_hea242",()->new RbmkFuelRodItem(rbmk_pellet_hea242.get())
				.setYield(100000000D)
				.setStats(45)
				.setFunction(EnumBurnFunc.LINEAR)
				.setHeat(2D)
				.setMeltingPoint(2386)
				.setTint(tintAmericium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_men=ITEMS.register("rbmk_fuel_men",()->new RbmkFuelRodItem(rbmk_pellet_men.get())
				.setYield(100000000D)
				.setStats(30)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setDepletionFunction(EnumDepleteFunc.RAISING_SLOPE)
				.setHeat(0.75)
				.setMeltingPoint(2800)
				.setNeutronTypes(NeutronType.ANY, NeutronType.FAST)                                                               
				.setTint(tintNeptunium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_hen=ITEMS.register("rbmk_fuel_hen",()->new RbmkFuelRodItem(rbmk_pellet_hen.get())
				.setYield(100000000D)
				.setStats(40)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setMeltingPoint(2800)
				.setNeutronTypes(NeutronType.FAST, NeutronType.FAST)
				.setTint(tintNeptunium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_mox=ITEMS.register("rbmk_fuel_mox",()->new RbmkFuelRodItem(rbmk_pellet_mox.get())
				.setYield(100000000D)
				.setStats(40)
				.setFunction(EnumBurnFunc.LOG_TEN)
				.setDepletionFunction(EnumDepleteFunc.RAISING_SLOPE)
				.setMeltingPoint(2815)
				.setTint(tintUranium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_les=ITEMS.register("rbmk_fuel_les",()->new RbmkFuelRodItem(rbmk_pellet_les.get())
				.setYield(100000000D)
				.setStats(50)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setHeat(1.25D)
				.setMeltingPoint(2500)
				.setNeutronTypes(NeutronType.SLOW, NeutronType.SLOW)                       
				.setTint(tintSchrabidium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_mes=ITEMS.register("rbmk_fuel_mes",()->new RbmkFuelRodItem(rbmk_pellet_mes.get())
				.setYield(100000000D)
				.setStats(75D)
				.setFunction(EnumBurnFunc.ARCH)
				.setHeat(1.5D)
				.setMeltingPoint(2750)
				.setTint(tintSchrabidium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_hes=ITEMS.register("rbmk_fuel_hes",()->new RbmkFuelRodItem(rbmk_pellet_hes.get())
				.setYield(100000000D)
				.setStats(90)
				.setFunction(EnumBurnFunc.LINEAR)
				.setDepletionFunction(EnumDepleteFunc.LINEAR)
				.setHeat(1.75D)
				.setMeltingPoint(3000)
				.setTint(tintSchrabidium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_leaus=ITEMS.register("rbmk_fuel_leaus",()->new RbmkFuelRodItem(rbmk_pellet_leaus.get())
				.setYield(100000000D)
				.setStats(30)
				.setFunction(EnumBurnFunc.SIGMOID)
				.setDepletionFunction(EnumDepleteFunc.LINEAR)
				.setXenon(0.05D, 50D)
				.setHeat(1.5D)
				.setTint(tintAustralium).setMeltingPoint(7029));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_heaus=ITEMS.register("rbmk_fuel_heaus",()->new RbmkFuelRodItem(rbmk_pellet_heaus.get())
				.setYield(100000000D)
				.setStats(35)
				.setFunction(EnumBurnFunc.LINEAR)
				.setXenon(0.05D, 50D)
				.setHeat(1.5D)
				.setTint(tintAustralium).setMeltingPoint(5211));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_po210be=ITEMS.register("rbmk_fuel_po210be",()->new RbmkFuelRodItem(rbmk_pellet_po210be.get())
				.setYield(25000000D)
				.setStats(0D, 50)
				.setFunction(EnumBurnFunc.PASSIVE)
				.setDepletionFunction(EnumDepleteFunc.LINEAR)
				.setXenon(0.0D, 50D)
				.setHeat(0.1D)
				.setDiffusion(0.05D)
				.setMeltingPoint(1287)
				.setNeutronTypes(NeutronType.SLOW, NeutronType.SLOW)                       
				.setTint(tintPolonium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_ra226be=ITEMS.register("rbmk_fuel_ra226be",()->new RbmkFuelRodItem(rbmk_pellet_ra226be.get())
				.setYield(100000000D)
				.setStats(0D, 20)
				.setFunction(EnumBurnFunc.PASSIVE)
				.setDepletionFunction(EnumDepleteFunc.LINEAR)
				.setXenon(0.0D, 50D)
				.setHeat(0.035D)
				.setDiffusion(0.5D)
				.setMeltingPoint(700)
				.setNeutronTypes(NeutronType.SLOW, NeutronType.SLOW)                       
				.setTint(tintRadium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_pu238be=ITEMS.register("rbmk_fuel_pu238be",()->new RbmkFuelRodItem(rbmk_pellet_pu238be.get())
				.setYield(50000000D)
				.setStats(40, 40)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setHeat(0.1D)
				.setDiffusion(0.05D)
				.setMeltingPoint(1287)
				.setNeutronTypes(NeutronType.SLOW, NeutronType.SLOW)                       
				.setTint(tintPlutonium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_balefire_gold=ITEMS.register("rbmk_fuel_balefire_gold",()->new RbmkFuelRodItem(rbmk_pellet_balefire_gold.get())
				.setYield(100000000D)
				.setStats(50, 10)
				.setFunction(EnumBurnFunc.ARCH)
				.setDepletionFunction(EnumDepleteFunc.LINEAR)
				.setXenon(0.0D, 50D)
				.setMeltingPoint(2000)
				.setTint(tintFlashgold));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_flashlead=ITEMS.register("rbmk_fuel_flashlead",()->new RbmkFuelRodItem(rbmk_pellet_flashlead.get())
				.setYield(250000000D)
				.setStats(40, 50)
				.setFunction(EnumBurnFunc.ARCH)
				.setDepletionFunction(EnumDepleteFunc.LINEAR)
				.setXenon(0.0D, 50D)
				.setMeltingPoint(2050)
				.setTint(tintFlashlead));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_balefire=ITEMS.register("rbmk_fuel_balefire",()->new RbmkFuelRodItem(rbmk_pellet_balefire.get())
				.setYield(100000000D)
				.setStats(100, 35)
				.setFunction(EnumBurnFunc.LINEAR)
				.setXenon(0.0D, 50D)
				.setHeat(3D)
				.setMeltingPoint(3652)
				.setTint(tintBalefire));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_zfb_bismuth=ITEMS.register("rbmk_fuel_zfb_bismuth",()->new RbmkFuelRodItem(rbmk_pellet_zfb_bismuth.get())
				.setYield(50000000D)
				.setStats(20)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setHeat(1.75D)
				.setMeltingPoint(2744)
				.setTint(tintZirconium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_zfb_pu241=ITEMS.register("rbmk_fuel_zfb_pu241",()->new RbmkFuelRodItem(rbmk_pellet_zfb_pu241.get())
				.setYield(50000000D)
				.setStats(20)
				.setFunction(EnumBurnFunc.SQUARE_ROOT)
				.setMeltingPoint(2865)
				.setTint(tintZirconium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_zfb_am_mix=ITEMS.register("rbmk_fuel_zfb_am_mix",()->new RbmkFuelRodItem(rbmk_pellet_zfb_am_mix.get())
				.setYield(50000000D)
				.setStats(20)
				.setFunction(EnumBurnFunc.LINEAR)
				.setHeat(1.75D)
				.setMeltingPoint(2744)
				.setTint(tintZirconium));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_drx=ITEMS.register("rbmk_fuel_drx",()->new RbmkFuelRodItem(rbmk_pellet_drx.get())
				.setYield(10000000D)
				.setStats(1000, 10)
				.setFunction(EnumBurnFunc.QUADRATIC)
				.setHeat(0.1D)
				.setMeltingPoint(100000)
				.setTint(tintDRX));
 public static final DeferredItem<RbmkFuelRodItem> rbmk_fuel_test=ITEMS.register("rbmk_fuel_test",()->new RbmkFuelRodItem("test","THE VOICES")
				.setYield(1000000D)
				.setStats(100)
				.setFunction(EnumBurnFunc.EXPERIMENTAL)
				.setHeat(1.0D)
				.setMeltingPoint(100000)
				);
 public static final DeferredItem<DigammaParticleItem> PARTICLE=ITEMS.register("particle_digamma",()->new DigammaParticleItem(60));
 public static final List<DeferredItem<RbmkPelletItem>> PELLETS=List.of(rbmk_pellet_ueu,rbmk_pellet_meu,rbmk_pellet_heu233,rbmk_pellet_heu235,rbmk_pellet_uzh,rbmk_pellet_thmeu,rbmk_pellet_lep,rbmk_pellet_mep,rbmk_pellet_hep239,rbmk_pellet_hep241,rbmk_pellet_lea,rbmk_pellet_mea,rbmk_pellet_hea241,rbmk_pellet_hea242,rbmk_pellet_men,rbmk_pellet_hen,rbmk_pellet_mox,rbmk_pellet_les,rbmk_pellet_mes,rbmk_pellet_hes,rbmk_pellet_leaus,rbmk_pellet_heaus,rbmk_pellet_po210be,rbmk_pellet_ra226be,rbmk_pellet_pu238be,rbmk_pellet_balefire_gold,rbmk_pellet_flashlead,rbmk_pellet_balefire,rbmk_pellet_zfb_bismuth,rbmk_pellet_zfb_pu241,rbmk_pellet_zfb_am_mix,rbmk_pellet_drx);
 public static final List<DeferredItem<RbmkFuelRodItem>> RODS=List.of(rbmk_fuel_ueu,rbmk_fuel_meu,rbmk_fuel_heu233,rbmk_fuel_heu235,rbmk_fuel_uzh,rbmk_fuel_thmeu,rbmk_fuel_lep,rbmk_fuel_mep,rbmk_fuel_hep239,rbmk_fuel_hep241,rbmk_fuel_lea,rbmk_fuel_mea,rbmk_fuel_hea241,rbmk_fuel_hea242,rbmk_fuel_men,rbmk_fuel_hen,rbmk_fuel_mox,rbmk_fuel_les,rbmk_fuel_mes,rbmk_fuel_hes,rbmk_fuel_leaus,rbmk_fuel_heaus,rbmk_fuel_po210be,rbmk_fuel_ra226be,rbmk_fuel_pu238be,rbmk_fuel_balefire_gold,rbmk_fuel_flashlead,rbmk_fuel_balefire,rbmk_fuel_zfb_bismuth,rbmk_fuel_zfb_pu241,rbmk_fuel_zfb_am_mix,rbmk_fuel_drx,rbmk_fuel_test);
 public static final DeferredHolder<RecipeSerializer<?>,SimpleCraftingRecipeSerializer<RbmkFuelDisassembly>> DISASSEMBLY=SERIALIZERS.register("rbmk_fuel_disassembly",()->new SimpleCraftingRecipeSerializer<>(RbmkFuelDisassembly::new));
 public static void register(IEventBus bus){ITEMS.register(bus);SERIALIZERS.register(bus);RbmkDials.initialize();}public static List<ItemStack> allStacks(){var result=new ArrayList<ItemStack>();for(var item:PELLETS)result.addAll(item.get().creativeStacks());for(var item:RODS)result.add(new ItemStack(item.get()));result.add(new ItemStack(PARTICLE.get()));return List.copyOf(result);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?allStacks():List.of();}private RbmkFuelContent(){}
}
