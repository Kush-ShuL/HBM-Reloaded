package com.hbm.machine.thresher;

import com.hbm.api.machine.*;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.block.oilplant.*;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.woodprocessing.WoodEffects;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.registry.HbmContent;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;

/** Source fuel-only sweep/crop/replant lifecycle. No fake process table, HE, menu or inventory. */
public final class ThresherEntity extends BlockEntity implements StandardFluidReceiver,IFluidCopiable,MuffledMachine,MachineView {
    public static Consumer<ThresherEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};
    public final FluidTank tank=new FluidTank(HbmFluids.WOODOIL,100);
    public boolean isOn,isSuspended,muffled,tilted;
    public int delay;
    public float syncAngle,angle,prevAngle,spin,lastSpin;
    private int state,turnProgress;
    private FluidPorts ports;
    private ForgeDirection portRotation;
    private CompoundTag lastSnapshot;
    public ThresherEntity(BlockPos pos,BlockState state){super(ThresherContent.ENTITY.get(),pos,state);}
    private ForgeDirection sourceFacing(){return ForgeDirection.getOrientation(getBlockState().getValue(ThresherBlock.META));}
    public static void tick(Level level,BlockPos pos,BlockState state,ThresherEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else machine.clientTick();}
    private void serverTick(ServerLevel server){
        var dir=sourceFacing().getOpposite();var rot=dir.getRotation(ForgeDirection.UP);
        if(ports==null){portRotation=rot;var faces=rot==ForgeDirection.UNKNOWN?new NodeConnection[0]:new NodeConnection[]{new NodeConnection(worldPosition.relative(rot.nativeDirection()),rot.nativeDirection()),new NodeConnection(worldPosition.relative(rot.getOpposite().nativeDirection()),rot.getOpposite().nativeDirection())};ports=new FluidPorts(new BlockPos[]{worldPosition},faces);}
        ports.update(server,this);
        if(!isSuspended&&server.getGameTime()%20==0){if(tank.getFill()>0){tank.setFill(tank.getFill()-1);isOn=true;}else isOn=false;}
        if(isOn&&!isSuspended){
            if(state==0){delay--;if(delay<=0)state=1;}
            if(state==1){angle+=82.5F/60F;if(angle>=82.5F){angle=82.5F;state=2;}}
            else if(state==2){angle-=82.5F/60F;if(angle<=0){angle=0;state=0;delay=200+server.random.nextInt(100);}}
            if(angle!=0)sweep(server,dir,rot);
        }
        var packet=getUpdatePacket();var snapshot=packet.getTag();if(!snapshot.equals(lastSnapshot)||server.getGameTime()%20==0){lastSnapshot=snapshot.copy();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);}
        setChanged();
    }
    private void sweep(ServerLevel server,ForgeDirection dir,ForgeDirection rot){
        var upper=LegacyVector.createVectorHelper(-dir.offsetX*4,0,-dir.offsetZ*4);
        var lower=LegacyVector.createVectorHelper(-dir.offsetX*4,0,-dir.offsetZ*4);
        float a=(float)Math.toRadians(82.5-angle);
        if(dir.offsetZ!=0){rotateX(upper,a);rotateX(lower,(float)-Math.toRadians(82.5-angle));}
        if(dir.offsetX!=0){upper.rotateAroundZ(a);lower.rotateAroundZ((float)-Math.toRadians(82.5-angle));}
        double endX=worldPosition.getX()+.5-dir.offsetX+upper.xCoord+lower.xCoord-dir.offsetX*2;
        double endZ=worldPosition.getZ()+.5-dir.offsetZ+upper.zCoord+lower.zCoord-dir.offsetZ*2;
        for(int i=-3;i<=3;i++){
            var pos=new BlockPos((int)Math.floor(endX+rot.offsetX*i),worldPosition.getY(),(int)Math.floor(endZ+rot.offsetZ*i));var block=server.getBlockState(pos);
            if(LegacyBlockTraits.isNormalCube(server,pos,block)&&!canCut(block)){state=2;break;}
            if(isOldDoublePlant(block)){
                int metadata=doublePlantMetadata(block);
                if((metadata&7)==0&&server.random.nextInt(250)==0){burst(pos,block);dropItem(new ItemStack(Blocks.SUNFLOWER));}
                if((metadata&7)==2&&server.random.nextInt(100)==0){burst(pos,block);dropItem(new ItemStack(Items.WHEAT_SEEDS));}
                continue;
            }
            if(block.getBlock() instanceof OilTallPlantBlock){cutTallPlant(pos,block);continue;}
            if(block.is(Blocks.SUGAR_CANE)||block.is(Blocks.CACTUS)){cutCane(pos,block.getBlock());continue;}
            if(canCut(block)&&!shouldIgnore(server,pos,block))cutCrop(server,pos,block);
        }
        var bounds=new AABB(endX,worldPosition.getY()+.5,endZ,endX,worldPosition.getY()+.5,endZ).inflate(Math.abs(dir.offsetX*.5)+Math.abs(rot.offsetX*4.5),.5,Math.abs(dir.offsetZ*.5)+Math.abs(rot.offsetZ*4.5));
        WoodEffects.cut(server,bounds,entity->{if(entity instanceof Enemy&&!entity.isAlive())dropItem(new ItemStack(HbmContent.item("nitra_small")));});
    }
    /** Old Vec3 X rotation, retaining float sin/cos before double products. */
    private static void rotateX(LegacyVector vector,float angle){float c=Mth.cos(angle),s=Mth.sin(angle);double y=vector.yCoord*c+vector.zCoord*s,z=vector.zCoord*c-vector.yCoord*s;vector.yCoord=y;vector.zCoord=z;}
    public static boolean canCut(BlockState state){return state.getBlock() instanceof BonemealableBlock||state.is(Blocks.NETHER_WART)||state.is(Blocks.MELON)||state.is(Blocks.PUMPKIN);}
    public static boolean shouldIgnore(Level level,BlockPos pos,BlockState state){if(state.getBlock() instanceof StemBlock||state.getBlock() instanceof AttachedStemBlock)return true;if(state.is(Blocks.NETHER_WART))return state.getValue(NetherWartBlock.AGE)<3;return state.getBlock() instanceof BonemealableBlock growable&&growable.isValidBonemealTarget(level,pos,state);}
    private static boolean isOldDoublePlant(BlockState state){return state.is(Blocks.SUNFLOWER)||state.is(Blocks.LILAC)||state.is(Blocks.TALL_GRASS)||state.is(Blocks.LARGE_FERN)||state.is(Blocks.ROSE_BUSH)||state.is(Blocks.PEONY);}
    private static int doublePlantMetadata(BlockState state){if(state.getValue(DoublePlantBlock.HALF)==DoubleBlockHalf.UPPER)return 8;return state.is(Blocks.SUNFLOWER)?0:state.is(Blocks.LILAC)?1:state.is(Blocks.TALL_GRASS)?2:state.is(Blocks.LARGE_FERN)?3:state.is(Blocks.ROSE_BUSH)?4:5;}
    private void cutTallPlant(BlockPos pos,BlockState state){int meta=state.getValue(OilFlowerBlock.META);if(meta<=7){pos=pos.above();var upper=level.getBlockState(pos);if(!upper.is(state.getBlock()))return;state=upper;meta=state.getValue(OilFlowerBlock.META);}if(meta==9||meta==10)return;burst(pos,state);for(var drop:Block.getDrops(state,(ServerLevel)level,pos,level.getBlockEntity(pos)))dropItem(drop);level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);}
    private void cutCane(BlockPos pos,Block target){int offset=level.getBlockState(pos.below()).is(target)?-1:0;for(int i=2+offset;i>offset;i--){var cut=pos.above(i);var state=level.getBlockState(cut);burst(cut,state);for(var drop:Block.getDrops(state,(ServerLevel)level,cut,level.getBlockEntity(cut)))dropItem(drop);level.setBlock(cut,Blocks.AIR.defaultBlockState(),3);}}
    private void cutCrop(ServerLevel server,BlockPos pos,BlockState state){
        var soil=server.getBlockState(pos.below());burst(pos,state);var replacement=Blocks.AIR.defaultBlockState();
        if(!server.restoringBlockSnapshots){boolean replanted=false;for(var drop:Block.getDrops(state,server,pos,server.getBlockEntity(pos))){
            if(!replanted){var plant=plantFromItem(drop);if(plant!=null){var decision=soil.canSustainPlant(server,pos.below(),Direction.UP,plant);if(decision.isTrue()||decision.isDefault()&&plant.canSurvive(server,pos)){replacement=plant;replanted=true;drop.shrink(1);}}}
            dropItem(drop);
        }if(state.is(Blocks.WHEAT)&&!replanted)replacement=Blocks.WHEAT.defaultBlockState();}
        server.setBlock(pos,replacement,3);
    }
    /** NeoForge1.21 removed IPlantable: real seed BlockItems supply their actual plant state to canSustainPlant. */
    private static BlockState plantFromItem(ItemStack stack){
        // Official1.7 Forge ItemBlock is not IPlantable: ordinary flowers, saplings, pumpkin and HBM ItemBlockBase
        // must not turn into auto-replant seeds. The real vanilla ItemSeeds/ItemSeedFood/NetherWart successors are
        // ItemNameBlockItem, with the actual planted block retained by the item.
        if(!(stack.getItem() instanceof ItemNameBlockItem item))return null;
        return item.getBlock().defaultBlockState();
    }
    private void burst(BlockPos pos,BlockState state){level.levelEvent(2001,pos,Block.getId(state));}
    public void dropItem(ItemStack stack){
        var dir=sourceFacing();var item=new ItemEntity(level,worldPosition.getX()+.5-dir.offsetX*.75,worldPosition.getY(),worldPosition.getZ()+.5-dir.offsetZ*.75,stack);item.setPickUpDelay(10);level.addFreshEntity(item);
        item.setDeltaMovement(dir.offsetX*-.2+.2,item.getDeltaMovement().y,dir.offsetZ*-.2);
        ((ServerLevel)level).getChunkSource().broadcast(item,new ClientboundSetEntityMotionPacket(item));
    }
    private void clientTick(){
        lastSpin=spin;
        if(isOn&&!isSuspended){if(angle>0)spin+=15;var dir=sourceFacing().getOpposite();var rot=dir.getRotation(ForgeDirection.UP);level.addParticle(ParticleTypes.SMOKE,worldPosition.getX()+.5+dir.offsetX*.8125+rot.offsetX*.375,worldPosition.getY()+1.5625,worldPosition.getZ()+.5+dir.offsetZ*.8125+rot.offsetZ*.375,0,0,0);}
        CLIENT_TICK.accept(this);
        if(spin>=360){spin-=360;lastSpin-=360;}prevAngle=angle;
        if(turnProgress>0){angle=(float)((double)angle+Mth.wrapDegrees((double)syncAngle-(double)angle)/(double)turnProgress);turnProgress--;}else angle=syncAngle;
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("isOn",isOn);tag.putBoolean("isSuspended",isSuspended);tag.putFloat("angle",angle);tag.putInt("state",state);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tank.writeToNBT(tag,"t");}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);isOn=tag.getBoolean("isOn");isSuspended=tag.getBoolean("isSuspended");angle=tag.getFloat("angle");state=tag.getInt("state");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");tank.readFromNBT(tag,"t");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("isOn",isOn);tag.putBoolean("isSuspended",isSuspended);tag.putBoolean("muffled",muffled);tag.putFloat("angle",angle);tank.writeToNBT(tag,"t");return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){isOn=tag.getBoolean("isOn");isSuspended=tag.getBoolean("isSuspended");muffled=tag.getBoolean("muffled");syncAngle=tag.getFloat("angle");turnProgress=3;tank.readFromNBT(tag,"t");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}@Override public FluidTank getTankToPaste(){return tank;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){if(side==null)return false;var rotation=portRotation!=null?portRotation:sourceFacing().getOpposite().getRotation(ForgeDirection.UP);return rotation!=ForgeDirection.UNKNOWN&&(side==rotation.nativeDirection()||side==rotation.getOpposite().nativeDirection());}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
