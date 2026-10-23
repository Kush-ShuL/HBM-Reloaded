package com.hbm.item.structurelooteditor;

import com.hbm.api.machine.ICopiable;
import com.hbm.config.SourceServerConfig;
import com.hbm.world.structure.loot.*;
import com.hbm.world.structure.templates.TemplateStateMappings;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original settings fields, pending trigger and ICopiable; no fake inventory or menu. */
public final class LootEditorEntity extends BlockEntity implements ICopiable {
    public BlockState replacement=StructureLootContent.PILE.get().defaultBlockState();
    public String pool="LOOT_BOOKLET";
    public int minimum,maximum=1,lockCode;
    public float placedRotation,lockMod;
    public boolean cheesable=true,trigger;
    transient LootEditorPlacement.Work work;transient boolean executing;transient long lastAttempt=Long.MIN_VALUE;
    private CompoundTag lastSynced;
    public LootEditorEntity(BlockPos pos,BlockState state){super(LootEditorContent.ENTITY.get(),pos,state);}
    public static boolean lootable(BlockPos pos,BlockState state){return state.is(StructureLootContent.PILE.get())||state.getBlock() instanceof EntityBlock factory&&factory.newBlockEntity(pos,state) instanceof Container;}
    public List<String> poolNames(ServerLevel level){return replacement.is(StructureLootContent.PILE.get())?StructureLootPresets.editorNames():LegacyStructureLoot.editorNames(level);}
    public void changed(){LootEditorPlacement.cancel(this);setChanged();}
    public CompoundTag settings(){var tag=new CompoundTag();tag.put("replacement",NbtUtils.writeBlockState(replacement));tag.putInt("min",minimum);tag.putInt("max",maximum);tag.putString("pool",pool);tag.putInt("lockCode",lockCode);tag.putFloat("lockMod",lockMod);tag.putBoolean("cheesable",cheesable);return tag;}
    private void readSettings(CompoundTag tag){replacement=TemplateStateMappings.parse(tag.getCompound("replacement"));minimum=tag.getInt("min");maximum=tag.getInt("max");pool=tag.getString("pool");lockCode=tag.getInt("lockCode");lockMod=tag.getFloat("lockMod");cheesable=tag.getBoolean("cheesable");}
    /** The existing real source-TE transform callback, including manual332 LOAD. No active context means no arming. */
    public static void sourceTransform(CompoundTag tag,int turns){tag.putBoolean("trigger",!SourceServerConfig.STRUCTURE_DEBUG.get());tag.putFloat("rot",Mth.wrapDegrees(tag.getFloat("rot")+turns*90));}
    public static void tick(Level level,BlockPos pos,BlockState state,LootEditorEntity tile){
        if(!(level instanceof ServerLevel server))return;
        if(tile.trigger){LootEditorPlacement.enlist(tile);LootEditorPlacement.advance(server,tile);return;}
        var current=tile.settings();
        if(!current.equals(tile.lastSynced)||level.getGameTime()%20==0){tile.lastSynced=current.copy();var packet=tile.getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=225)player.connection.send(packet);}
    }
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.put("replacement",NbtUtils.writeBlockState(replacement));tag.putInt("minItems",minimum);tag.putInt("maxItems",maximum);tag.putString("poolName",pool);tag.putInt("lockCode",lockCode);tag.putFloat("lockMod",lockMod);tag.putBoolean("cheesable",cheesable);return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){replacement=TemplateStateMappings.parse(tag.getCompound("replacement"));minimum=tag.getInt("minItems");maximum=tag.getInt("maxItems");pool=tag.getString("poolName");lockCode=tag.getInt("lockCode");lockMod=tag.getFloat("lockMod");cheesable=tag.getBoolean("cheesable");changed();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.merge(settings());tag.putFloat("rot",placedRotation);tag.putBoolean("trigger",trigger);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readSettings(tag);placedRotation=tag.getFloat("rot");trigger=tag.getBoolean("trigger");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return settings();}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readSettings(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public void setRemoved(){super.setRemoved();LootEditorPlacement.unloaded(this);}
    @Override public void onChunkUnloaded(){super.onChunkUnloaded();LootEditorPlacement.unloaded(this);}
}
