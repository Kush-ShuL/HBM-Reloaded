package com.hbm.explosion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import com.hbm.explosion.vanillant.standard.EntityProcessorStandard;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

@Deprecated
public class ExplosionNT extends Explosion {
	
	public Set<ExAttrib> atttributes = new HashSet();
    public final double explosionX, explosionY, explosionZ;
    public float explosionSize;
    public Entity exploder;
    public boolean isSmoking = true;
    public final List<BlockPos> affectedBlockPositions = new java.util.ArrayList<>();

	private Random explosionRNG = new Random();
	private Level worldObj;
	protected int resolution = 16;
	protected Map affectedEntities = new HashMap();

	@Deprecated public static final List<ExAttrib> nukeAttribs = Arrays.asList(new ExAttrib[] { ExAttrib.FIRE, ExAttrib.NOPARTICLE, ExAttrib.NOSOUND, ExAttrib.NODROP, ExAttrib.NOHURT });

	public ExplosionNT(Level world, Entity exploder, double x, double y, double z, float strength) {
		super(world, exploder, x, y, z, strength, false, Explosion.BlockInteraction.DESTROY);
        this.exploder=exploder;this.explosionX=x;this.explosionY=y;this.explosionZ=z;this.explosionSize=strength;
        initializeErosion();
		this.worldObj = world;
	}

	public ExplosionNT addAttrib(ExAttrib attrib) {
		atttributes.add(attrib);
		return this;
	}

	public ExplosionNT addAllAttrib(List<ExAttrib> attrib) {
		atttributes.addAll(attrib);
		return this;
	}

	public ExplosionNT addAllAttrib(ExAttrib... attrib) {
		for(ExAttrib a : attrib) atttributes.add(a);
		return this;
	}

	public ExplosionNT overrideResolution(int res) {
		resolution = res;
		return this;
	}

	public void explode() {
		doExplosionA();
		doExplosionB(false);
	}

