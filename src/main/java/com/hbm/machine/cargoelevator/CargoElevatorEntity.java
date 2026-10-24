package com.hbm.machine.cargoelevator;

import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Source two blocks/second, lower-lift merge, three-tick client interpolation and actual ROR interface. */
public final class CargoElevatorEntity extends BlockEntity implements IRORInteractive,MuffledMachine {
    public int height,targetExtension;
    public double extension,prevExtension,syncExtension;
    public boolean renderPlatform;
    private int sync;
    private boolean muffled,tilted;
    private CompoundTag lastNetworkTag;
    public static final double SPEED=2D/20D;
    public CargoElevatorEntity(BlockPos pos,BlockState state){super(CargoElevatorContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return state.is(CargoElevatorContent.BLOCK.get())&&DummyableBlock.isCore(state);}
    public static void tick(Level level,BlockPos pos,BlockState state,CargoElevatorEntity lift){
        lift.prevExtension=lift.extension;
        if(level instanceof ServerLevel server){
            var lower=level.getBlockState(pos.below());
            if(lower.is(CargoElevatorContent.BLOCK.get())){
                var below=CargoElevatorContent.BLOCK.get().elevator(level,pos.below());
                if(below!=null&&below.getBlockPos().getX()==pos.getX()&&below.getBlockPos().getZ()==pos.getZ()){
                    below.height+=lift.height+1;
                    DummyableBlock.safely(()->{for(int x=pos.getX()-1;x<pos.getX()+2;x++)for(int z=pos.getZ()-1;z<pos.getZ()+2;z++)for(int y=pos.getY();y<=pos.getY()+lift.height;y++){var cell=new BlockPos(x,y,z);if(level.getBlockEntity(cell)instanceof CargoElevatorEntity)level.removeBlockEntity(cell);level.setBlock(cell,CargoElevatorContent.BLOCK.get().defaultBlockState().setValue(DummyableBlock.LEGACY_META,1),3);}});
                    return;
                }
            }
            if(lift.extension<lift.targetExtension)lift.extension=Mth.clamp(lift.extension+SPEED,0,lift.targetExtension);
            else if(lift.extension>lift.targetExtension)lift.extension=Mth.clamp(lift.extension-SPEED,lift.targetExtension,lift.height);
            lift.extension=Mth.clamp(lift.extension,0,lift.height);lift.renderPlatform=true;
            var network=lift.networkTag();
            // Original TileEntityLoadedBase.networkPackNT: changes immediately, identical payload once/second.
            if(!network.equals(lift.lastNetworkTag)||server.getGameTime()%20==0){lift.lastNetworkTag=network.copy();var packet=ClientboundBlockEntityDataPacket.create(lift,(entity,lookup)->network);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<300D*300D)player.connection.send(packet);dispatchScope.commit();}}
        }else if(lift.sync>0){lift.extension+=(lift.syncExtension-lift.extension)/(float)lift.sync;--lift.sync;}
        else lift.extension=lift.syncExtension;
        if(lift.extension!=lift.prevExtension){
            double upper=pos.getY()+1+Math.max(lift.extension,lift.prevExtension),lower=pos.getY()+1+Math.min(lift.extension,lift.prevExtension);
            var moving=level.getEntities((net.minecraft.world.entity.Entity)null,new AABB(pos.getX()-.99,lower,pos.getZ()-.99,pos.getX()+1.99,upper,pos.getZ()+1.99));
            for(var entity:moving){
                if(entity instanceof Player&&!level.isClientSide)continue;
                double bottom=entity.getBoundingBox().minY;
                if(bottom>=lower&&bottom<=upper){double delta=bottom-(pos.getY()+1+lift.extension);entity.move(MoverType.SELF,new Vec3(0,-delta,0));entity.setOnGround(true);entity.move(MoverType.SELF,new Vec3(0,-.125,0));}
            }
        }
    }
    public void toggleElevator(){targetExtension=targetExtension==0?height:0;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putDouble("extension",extension);tag.putInt("targetExtension",targetExtension);tag.putInt("height",height);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);extension=tag.getDouble("extension");targetExtension=tag.getInt("targetExtension");height=tag.getInt("height");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    private CompoundTag networkTag(){var tag=new CompoundTag();tag.putBoolean("CargoElevatorNetwork",true);tag.putBoolean("renderPlatform",renderPlatform);tag.putShort("height",(short)height);tag.putDouble("extension",extension);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return networkTag();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this,(entity,lookup)->networkTag());}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){
        if(!tag.getBoolean("CargoElevatorNetwork")){loadWithComponents(tag,lookup);return;}
        renderPlatform=tag.getBoolean("renderPlatform");height=tag.getShort("height");syncExtension=tag.getDouble("extension");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(syncExtension>0&&syncExtension<height)sync=3;
    }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public String runRORFunction(String name,String[] params){if((PREFIX_FUNCTION+"setextension").equals(name)&&params.length>0)targetExtension=IRORInteractive.parseInt(params[0],0,height);return null;}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"extension",PREFIX_FUNCTION+"setextension"+NAME_SEPARATOR+"amount"};}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
