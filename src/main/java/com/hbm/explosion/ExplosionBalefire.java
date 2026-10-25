package com.hbm.explosion;

import com.hbm.registry.HbmContent;
import com.hbm.bomb.nuclear.NuclearSpecialContent;
import com.hbm.block.ore.LegacyOreBlock;
import net.minecraft.core.BlockPos;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class ExplosionBalefire
{
    private final com.hbm.bomb.nuclear.NuclearChunkAccess chunks=new com.hbm.bomb.nuclear.NuclearChunkAccess();
    public void close(){chunks.close();}
    public boolean yielded;
	public int posX;
	public int posY;
	public int posZ;
	public int lastposX = 0;
	public int lastposZ = 0;
	public int radius;
	public int radius2;
	public Level worldObj;
	private int n = 1;
	private int nlimit;
	private int shell;
	private int leg;
	private int element;
	
	public void saveToNbt(CompoundTag nbt, String name) {
		nbt.putInt(name + "posX", posX);
		nbt.putInt(name + "posY", posY);
		nbt.putInt(name + "posZ", posZ);
		nbt.putInt(name + "lastposX", lastposX);
		nbt.putInt(name + "lastposZ", lastposZ);
		nbt.putInt(name + "radius", radius);
		nbt.putInt(name + "radius2", radius2);
		nbt.putInt(name + "n", n);
		nbt.putInt(name + "nlimit", nlimit);
		nbt.putInt(name + "shell", shell);
		nbt.putInt(name + "leg", leg);
		nbt.putInt(name + "element", element);
	}
	
	public void readFromNbt(CompoundTag nbt, String name) {
		posX = nbt.getInt(name + "posX");
		posY = nbt.getInt(name + "posY");
		posZ = nbt.getInt(name + "posZ");
		lastposX = nbt.getInt(name + "lastposX");
		lastposZ = nbt.getInt(name + "lastposZ");
		radius = nbt.getInt(name + "radius");
		radius2 = nbt.getInt(name + "radius2");
		n = Math.max(nbt.getInt(name + "n"), 1); //prevents invalid read operation
		nlimit = nbt.getInt(name + "nlimit");
		shell = nbt.getInt(name + "shell");
		leg = nbt.getInt(name + "leg");
		element = nbt.getInt(name + "element");
	}
	
	public ExplosionBalefire(int x, int y, int z, Level world, int rad)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		this.worldObj = world;
		
		this.radius = rad;
		this.radius2 = this.radius * this.radius;

		this.nlimit = this.radius2 * 4;
	}
	
	public boolean update() {return updateUntil(Long.MAX_VALUE);}
    public boolean updateUntil(long deadline) {
        yielded=false;
        if(System.nanoTime()>=deadline){yielded=true;return false;}
        int horizontal=(int)(radius-Math.sqrt(lastposX*lastposX+lastposZ*lastposZ));
        if(horizontal>0&&worldObj instanceof net.minecraft.server.level.ServerLevel level&&chunks.get(level,(posX+lastposX)>>4,(posZ+lastposZ)>>4)==null){yielded=true;return false;}
		
		if(n == 0) return true;
		
		breakColumn(this.lastposX, this.lastposZ);
		this.shell = (int) Math.floor((Math.sqrt(n) + 1) / 2);
		int shell2 = this.shell * 2;
		
		if(shell2 == 0) return true;
		
		this.leg = (int) Math.floor((this.n - (shell2 - 1) * (shell2 - 1)) / shell2);
		this.element = (this.n - (shell2 - 1) * (shell2 - 1)) - shell2 * this.leg - this.shell + 1;
		this.lastposX = this.leg == 0 ? this.shell : this.leg == 1 ? -this.element : this.leg == 2 ? -this.shell : this.element;
		this.lastposZ = this.leg == 0 ? this.element : this.leg == 1 ? this.shell : this.leg == 2 ? -this.element : -this.shell;
		this.n++;
		return this.n > this.nlimit;
	}

	private void breakColumn(int x, int z)
	{
		int dist = (int) (radius - Math.sqrt(x * x + z * z));
		
		if (dist > 0) {
			int pX = posX + x;
			int pZ = posZ + z;
			
			int y  = com.hbm.world.SourceWorldHeight.get(worldObj,pX,pZ);
			int maxdepth = (int) (10 + radius * 0.25);
			int depth = (int) ((maxdepth * dist / radius) + (Math.sin(dist * 0.15 + 2) * 2));//
			
			depth = Math.max(y-depth,worldObj.getMinBuildHeight());
			
			while(y > depth) {

				if(com.hbm.bomb.nuclear.FalloutRules.matches(worldObj.getBlockState(new BlockPos(pX,y,pZ)),"block_schrabidium_cluster",-1)) {
					
					if(worldObj.random.nextInt(10) == 0) {
						worldObj.setBlock(new BlockPos(pX,y+1,pZ),NuclearSpecialContent.BALEFIRE.get().defaultBlockState(),3);
						worldObj.setBlock(new BlockPos(pX,y,pZ),com.hbm.bomb.nuclear.FalloutRules.state("block_euphemium_cluster",com.hbm.explosion.LegacyExplosionMath.metadata(worldObj.getBlockState(new BlockPos(pX,y,pZ)))),3);
					}
					return;
				}
				
				worldObj.removeBlock(new BlockPos(pX,y,pZ),false);
				
				y--;
			}
			
			if(worldObj.random.nextInt(10) == 0) {
				worldObj.setBlock(new BlockPos(pX,depth+1,pZ),NuclearSpecialContent.BALEFIRE.get().defaultBlockState(),3);
				
				if(com.hbm.bomb.nuclear.FalloutRules.matches(worldObj.getBlockState(new BlockPos(pX,y,pZ)),"block_schrabidium_cluster",-1))
					worldObj.setBlock(new BlockPos(pX,y,pZ),com.hbm.bomb.nuclear.FalloutRules.state("block_euphemium_cluster",com.hbm.explosion.LegacyExplosionMath.metadata(worldObj.getBlockState(new BlockPos(pX,y,pZ)))),3);
			}

			for(int i = depth; i > depth - 5; i--) {
				if((worldObj.getBlockState(new BlockPos(pX,i,pZ)).is(Blocks.STONE)||worldObj.getBlockState(new BlockPos(pX,i,pZ)).is(Blocks.DEEPSLATE)))
					worldObj.setBlock(new BlockPos(pX,i,pZ),HbmContent.block("sellafield_slaked").defaultBlockState(),3);
			}
		}
	}
}
