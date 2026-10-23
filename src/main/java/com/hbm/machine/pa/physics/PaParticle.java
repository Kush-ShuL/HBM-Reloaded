package com.hbm.machine.pa.physics;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
/** Actual source non-entity particle: positions/direction/momentum/defocus/distance and two consumed source stacks. */
public final class PaParticle{
    public interface Source{void updateState(PaState state);void lastSpeed(int momentum);}
    private final Source source;public int x,y,z,momentum,defocus,distanceTraveled;public ForgeDirection dir;public boolean invalid;public final ItemStack input1,input2;public static final int MAX_DEFOCUS=1000;
    public PaParticle(Source source,int x,int y,int z,ForgeDirection dir,ItemStack input1,ItemStack input2){this.source=source;this.x=x;this.y=y;this.z=z;this.dir=dir;this.input1=input1;this.input2=input2;}
    public void crash(PaState state){invalid=true;source.updateState(state);}public void move(BlockPos pos){x=pos.getX();y=pos.getY();z=pos.getZ();source.lastSpeed(momentum);}
    public void addDistance(int distance){distanceTraveled+=distance;}public void resetDistance(){distanceTraveled=0;}public void defocus(int amount){defocus+=amount;if(defocus>MAX_DEFOCUS)crash(PaState.CRASH_DEFOCUS);}public void focus(int amount){defocus-=amount;if(defocus<0)defocus=0;}
}
