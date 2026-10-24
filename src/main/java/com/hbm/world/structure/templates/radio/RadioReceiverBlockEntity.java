package com.hbm.world.structure.templates.radio;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source TileEntityRadioRec's one-tick timestamp filter and actual instrument/note/octave playback. */
public final class RadioReceiverBlockEntity extends BlockEntity implements com.hbm.multiblock.MuffledMachine {
    public String channel="";public boolean isOn,muffled,tilted;
    private String lastChannel;private boolean packed,lastOn;
    public RadioReceiverBlockEntity(BlockPos pos,BlockState state){super(RadioReceiverContent.ENTITY.get(),pos,state);}
    public void tickServer(){if(!(level instanceof ServerLevel server))return;
        if(isOn&&!channel.isEmpty()){var transmission=RTTYSystem.listen(server,channel);if(transmission!=null&&transmission.timeStamp==server.getGameTime()-1L)for(var hit:NoteBuilder.translate(transmission.signal+"")){
            var sound=switch(hit.instrument()){case BASSDRUM->SoundEvents.NOTE_BLOCK_BASEDRUM;case SNARE->SoundEvents.NOTE_BLOCK_SNARE;case CLICKS->SoundEvents.NOTE_BLOCK_HAT;case BASSGUITAR->SoundEvents.NOTE_BLOCK_BASS;default->SoundEvents.NOTE_BLOCK_HARP;};
            int note=hit.note().ordinal()+hit.octave().ordinal()*12;float pitch=(float)Math.pow(2D,(note-12)/12D);server.playSound(null,worldPosition.getX()+.5D,worldPosition.getY()+.5D,worldPosition.getZ()+.5D,sound.value(),SoundSource.BLOCKS,3F,pitch);
        }}
        // TileEntityLoadedBase.networkPackNT suppresses duplicate serialized payloads, except every20 ticks for renewed trackers.
        if(packed&&java.util.Objects.equals(channel,lastChannel)&&isOn==lastOn&&server.getGameTime()%20L!=0)return;
        packed=true;lastChannel=channel;lastOn=isOn;var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225D)player.connection.send(packet);dispatchScope.commit();}
    }
    private void write(CompoundTag tag){tag.putString("channel",channel);tag.putBoolean("isOn",isOn);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);write(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);channel=tag.getString("channel");isOn=tag.getBoolean("isOn");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();write(tag);return tag;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
