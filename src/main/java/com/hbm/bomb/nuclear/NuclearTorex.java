package com.hbm.bomb.nuclear;

import java.awt.Color;
import java.util.ArrayList;

import com.hbm.client.bomb.nuclear.NuclearClientEffects;





import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import net.minecraft.world.level.Level;

/*
 * Toroidial Convection Simulation Explosion Effect
 * Tor                             Ex
 */
public class NuclearTorex extends Entity implements com.hbm.entity.LegacyTrackingRange,net.neoforged.neoforge.entity.IEntityWithComplexSpawn {
    private int spawnAge,simulationAge=1,rebuildTarget;
    private long spawnWorldTime;
    private boolean rebuilding,suppressInitialFeedback;
    private final it.unimi.dsi.fastutil.ints.IntArrayList groundTargets=new it.unimi.dsi.fastutil.ints.IntArrayList();
    private static java.lang.ref.WeakReference<Level> catchupWorld=new java.lang.ref.WeakReference<>(null);
    private static long catchupTick=Long.MIN_VALUE,catchupDeadline;
    public boolean visualReady(){return !rebuilding;}
    public boolean initialFeedbackAllowed(){return !suppressInitialFeedback;}
    private long catchupDeadline(){long tick=level().getGameTime();if(catchupWorld.get()!=level()||catchupTick!=tick){catchupWorld=new java.lang.ref.WeakReference<>(level());catchupTick=tick;catchupDeadline=System.nanoTime()+4_000_000L;}return Math.min(catchupDeadline,System.nanoTime()+1_000_000L);}
    @Override public void writeSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeVarInt(tickCount);buffer.writeLong(level().getGameTime());buffer.writeLong(getUUID().getMostSignificantBits()*31L+getUUID().getLeastSignificantBits());buffer.writeFloat((float)getScale());buffer.writeVarInt(entityData.get(TYPE));buffer.writeVarInt(groundTargets.size());for(int target:groundTargets)buffer.writeInt(target);}
    @Override public void readSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer){spawnAge=buffer.readVarInt();spawnWorldTime=buffer.readLong();random.setSeed(buffer.readLong());entityData.set(SCALE,buffer.readFloat());entityData.set(TYPE,buffer.readVarInt());int count=buffer.readVarInt();for(int i=0;i<count;i++)groundTargets.add(buffer.readInt());tickCount=spawnAge;suppressInitialFeedback=spawnAge>1;rebuilding=suppressInitialFeedback;rebuildTarget=spawnAge;simulationAge=1;if(suppressInitialFeedback){didPlaySound=true;didShake=true;}}
    private void addCloudlet(Cloudlet cloud){if(!rebuilding||rebuildTarget-tickCount+1<=cloud.cloudletLife)cloudlets.add(cloud);}


	@Override public int legacyTrackingRange(){return 1000;}
	public double coreHeight = 3;
	public double convectionHeight = 3;
	public double torusWidth = 3;
	public double rollerSize = 1;
	public double heat = 1;
	public double lastSpawnY = - 1;
	private boolean spawnHeightInitialized;
	public ArrayList<Cloudlet> cloudlets = new ArrayList();
	//public static int cloudletLife = 200;

	public boolean didPlaySound = false;
	public boolean didShake = false;

	public NuclearTorex(EntityType<? extends NuclearTorex> type, Level world) {
		super(type, world);
		this.noPhysics=true;
	}

    private static final EntityDataAccessor<Float> SCALE=SynchedEntityData.defineId(NuclearTorex.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TYPE=SynchedEntityData.defineId(NuclearTorex.class,EntityDataSerializers.INT);
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(SCALE,1F);builder.define(TYPE,0);}

	@Override
	public void tick() {
		
		double s = 1.5; //this.getScale();
		double cs = 1.5;
		int maxAge = this.getMaxAge();
		
        if(level().isClientSide){
            if(spawnAge>1)tickCount=Math.max(tickCount,spawnAge+(int)Math.max(0,level().getGameTime()-spawnWorldTime));
            if(rebuilding){
                int actualAge=tickCount;long deadline=catchupDeadline();
                while(simulationAge<=actualAge&&System.nanoTime()<deadline){tickCount=simulationAge;clientTick(false);simulationAge++;}
                tickCount=actualAge;if(simulationAge>actualAge)rebuilding=false;
            }else clientTick(!suppressInitialFeedback);
        }else{
            int target=groundTarget();while(groundTargets.size()<tickCount)groundTargets.add(target);
        }

		
		if(!level().isClientSide && this.tickCount > maxAge) {
			this.discard();
		}
	}
	
    private int groundTarget(){return Math.max(level().getHeight(Heightmap.Types.MOTION_BLOCKING,Mth.floor(getX()),Mth.floor(getZ()))-3,level().getMinBuildHeight()+1);}
    private void clientTick(boolean feedback){double s=1.5,cs=1.5;int maxAge=getMaxAge();
			
			if(tickCount == 1) this.setScale((float) s);
			
			// -1 is a real emitter height in modern worlds. Testing that height as
			// an initialization sentinel restarts an underground stalk every time
			// its source 0.5-block ascent crosses Y=-1.
			if(!spawnHeightInitialized) {
				lastSpawnY = getY() - 3;
				spawnHeightInitialized = true;
			}
			
			if(feedback && tickCount < 100) NuclearClientEffects.lightningFlash();
			
			// The old lower bound 1 was one block above minY=0. Keeping that absolute
			// value in a -64 world makes the emitter climb sixty blocks out of a flat
			// world's ground and leaves the mushroom stem behind.
			int spawnTarget = rebuilding && tickCount>0 && tickCount<=groundTargets.size()?groundTargets.getInt(tickCount-1):groundTarget();
			double moveSpeed = 0.5D;
			
			if(Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
				lastSpawnY = spawnTarget;
			} else {
				lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
			}
			
			// spawn mush clouds
			double range = (torusWidth - rollerSize) * 0.25;
			double simSpeed = getSimulationSpeed();
			int toSpawn = (int) Math.ceil(10 * simSpeed * simSpeed);
			int lifetime = Math.min((tickCount * tickCount) + 200, maxAge - tickCount + 200);
				
			for(int i = 0; i < toSpawn; i++) {
				double x = getX() + random.nextGaussian() * range;
				double z = getZ() + random.nextGaussian() * range;
				Cloudlet cloud = new Cloudlet(x, lastSpawnY, z, (float)(random.nextDouble() * 2D * Math.PI), 0, lifetime);
				cloud.setScale(1F + this.tickCount * 0.005F * (float) cs, 5F * (float) cs);
				addCloudlet(cloud);
			}
			
			// spawn shock clouds
			if(tickCount < 150) {
				
				int cloudCount = tickCount * 5;
				int shockLife = Math.max(300 - tickCount * 20, 50);
				
				for(int i = 0; i < cloudCount; i++) {
					LegacyVector vec = LegacyVector.createVectorHelper((tickCount * 1.5 + random.nextDouble()) * 1.5, 0, 0);
					float rot = (float) (Math.PI * 2 * random.nextDouble());
					vec.rotateAroundY(rot);
					this.addCloudlet(new Cloudlet(vec.xCoord + getX(), level().getHeight(Heightmap.Types.MOTION_BLOCKING,(int) (vec.xCoord + getX()) + 1, (int) (vec.zCoord + getZ())), vec.zCoord + getZ(), rot, 0, shockLife, TorexType.SHOCK)
							.setScale(7F, 2F)
							.setMotion(tickCount > 15 ? 0.75 : 0));
				}
				
				if(feedback && !didPlaySound) {
					if(NuclearClientEffects.player() != null && NuclearClientEffects.player().distanceTo(this) < (tickCount * 1.5 + 1) * 1.5) {
						NuclearClientEffects.playExplosion(getX(),getY(),getZ());
						didPlaySound = true;
					}
				}
			}
			
			// spawn ring clouds
			if(tickCount < 130 * s) {
				lifetime *= s;
				for(int i = 0; i < 2; i++) {
					Cloudlet cloud = new Cloudlet(getX(), getY() + coreHeight, getZ(), (float)(random.nextDouble() * 2D * Math.PI), 0, lifetime, TorexType.RING);
					cloud.setScale(1F + this.tickCount * 0.0025F * (float) (cs * cs), 3F * (float) (cs * cs));
					addCloudlet(cloud);
				}
			}
			
			// spawn condensation clouds
			if(tickCount > 130 * s && tickCount < 600 * s) {
				
				for(int i = 0; i < 20; i++) {
					for(int j = 0; j < 4; j++) {
						float angle = (float) (Math.PI * 2 * random.nextDouble());
						LegacyVector vec = LegacyVector.createVectorHelper(torusWidth + rollerSize * (5 + random.nextDouble()), 0, 0);
						vec.rotateAroundZ((float) (Math.PI / 45 * j));
						vec.rotateAroundY(angle);
						Cloudlet cloud = new Cloudlet(getX() + vec.xCoord, getY() + coreHeight - 5 + j * s, getZ() + vec.zCoord, angle, 0, (int) ((20 + tickCount / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
						cloud.setScale(0.125F * (float) (cs), 3F * (float) (cs));
						addCloudlet(cloud);
					}
				}
			}
			if(tickCount > 200 * s && tickCount < 600 * s) {
				
				for(int i = 0; i < 20; i++) {
					for(int j = 0; j < 4; j++) {
						float angle = (float) (Math.PI * 2 * random.nextDouble());
						LegacyVector vec = LegacyVector.createVectorHelper(torusWidth + rollerSize * (3 + random.nextDouble() * 0.5), 0, 0);
						vec.rotateAroundZ((float) (Math.PI / 45 * j));
						vec.rotateAroundY(angle);
						Cloudlet cloud = new Cloudlet(getX() + vec.xCoord, getY() + coreHeight + 25 + j * cs, getZ() + vec.zCoord, angle, 0, (int) ((20 + tickCount / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
						cloud.setScale(0.125F * (float) (cs), 3F * (float) (cs));
						addCloudlet(cloud);
					}
				}
			}
			
			for(Cloudlet cloud : cloudlets) {
				cloud.update();
			}
			coreHeight += 0.15 / s;
			torusWidth += 0.05 / s;
			rollerSize = torusWidth * 0.35;
			convectionHeight = coreHeight + rollerSize;
			
			int maxHeat = (int) (50 * cs);
			heat = maxHeat - Math.pow((maxHeat * this.tickCount) / maxAge, 1);
			
			cloudlets.removeIf(x -> x.isDead);
		
    }

	public NuclearTorex setScale(float scale) {
		if(!level().isClientSide) entityData.set(SCALE, scale);
		this.coreHeight = this.coreHeight / 1.5D * scale;
		this.convectionHeight = this.convectionHeight / 1.5D * scale;
		this.torusWidth = this.torusWidth / 1.5D * scale;
		this.rollerSize = this.rollerSize / 1.5D * scale;
		return this;
	}
	
	public NuclearTorex setType(int type) {
		this.entityData.set(TYPE,type);
		return this;
	}
	
	public double getSimulationSpeed() {
		
		int lifetime = getMaxAge();
		int simSlow = lifetime / 4;
		int simStop = lifetime / 2;
		int life = NuclearTorex.this.tickCount;
		
		if(life > simStop) {
			return 0D;
		}
		
		if(life > simSlow) {
			return 1D - ((double)(life - simSlow) / (double)(simStop - simSlow));
		}
		
		return 1.0D;
	}
	
	public double getScale() {
		return this.entityData.get(SCALE);
	}
	
	public double getSaturation() {
		double d = (double) this.tickCount / (double) this.getMaxAge();
		return 1D - (d * d * d * d);
	}
	
	public double getGreying() {
		
		int lifetime = getMaxAge();
		int greying = lifetime * 3 / 4;
		
		if(tickCount > greying) {
			return 1 + ((double)(tickCount - greying) / (double)(lifetime - greying));
		}
		
		return 1D;
	}
	
	public float getAlpha() {
		
		int lifetime = getMaxAge();
		int fadeOut = lifetime * 3 / 4;
		int life = NuclearTorex.this.tickCount;
		
		if(life > fadeOut) {
			float fac = (float)(life - fadeOut) / (float)(lifetime - fadeOut);
			return 1F - fac;
		}
		
		return 1.0F;
	}
	
	public int getMaxAge() {
		double s = this.getScale();
		return (int) (45 * 20 * s);
	}

	public class Cloudlet {

		public double posX;
		public double posY;
		public double posZ;
		public double prevPosX;
		public double prevPosY;
		public double prevPosZ;
		public double motionX;
		public double motionY;
		public double motionZ;
		public int age;
		public int cloudletLife;
		public float angle;
		public boolean isDead = false;
		float rangeMod = 1.0F;
		public float colorMod = 1.0F;
		public LegacyVector color;
		public LegacyVector prevColor;
		public TorexType type;

		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
			this(posX, posY, posZ, angle, age, maxAge, TorexType.STANDARD);
		}

		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.age = age;
			this.cloudletLife = maxAge;
			this.angle = angle;
			this.rangeMod = 0.3F + random.nextFloat() * 0.7F;
			this.colorMod = 0.8F + random.nextFloat() * 0.2F;
			this.type = type;
			
			this.updateColor();
		}
		
		private void update() {
			
			age++;
			
			if(age > cloudletLife) {
				this.isDead = true;
			}

			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			
			LegacyVector simPos = LegacyVector.createVectorHelper(NuclearTorex.this.getX() - this.posX, 0, NuclearTorex.this.getZ() - this.posZ);
			double simPosX = NuclearTorex.this.getX() + simPos.lengthVector();
			double simPosZ = NuclearTorex.this.getZ() + 0D;
			
			if(this.type == TorexType.STANDARD) {
				LegacyVector convection = getConvectionMotion(simPosX, simPosZ);
				LegacyVector lift = getLiftMotion(simPosX, simPosZ);
				
				double factor = Mth.clamp((this.posY - NuclearTorex.this.getY()) / NuclearTorex.this.coreHeight, 0, 1);
				this.motionX = convection.xCoord * factor + lift.xCoord * (1D - factor);
				this.motionY = convection.yCoord * factor + lift.yCoord * (1D - factor);
				this.motionZ = convection.zCoord * factor + lift.zCoord * (1D - factor);
			} else if(this.type == TorexType.SHOCK) {
				
				double factor = Mth.clamp((this.posY - NuclearTorex.this.getY()) / NuclearTorex.this.coreHeight, 0, 1);
				LegacyVector motion = LegacyVector.createVectorHelper(1, 0, 0);
				motion.rotateAroundY(this.angle);
				this.motionX = motion.xCoord * factor;
				this.motionY = motion.yCoord * factor;
				this.motionZ = motion.zCoord * factor;
			} else if(this.type == TorexType.RING) {
				LegacyVector motion = getRingMotion(simPosX, simPosZ);
				this.motionX = motion.xCoord;
				this.motionY = motion.yCoord;
				this.motionZ = motion.zCoord;
			} else if(this.type == TorexType.CONDENSATION) {
				LegacyVector motion = getCondensationMotion();
				this.motionX = motion.xCoord;
				this.motionY = motion.yCoord;
				this.motionZ = motion.zCoord;
			}
			
			double mult = this.motionMult * getSimulationSpeed();
			
			this.posX += this.motionX * mult;
			this.posY += this.motionY * mult;
			this.posZ += this.motionZ * mult;
			
			this.updateColor();
		}
		
		private LegacyVector getCondensationMotion() {
			LegacyVector delta = LegacyVector.createVectorHelper(posX - NuclearTorex.this.getX(), 0, posZ - NuclearTorex.this.getZ());
			double speed = 0.00002 * NuclearTorex.this.tickCount;
			delta.xCoord *= speed;
			delta.zCoord *= speed;
			return delta;
		}
		
		private LegacyVector getRingMotion(double simPosX, double simPosZ) {
			
			if(simPosX > NuclearTorex.this.getX() + torusWidth * 2)
				return LegacyVector.createVectorHelper(0, 0, 0);
			
			/* the position of the torus' outer ring center */
			LegacyVector torusPos = LegacyVector.createVectorHelper(
					(NuclearTorex.this.getX() + torusWidth),
					(NuclearTorex.this.getY() + coreHeight * 0.5),
					NuclearTorex.this.getZ());
			
			/* the difference between the cloudlet and the torus' ring center */
			LegacyVector delta = LegacyVector.createVectorHelper(torusPos.xCoord - simPosX, torusPos.yCoord - this.posY, torusPos.zCoord - simPosZ);
			
			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = NuclearTorex.this.rollerSize * this.rangeMod * 0.25;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.lengthVector() / roller - 1D;
			
			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]
			
			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			LegacyVector rot = LegacyVector.createVectorHelper(-delta.xCoord / dist, -delta.yCoord / dist, -delta.zCoord / dist);
			/* rotate by the approximate angle */
			rot.rotateAroundZ(angle);
			
			/* the direction from the cloudlet to the target position on the perimeter */
			LegacyVector motion = LegacyVector.createVectorHelper(
					torusPos.xCoord + rot.xCoord - simPosX,
					torusPos.yCoord + rot.yCoord - this.posY,
					torusPos.zCoord + rot.zCoord - simPosZ);
			
			double speed = 0.001D;
			motion.xCoord *= speed;
			motion.yCoord *= speed;
			motion.zCoord *= speed;
			
			motion = motion.normalize();
			motion.rotateAroundY(this.angle);
			
			return motion;
		}
		
		/* simulated on a 2D-plane along the X/Y axis */
		private LegacyVector getConvectionMotion(double simPosX, double simPosZ) {
			
			/* the position of the torus' outer ring center */
			LegacyVector torusPos = LegacyVector.createVectorHelper(
					(NuclearTorex.this.getX() + torusWidth),
					(NuclearTorex.this.getY() + coreHeight),
					NuclearTorex.this.getZ());
			
			/* the difference between the cloudlet and the torus' ring center */
			LegacyVector delta = LegacyVector.createVectorHelper(torusPos.xCoord - simPosX, torusPos.yCoord - this.posY, torusPos.zCoord - simPosZ);
			
			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = NuclearTorex.this.rollerSize * this.rangeMod;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.lengthVector() / roller - 1D;
			
			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]
			
			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			LegacyVector rot = LegacyVector.createVectorHelper(-delta.xCoord / dist, -delta.yCoord / dist, -delta.zCoord / dist);
			/* rotate by the approximate angle */
			rot.rotateAroundZ(angle);
			
			/* the direction from the cloudlet to the target position on the perimeter */
			LegacyVector motion = LegacyVector.createVectorHelper(
					torusPos.xCoord + rot.xCoord - simPosX,
					torusPos.yCoord + rot.yCoord - this.posY,
					torusPos.zCoord + rot.zCoord - simPosZ);
			
			motion = motion.normalize();
			motion.rotateAroundY(this.angle);
			
			return motion;
		}
		
		private LegacyVector getLiftMotion(double simPosX, double simPosZ) {
			double scale = Mth.clamp(1D - (simPosX - (NuclearTorex.this.getX() + torusWidth)), 0, 1);
			
			LegacyVector motion = LegacyVector.createVectorHelper(NuclearTorex.this.getX() - this.posX, (NuclearTorex.this.getY() + convectionHeight) - this.posY, NuclearTorex.this.getZ() - this.posZ);
			
			motion = motion.normalize();
			motion.xCoord *= scale;
			motion.yCoord *= scale;
			motion.zCoord *= scale;
			
			return motion;
		}
		
		private void updateColor() {
			this.prevColor = this.color;

			double exX = NuclearTorex.this.getX();
			double exY = NuclearTorex.this.getY() + NuclearTorex.this.coreHeight;
			double exZ = NuclearTorex.this.getZ();

			double distX = exX - posX;
			double distY = exY - posY;
			double distZ = exZ - posZ;
			
			double distSq = distX * distX + distY * distY + distZ * distZ;
			distSq /= NuclearTorex.this.heat;
			double dist = Math.sqrt(distSq);
			
			dist = Math.max(dist, 1);
			double col = 2D / dist;
			
			int type = NuclearTorex.this.entityData.get(TYPE);
			
			if(type == 1) {
				this.color = LegacyVector.createVectorHelper(
						Math.max(col * 1, 0.25),
						Math.max(col * 2, 0.25),
						Math.max(col * 0.5, 0.25)
						);
			} else if(type == 2) {
				Color color = Color.getHSBColor(this.angle / 2F / (float) Math.PI, 1F, 1F);
				if(this.type == TorexType.RING) {
					this.color = LegacyVector.createVectorHelper(
							Math.max(col * 1, 0.25),
							Math.max(col * 1, 0.25),
							Math.max(col * 1, 0.25)
							);
				} else {
					this.color = LegacyVector.createVectorHelper(color.getRed() / 255D, color.getGreen() / 255D, color.getBlue() / 255D);
				}
			} else {
				this.color = LegacyVector.createVectorHelper(
						Math.max(col * 2, 0.25),
						Math.max(col * 1.5, 0.25),
						Math.max(col * 0.5, 0.25)
						);
			}
		}
		
		public LegacyVector getInterpPos(float interp) {
			float scale = (float) NuclearTorex.this.getScale();
			LegacyVector base = LegacyVector.createVectorHelper(
					prevPosX + (posX - prevPosX) * interp,
					prevPosY + (posY - prevPosY) * interp,
					prevPosZ + (posZ - prevPosZ) * interp);

			if(this.type != TorexType.SHOCK) { //no rescale for the shockwave as this messes with the positions
				base.xCoord = ((base.xCoord) - NuclearTorex.this.getX()) * scale + NuclearTorex.this.getX();
				base.yCoord = ((base.yCoord) - NuclearTorex.this.getY()) * scale + NuclearTorex.this.getY();
				base.zCoord = ((base.zCoord) - NuclearTorex.this.getZ()) * scale + NuclearTorex.this.getZ();
			}
			
			return base;
		}
		
		public LegacyVector getInterpColor(float interp) {
			
			if(this.type == TorexType.CONDENSATION) {
				return LegacyVector.createVectorHelper(1F, 1F, 1F);
			}
			
			double greying = NuclearTorex.this.getGreying();
			
			if(this.type == TorexType.RING) {
				greying += 1;
			}
			
			return LegacyVector.createVectorHelper(
					(prevColor.xCoord + (color.xCoord - prevColor.xCoord) * interp) * greying,
					(prevColor.yCoord + (color.yCoord - prevColor.yCoord) * interp) * greying,
					(prevColor.zCoord + (color.zCoord - prevColor.zCoord) * interp) * greying);
		}
		
		public float getAlpha() {
			float alpha = (1F - ((float)age / (float)cloudletLife)) * NuclearTorex.this.getAlpha();
			if(this.type == TorexType.CONDENSATION) alpha *= 0.25;
			return alpha;
		}
		
		private float startingScale = 1;
		private float growingScale = 5F;
		
		public float getScale() {
			float base = startingScale + ((float)age / (float)cloudletLife) * growingScale;
			if(this.type != TorexType.SHOCK) base *= (float) NuclearTorex.this.getScale();
			return base;
		}
		
		public Cloudlet setScale(float start, float grow) {
			this.startingScale = start;
			this.growingScale = grow;
			return this;
		}
		
		private double motionMult = 1F;
		
		public Cloudlet setMotion(double mult) {
			this.motionMult = mult;
			return this;
		}
	}
	
	public static enum TorexType {
		STANDARD,
		SHOCK,
		RING,
		CONDENSATION
	}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
    @Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return true;}

    private static double squirt(double x){return Math.sqrt(x+1D/((x+2D)*(x+2D)))-1D/(x+2D);}

	public static void statFacStandard(Level world, double x, double y, double z, float scale) { statFac(world, x, y, z, scale, 0); }
	public static void statFacBale(Level world, double x, double y, double z, float scale) { statFac(world, x, y, z, scale, 1); }
	
	public static void statFac(Level world, double x, double y, double z, float scale, int type) {
		NuclearTorex torex = new NuclearTorex(NuclearContent.TOREX.get(),world).setScale(Mth.clamp((float) squirt(scale * 0.01) * 1.5F, 0.5F, 5F));
		torex.setType(type);
		torex.setPos(x, y, z);
		
		world.addFreshEntity(torex);
		
	}
}
