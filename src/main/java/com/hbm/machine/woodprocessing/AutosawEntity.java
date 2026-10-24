package com.hbm.machine.woodprocessing;

import com.hbm.api.machine.*;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.block.oilplant.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.MuffledMachine;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Original five-fuel engine, physical arm sweep and exact18-neighbor0–1 ownership forest felling. */
public final class AutosawEntity extends BlockEntity implements StandardFluidReceiver,IFluidCopiable,MuffledMachine,MachineView {
    private static final int MIN_DIST=2,MAX_DIST=9,FELL_BFS_RADIUS=19,FELL_VERTICAL_RANGE=32,FELL_MAX_BASE_DEPTH=16;
    private static final int[][] EIGHTEEN_DIRS={{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1},{1,1,0},{1,-1,0},{-1,1,0},{-1,-1,0},{1,0,1},{1,0,-1},{-1,0,1},{-1,0,-1},{0,1,1},{0,1,-1},{0,-1,1},{0,-1,-1}};
    public static final Set<HbmFluidType> ACCEPTED_FUELS=Set.of(HbmFluids.WOODOIL,HbmFluids.ETHANOL,HbmFluids.FISHOIL,HbmFluids.HEAVYOIL,HbmFluids.COALCREOSOTE);
    public static Consumer<AutosawEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};
    public final FluidTank tank=new FluidTank(HbmFluids.WOODOIL,100);
    public boolean isOn,isSuspended,muffled,tilted;
    private int forceSkip,state,turnProgress;
    public float syncYaw,rotationYaw,prevRotationYaw,syncPitch,rotationPitch,prevRotationPitch,spin,lastSpin;
    private FluidPorts ports;private CompoundTag lastSnapshot;
    public AutosawEntity(BlockPos pos,BlockState state){super(WoodProcessingContent.AUTOSAW_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,AutosawEntity saw){if(level instanceof ServerLevel server)saw.serverTick(server);else saw.clientTick();}
    private void serverTick(ServerLevel server){
        if(ports==null)ports=new FluidPorts(worldPosition);ports.update(server,this);
        if(!isSuspended&&server.getGameTime()%20==0){if(tank.getFill()>0){tank.setFill(tank.getFill()-1);isOn=true;}else isOn=false;}
        if(isOn&&!isSuspended){
            var upper=new Vec3(0,0,-4).xRot((float)Math.toRadians(80-rotationPitch)).yRot(-(float)Math.toRadians(rotationYaw));
            var lower=new Vec3(0,0,-4).xRot(-(float)Math.toRadians(80-rotationPitch)).yRot(-(float)Math.toRadians(rotationYaw));
            var tip=new Vec3(0,0,-2).yRot(-(float)Math.toRadians(rotationYaw));
            double cx=worldPosition.getX()+.5+upper.x+lower.x+tip.x,cy=worldPosition.getY()+1.75,cz=worldPosition.getZ()+.5+upper.z+lower.z+tip.z;
            WoodEffects.cut(server,new AABB(cx-1,cy-.25,cz-1,cx+1,cy+.25,cz+1));
            if(state==0){rotationYaw+=1;if(rotationYaw>=360)rotationYaw-=360;if(forceSkip>0)forceSkip--;else{
                double cutAngle=Math.toRadians(5),yaw=Math.toRadians((rotationYaw+270)%360);
                outer:for(int dx=-MAX_DIST;dx<=MAX_DIST;dx++)for(int dz=-MAX_DIST;dz<=MAX_DIST;dz++){
                    int distance=dx*dx+dz*dz;if(distance<=MIN_DIST*MIN_DIST||distance>MAX_DIST*MAX_DIST)continue;
                    double relative=Math.abs(Math.atan2(dz,dx)-yaw);relative=Math.abs((relative+Math.PI)%(2*Math.PI)-Math.PI);if(relative>cutAngle)continue;
                    var pos=worldPosition.offset(dx,1,dz);var block=server.getBlockState(pos);var material=material(block);
                    if(!(material.equals("wood")||material.equals("leaves")||material.equals("plants"))||shouldIgnore(block))continue;
                    state=1;break outer;
                }
            }}
            int y=Mth.floor(cy),x0=Mth.floor(cx-.5),x1=Mth.floor(cx+.5),z0=Mth.floor(cz-.5),z1=Mth.floor(cz+.5);
            tryInteract(new BlockPos(x0,y,z0));tryInteract(new BlockPos(x1,y,z0));tryInteract(new BlockPos(x0,y,z1));tryInteract(new BlockPos(x1,y,z1));
            if(state==1){rotationPitch+=2;if(rotationPitch>80){rotationPitch=80;state=2;}}
            if(state==2){rotationPitch-=2;if(rotationPitch<=0){rotationPitch=0;state=0;}}
        }
        var packet=getUpdatePacket();var snapshot=packet.getTag();if(!snapshot.equals(lastSnapshot)||server.getGameTime()%20==0){lastSnapshot=snapshot.copy();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);dispatchScope.commit();}}
        setChanged();
    }
    private void clientTick(){
        lastSpin=spin;
        if(isOn&&!isSuspended){spin+=15;var smoke=new Vec3(.625,0,1.625).yRot(-(float)Math.toRadians(rotationYaw));level.addParticle(ParticleTypes.SMOKE,worldPosition.getX()+.5+smoke.x,worldPosition.getY()+2.0625,worldPosition.getZ()+.5+smoke.z,0,0,0);}
        CLIENT_TICK.accept(this);
        if(spin>=360){spin-=360;lastSpin-=360;}
        prevRotationYaw=rotationYaw;prevRotationPitch=rotationPitch;
        if(turnProgress>0){rotationYaw+=(float)(Mth.wrapDegrees(syncYaw-rotationYaw)/turnProgress);rotationPitch+=(float)(Mth.wrapDegrees(syncPitch-rotationPitch)/turnProgress);turnProgress--;}else{rotationYaw=syncYaw;rotationPitch=syncPitch;}
    }
    private static String material(BlockState state){if(state.getBlock() instanceof OilTallPlantBlock||state.getBlock() instanceof OilFlowerBlock)return "plants";if(state.getBlock() instanceof LeavesBlock)return "leaves";return LegacyMaterials.of(state).name();}
    public static boolean shouldIgnore(BlockState state){return state.is(OilBotanyContent.TALL.get())&&(state.getValue(OilFlowerBlock.META)==9||state.getValue(OilFlowerBlock.META)==10);}
    private void tryInteract(BlockPos pos){var block=level.getBlockState(pos);if(!shouldIgnore(block)){var material=material(block);if(material.equals("leaves")||material.equals("plants"))cutCrop(pos);else if(material.equals("wood")){fellTree(pos);if(state==1)state=2;}}
        if(state==1&&level.getBlockState(pos).isRedstoneConductor(level,pos)){state=2;forceSkip=5;}
    }
    private void cutCrop(BlockPos pos){
        var block=level.getBlockState(pos);level.levelEvent(2001,pos,Block.getId(block));
        if(level instanceof ServerLevel server&&!server.restoringBlockSnapshots)for(var drop:Block.getDrops(block,server,pos,server.getBlockEntity(pos))){
            double dx=server.random.nextFloat()*.7F+.15F,dy=server.random.nextFloat()*.7F+.15F,dz=server.random.nextFloat()*.7F+.15F;
            var item=new ItemEntity(server,pos.getX()+dx,pos.getY()+dy,pos.getZ()+dz,drop);item.setPickUpDelay(10);server.addFreshEntity(item);
        }
        level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);
    }
    // The source fauxpointtwelve.BlockPos hash is deliberately not the modern Vec3i hash.
    // Preserve HashMap iteration/tie order used by the original multi-source ownership algorithm.
    private record TreePos(int x,int y,int z){@Override public int hashCode(){return(y+z*27644437)*27644437+x;}BlockPos nativePos(){return new BlockPos(x,y,z);}}
    private void fellTree(BlockPos hit){
        int sawY=hit.getY();var hitCol=new TreePos(hit.getX(),-1,hit.getZ());var trunks=new HashMap<TreePos,TreePos>();
        for(int dx=-MAX_DIST;dx<=MAX_DIST;dx++)for(int dz=-MAX_DIST;dz<=MAX_DIST;dz++){
            if(dx*dx+dz*dz>MAX_DIST*MAX_DIST)continue;int x=worldPosition.getX()+dx,z=worldPosition.getZ()+dz;
            if(!material(level.getBlockState(new BlockPos(x,sawY,z))).equals("wood"))continue;
            int baseY=sawY;while(sawY-baseY<FELL_MAX_BASE_DEPTH&&material(level.getBlockState(new BlockPos(x,baseY-1,z))).equals("wood"))baseY--;
            if(!canSupportSapling(new BlockPos(x,baseY-1,z)))continue;trunks.put(new TreePos(x,-1,z),new TreePos(x,baseY,z));
        }
        if(!trunks.containsKey(hitCol)){int baseY=hit.getY();while(sawY-baseY<FELL_MAX_BASE_DEPTH&&material(level.getBlockState(new BlockPos(hit.getX(),baseY-1,hit.getZ()))).equals("wood"))baseY--;trunks.put(hitCol,new TreePos(hit.getX(),baseY,hit.getZ()));}
        var owner=new HashMap<TreePos,TreePos>();var deque=new ArrayDeque<TreePos[]>();int hitColCount=1;
        int minY=Math.max(level.getMinBuildHeight(),sawY-FELL_MAX_BASE_DEPTH),maxY=Math.min(level.getMaxBuildHeight()-1,sawY+FELL_VERTICAL_RANGE);
        for(var trunk:trunks.entrySet())deque.addFirst(new TreePos[]{trunk.getValue(),trunk.getKey()});
        while(!deque.isEmpty()){
            var pair=deque.pollFirst();var current=pair[0];var currentCol=pair[1];
            if(owner.containsKey(current)){if(currentCol.equals(hitCol)){hitColCount--;if(hitColCount==0)break;}continue;}
            owner.put(current,currentCol);
            for(var direction:EIGHTEEN_DIRS){int x=current.x+direction[0],y=current.y+direction[1],z=current.z+direction[2],dx=x-worldPosition.getX(),dz=z-worldPosition.getZ();
                if(dx*dx+dz*dz>FELL_BFS_RADIUS*FELL_BFS_RADIUS||y<minY||y>maxY)continue;
                var next=new TreePos(x,y,z);if(owner.containsKey(next))continue;var block=level.getBlockState(next.nativePos());var material=material(block);
                if(!material.equals("wood")&&!material.equals("leaves")&&!(block.getBlock() instanceof LeavesBlock))continue;
                var entry=new TreePos[]{next,currentCol};if(direction[0]==0&&direction[2]==0)deque.addFirst(entry);else deque.addLast(entry);if(currentCol.equals(hitCol))hitColCount++;
            }
            if(currentCol.equals(hitCol)){hitColCount--;if(hitColCount==0)break;}
        }
        for(var entry:owner.entrySet()){
            if(!entry.getValue().equals(hitCol))continue;var pos=entry.getKey().nativePos();var block=level.getBlockState(pos);
            if(material(block).equals("wood")&&isWithinWorkingArea(pos)&&canSupportSapling(pos.below())){
                Block sapling=Blocks.OAK_SAPLING;
                if((block.is(Blocks.SPRUCE_LOG)||block.is(Blocks.SPRUCE_WOOD)))sapling=Blocks.SPRUCE_SAPLING;else if((block.is(Blocks.BIRCH_LOG)||block.is(Blocks.BIRCH_WOOD)))sapling=Blocks.BIRCH_SAPLING;else if((block.is(Blocks.JUNGLE_LOG)||block.is(Blocks.JUNGLE_WOOD)))sapling=Blocks.JUNGLE_SAPLING;else if((block.is(Blocks.ACACIA_LOG)||block.is(Blocks.ACACIA_WOOD)))sapling=Blocks.ACACIA_SAPLING;else if((block.is(Blocks.DARK_OAK_LOG)||block.is(Blocks.DARK_OAK_WOOD)))sapling=Blocks.DARK_OAK_SAPLING;
                level.destroyBlock(pos,true);level.setBlock(pos,sapling.defaultBlockState(),3);
            }else level.destroyBlock(pos,true);
        }
    }
    private boolean isWithinWorkingArea(BlockPos pos){int dx=pos.getX()-worldPosition.getX(),dz=pos.getZ()-worldPosition.getZ(),distance=dx*dx+dz*dz;return distance>MIN_DIST*MIN_DIST&&distance<=MAX_DIST*MAX_DIST;}
    private boolean canSupportSapling(BlockPos soil){var sapling=Blocks.OAK_SAPLING.defaultBlockState();var decision=level.getBlockState(soil).canSustainPlant(level,soil,Direction.UP,sapling);return decision.isDefault()?sapling.canSurvive(level,soil.above()):decision.isTrue();}
    private void saveState(CompoundTag tag){tag.putBoolean("isOn",isOn);tag.putBoolean("isSuspended",isSuspended);tag.putInt("skip",forceSkip);tag.putFloat("yaw",rotationYaw);tag.putFloat("pitch",rotationPitch);tag.putInt("state",state);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tank.writeToNBT(tag,"t");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);saveState(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);isOn=tag.getBoolean("isOn");isSuspended=tag.getBoolean("isSuspended");forceSkip=tag.getInt("skip");rotationYaw=tag.getFloat("yaw");rotationPitch=tag.getFloat("pitch");state=tag.getInt("state");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");tank.readFromNBT(tag,"t");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("isOn",isOn);tag.putBoolean("isSuspended",isSuspended);tag.putBoolean("muffled",muffled);tag.putFloat("yaw",rotationYaw);tag.putFloat("pitch",rotationPitch);tank.writeToNBT(tag,"t");return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){isOn=tag.getBoolean("isOn");isSuspended=tag.getBoolean("isSuspended");muffled=tag.getBoolean("muffled");syncYaw=tag.getFloat("yaw");syncPitch=tag.getFloat("pitch");turnProgress=3;tank.readFromNBT(tag,"t");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}@Override public FluidTank getTankToPaste(){return tank;}
    private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}@Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
}
