package com.hbm.machine.pneumaticstorage;

import api.hbm.ntl.StackCache;
import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.hazard.HbmHazards;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;

/** Source nine filters/nine real outputs, three request strategies and the actual shared RoR bus. */
public final class PneumaticExporter extends PneumaticMachineBase implements IRORInteractive {
    public boolean continuousRequest,rorConfiguredMode,lastRedstone;
    public int requestMode;
    public final short[][] rorFilters=new short[9][3];
    private final String[] filterIds=new String[9];
    public final int[] slotDelay=new int[9];
    private static final int[] OUTPUTS=java.util.stream.IntStream.range(9,18).toArray();
    public PneumaticExporter(BlockPos pos,BlockState state){super(PneumaticKind.EXPORTER,pos,state);}
    @Override public int[] getSlotsForFace(Direction side){return OUTPUTS.clone();}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=9;}
    @Override protected void updateServer(ServerLevel server){super.updateServer(server);for(int i=0;i<9;i++)if(slotDelay[i]>0)slotDelay[i]--;boolean powered=server.hasNeighborSignal(worldPosition);if(continuousRequest)doRequest(false);else if(powered&&!lastRedstone)doRequest(true);lastRedstone=powered;synchronize(server);}
    public ItemStack filter(int index){
        if(!rorConfiguredMode){var stack=slots[index];if(stack.isEmpty())return ItemStack.EMPTY;return plain(stack.getItem(),stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),stack.getCount());}
        var data=rorFilters[index];Item item=filterIds[index]==null?Item.byId(data[0]):BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(filterIds[index]));return plain(item,data[1],data[2]);
    }
    private static ItemStack plain(Item item,int meta,int count){if(item==null||item==Items.AIR||count<=0)return ItemStack.EMPTY;try{return GenericRecipeConfigCodec.legacyStack(BuiltInRegistries.ITEM.getKey(item).toString(),meta,Map.of()).copyWithCount(count);}catch(IllegalArgumentException error){return ItemStack.EMPTY;}}
    public long availability(ItemStack stack){if(stack.isEmpty()||cache==null||cache.hasExpired)return 0;var slot=cache.cacheSlots.get(StackCache.getStackIdentity(stack));return slot==null?0:slot.stacksize;}
    private int space(int index,ItemStack filter){var existing=slots[index+9];if(!existing.isEmpty()&&!ItemStack.isSameItemSameComponents(existing,filter))return -1;return filter.getMaxStackSize()-existing.getCount();}
    public void doRequest(boolean force){
        if(requestMode!=2){for(int i=0;i<9;i++)if(!requestSlot(i,force))slotDelay[i]=10;return;}
        if(cache==null||cache.hasExpired)return;
        if(!force)for(int i=0;i<9;i++)if(!filter(i).isEmpty()&&slotDelay[i]>0)return;
        for(int i=0;i<9;i++){var filter=filter(i);if(filter.isEmpty())continue;if(space(i,filter)<filter.getCount()||availability(filter)<filter.getCount()){slotDelay[i]=10;return;}}
        for(int i=0;i<9;i++){var filter=filter(i);if(filter.isEmpty())continue;int used=(int)cache.consumeItemsAndReturnQuantity(filter,filter.getCount());if(used>0)setItem(i+9,filter.copyWithCount(slots[i+9].getCount()+used));}setChanged();
    }
    public boolean requestSlot(int index,boolean force){
        if(index<0||index>=9)return false;if(!force&&slotDelay[index]>0)return true;if(cache==null||cache.hasExpired)return false;var filter=filter(index);if(filter.isEmpty())return false;int space=space(index,filter);long available=availability(filter);if(space<0||requestMode!=0&&(space<filter.getCount()||available<filter.getCount())||available<=0)return false;int toPull=(int)Math.min(Math.min(filter.getCount(),available),space);int used=(int)cache.consumeItemsAndReturnQuantity(filter,toPull);setItem(index+9,filter.copyWithCount(slots[index+9].getCount()+used));return true;
    }
    public void toggle(String key){switch(key){case "continuous"->continuousRequest=!continuousRequest;case "request"->requestMode=(requestMode+1)%3;case "ror"->rorConfiguredMode=!rorConfiguredMode;default->{return;}}setChanged();}
    @Override protected void saveFields(CompoundTag tag){super.saveFields(tag);tag.putBoolean("continuousRequest",continuousRequest);tag.putBoolean("rorConfiguredMode",rorConfiguredMode);tag.putByte("requestMode",(byte)requestMode);tag.putBoolean("lastRedstone",lastRedstone);tag.putIntArray("slotDelay",slotDelay);for(int i=0;i<9;i++){for(int j=0;j<3;j++)tag.putShort("filter_"+i+"_"+j,rorFilters[i][j]);if(filterIds[i]!=null)tag.putString("filter_id_"+i,filterIds[i]);}}
    @Override protected void loadFields(CompoundTag tag){super.loadFields(tag);continuousRequest=tag.getBoolean("continuousRequest");rorConfiguredMode=tag.getBoolean("rorConfiguredMode");requestMode=Math.floorMod(tag.getByte("requestMode"),3);lastRedstone=tag.getBoolean("lastRedstone");var delay=tag.getIntArray("slotDelay");Arrays.fill(slotDelay,0);System.arraycopy(delay,0,slotDelay,0,Math.min(9,delay.length));for(int i=0;i<9;i++){for(int j=0;j<3;j++)rorFilters[i][j]=tag.getShort("filter_"+i+"_"+j);filterIds[i]=tag.contains("filter_id_"+i)?tag.getString("filter_id_"+i):null;}}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_FUNCTION+"setfilter!slot:itemid:itemmeta:amount",PREFIX_FUNCTION+"setcontinuous!on/off",PREFIX_FUNCTION+"request",PREFIX_FUNCTION+"requestslot!slot",PREFIX_FUNCTION+"checkavailability!itemid:itemmeta:returnchannel"};}
    @Override public String runRORFunction(String name,String[] params){
        if((PREFIX_FUNCTION+"setfilter").equals(name)&&params.length==4){int slot=IRORInteractive.parseInt(params[0],1,9)-1,id=IRORInteractive.parseInt(params[1],0,Short.MAX_VALUE);rorFilters[slot][0]=(short)id;rorFilters[slot][1]=(short)IRORInteractive.parseInt(params[2],0,Short.MAX_VALUE);rorFilters[slot][2]=(short)IRORInteractive.parseInt(params[3],1,64);var item=Item.byId(id);filterIds[slot]=item==null?null:BuiltInRegistries.ITEM.getKey(item).toString();setChanged();}
        if((PREFIX_FUNCTION+"setcontinuous").equals(name)&&params.length==1){if("on".equals(params[0]))continuousRequest=true;if("off".equals(params[0]))continuousRequest=false;setChanged();}
        if((PREFIX_FUNCTION+"request").equals(name))doRequest(true);
        if((PREFIX_FUNCTION+"requestslot").equals(name)&&params.length==1){int slot=IRORInteractive.parseInt(params[0],1,9)-1;if(!requestSlot(slot,true))slotDelay[slot]=10;}
        if((PREFIX_FUNCTION+"checkavailability").equals(name)&&params.length==3){int id=IRORInteractive.parseInt(params[0],0,Short.MAX_VALUE),meta=IRORInteractive.parseInt(params[1],0,Short.MAX_VALUE);if(level!=null)RTTYSystem.broadcast(level,params[2],""+availability(plain(Item.byId(id),meta,1)));}return null;
    }
}
