package com.hbm.machine.tapedrive;

import com.hbm.machine.reactor.iter.torus.DriveItem;
import com.hbm.machine.reactor.iter.torus.TorusContent;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.satellite.*;
import com.hbm.satellite.link.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original12-slot satellite data consumer; the unique satellite backend owns production and persistence. */
public final class TapeDriveEntity extends ThermalMachine {
    public static final byte SLOT_EMPTY=0,SLOT_ANY=1,SLOT_EMPTY_TAPE=2,SLOT_FILLED_TAPE=3;
    public final byte[] tapes=new byte[12];
    public boolean tilted;
    public TapeDriveEntity(BlockPos pos,BlockState state){super(TapeDriveContent.ENTITY.get(),pos,state,12);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state);}
    @Override protected String title(){return "container.machineTapeDrive";}
    public static boolean isDrive(ItemStack stack){return stack.getItem() instanceof DriveItem;}
    /** Source drive ordinal now comes from the physical item, never an editable stack component. */
    public static int rawMetadata(ItemStack stack){return stack.getItem() instanceof DriveItem drive?drive.type().ordinal():stack.getDamageValue();}
    @Override public int getMaxStackSize(){return 1;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return isDrive(stack);}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3,4,5,6,7,8,9,10,11};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){int meta=rawMetadata(stack);return meta!=SatelliteDriveType.DISK_EMPTY.ordinal()&&meta!=SatelliteDriveType.FLASH_EMPTY.ordinal();}
    public static void tick(Level level,BlockPos pos,BlockState state,TapeDriveEntity machine){
        if(!(level instanceof ServerLevel server))return;
        if(level.getGameTime()%10==0)machine.writeSatelliteData(server,state.getValue(TapeDriveBlock.META));
        machine.sync(50);
    }
    private void writeSatelliteData(ServerLevel server,int meta){
        // ForgeDirection.UNKNOWN has zero offsets; even UP/DOWN retain the source's unchanged Y.
        int dx=meta==4?1:meta==5?-1:0,dz=meta==2?1:meta==3?-1:0;
        BlockPos neighbor=worldPosition.offset(dx,0,dz);
        if(!server.hasChunkAt(neighbor))return;
        BlockEntity connected=server.getBlockEntity(neighbor);
        if(connected instanceof SatelliteLinkPort port)connected=port.core();
        if(!(connected instanceof SatelliteLinkBlockEntity link)||!link.connected)return;
        SatelliteSavedData data=SatelliteSavedData.getData(server);
        SatelliteBase satellite=data.sats.get(link.freq);
        if(satellite==null||!satellite.hasData(server))return;
        for(int slot=0;slot<12;slot++){
            ItemStack stack=slots.get(slot);if(!isDrive(stack))continue;
            SatelliteDriveType output=satellite.getOutputData(((DriveItem)stack.getItem()).type());
            if(output==null)continue;
            satellite.consumeData();
            ItemStack written=TorusContent.driveStack(output);
            slots.set(slot,written);data.setDirty();setChanged();break;
        }
    }
    private byte[] sourceSlotStates(){
        byte[] values=new byte[12];
        for(int i=0;i<12;i++){
            ItemStack stack=slots.get(i);if(stack.isEmpty())continue;
            byte type=SLOT_ANY;
            if(isDrive(stack)){
                int meta=rawMetadata(stack);
                type=meta==0||meta==1?SLOT_EMPTY_TAPE:meta==2||meta==3?SLOT_ANY:SLOT_FILLED_TAPE;
            }
            values[i]=type;
        }
        return values;
    }
    @Override protected void saveMachine(CompoundTag tag){tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){tilted=tag.getBoolean("tilted");if(tag.contains("name")&&!tag.contains("CustomName"))setCustomName(Component.literal(tag.getString("name")));if(tag.contains("tapes")){byte[] values=tag.getByteArray("tapes");System.arraycopy(values,0,tapes,0,Math.min(values.length,tapes.length));}}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){CompoundTag tag=super.getUpdateTag(lookup);tag.putByteArray("tapes",sourceSlotStates());return tag;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new TapeDriveMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
}
