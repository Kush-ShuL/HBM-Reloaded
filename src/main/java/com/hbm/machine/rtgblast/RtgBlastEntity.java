package com.hbm.machine.rtgblast;

import com.hbm.api.machine.MachineView;
import com.hbm.item.rtg.RtgPelletItem;
import com.hbm.machine.alloy.*;
import com.hbm.multiblock.*;
import com.hbm.ore.OreProcessing;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Real TileEntityDiFurnaceRTG. hasPower is intentionally NOT a pure query: original call order consumes fuel life. */
public final class RtgBlastEntity extends BlockEntity implements WorldlyContainer,MenuProvider,MachineView,NamedMultiblock,MuffledMachine {
    public static final short TIME_REQUIRED=1200;
    private static final int[] ALL={0,1,2,3,4,5,6,7,8};
    private final ItemStack[] items=new ItemStack[9];
    public short progress;private short processSpeed;
    public byte sideUpper=1,sideLower=1;
    public boolean muffled,tilted;
    private boolean running;
    private String name="",baseName="";
    private final ContainerData data=new ContainerData(){
        @Override public int get(int index){return switch(index){case 0->progress;case 1->processSpeed;case 2->sideUpper;case 3->sideLower;default->0;};}
        @Override public void set(int index,int value){}
        @Override public int getCount(){return 4;}
    };
    public RtgBlastEntity(BlockPos pos,BlockState state){super(RtgBlastContent.ENTITY.get(),pos,state);Arrays.fill(items,ItemStack.EMPTY);}
    /** Exact RTGUtil sum before each pellet decay; the shared pellet owns all heat/config/half-life semantics. */
    public boolean hasPower(){int total=0;for(int i=3;i<9;i++)if(items[i].getItem() instanceof RtgPelletItem pellet){total+=pellet.power(items[i]);items[i]=pellet.handleDecay(items[i]);}processSpeed=(short)total;return processSpeed>=15;}
    public short heat(){return processSpeed;}
    private RecipeHolder<AlloyRecipe> recipe(){
        if(level==null||items[0].isEmpty()||items[1].isEmpty())return null;
        var input=new AlloyInput(items[0],items[1]);
        return level.getRecipeManager().getAllRecipesFor(AlloyContent.TYPE.get()).stream().sorted(Comparator.comparingInt(holder->holder.value().order()))
                .filter(holder->holder.value().matches(input,level)).findFirst().orElse(null);
    }
    private double oreRate(){return OreProcessing.rate(new ItemStack[]{items[0],items[1]},new int[]{1,1});}
    public boolean canProcess(){
        // Keep the source && and its side effect when either input is absent.
        if((items[0].isEmpty()||items[1].isEmpty())&&!hasPower())return false;
        var recipe=recipe();if(recipe==null)return false;
        var output=OreProcessing.maximumOutput(recipe.value().result(),oreRate());
        if(items[2].isEmpty())return true;
        if(!AlloyBlockEntity.sameLegacy(items[2],output))return false;
        if(items[2].getCount()+output.getCount()>getMaxStackSize())return false;
        if(items[2].getCount()<getMaxStackSize()&&items[2].getCount()<items[2].getMaxStackSize())return true;
        return items[2].getCount()<output.getMaxStackSize();
    }
    private void process(){
        if(!canProcess())return;var recipe=recipe();if(recipe==null)return;
        var output=OreProcessing.output(recipe.value().result(),oreRate(),level.random);
        if(items[2].isEmpty())items[2]=output.copy();else if(AlloyBlockEntity.sameLegacy(items[2],output))items[2].grow(output.getCount());
        for(int i=0;i<2;i++){if(items[i].getCount()<=0)items[i]=new ItemStack(items[i].getItem());else items[i].shrink(1);if(items[i].isEmpty())items[i]=ItemStack.EMPTY;}setChanged();
    }
    public static void tick(Level level,BlockPos pos,BlockState state,RtgBlastEntity furnace){if(level instanceof ServerLevel server)furnace.serverTick(server);}
    private void serverTick(ServerLevel server){
        if(canProcess()&&hasPower()){progress+=processSpeed;if(progress>=TIME_REQUIRED){process();progress=0;}}else progress=0;
        running=progress>0||(canProcess()&&hasPower()); // Preserve second real source availability check and short circuit.
        var next=(running?RtgBlastContent.ON.get():RtgBlastContent.OFF.get()).defaultBlockState().setValue(RtgBlastBlock.META,getBlockState().getValue(RtgBlastBlock.META));
        server.setBlock(worldPosition,next,3);
        var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<100D)player.connection.send(packet);dispatchScope.commit();}
    }
    public void cycleSide(int slot){if(slot==0)sideUpper=(byte)((sideUpper+1)%6);if(slot==1)sideLower=(byte)((sideLower+1)%6);setChanged();}
    @Override public Component getDisplayName(){return name.isEmpty()?Component.translatable("container.diFurnaceRTG"):Component.literal(name);}
    // The source subclass name shadows its base's saved name. Keep that original loss-on-reload behavior.
    @Override public void setCustomName(Component name){this.name=name.getString();setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RtgBlastMenu(id,inventory,this,data);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<9;i++)if(!items[i].isEmpty()){var item=(CompoundTag)items[i].save(lookup);item.putByte("slot",(byte)i);list.add(item);}tag.put("items",list);
        if(!baseName.isEmpty())tag.putString("name",baseName);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);
        tag.putShort("progress",progress);tag.putShort("speed",processSpeed);tag.putByteArray("modes",new byte[]{sideUpper,sideLower});
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);Arrays.fill(items,ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<9)items[slot]=ItemStack.parseOptional(lookup,item);}
        baseName=tag.getString("name");name="";muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");progress=tag.getShort("progress");processSpeed=tag.getShort("speed");
        var modes=tag.getByteArray("modes");if(modes.length>=2){sideUpper=modes[0];sideLower=modes[1];}
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putShort("progress",progress);tag.putShort("speed",processSpeed);tag.putByteArray("modes",new byte[]{sideUpper,sideLower});return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");progress=tag.getShort("progress");processSpeed=tag.getShort("speed");var modes=tag.getByteArray("modes");if(modes.length>=2){sideUpper=modes[0];sideLower=modes[1];}}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 9;}
    @Override public boolean isEmpty(){return Arrays.stream(items).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return items[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=items[slot].split(count);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=items[slot];items[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){items[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){Arrays.fill(items,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot==2)return false;if(stack.getItem() instanceof RtgPelletItem)return slot>2;return true;}
    @Override public int[] getSlotsForFace(Direction side){return ALL.clone();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){if(slot==0&&sideUpper!=side.get3DDataValue())return false;if(slot==1&&sideLower!=side.get3DDataValue())return false;return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>2?!(stack.getItem() instanceof RtgPelletItem):slot==2;}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<ProgressView> progressBars(){return running?List.of(new ProgressView("jade.hbm.processing",progress,TIME_REQUIRED,0xe3aa47)):List.of();}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
