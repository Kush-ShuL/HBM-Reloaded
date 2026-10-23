package com.hbm.machine.generator.gasturbine;

import com.hbm.fluid.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import java.math.BigInteger;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Mechanically source-backed startup/coastdown/temperature/RPM/fuel/steam algorithm. */
public abstract class GasTurbineOperation extends ThermalMachine implements com.hbm.multiblock.MuffledMachine {
    public static final long maxPower=1000000L;
    protected BigInteger power=BigInteger.ZERO;
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("GAS"),100000),new FluidTank(HbmFluids.fromName("LUBRICANT"),16000),new FluidTank(HbmFluids.WATER,16000),new FluidTank(HbmFluids.fromName("HOTSTEAM"),160000)};
    public static final Map<HbmFluidType,Double> fuelMaxCons=Map.of(HbmFluids.fromName("GAS"),50D,HbmFluids.fromName("SYNGAS"),10D,HbmFluids.fromName("OXYHYDROGEN"),100D,HbmFluids.fromName("REFORMGAS"),5D);
    public int rpm,temp,powerSliderPos,throttle,state,counter,instantPowerOutput;
    public final int rpmIdle=10,tempIdle=300;
    public boolean autoMode,muffled,tilted;
    public double waterToBoil;
    private int rpmLast,tempLast;
    private double fuelToConsume;
    protected GasTurbineOperation(BlockPos pos,BlockState state){super(GasTurbineContent.ENTITY.get(),pos,state,2);}
    protected float getVolume(float volume){return muffled?volume*.1F:volume;}
    protected abstract void pollute(HbmFluidType type,float amount);
    protected abstract void sourceSound(double x,double y,double z,String sound,float volume,float pitch);
protected final void stopIfNotReady() {

		if(tanks[0].getFill() == 0 || tanks[1].getFill() == 0) {
			state = 0;
		}
		if(!hasAcceptableFuel()) {
			state = 0;
		}
	}

public boolean hasAcceptableFuel() {

		if(tanks[0].getTankType().hasTrait(FluidTraits.Combustible.class)) {
			return tanks[0].getTankType().getTrait(FluidTraits.Combustible.class).getGrade() == FluidTraits.FuelGrade.GAS;
		}

		return false;
	}

protected final void startup() {

		counter++;

		if(counter <= 20) //rpm gauge 0-100-0
			rpm = 5 * counter;
		else if (counter > 20 && counter <= 40)
			rpm = 100 - 5 * (counter - 20);
		else if (counter > 50) {
			rpm = (int) (rpmIdle * (counter - 50) / 530); //slowly ramps up temp and RPM
			temp = (int) (tempIdle * (counter - 50) / 530);
		}

		if(counter == 50) {
			sourceSound(worldPosition.getX(), worldPosition.getY() + 2, worldPosition.getZ(), "hbm:block.turbinegasStartup", getVolume(1.0F), 1.0F);
		}

		if(counter == 580) {
			counter = 225; // ensures it shuts down properly when done immediately after startup
			state = 1;
		}
	}

protected final void shutdown() {

		autoMode = false;
		instantPowerOutput = 0;

		if(powerSliderPos > 0)
			powerSliderPos--;

		if(rpm <= 10 && counter > 0) {

			if(counter == 225) {

				sourceSound(worldPosition.getX(), worldPosition.getY() + 2, worldPosition.getZ(), "hbm:block.turbinegasShutdown", getVolume(1.0F), 1.0F);

				rpmLast = rpm;
				tempLast = temp;
			}

			counter--;

			rpm = (int) (rpmLast * (counter) / 225);
			temp = (int) (tempLast * (counter) / 225);

		} else if(rpm > 11) { //quickly slows down the turbine to idle before shutdown
			counter = 42069; //absolutely necessary to avoid fuckeries on shutdown
			rpm--;
		} else if(rpm == 11) {
			counter = 225;
			rpm--;
		}
	}

