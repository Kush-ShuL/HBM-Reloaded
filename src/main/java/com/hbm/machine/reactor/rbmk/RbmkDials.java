package com.hbm.machine.reactor.rbmk;
import java.util.*;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.level.*;import net.minecraft.util.Mth;import net.neoforged.neoforge.common.NeoForge;import net.neoforged.neoforge.event.level.LevelEvent;import com.hbm.machine.reactor.zirnox.ReactorWorldRules;
/** All original world dial names, defaults, bounds and negated flags, saved and editable through native /gamerule. */
public final class RbmkDials {
public enum RBMKKeys {
		KEY_SAVE_DIALS("dialSaveDials", true),
		KEY_PASSIVE_COOLING("dialPassiveCooling", 2.5),
		KEY_PASSIVE_COOLING_INNER("dialPassiveCoolingInner", 0.1),
		KEY_COLUMN_HEAT_FLOW("dialColumnHeatFlow", 0.2),
		KEY_FUEL_DIFFUSION_MOD("dialDiffusionMod", 1.0),
		KEY_HEAT_PROVISION("dialHeatProvision", 0.2),
		KEY_COLUMN_HEIGHT("dialColumnHeight", 4),
		KEY_PERMANENT_SCRAP("dialEnablePermaScrap", true),
		KEY_BOILER_HEAT_CONSUMPTION("dialBoilerHeatConsumption", 0.1),
		KEY_CONTROL_SPEED_MOD("dialControlSpeed", 1.0),
		KEY_REACTIVITY_MOD("dialReactivityMod", 1.0),
		KEY_OUTGASSER_MOD("dialOutgasserSpeedMod", 1.0),
		KEY_SURGE_MOD("dialControlSurgeMod", 1.0),
		KEY_FLUX_RANGE("dialFluxRange", 5),
		KEY_REASIM_RANGE("dialReasimRange", 10),
		KEY_REASIM_BOILERS("dialReasimBoilers", false),
		KEY_REASIM_BOILER_SPEED("dialReasimBoilerSpeed", 0.05),
		KEY_DISABLE_MELTDOWNS("dialDisableMeltdowns", false),
		KEY_ENABLE_MELTDOWN_OVERPRESSURE("dialEnableMeltdownOverpressure", false),
		KEY_MODERATOR_EFFICIENCY("dialModeratorEfficiency", 1.0),
		KEY_ABSORBER_EFFICIENCY("dialAbsorberEfficiency", 1.0),
		KEY_REFLECTOR_EFFICIENCY("dialReflectorEfficiency", 1.0),
		KEY_DISABLE_DEPLETION("dialDisableDepletion", false),
		KEY_DISABLE_XENON("dialDisableXenon", false),
		KEY_ABSORBER_HEAT_CONVERSION("dialAbsorberHeatConversion", 0.05);

		public final String keyString;
		public final Object defValue;

