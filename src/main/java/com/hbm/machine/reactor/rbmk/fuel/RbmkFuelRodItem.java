package com.hbm.machine.reactor.rbmk.fuel;
import java.util.*;import java.util.function.*;import com.hbm.machine.reactor.rbmk.*;import com.hbm.machine.reactor.fuelpool.FuelPoolRod;import com.hbm.hazard.HazardItemState;import com.hbm.registry.HbmContent;import net.minecraft.ChatFormatting;import net.minecraft.core.component.DataComponents;import net.minecraft.nbt.CompoundTag;import net.minecraft.network.chat.Component;import net.minecraft.util.Mth;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.*;import net.minecraft.world.item.component.CustomData;import net.minecraft.world.level.Level;
/** Exact X5846 ItemRBMKRod burn, xenon, enrichment, thermal and optional flux-curve functions. State belongs to each stack. */
public final class RbmkFuelRodItem extends Item implements HazardItemState,FuelPoolRod {
	public RbmkPelletItem pellet;
	public String fullName = "";			//full name of the fuel rod
	public double reactivity;				//endpoint of the function
	public double selfRate;					//self-inflicted flux from self-igniting fuels
	public EnumBurnFunc function = EnumBurnFunc.LOG_TEN;
	public EnumDepleteFunc depFunc = EnumDepleteFunc.GENTLE_SLOPE;
	public double xGen = 0.5D;				//multiplier for xenon production
	public double xBurn = 50D;				//divider for xenon burnup
	public double heat = 1D;				//heat produced per outFlux
	public double yield;					//total potential inFlux the rod can take in its lifetime
	public double meltingPoint = 1000D;		//the maximum heat of the rod's hull before shit hits the fan. the core can be as hot as it wants to be
	public double diffusion = 0.02D;		//the speed at which the core heats the hull
	public NeutronType nType = NeutronType.SLOW;		//neutronType, the most efficient neutron type for fission
	public NeutronType rType = NeutronType.FAST;		//releaseType, the type of neutrons released by this fuel
	public int colorTint = 0x304825;		//RGB color of the rod when rendered in the fuel channel
	public double heatCoeffStart = 0D;		//when the heat coefficient starts acting
	public double heatCoeffLength = 0D;		//when the reaction multiplier of the coefficient hits 0 after taking effect


 public final String variant;public RbmkFuelRodItem(RbmkPelletItem pellet){this(pellet.variant,pellet.fullName);this.pellet=pellet;craftableRods.add(this);}public RbmkFuelRodItem(String variant,String fullName){super(new Properties().stacksTo(1));this.variant=variant;this.fullName=fullName;}public static final List<RbmkFuelRodItem> craftableRods=new ArrayList<>();
	public RbmkFuelRodItem setTint(int tint) {
		this.colorTint = tint;
		return this;
	}

	public RbmkFuelRodItem setYield(double yield) {
		this.yield = yield;
		return this;
	}

	public RbmkFuelRodItem setStats(double funcEnd) {
		return setStats(funcEnd, 0);
	}

	public RbmkFuelRodItem setStats(double funcEnd, double selfRate) {
		this.reactivity = funcEnd;
		this.selfRate = selfRate;
		return this;
	}

	public RbmkFuelRodItem setFunction(EnumBurnFunc func) {
		this.function = func;
		return this;
	}

	public RbmkFuelRodItem setDepletionFunction(EnumDepleteFunc func) {
		this.depFunc = func;
		return this;
	}
	
	public RbmkFuelRodItem setHeatCoeff(double start, double length) {
		this.heatCoeffStart = start;
		this.heatCoeffLength = length;
		return this;
	}

	public RbmkFuelRodItem setXenon(double gen, double burn) {
		this.xGen = gen;
		this.xBurn = burn;
		return this;
	}

	public RbmkFuelRodItem setHeat(double heat) {
		this.heat = heat;
		return this;
	}

	public RbmkFuelRodItem setDiffusion(double diffusion) {
		this.diffusion = diffusion;
		return this;
	}

	public RbmkFuelRodItem setMeltingPoint(double meltingPoint) {
		this.meltingPoint = meltingPoint;
		return this;
	}

	public RbmkFuelRodItem setNeutronTypes(NeutronType nType, NeutronType rType) {
		this.nType = nType;
		this.rType = rType;
		return this;
	}

