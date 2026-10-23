package com.hbm.machine.reactor.rbmk.panels.lever;
import com.hbm.api.computer.*;
import com.hbm.item.tool.WeldingParticlePacket;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
/** Source dual mechanical levers,10tick movement, original RTTY endpoints/sounds/tau flashes and3callbacks. */
public final class RbmkLeverEntity extends RbmkRadioPanelEntity implements ComputerControl{
    public final LeverUnit[] levers=new LeverUnit[2];
    public RbmkLeverEntity(BlockPos pos,BlockState state){super(RbmkLeverContent.ENTITY.get(),pos,state);for(int i=0;i<2;i++)levers[i]=new LeverUnit(i);}
    @Override protected void serverTick(ServerLevel level){for(var lever:levers)lever.update();}@Override protected void clientTick(){for(var lever:levers)lever.updateClient();}
    private void sound(String name,float volume){level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get(name),SoundSource.BLOCKS,volume,1F);}
    public final class LeverUnit{
        public final int index;public boolean active,polling,isTurningOn;public String label="",rtty="",commandOn="",commandOff="";public float flipProgress,prevFlipProgress,flipSync;public int turnProgress;public static final float FLIP_SPEED=1F/10F;
        LeverUnit(int index){this.index=index;label="Lever "+(index+1);}
        public void click(){if(!active)return;if(flipProgress<=0||flipProgress>=1)sound("block.leverStart",1F);isTurningOn=!isTurningOn;setChanged();}
        void update(){prevFlipProgress=flipProgress;if(!active)return;boolean arcFlash=false;if(polling){if(flipProgress>=1F&&canSend(commandOn))RTTYSystem.broadcast(level,rtty,commandOn);if(flipProgress<=0F&&canSend(commandOff))RTTYSystem.broadcast(level,rtty,commandOff);}if(isTurningOn&&flipProgress<1F){flipProgress+=FLIP_SPEED;if(flipProgress>=1F){flipProgress=1F;if(!polling&&canSend(commandOn))RTTYSystem.broadcast(level,rtty,commandOn);sound("block.leverStop",.5F);arcFlash=true;}}else if(!isTurningOn&&flipProgress>0F){if(prevFlipProgress>=1)arcFlash=true;flipProgress-=FLIP_SPEED;if(flipProgress<=0F){flipProgress=0F;if(!polling&&canSend(commandOff))RTTYSystem.broadcast(level,rtty,commandOff);sound("block.leverStop",.5F);}}if(arcFlash){sound("block.spark",1F);var dir=ForgeDirection.getOrientation(getBlockState().getValue(DummyableBlock.LEGACY_META));var rot=dir.getRotation(ForgeDirection.UP);for(int i=0;i<2;i++){double x=worldPosition.getX()+.5+dir.offsetX*.4-rot.offsetX*(index-.5)*(i==0?.375:.625),y=worldPosition.getY()+.4375-.03125,z=worldPosition.getZ()+.5+dir.offsetZ*.4-rot.offsetZ*(index-.5)*(i==0?.375:.625);var point=new Vec3(x,y,z);var packet=new WeldingParticlePacket(x,y,z,5,true);for(var player:((ServerLevel)level).players())if(player.distanceToSqr(point)<625)PacketDistributor.sendToPlayer(player,packet);}}}
        void updateClient(){prevFlipProgress=flipProgress;if(turnProgress>0){flipProgress=flipProgress+(flipSync-flipProgress)/turnProgress;--turnProgress;}else flipProgress=flipSync;}
        boolean canSend(String command){return rtty!=null&&!rtty.isEmpty()&&command!=null&&!command.isEmpty();}
        void write(ByteBuf buffer){buffer.writeBoolean(active).writeBoolean(polling).writeFloat(flipProgress);string(buffer,label);string(buffer,rtty);string(buffer,commandOn);string(buffer,commandOff);}
        void read(ByteBuf buffer){active=buffer.readBoolean();polling=buffer.readBoolean();flipSync=buffer.readFloat();label=string(buffer);rtty=string(buffer);commandOn=string(buffer);commandOff=string(buffer);turnProgress=3;}
        void save(CompoundTag tag,int i){tag.putBoolean("active"+i,active);tag.putBoolean("polling"+i,polling);tag.putBoolean("isTurningOn"+i,isTurningOn);tag.putFloat("flipProgress"+i,flipProgress);tag.putString("label"+i,label);tag.putString("rtty"+i,rtty);tag.putString("commandOn"+i,commandOn);tag.putString("commandOff"+i,commandOff);}
        void load(CompoundTag tag,int i){active=tag.getBoolean("active"+i);polling=tag.getBoolean("polling"+i);isTurningOn=tag.getBoolean("isTurningOn"+i);flipProgress=tag.getFloat("flipProgress"+i);label=tag.getString("label"+i);rtty=tag.getString("rtty"+i);commandOn=tag.getString("commandOn"+i);commandOff=tag.getString("commandOff"+i);}
    }
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);for(var lever:levers)lever.write(buffer);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);for(var lever:levers)lever.read(buffer);}@Override protected void savePanel(CompoundTag tag){super.savePanel(tag);for(int i=0;i<2;i++)levers[i].save(tag,i);}@Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);for(int i=0;i<2;i++)levers[i].load(tag,i);}
    @Override public void receiveControl(Player player,CompoundTag tag){int active=tag.getByte("active"),polling=tag.getByte("polling");for(int i=0;i<2;i++){var lever=levers[i];lever.active=(active&(1<<i))!=0;lever.polling=(polling&(1<<i))!=0;lever.label=tag.getString("label"+i);lever.rtty=tag.getString("rtty"+i);lever.commandOn=tag.getString("cmdOn"+i);lever.commandOff=tag.getString("cmdOff"+i);}setChanged();}
    @Override public String computerType(){return "rbmk_lever";}@Override public String[] computerMethods(){return new String[]{"getLeverInfo","setLeverActive","setLeverLabel"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{int index=args.checkInteger(0)-1;if(index<0||index>=2)return new Object[]{method.equals("getLeverInfo")?null:false,"Invalid index (1-2)"};var lever=levers[index];if(method.equals("getLeverInfo")){var data=new LinkedHashMap<String,Object>();data.put("active",lever.active);data.put("polling",lever.polling);data.put("state",lever.flipProgress>=1D);data.put("progress",lever.flipProgress);data.put("label",lever.label);data.put("channel",lever.rtty);data.put("commandOn",lever.commandOn);data.put("commandOff",lever.commandOff);return new Object[]{data};}switch(method){case "setLeverActive"->lever.active=args.checkBoolean(1);case "setLeverLabel"->lever.label=args.checkString(1);default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{true};}
}