		RBMKKeys(String key, Object def) {
			keyString = key;
			defValue = def;
		}
	}

	
 private static final Map<RBMKKeys,GameRules.Key<?>> KEYS=new EnumMap<>(RBMKKeys.class);static{for(var key:RBMKKeys.values()){GameRules.Key<?> nativeKey;if(key==RBMKKeys.KEY_PERMANENT_SCRAP)nativeKey=ReactorWorldRules.PERMANENT_SCRAP;else if(key.defValue instanceof Boolean value)nativeKey=GameRules.register(key.keyString,GameRules.Category.MISC,GameRules.BooleanValue.create(value));else if(key.defValue instanceof Integer value)nativeKey=GameRules.register(key.keyString,GameRules.Category.MISC,GameRules.IntegerValue.create(value));else nativeKey=GameRules.register(key.keyString,GameRules.Category.MISC,RbmkDecimalValue.create((Double)key.defValue));KEYS.put(key,nativeKey);}}
 public static void initialize(){NeoForge.EVENT_BUS.addListener((LevelEvent.Load event)->{if(event.getLevel() instanceof ServerLevel level)createDials(level,false);});}
 public static void createDials(Level world){createDials(world,false);}public static void createDials(Level world,boolean force){if(world.isClientSide)return;if(force||!(boolean)raw(world,RBMKKeys.KEY_SAVE_DIALS))for(var key:RBMKKeys.values())set(world,key,key.defValue);}
 @SuppressWarnings({"unchecked","rawtypes"})private static Object raw(Level world,RBMKKeys key){GameRules.Value value=world.getGameRules().getRule((GameRules.Key)KEYS.get(key));if(value instanceof GameRules.BooleanValue bool)return bool.get();if(value instanceof GameRules.IntegerValue integer)return integer.get();return ((RbmkDecimalValue)value).get();}
 @SuppressWarnings({"unchecked","rawtypes"})public static void set(Level world,RBMKKeys key,Object value){if(world.isClientSide)return;var server=world.getServer();GameRules.Value rule=world.getGameRules().getRule((GameRules.Key)KEYS.get(key));if(rule instanceof GameRules.BooleanValue bool)bool.set((Boolean)value,server);else if(rule instanceof GameRules.IntegerValue integer)integer.set(((Number)value).intValue(),server);else ((RbmkDecimalValue)rule).set(((Number)value).doubleValue(),server);}
 public static void refresh(Level world){} // Native rules are read directly, so source command-driven cache refresh cannot become stale.
 public static Object getGameRule(Level world,RBMKKeys key){if(world.isClientSide)return key.defValue;Object value=raw(world,key);if(value instanceof Boolean){return key==RBMKKeys.KEY_REASIM_BOILERS?(Boolean)value||com.hbm.config.GeneralModes.forceReasimBoilers():value;}if(value instanceof Integer integer)return switch(key){case KEY_COLUMN_HEIGHT->Mth.clamp(integer,2,16)-1;case KEY_FLUX_RANGE,KEY_REASIM_RANGE->Mth.clamp(integer,1,100);default->integer;};double decimal=(Double)value;return switch(key){case KEY_COLUMN_HEAT_FLOW,KEY_HEAT_PROVISION,KEY_REASIM_BOILER_SPEED,KEY_MODERATOR_EFFICIENCY,KEY_ABSORBER_EFFICIENCY,KEY_ABSORBER_HEAT_CONVERSION,KEY_REFLECTOR_EFFICIENCY->Mth.clamp(decimal,0,1);default->Math.max(decimal,0);};}
	/**
	 * Returns the amount of heat per tick removed from components passively, when at an edge
	 * @param world
	 * @return >0
	 */
	public static double getPassiveCooling(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_PASSIVE_COOLING);
	}

	/**
	 * Returns the amount of heat per tick removed from components passively, when surrounded by other components
	 * @param world
	 * @return >0
	 */
	public static double getPassiveCoolingInner(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_PASSIVE_COOLING_INNER);
	}

	/**
	 * Returns the percentual step size how quickly neighboring component heat equalizes. 1 is instant, 0.5 is in 50% steps, et cetera.
	 * @param world
	 * @return [0;1]
	 */
	public static double getColumnHeatFlow(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_COLUMN_HEAT_FLOW);
	}

	/**
	 * Returns a modifier for fuel rod diffusion, i.e. how quickly the core and hull temperatures equalize.
	 * @param world
	 * @return >0
	 */
	public static double getFuelDiffusionMod(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_FUEL_DIFFUSION_MOD);
	}

	/**
	 * Returns the percentual step size how quickly the fuel hull heat and the component heat equalizes. 1 is instant, 0.5 is in 50% steps, et cetera.
	 * @param world
	 * @return [0;1]
	 */
	public static double getFuelHeatProvision(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_HEAT_PROVISION);
	}

	/**
	 * Simple integer that decides how tall the structure is.
	 * @param world
	 * @return [0;15]
	 */
	public static int getColumnHeight(Level world) {
		return (int) getGameRule(world, RBMKKeys.KEY_COLUMN_HEIGHT);
	}

	/**
	 * Whether or not scrap entities despawn on their own or remain alive until picked up.
	 * @param world
	 * @return
	 */
	public static boolean getPermaScrap(Level world) {
		return (boolean) getGameRule(world, RBMKKeys.KEY_PERMANENT_SCRAP);
	}

	/**
	 * How many heat units are consumed per mB water used.
	 * @param world
	 * @return >0
	 */
	public static double getBoilerHeatConsumption(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_BOILER_HEAT_CONSUMPTION);
	}

	/**
	 * A multiplier for how quickly the control rods move.
	 * @param world
	 * @return >0
	 */
	public static double getControlSpeed(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_CONTROL_SPEED_MOD);
	}

	/**
	 * A multiplier for how much flux the rods give out.
	 * @param world
	 * @return >0
	 */
	public static double getReactivityMod(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_REACTIVITY_MOD);
	}

	/**
	 * A multiplier for how much flux the rods give out.
	 * @param world
	 * @return >0
	 */
	public static double getOutgasserMod(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_OUTGASSER_MOD);
	}

	/**
	 * A multiplier for how high the power surge goes when inserting control rods.
	 * @param world
	 * @return >0
	 */
	public static double getSurgeMod(Level world) {
		if(world.isClientSide) { // The control rods use this gamerule for RBMK diag, which happens to be calculated on the client side. whoops!
			return Math.max((Double)raw(world,RBMKKeys.KEY_PASSIVE_COOLING),0D);
		}
		return (double) getGameRule(world, RBMKKeys.KEY_SURGE_MOD);
	}

	/**
	 * Simple integer that decides how far the flux of a normal fuel rod reaches.
	 * @param world
	 * @return [1;100]
	 */
	public static int getFluxRange(Level world) {
		return (int) getGameRule(world, RBMKKeys.KEY_FLUX_RANGE);
	}

	/**
	 * Simple integer that decides how far the flux of a ReaSim fuel rod reaches.
	 * @param world
	 * @return [1;100]
	 */
	public static int getReaSimRange(Level world) {
		return (int) getGameRule(world, RBMKKeys.KEY_REASIM_RANGE);
	}

	/**
	 * Whether or not all components should act like boilers with dedicated in/outlet blocks
	 * @param world
	 * @return
	 */
	public static boolean getReasimBoilers(Level world) {
		return (boolean) getGameRule(world, RBMKKeys.KEY_REASIM_BOILERS);
	}

	/**
	 * How much % of the possible steam ends up being produced per tick
	 * @param world
	 * @return [0;1]
	 */
	public static double getReaSimBoilerSpeed(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_REASIM_BOILER_SPEED);
	}

	/**
	 * Whether or not fuel columns should initiate a meltdown when overheating
	 * The method is in reverse because the default for older worlds will be 'false'
	 * @param world
	 * @return
	 */
	public static boolean getMeltdownsDisabled(Level world) {
		return (boolean) getGameRule(world, RBMKKeys.KEY_DISABLE_MELTDOWNS);
	}

	/**
	 * Whether or not connected pipes and turbines should explode when the reactor undergoes a meltdown.
	 * @param world
	 * @return
	 */
	public static boolean getOverpressure(Level world) {
		return (boolean) getGameRule(world, RBMKKeys.KEY_ENABLE_MELTDOWN_OVERPRESSURE);
	}

	/**
	 * The percentage of neutrons to moderate from fast to slow when they pass through a moderator.
	 * @param world
	 * @return
	 */
	public static double getModeratorEfficiency(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_MODERATOR_EFFICIENCY);
	}

	/**
	 * The percentage of neutrons to be absorbed when a stream hits an absorber column.
	 * @param world
	 * @return
	 */
	public static double getAbsorberEfficiency(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_ABSORBER_EFFICIENCY);
	}

	/**
	 * How many °C are generated per one flux that hits an absorber.
	 * @param world
	 * @return
	 */
	public static double getAbsorberHeatConversion(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_ABSORBER_HEAT_CONVERSION);
	}

	/**
	 * The percentage of neutron to reflect when a stream hits a reflector column.
	 * @param world
	 * @return
	 */
	public static double getReflectorEfficiency(Level world) {
		return (double) getGameRule(world, RBMKKeys.KEY_REFLECTOR_EFFICIENCY);
	}
	
	/**
	 * Whether fuel rods should deplete, disabling this makes rods last forever
	 * @param world
	 * @return
	 */
	public static boolean getDepletion(Level world) {
		return !((boolean) getGameRule(world, RBMKKeys.KEY_DISABLE_DEPLETION));
	}
	
	/**
	 * Whether xenon poison should be calculated
	 * @param world
	 * @return
	 */
	public static boolean getXenon(Level world) {
		return !((boolean) getGameRule(world, RBMKKeys.KEY_DISABLE_XENON));
	}

 private RbmkDials(){}
}