	/**
	 * Adjusts the input flux using the poison level
	 * Generates, then burns poison
	 * Calculates the outflux based on influx, enrichment and poison
	 * Depletes the yield, then returns the outflux
	 * @param stack
	 * @param inFlux
	 * @return outFlux
	 */
	public double burn(Level world, ItemStack stack, double inFlux) {

		inFlux += selfRate;

		//if xenon poison is enabled
		if(RbmkDials.getXenon(world)) {
			double xenon = getPoison(stack);
			xenon -= xenonBurnFunc(inFlux);

			inFlux *= (1D - getPoisonLevel(stack));

			xenon += xenonGenFunc(inFlux);

			if(xenon < 0D) xenon = 0D;
			if(xenon > 100D) xenon = 100D;

			setPoison(stack, xenon);
		}

		double mult = 1D;
		double coreHeat = this.getCoreHeat(stack);
		
		if(this.heatCoeffStart != 0) {
			if(coreHeat >= this.heatCoeffStart) {
				double prog = (coreHeat - this.heatCoeffStart) / this.heatCoeffLength;
				if(prog > 1) prog = 1;
				mult = Math.sin((prog * Math.PI + Math.PI) / 2);
			}
		}
		
		double outFlux = reactivityFunc(inFlux, getEnrichment(stack) * mult) * RbmkDials.getReactivityMod(world);

		//if depletion is enabled
		if(RbmkDials.getDepletion(world)) {
			double y = getYield(stack);
			y -= inFlux;

			if(y < 0D) y = 0D;

			setYield(stack, y);
		}
		coreHeat += outFlux * heat;

		this.setCoreHeat(stack, rectify(coreHeat));

		return outFlux;
	}

	private double rectify(double num) {

		if(num > 1_000_000D) num = 1_000_000D;
		if(num < 20D || Double.isNaN(num)) num = 20D;

		return num;
	}

	/**
	 * Heat up the core based on the outFlux, then move some heat to the hull
	 * @param stack
	 */
	public void updateHeat(Level world, ItemStack stack, double mod) {

		double coreHeat = this.getCoreHeat(stack);
		double hullHeat = this.getHullHeat(stack);

		if(coreHeat > hullHeat) {

			double mid = (coreHeat - hullHeat) / 2D;

			coreHeat -= mid * this.diffusion * RbmkDials.getFuelDiffusionMod(world) * mod;
			hullHeat += mid * this.diffusion * RbmkDials.getFuelDiffusionMod(world) * mod;

			this.setCoreHeat(stack, rectify(coreHeat));
			this.setHullHeat(stack, rectify(hullHeat));
		}
	}

	/**
	 * return one tick's worth of heat and cool the hull of the fuel rod, this heat goes into the fuel rod assembly block
	 * @param stack
	 * @return
	 */
	public double provideHeat(Level world, ItemStack stack, double heat, double mod) {

		double hullHeat = this.getHullHeat(stack);

		//metldown! the hull melts so the entire structure stops making sense
		//hull and core heats are instantly equalized into 33% of their sum each,
		//the rest is sent to the component which is always fatal
		if(hullHeat > this.meltingPoint) {
			double coreHeat = this.getCoreHeat(stack);
			double avg = (heat + hullHeat + coreHeat) / 3D;
			this.setCoreHeat(stack, avg);
			this.setHullHeat(stack, avg);
			return avg - heat;
		}

		if(hullHeat <= heat)
			return 0;

		double ret = (hullHeat - heat) / 2;

		ret *= RbmkDials.getFuelHeatProvision(world) * mod;

		hullHeat -= ret;
		this.setHullHeat(stack, hullHeat);

		return ret;
	}

	public static enum EnumBurnFunc {
		PASSIVE(ChatFormatting.DARK_GREEN + "SAFE / PASSIVE"),			//const, no reactivity
		LOG_TEN(ChatFormatting.YELLOW + "MEDIUM / LOGARITHMIC"),		//log10(x + 1) * reactivity * 50
		PLATEU(ChatFormatting.GREEN + "SAFE / EULER"),					//(1 - e^(-x/25)) * reactivity * 100
		ARCH(ChatFormatting.RED + "DANGEROUS / NEGATIVE-QUADRATIC"),	//x-(x²/1000) * reactivity
		SIGMOID(ChatFormatting.GREEN + "SAFE / SIGMOID"),				//100 / (1 + e^(-(x - 50) / 10)) <- tiny amount of reactivity at x=0 !
		SQUARE_ROOT(ChatFormatting.YELLOW + "MEDIUM / SQUARE ROOT"),	//sqrt(x) * 10 * reactivity
		LINEAR(ChatFormatting.RED + "DANGEROUS / LINEAR"),				//x * reactivity
		QUADRATIC(ChatFormatting.RED + "DANGEROUS / QUADRATIC"),		//x^2 / 100 * reactivity
		EXPERIMENTAL(ChatFormatting.RED + "EXPERIMENTAL / SINE SLOPE");		//x * (sin(x) + 1)