protected final int getFluidBurnTemp(HbmFluidType type) {
		double dFuel = type.hasTrait(FluidTraits.Combustible.class) ? type.getTrait(FluidTraits.Combustible.class).getCombustionEnergy() : 0;
		return (int) Math.floor(800D - (Math.pow(Math.E, -dFuel / 100_000D)) * 300D);
	}

protected final void run() {

		if((int) (throttle * 0.9) > rpm - rpmIdle) { //simulates the rotor's moment of inertia
			if(level.getGameTime() % 5 == 0) {
				rpm++;
			}
		} else if((int) (throttle * 0.9) < rpm - rpmIdle) {
			if(level.getGameTime() % 2 == 0) {
				rpm--;
			}
		}

		int maxTemp = getFluidBurnTemp(tanks[0].getTankType()); // fuelMaxTemp.get(tanks[0].getTankType())

		if(throttle * 5 * (maxTemp - tempIdle) / 500 > temp - tempIdle) { //simulates the heat exchanger's resistance to temperature variation
			if(level.getGameTime() % 2 == 0) {
				temp++;
			}
		} else if(throttle * 5 * (maxTemp - tempIdle) / 500 < temp - tempIdle) {
			if(level.getGameTime() % 2 == 0) {
				temp--;
			}
		}

		double consumption = fuelMaxCons.getOrDefault(tanks[0].getTankType(), 5D);
		makePower(consumption, throttle);
	}

protected final void makePower(double consMax, int throttle) {

		double idleConsumption = consMax * 0.05D;
		double consumption = idleConsumption + consMax * throttle / 100;

		if(level.getGameTime() % 5 == 0) {
			pollute(tanks[0].getTankType(), (float)consumption * 0.5F);
		}

		fuelToConsume += consumption;

		tanks[0].setFill(tanks[0].getFill() - (int) Math.floor(fuelToConsume));
		fuelToConsume -= (int) Math.floor(fuelToConsume);

		if(level.getGameTime() % 10 == 0) //lube consumption
			tanks[1].setFill(tanks[1].getFill() - 1);

		if(tanks[0].getFill() < 0) { //avoids negative amounts of fluid
			tanks[0].setFill(0);
			state = 0;
		}
		if(tanks[1].getFill() < 0) {
			tanks[1].setFill(0);
			state = 0;
		}


		long energy = 0; //energy per mb of fuel

		if(tanks[0].getTankType().hasTrait(FluidTraits.Combustible.class)) {
			energy = tanks[0].getTankType().getTrait(FluidTraits.Combustible.class).getCombustionEnergy() / 1000L;
		}

		int rpmEff = rpm - rpmIdle; // RPM above idle level, 0-90

		//consMax*energy is equivalent to power production at 100%
		if(instantPowerOutput < (consMax * energy * rpmEff / 90)) { //this shit avoids power rising in steps of 2000 or so HE at a time, instead it does it smoothly
			instantPowerOutput += Math.random() * 0.005 * consMax * energy;
			if(instantPowerOutput > (consMax * energy * rpmEff / 90))
				instantPowerOutput = (int) (consMax * energy * rpmEff / 90);
		}
		else if(instantPowerOutput > (consMax * energy * rpmEff / 90)) {
			instantPowerOutput -= Math.random() * 0.011 * consMax * energy;
			if(instantPowerOutput < (consMax * energy * rpmEff / 90))
				instantPowerOutput = (int) (consMax * energy * rpmEff / 90);
		}
		this.power = this.power.add(BigInteger.valueOf(instantPowerOutput));

		double waterPerTick = (consMax * energy * (temp - tempIdle) / 220000); //it just works fuck you

		this.waterToBoil = waterPerTick; //caching in a field for the EC compat to use

		int heatCycles = (int) Math.floor(waterToBoil);
		int waterCycles = tanks[2].getFill();
		int steamCycles = (tanks[3].getMaxFill() - tanks[3].getFill()) / 10;
		int cycles = Math.min(heatCycles, Math.min(waterCycles, steamCycles));

		tanks[2].setFill(tanks[2].getFill() - cycles);
		tanks[3].setFill(tanks[3].getFill() + cycles * 10);
	}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
