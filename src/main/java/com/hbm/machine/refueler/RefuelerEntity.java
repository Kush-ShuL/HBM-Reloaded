package com.hbm.machine.refueler;

import com.hbm.api.machine.MachineView;
import com.hbm.armor.ArmorModStorage;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Exact100mB source tank and five equipment slots; installed armor mods use the existing sole storage. */
public final class RefuelerEntity extends BlockEntity implements StandardFluidReceiver,MachineView {
    public static java.util.function.Consumer<RefuelerEntity> CLIENT_TICK=station->{};
    public final FluidTank tank=new FluidTank(HbmFluids.fromName("KEROSENE"),100);
    public double fillLevel,prevFillLevel;
    public boolean operating;
    private int operatingTime;
    private FluidPorts ports;
    public RefuelerEntity(BlockPos pos,BlockState state){super(RefuelerContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,RefuelerEntity station){
        if(level.isClientSide){CLIENT_TICK.accept(station);station.prevFillLevel=station.fillLevel;double target=(double)station.tank.getFill()/station.tank.getMaxFill();float speed=target>station.fillLevel||!station.operating?.1F:.01F;station.fillLevel+=(target-station.fillLevel)*speed;return;}
        if(!(level instanceof ServerLevel server))return;
        if(station.ports==null){
            int meta=state.getValue(RefuelerBlock.META);
            if(meta<=5){Direction dir=Direction.from3DDataValue(meta).getOpposite();BlockPos port=pos.relative(dir);station.ports=new FluidPorts(new BlockPos[]{port},new NodeConnection[]{new NodeConnection(port.relative(dir),dir)});}
            else station.ports=new FluidPorts(new BlockPos[]{pos},new NodeConnection[0]);
        }
        station.ports.update(server,station);int old=station.tank.getFill();station.operating=false;
        for(Player player:server.getEntitiesOfClass(Player.class,new AABB(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+.5,pos.getZ()+1))){
            boolean changed=false;
            for(EquipmentSlot slot:List.of(EquipmentSlot.MAINHAND,EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD)){
                ItemStack stack=player.getItemBySlot(slot);if(stack.isEmpty())continue;
                if(station.fill(stack))changed=true;
                if(stack.getItem()instanceof ArmorItem&&ArmorModStorage.hasMods(stack))for(ItemStack mod:ArmorModStorage.pryMods(stack,server.registryAccess()))if(!mod.isEmpty()&&station.fill(mod)){ArmorModStorage.applyMod(stack,mod,server.registryAccess());changed=true;}
            }
            if(changed){station.operating=true;player.getInventory().setChanged();}
        }
        if(station.operating){if(station.operatingTime%20==0)server.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,.2F,.5F);station.operatingTime++;}else station.operatingTime=0;
        if(old!=station.tank.getFill())station.setChanged();
        // Source networkPackNT(150): send the actual native BE packet only to that radius.
        var packet=ClientboundBlockEntityDataPacket.create(station);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<22500)player.connection.send(packet);dispatchScope.commit();}
    }
    private boolean fill(ItemStack stack){if(!(stack.getItem()instanceof FillableItem item)||!item.acceptsFluid(tank.getTankType(),stack))return false;int before=tank.getFill();tank.setFill(item.tryFill(tank.getTankType(),before,stack));return tank.getFill()<before;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}
    @Override public FluidTank[] getReceivingTanks(){return getAllTanks();}
    @Override public boolean canConnect(HbmFluidType type,Direction side){int meta=getBlockState().getValue(RefuelerBlock.META);return meta<=5&&side==Direction.from3DDataValue(meta).getOpposite();}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remainder=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(remainder<amount)setChanged();return remainder;}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tank.writeToNBT(tag,"t");}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);tank.readFromNBT(tag,"t");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tank.writeToNBT(tag,"t");tag.putBoolean("operating",operating);return tag;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){tank.readFromNBT(tag,"t");operating=tag.getBoolean("operating");}
    private void close(){if(level instanceof ServerLevel server&&ports!=null)ports.remove(server);ports=null;}
    @Override public void setRemoved(){close();super.setRemoved();}
    @Override public void onChunkUnloaded(){close();super.onChunkUnloaded();}
}