		public String title = "";

		private EnumBurnFunc(String title) {
			this.title = title;
		}
	}

	/**
	 * @param enrichment [0;100] ...or at least those are sane levels
	 * @return the amount of reactivity yielded, unmodified by xenon
	 */
	public double reactivityFunc(double in, double enrichment) {

		double flux = in * reactivityModByEnrichment(enrichment);

		switch(this.function) {
		case PASSIVE: return selfRate * enrichment;
		case LOG_TEN: return Math.log10(flux + 1) * 0.5D * reactivity;
		case PLATEU: return (1 - Math.pow(Math.E, -flux / 25D)) * reactivity;
		case ARCH: return Math.max((flux - (flux * flux / 10000D)) / 100D * reactivity, 0D);
		case SIGMOID: return reactivity / (1 + Math.pow(Math.E, -(flux - 50D) / 10D));
		case SQUARE_ROOT: return Math.sqrt(flux) * reactivity / 10D;
		case LINEAR: return flux / 100D * reactivity;
		case QUADRATIC: return flux * flux / 10000D * reactivity;
		case EXPERIMENTAL: return flux * (Math.sin(flux) + 1) * reactivity;
		}

		return 0;
	}

	public String getFuncDescription(ItemStack stack) {

		String function;

		switch(this.function) {
		case PASSIVE: function = ChatFormatting.RED + "" + selfRate;
			break;
		case LOG_TEN: function = "log10(%1$s + 1) * 0.5 * %2$s";
			break;
		case PLATEU: function = "(1 - e^(-%1$s / 25)) * %2$s";
			break;
		case ARCH: function = "(%1$s - %1$s² / 10000) / 100 * %2$s [0;∞]";
			break;
		case SIGMOID: function = "%2$s / (1 + e^(-(%1$s - 50) / 10))";
			break;
		case SQUARE_ROOT: function = "sqrt(%1$s) * %2$s / 10";
			break;
		case LINEAR: function = "%1$s / 100 * %2$s";
			break;
		case QUADRATIC: function = "%1$s² / 10000 * %2$s";
			break;
		case EXPERIMENTAL: function = "%1$s * (sin(%1$s) + 1) * %2$s";
			break;
		default: function = "ERROR";
		}

		double enrichment = getEnrichment(stack);
		String flux = selfRate > 0 ? "(x" + ChatFormatting.RED + " + " + selfRate + "" + ChatFormatting.WHITE + ")" : "x";

		if(enrichment < 1) {
			enrichment = reactivityModByEnrichment(enrichment);
			String enrichmentMod = ChatFormatting.YELLOW + "" + ((int)(enrichment * 1000D) / 1000D) + ChatFormatting.WHITE;
			String enrichmentPer = ChatFormatting.GOLD + " (" + ((int)(enrichment * 1000D) / 10D) + "%)";

			flux = "(" + flux + " * " + enrichmentMod + ")";
			return String.format(Locale.US, function, flux, reactivity).concat(enrichmentPer);
		}

		return String.format(Locale.US, function, flux, reactivity);
	}

	public static enum EnumDepleteFunc {
		LINEAR,			//old function
		RAISING_SLOPE,	//for breeding fuels such as MEU, maximum of 110% at 28% depletion
		BOOSTED_SLOPE,	//for strong breeding fuels such Th232, maximum of 132% at 64% depletion
		GENTLE_SLOPE,	//recommended for most fuels, maximum barely over the start, near the beginning
		STATIC;			//for arcade-style neutron sources
	}

