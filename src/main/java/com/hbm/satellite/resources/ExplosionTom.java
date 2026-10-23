package com.hbm.satellite.resources;

import com.hbm.registry.HbmContent;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockState;


import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class ExplosionTom {
	public int posX;
	public int posY;
	public int posZ;
	public int lastposX = 0;
	public int lastposZ = 0;
	public int radius;
	public int radius2;
	public ServerLevel worldObj;
	private int n = 1;
	private int nlimit;
	private int shell;
	private int leg;
	private int element;
    private final NuclearChunkAccess chunks=new NuclearChunkAccess(com.hbm.bomb.nuclear.NuclearConfig.TOM_RESIDENT_CHUNKS.get());
    private boolean waitingForChunks;
    private boolean columnActive,yielded;
    private int columnY,columnFloor,columnThreshold;
    private double columnDistance;
    // A pinned, ready halo is stable on the server thread until this worker closes
    // or moves to a different center chunk. Reads never request/generate terrain.
    private LevelChunk[] readyHalo;
    private int haloX,haloZ;
    private final BlockPos.MutableBlockPos readPosition=new BlockPos.MutableBlockPos();
    private final BlockState tektite=HbmContent.block("tektite").defaultBlockState();
    private final BlockState osmiridium=HbmContent.block("ore_tektite_osmiridium").defaultBlockState();

    /** Pending means no column, random draw, or source cursor advance occurred. */
    public boolean waitingForChunks(){return waitingForChunks;}
    public boolean yielded(){return yielded;}
    public void close(){chunks.close();readyHalo=null;waitingForChunks=false;}

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
        nbt.putBoolean(name+"columnActive",columnActive);
        nbt.putInt(name+"columnY",columnY);nbt.putInt(name+"columnFloor",columnFloor);
        nbt.putInt(name+"columnThreshold",columnThreshold);nbt.putDouble(name+"columnDistance",columnDistance);
	}

	public void readFromNbt(CompoundTag nbt, String name) {
		posX = nbt.getInt(name + "posX");
		posY = nbt.getInt(name + "posY");
		posZ = nbt.getInt(name + "posZ");
		lastposX = nbt.getInt(name + "lastposX");
		lastposZ = nbt.getInt(name + "lastposZ");
		radius = nbt.getInt(name + "radius");
		radius2 = nbt.getInt(name + "radius2");
		n = nbt.getInt(name + "n");
		nlimit = nbt.getInt(name + "nlimit");
		shell = nbt.getInt(name + "shell");
		leg = nbt.getInt(name + "leg");
		element = nbt.getInt(name + "element");
        columnActive=nbt.getBoolean(name+"columnActive");
        columnY=nbt.getInt(name+"columnY");columnFloor=nbt.getInt(name+"columnFloor");
        columnThreshold=nbt.getInt(name+"columnThreshold");columnDistance=nbt.getDouble(name+"columnDistance");
	}

	public ExplosionTom(int x, int y, int z, ServerLevel world, int rad) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;

		this.worldObj = world;

		this.radius = rad;
		this.radius2 = this.radius * this.radius;

		this.nlimit = this.radius2 * 4;
	}

	public boolean update(){return updateUntil(Long.MAX_VALUE);}

    public boolean updateUntil(long deadline) {
        waitingForChunks=false;yielded=false;
        if(System.nanoTime()>=deadline){yielded=true;return false;}
        // The actual column reads/mutates pX/Z +/-2 and sends original neighbor updates.
        // Acquire the source column's whole3x3 halo BEFORE its first random draw/read.
        if(this.radius2-(this.lastposX*this.lastposX+this.lastposZ*this.lastposZ)>0
                &&!prepareHalo((posX+lastposX)>>4,(posZ+lastposZ)>>4)){
            waitingForChunks=true;return false;
        }
        if(!breakColumnUntil(this.lastposX,this.lastposZ,deadline)){yielded=true;return false;}
		this.shell = (int) Math.floor((Math.sqrt(n) + 1) / 2);
		int shell2 = this.shell * 2;
		this.leg = (int) Math.floor((this.n - (shell2 - 1) * (shell2 - 1)) / shell2);
		this.element = (this.n - (shell2 - 1) * (shell2 - 1)) - shell2 * this.leg - this.shell + 1;
		this.lastposX = this.leg == 0 ? this.shell : this.leg == 1 ? -this.element : this.leg == 2 ? -this.shell : this.element;
		this.lastposZ = this.leg == 0 ? this.element : this.leg == 1 ? this.shell : this.leg == 2 ? -this.element : -this.shell;
		this.n++;
		return this.n > this.nlimit;
	}

	private boolean breakColumnUntil(int x,int z,long deadline) {
		int dist = this.radius2 - (x * x + z * z);

		if(dist > 0) {
			int pX = posX + x;
			int pZ = posZ + z;
            int terrain=63;
            if(!columnActive){
			double X = Math.pow((this.posX - pX), 2);
			double Z = Math.pow((this.posZ - pZ), 2);
			double distance = (float)Math.sqrt(X + Z); // Distance calculations used for crater rim stuff

			int y = 256;

			double cA = (terrain - Math.pow(Math.E, -Math.pow(Math.sqrt(x * x + z * z), 2) / 40000) * 13) + worldObj.random.nextInt(2); // Basic crater bowl shape
			double cB = cA + Math.pow(Math.E, -Math.pow(Math.sqrt(x * x + z * z) - 200, 2) / 400) * 13 ;// Crater peak ring
			int craterFloor = (int) (cB + Math.pow(Math.E, -Math.pow(Math.sqrt(x * x + z * z) - 500, 2) / 2000) * 37); // Crater rim
			for(int i = 256; i > 0; i--) {
				if(i == craterFloor || !state(pX, i, pZ).is(Blocks.AIR)) {
					y = i;
					break;
				}
			}
			int height = terrain - 14;
			int offset = 20;
			int threshold = (int) ((float) Math.sqrt(x * x + z * z) * (float) (height + offset) / (float) this.radius) + worldObj.random.nextInt(2) - offset;

            columnY=y;columnFloor=craterFloor;columnThreshold=threshold;columnDistance=distance;columnActive=true;
            }
            int y=columnY,craterFloor=columnFloor,threshold=columnThreshold;
            double distance=columnDistance;
			while(y > threshold) {
                if(System.nanoTime()>=deadline){columnY=y;return false;}

				if(y == 0) break;
				
				if(y <= craterFloor) {

					if(worldObj.random.nextInt(200) == 0) {
						set(pX,y,pZ,osmiridium);
					} else {
						set(pX,y,pZ,tektite);
					}

				} else {
					if(y > terrain + 1) {
						if(distance < 500) // used so that old terrain inside crater rim is destroyed, while rim material "floods" terrain outside.
						{
							for(int i = -2; i < 3; i++) {
								for(int j = -2; j < 3; j++) {
									for(int k = -2; k < 3; k++) {
										var nearby=state(pX+i,y+j,pZ+k);
										if(water(nearby) || nearby.is(BlockTags.ICE) || nearby.is(Blocks.SNOW) || nearby.ignitedByLava()) {
											remove(pX+i,y+j,pZ+k);
											remove(pX,y,pZ);
										}
									}
								}
							}
							set(pX,y,pZ,Blocks.AIR.defaultBlockState());
						}
					} else {
						for(int i = -2; i < 3; i++) {
							for(int j = -2; j < 3; j++) {
								for(int k = -2; k < 3; k++) {
									var nearby=state(pX+i,y+j,pZ+k);
									if(water(nearby) || nearby.is(BlockTags.ICE) || state(pX + i, y, pZ + k).is(Blocks.AIR)) {
										set(pX+i,y,pZ+k,Blocks.LAVA.defaultBlockState());
										set(pX,y,pZ,Blocks.LAVA.defaultBlockState());
									}
								}
							}
						}
						set(pX,y,pZ,Blocks.LAVA.defaultBlockState());
					}

				}
				y--;columnY=y;
			}
            columnActive=false;
		}
        return true;
	}
    private boolean prepareHalo(int x,int z){
        if(readyHalo!=null&&haloX==x&&haloZ==z)return true;
        if(chunks.getWithNeighbors(worldObj,x,z)==null)return false;
        var next=new LevelChunk[9];
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){
            var chunk=chunks.peekReady(worldObj,x+dx,z+dz);
            if(chunk==null)return false;
            next[(dx+1)*3+dz+1]=chunk;
        }
        readyHalo=next;haloX=x;haloZ=z;return true;
    }
    private BlockState state(int x,int y,int z){
        if(worldObj.isOutsideBuildHeight(y))return Blocks.VOID_AIR.defaultBlockState();
        return readyHalo[((x>>4)-haloX+1)*3+(z>>4)-haloZ+1].getBlockState(readPosition.set(x,y,z));
    }
    // Native chunk writes already reject an identical state. Avoid allocating and
    // entering world mutation/shape machinery for those repeated no-ops.
    private void set(int x,int y,int z,BlockState next){
        if(state(x,y,z)!=next)worldObj.setBlock(new BlockPos(x,y,z),next,net.minecraft.world.level.block.Block.UPDATE_CLIENTS|net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE);
    }
    private void remove(int x,int y,int z){
        if(!state(x,y,z).is(Blocks.AIR))worldObj.setBlock(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState(),net.minecraft.world.level.block.Block.UPDATE_ALL);
    }
    private static boolean water(BlockState state){return state.getFluidState().is(FluidTags.WATER);}
}