	public void doExplosionA() {
		float f = this.explosionSize;
		HashSet hashset = new HashSet();
		int i;
		int j;
		int k;
		double currentX;
		double currentY;
		double currentZ;

		for(i = 0; i < this.resolution; ++i) {
			for(j = 0; j < this.resolution; ++j) {
				for(k = 0; k < this.resolution; ++k) {
					
					if(i == 0 || i == this.resolution - 1 || j == 0 || j == this.resolution - 1 || k == 0 || k == this.resolution - 1) {
						
						double d0 = (double) ((float) i / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
						double d1 = (double) ((float) j / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
						double d2 = (double) ((float) k / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
						
						double dist = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= dist;
						d1 /= dist;
						d2 /= dist;
						
						float remainingPower = this.explosionSize * (0.7F + this.worldObj.random.nextFloat() * 0.6F);
						currentX = this.explosionX;
						currentY = this.explosionY;
						currentZ = this.explosionZ;

						for(float step = 0.3F; remainingPower > 0.0F; remainingPower -= step * 0.75F) {
							int xPos = Mth.floor(currentX);
							int yPos = Mth.floor(currentY);
							int zPos = Mth.floor(currentZ);
							BlockState state=this.worldObj.getBlockState(new BlockPos(xPos,yPos,zPos));
                            Block block=state.getBlock();

							if(!state.isAir()) {
								float resistance = this.exploder != null ? this.exploder.getBlockExplosionResistance(this,this.worldObj,new BlockPos(xPos,yPos,zPos),state,state.getFluidState(),state.getExplosionResistance(this.worldObj,new BlockPos(xPos,yPos,zPos),this)) : state.getExplosionResistance(worldObj,new BlockPos(xPos,yPos,zPos),this);
								remainingPower -= (resistance + 0.3F) * step;
							}

							if(block != Blocks.AIR && remainingPower > 0.0F && (this.exploder == null || this.exploder.shouldBlockExplode(this,this.worldObj,new BlockPos(xPos,yPos,zPos),state,remainingPower))) {
								hashset.add(new BlockPos(xPos, yPos, zPos));
								
							} else if(this.has(ExAttrib.ERRODE) && errosion.containsKey(block)) {
								hashset.add(new BlockPos(xPos, yPos, zPos));
							}

							currentX += d0 * (double) step;
							currentY += d1 * (double) step;
							currentZ += d2 * (double) step;
						}
					}
				}
			}
		}

		this.affectedBlockPositions.addAll(hashset);

		if(!has(ExAttrib.NOHURT)) {

			this.explosionSize *= 2.0F;
			i = Mth.floor(this.explosionX - (double) this.explosionSize - 1.0D);
			j = Mth.floor(this.explosionX + (double) this.explosionSize + 1.0D);
			k = Mth.floor(this.explosionY - (double) this.explosionSize - 1.0D);
			int i2 = Mth.floor(this.explosionY + (double) this.explosionSize + 1.0D);
			int l = Mth.floor(this.explosionZ - (double) this.explosionSize - 1.0D);
			int j2 = Mth.floor(this.explosionZ + (double) this.explosionSize + 1.0D);
			List list = this.worldObj.getEntities(this.exploder, new AABB((double) i, (double) k, (double) l, (double) j, (double) i2, (double) j2));
			net.neoforged.neoforge.event.EventHooks.onExplosionDetonate(this.worldObj, this, list, this.explosionSize);
			Vec3 vec3 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);

			for(int i1 = 0; i1 < list.size(); ++i1) {
				Entity entity = (Entity) list.get(i1);
				double d4 = Math.sqrt(entity.distanceToSqr(this.explosionX, this.explosionY, this.explosionZ)) / (double) this.explosionSize;

				if(d4 <= 1.0D) {
					currentX = entity.getX() - this.explosionX;
					currentY = entity.getEyeY() - this.explosionY;
					currentZ = entity.getZ() - this.explosionZ;
					double d9 = (double) (float)Math.sqrt(currentX * currentX + currentY * currentY + currentZ * currentZ);

					if(d9 != 0.0D) {
						currentX /= d9;
						currentY /= d9;
						currentZ /= d9;
						double d10 = (double) Explosion.getSeenPercent(vec3, entity);
						double d11 = (1.0D - d4) * d10;
						entity.hurt(setExplosionSource(this.worldObj,this), (float) ((int) ((d11 * d11 + d11) / 2.0D * 8.0D * (double) this.explosionSize + 1.0D)));
						double d8 = LegacyExplosionMath.dampenBlast(this.worldObj, entity, d11);
						entity.setDeltaMovement(entity.getDeltaMovement().add(currentX*d8,currentY*d8,currentZ*d8));

						if(entity instanceof Player) {
							this.affectedEntities.put((Player) entity, new Vec3(currentX * d11, currentY * d11, currentZ * d11));
						}
					}
				}
			}

			this.explosionSize = f;
		}
	}

    public static DamageSource setExplosionSource(Level level,Explosion explosion){return level.damageSources().explosion(explosion);}


	public void doExplosionB(boolean p_77279_1_) {

		if(!has(ExAttrib.NOSOUND))
			this.worldObj.playSound(null,this.explosionX,this.explosionY,this.explosionZ,net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,net.minecraft.sounds.SoundSource.BLOCKS, 4.0F, (1.0F + (this.worldObj.random.nextFloat() - this.worldObj.random.nextFloat()) * 0.2F) * 0.7F);

		if(!has(ExAttrib.NOPARTICLE)) {
			if(this.explosionSize >= 2.0F && this.isSmoking) {
				clientParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
			} else {
				clientParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
			}
		}

		Iterator iterator;
		BlockPos chunkposition;
		int i;
		int j;
		int k;
		Block block;
        BlockState state;

		if(this.isSmoking) {
			iterator = this.affectedBlockPositions.iterator();

			while(iterator.hasNext()) {
				chunkposition = (BlockPos) iterator.next();
				i = chunkposition.getX();
				j = chunkposition.getY();
				k = chunkposition.getZ();
				state=this.worldObj.getBlockState(new BlockPos(i,j,k));block=state.getBlock();

				if(!has(ExAttrib.NOPARTICLE)) {
					double d0 = (double) ((float) i + this.worldObj.random.nextFloat());
					double d1 = (double) ((float) j + this.worldObj.random.nextFloat());
					double d2 = (double) ((float) k + this.worldObj.random.nextFloat());
					double d3 = d0 - this.explosionX;
					double d4 = d1 - this.explosionY;
					double d5 = d2 - this.explosionZ;
					double d6 = (double) (float)Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
					d7 *= (double) (this.worldObj.random.nextFloat() * this.worldObj.random.nextFloat() + 0.3F);
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					clientParticle("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
					clientParticle("smoke", d0, d1, d2, d3, d4, d5);
				}

				if(!state.isAir()) {
					
					boolean doesErrode = false;
					Block errodesInto = Blocks.AIR;
					
					if(this.has(ExAttrib.ERRODE) && this.explosionRNG.nextFloat() < 0.6F) { //errosion has a 60% chance to occour
						
						if(errosion.containsKey(block)) {
							doesErrode = true;
							errodesInto = errosion.get(block);
						}
					}
					
					if(state.canDropFromExplosion(this.worldObj,new BlockPos(i,j,k),this) && !has(ExAttrib.NODROP) && !doesErrode) {
						float chance = 1.0F;

						if(!has(ExAttrib.ALLDROP))
							chance = 1.0F / this.explosionSize;

						drop(state,new BlockPos(i,j,k),chance);
					}

					state.onBlockExploded(this.worldObj,new BlockPos(i,j,k),this);
					
					if(state.isSolidRender(worldObj,new BlockPos(i,j,k))) {
						
						if(doesErrode) {
							this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),errodesInto.defaultBlockState());
						}
						
						if(has(ExAttrib.DIGAMMA)) {
							this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("ash_digamma").defaultBlockState());
							
							if(this.explosionRNG.nextInt(5) == 0 && this.worldObj.getBlockState(new BlockPos(i,j+1,k)).is(Blocks.AIR))
								this.worldObj.setBlockAndUpdate(new BlockPos(i,j+1,k),LegacyExplosionBlocks.require("fire_digamma").defaultBlockState());
							
						} else if(has(ExAttrib.DIGAMMA_CIRCUIT)) {
							
							if(i % 3 == 0 && k % 3 == 0) {
								this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("pribris_digamma").defaultBlockState());
							} else if((i % 3 == 0 || k % 3 == 0) && this.explosionRNG.nextBoolean()) {
								this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("pribris_digamma").defaultBlockState());
							} else {
								this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("ash_digamma").defaultBlockState());
								
								if(this.explosionRNG.nextInt(5) == 0 && this.worldObj.getBlockState(new BlockPos(i,j+1,k)).is(Blocks.AIR))
									this.worldObj.setBlockAndUpdate(new BlockPos(i,j+1,k),LegacyExplosionBlocks.require("fire_digamma").defaultBlockState());
							}
						} else if(has(ExAttrib.LAVA_V)) {
							this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("volcanic_lava_block").defaultBlockState());
						} else if(has(ExAttrib.LAVA_R)) {
							this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("rad_lava_block").defaultBlockState());
						}
					}
				}
			}
		}

		if(has(ExAttrib.FIRE) || has(ExAttrib.BALEFIRE) || has(ExAttrib.LAVA)) {
			iterator = this.affectedBlockPositions.iterator();

			while(iterator.hasNext()) {
				chunkposition = (BlockPos) iterator.next();
				i = chunkposition.getX();
				j = chunkposition.getY();
				k = chunkposition.getZ();
				state=this.worldObj.getBlockState(new BlockPos(i,j,k));block=state.getBlock();
				BlockState block1 = this.worldObj.getBlockState(new BlockPos(i,j-1,k));

				boolean shouldReplace = true;

				if(!has(ExAttrib.ALLMOD) && !has(ExAttrib.DIGAMMA))
					shouldReplace = this.explosionRNG.nextInt(3) == 0;

				if(state.isAir() && block1.isSolidRender(worldObj,new BlockPos(i,j-1,k)) && shouldReplace) {
					if(has(ExAttrib.FIRE))
						this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),Blocks.FIRE.defaultBlockState());
					else if(has(ExAttrib.BALEFIRE))
						this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),LegacyExplosionBlocks.require("balefire").defaultBlockState());
					else if(has(ExAttrib.LAVA))
						this.worldObj.setBlockAndUpdate(new BlockPos(i,j,k),Blocks.LAVA.defaultBlockState());
				}
			}
		}
	}

	@Override public Map<Player,Vec3> getHitPlayers() {
		return this.affectedEntities;
	}

    /** Original World.newExplosion fire leaf; retain AIR/normal-cube guards before its private RNG draw.
     * Opt-in only: original X5846 ExplosionNT FIRE's own different RNG order remains unchanged. */
    public void igniteVanillaAffectedBlocks(){
        for(BlockPos pos:affectedBlockPositions){
            var state=worldObj.getBlockState(pos);var below=worldObj.getBlockState(pos.below());
            if(state.isAir()&&below.isSolidRender(worldObj,pos.below())&&explosionRNG.nextInt(3)==0)worldObj.setBlock(pos,Blocks.FIRE.defaultBlockState(),3);
        }
    }
	public LivingEntity getExplosivePlacedBy() {
		return this.exploder == null ? null : (this.exploder instanceof PrimedTnt ? ((PrimedTnt) this.exploder).getOwner() : (this.exploder instanceof LivingEntity ? (LivingEntity) this.exploder : null));
	}

    @Override public List<BlockPos> getToBlow(){return affectedBlockPositions;}
    @Override public float radius(){return explosionSize;}
    @Override public Vec3 center(){return new Vec3(explosionX,explosionY,explosionZ);}
    @Override public Entity getDirectSourceEntity(){return exploder;}
    @Override public LivingEntity getIndirectSourceEntity(){return getExplosivePlacedBy();}
    private void drop(BlockState state,BlockPos pos,float chance){
        if(!(worldObj instanceof ServerLevel server)||!worldObj.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOBLOCKDROPS))return;
        var params=new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN,Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL,ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY,worldObj.getBlockEntity(pos)).withOptionalParameter(LootContextParams.THIS_ENTITY,exploder);
        for(ItemStack item:state.getDrops(params))if(worldObj.random.nextFloat()<=chance)Block.popResource(worldObj,pos,item);
    }
    private void clientParticle(String mode,double x,double y,double z,double vx,double vy,double vz){if(worldObj.isClientSide)com.hbm.client.bomb.nuclear.LegacyExplosionEffects.particle(mode,x,y,z,vx,vy,vz);}

    // unconventional name, sure, but it's short
	public boolean has(ExAttrib attrib) {
		return this.atttributes.contains(attrib);
	}
	
	//this solution is a bit hacky but in the end easier to work with
	public static enum ExAttrib {
		FIRE,		//classic vanilla fire explosion
		BALEFIRE,	//same with but with balefire
		DIGAMMA,
		DIGAMMA_CIRCUIT,
		LAVA,		//again the same thing but lava
		LAVA_V,		//again the same thing but volcanic lava
		LAVA_R,		//again the same thing but radioactive lava
		ERRODE,		//will turn select blocks into gravel or sand
		ALLMOD,		//block placer attributes like fire are applied for all destroyed blocks
		ALLDROP,	//miner TNT!
		NODROP,		//the opposite
		NOPARTICLE,
		NOSOUND,
		NOHURT
	}
	
	public static final HashMap<Block, Block> errosion = new HashMap();
	
	private static void initializeErosion() {
        if(!errosion.isEmpty())return;
		errosion.put(LegacyExplosionBlocks.require("concrete"), Blocks.GRAVEL);
		errosion.put(LegacyExplosionBlocks.require("concrete_smooth"), Blocks.GRAVEL);
		errosion.put(LegacyExplosionBlocks.require("brick_concrete"), LegacyExplosionBlocks.require("brick_concrete_broken"));
		errosion.put(LegacyExplosionBlocks.require("brick_concrete_broken"), Blocks.GRAVEL);
	}

}