	public double reactivityModByEnrichment(double enrichment) {

		switch(this.depFunc) {
		default:
		case LINEAR: return enrichment;
		case STATIC: return 1D;
		case BOOSTED_SLOPE: return enrichment + Math.sin((enrichment - 1) * (enrichment - 1) * Math.PI); //x + sin([x - 1]^2 * pi) works
		case RAISING_SLOPE: return enrichment + (Math.sin(enrichment * Math.PI) / 2D); //x + (sin(x * pi) / 2) actually works
		case GENTLE_SLOPE: return enrichment + (Math.sin(enrichment * Math.PI) / 3D); //x + (sin(x * pi) / 3) also works
		}
	}

	/**
	 * Xenon generated per tick, linear function
	 * @param flux
	 * @return
	 */
	public double xenonGenFunc(double flux) {
		return flux * xGen;
	}

	/**
	 * Xenon burned away per tick, quadratic function
	 * @param flux
	 * @return
	 */
	public double xenonBurnFunc(double flux) {
		return (flux * flux) / xBurn;
	}

	/**
	 * @param stack
	 * @return enrichment [0;1]
	 */
	public static double getEnrichment(ItemStack stack) {
		return getYield(stack) / ((RbmkFuelRodItem) stack.getItem()).yield;
	}

	/**
	 * @param stack
	 * @return poison [0;1]
	 */
	public static double getPoisonLevel(ItemStack stack) {
		return getPoison(stack) / 100D;
	}

	// START Special flux curve handling!
	// Nothing really uses this yet, though it's a really fun feature to play around with.

	// For the RBMK handler to see if the rod is special.
	public boolean specialFluxCurve = false;

	public RbmkFuelRodItem setFluxCurve(boolean bool) {
		specialFluxCurve = bool;
		return this;
	}

	/** Double 1: Flux ratio in.
	 * Double 2: Depletion value.
	 * Return double: Output flux ratio.
	 **/
	BiFunction<Double, Double, Double> ratioCurve;

	/** Double 1: Flux quantity in. <br>
	 * Double 2: Flux ratio in. <br>
	 * Return double: Output flux quantity.
	 **/
	BiFunction<Double, Double, Double> fluxCurve;

	public RbmkFuelRodItem setOutputRatioCurve(Function<Double, Double> func) {
		this.ratioCurve = (fluxRatioIn, depletion) -> func.apply(fluxRatioIn) * 1.0D;
		return this;
	}

	public RbmkFuelRodItem setDepletionOutputRatioCurve(BiFunction<Double, Double, Double> func) {
		this.ratioCurve = func;
		return this;
	}

	public RbmkFuelRodItem setOutputFluxCurve(BiFunction<Double, Double, Double> func) {
		this.fluxCurve = func;
		return this;
	}

	public double fluxRatioOut(double fluxRatioIn, double depletion) {
		return Mth.clamp(ratioCurve.apply(fluxRatioIn, depletion), 0, 1);
	}

	public double fluxFromRatio(double quantity, double ratio) {
		return fluxCurve.apply(quantity, ratio);
	}

	// END Special flux curve handling.


