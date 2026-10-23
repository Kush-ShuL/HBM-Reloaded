package com.hbm.machine.reactor;
import com.hbm.api.computer.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original four side probes and integer flux multiplier; no artificial heat, power or fluid input. */
public final class BreedingReactorBlockEntity extends ReactorInventory {
    public int flux;public float progress;
    public BreedingReactorBlockEntity(BlockPos pos,BlockState state){super(ReactorContent.BREEDING_ENTITY.get(),pos,state,2);}
    @Override protected String nameKey(){return "container.reactorBreeding";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new BreedingReactorMenu(id,inventory,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,BreedingReactorBlockEntity reactor){if(level instanceof ServerLevel server)reactor.serverTick(server);}
    private void serverTick(ServerLevel server){
        flux=0;for(var side:Direction.Plane.HORIZONTAL){var pos=worldPosition.relative(side);var state=server.getBlockState(pos);if(state.getBlock() instanceof ResearchReactorBlock block){var core=block.findCore(server,pos);if(core!=null&&server.getBlockEntity(core) instanceof ResearchReactorBlockEntity reactor)flux+=reactor.totalFlux;}}
        var recipe=recipe();if(canProcess(recipe)){progress+=.0025F*(flux/recipe.flux());if(progress>=1){progress=0;if(canProcess(recipe)){if(slots[1].isEmpty())slots[1]=recipe.result().copy();else slots[1].grow(recipe.result().getCount());slots[0].shrink(1);}setChanged();}}else progress=0;
        setChanged();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<20*20)player.connection.send(packet);
    }
    private BreedingRecipe recipe(){if(level==null||slots[0].isEmpty())return null;return BreedingRecipeConfig.recipes(level.getRecipeManager()).stream().filter(recipe->recipe.input().test(slots[0])).findFirst().orElse(null);}
    private boolean canProcess(BreedingRecipe recipe){if(recipe==null||flux<recipe.flux())return false;if(slots[1].isEmpty())return true;return sameLegacy(slots[1],recipe.result())&&slots[1].getCount()<slots[1].getMaxStackSize();}
    private static boolean sameLegacy(ItemStack a,ItemStack b){return a.getItem()==b.getItem()&&a.getOrDefault(HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(HbmHazards.LEGACY_METADATA.get(),b.getDamageValue()));}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1;}
    @Override protected void writeState(CompoundTag tag){tag.putInt("flux",flux);tag.putFloat("progress",progress);}
    @Override protected void readState(CompoundTag tag){flux=tag.getInt("flux");progress=tag.getFloat("progress");}
    @Override public List<ProgressView> progressBars(){if (!(progress > 0 || recipe() != null)) return java.util.List.of(); return List.of(new ProgressView("gui.hbm.reactor.progress",progress,1,0x08FF00));}
    @Override public String computerType(){return "breeding_reactor";}
    @Override public String[] computerMethods(){return new String[]{"getFlux","getProgress","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getFlux"->new Object[]{flux};case "getProgress"->new Object[]{progress};case "getInfo"->new Object[]{flux,progress};default->throw new ComputerException("No such method");};}
}