 @Override public Component getName(ItemStack stack){return Component.translatable("item."+net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).getPath()+".name");}
 @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}@Override public ItemStack getCraftingRemainingItem(ItemStack stack){return new ItemStack(HbmContent.item("rbmk_fuel_empty"));}
 public Component composition(){var text=Component.translatable("rbmk.fuel."+variant+".composition").withStyle(ChatFormatting.ITALIC);return variant.equals("drx")?text.withStyle(ChatFormatting.OBFUSCATED):text;}
 private Component burnLabel(){return Component.translatable("rbmk.burn."+function.name().toLowerCase(Locale.ROOT)).withStyle(switch(function){case PASSIVE->ChatFormatting.DARK_GREEN;case LOG_TEN,SQUARE_ROOT->ChatFormatting.YELLOW;case PLATEU,SIGMOID->ChatFormatting.GREEN;default->ChatFormatting.RED;});}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(composition());String family=variant.equals("drx")?"trait.rbmx.":"trait.rbmk.";if(getHullHeat(stack)>=50||getCoreHeat(stack)>=50)lines.add(Component.translatable("desc.item.wasteCooling").withStyle(ChatFormatting.GOLD));if(selfRate>0||function==EnumBurnFunc.SIGMOID)lines.add(Component.translatable(family+"source").withStyle(ChatFormatting.RED));lines.add(Component.translatable(family+"depletion",((int)(((yield-getYield(stack))/yield)*100000D))/1000D+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable(family+"xenon",((int)(getPoison(stack)*1000D)/1000D)+"%").withStyle(ChatFormatting.DARK_PURPLE));lines.add(Component.translatable(family+"splitsWith",Component.translatable(nType.unlocalized+(variant.equals("drx")?".x":""))).withStyle(ChatFormatting.BLUE));lines.add(Component.translatable(family+"splitsInto",Component.translatable(rType.unlocalized+(variant.equals("drx")?".x":""))).withStyle(ChatFormatting.BLUE));lines.add(Component.translatable(family+"fluxFunc",ChatFormatting.WHITE+getFuncDescription(stack)).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable(family+"funcType",burnLabel()).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable(family+"xenonGen",ChatFormatting.WHITE+"x * "+xGen).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable(family+"xenonBurn",ChatFormatting.WHITE+"x² / "+xBurn).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable(family+"heat",heat+"°C").withStyle(ChatFormatting.GOLD));lines.add(Component.translatable(family+"diffusion",diffusion+"¹/²").withStyle(ChatFormatting.GOLD));String unit=variant.equals("drx")?"m":"°C";lines.add(Component.translatable(family+"skinTemp",((int)(getHullHeat(stack)*10D)/10D)+unit).withStyle(ChatFormatting.RED));lines.add(Component.translatable(family+"coreTemp",((int)(getCoreHeat(stack)*10D)/10D)+unit).withStyle(ChatFormatting.RED));lines.add(Component.translatable(family+"melt",meltingPoint+unit).withStyle(ChatFormatting.DARK_RED));}
 public static void setYield(ItemStack stack,double value){setDouble(stack,"yield",value);}public static double getYield(ItemStack stack){return stack.getItem() instanceof RbmkFuelRodItem?getDouble(stack,"yield"):0;}public static void setPoison(ItemStack stack,double value){setDouble(stack,"xenon",value);}public static double getPoison(ItemStack stack){return getDouble(stack,"xenon");}public static void setCoreHeat(ItemStack stack,double value){setDouble(stack,"core",value);}public static double getCoreHeat(ItemStack stack){return getDouble(stack,"core");}public static void setHullHeat(ItemStack stack,double value){setDouble(stack,"hull",value);}public static double getHullHeat(ItemStack stack){return getDouble(stack,"hull");}
 private static CompoundTag data(ItemStack stack){var component=stack.get(DataComponents.CUSTOM_DATA);if(component!=null)return component.copyTag();setNBTDefaults(stack);return stack.get(DataComponents.CUSTOM_DATA).copyTag();}public static void setDouble(ItemStack stack,String key,double value){var tag=data(stack);tag.putDouble(key,value);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}public static double getDouble(ItemStack stack,String key){return data(stack).getDouble(key);}private static void setNBTDefaults(ItemStack stack){var tag=new CompoundTag();tag.putDouble("yield",((RbmkFuelRodItem)stack.getItem()).yield);tag.putDouble("core",20);tag.putDouble("hull",20);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 @Override public void onCraftedBy(ItemStack stack,Level level,Player player){setNBTDefaults(stack);super.onCraftedBy(stack,level,player);}@Override public boolean isBarVisible(ItemStack stack){return 1-getEnrichment(stack)>0;}@Override public int getBarWidth(ItemStack stack){return Math.round(13F-(float)(1-getEnrichment(stack))*13F);}@Override public int getBarColor(ItemStack stack){return Mth.hsvToRgb(Math.max(0,(float)getEnrichment(stack))/3F,1,1);}
 @Override public OptionalDouble hazardEnrichment(ItemStack stack){return OptionalDouble.of(getEnrichment(stack));}@Override public OptionalDouble hazardDepletion(ItemStack stack){return OptionalDouble.of(1-getEnrichment(stack));}@Override public OptionalDouble hazardXenon(ItemStack stack){return OptionalDouble.of(getPoisonLevel(stack));}@Override public OptionalDouble hazardHullHeat(ItemStack stack){return OptionalDouble.of(getHullHeat(stack));}@Override public double fuelPoolCoreHeat(ItemStack stack){return getCoreHeat(stack);}@Override public double fuelPoolHullHeat(ItemStack stack){return getHullHeat(stack);}@Override public void coolInFuelPool(Level level,ItemStack stack){updateHeat(level,stack,.025D);provideHeat(level,stack,20D,.025D);}
}
