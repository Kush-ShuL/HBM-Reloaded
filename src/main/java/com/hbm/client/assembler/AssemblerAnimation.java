package com.hbm.client.assembler;
import com.hbm.machine.assembler.AssemblerBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Original two independent arm state machines and ring motion; animation is client transient state. */
public final class AssemblerAnimation {
    private static final Map<AssemblerBlockEntity,AssemblerAnimation> STATES=new WeakHashMap<>();
    public final Arm[] arms={new Arm(),new Arm()};public boolean frame;public double prevRing,ring,ringTarget,ringSpeed;private int ringDelay;private Loop sound;
    public static AssemblerAnimation get(AssemblerBlockEntity machine){return STATES.computeIfAbsent(machine,key->new AssemblerAnimation());}
    public static void tick(AssemblerBlockEntity machine){get(machine).update(machine);}
    public static void removed(AssemblerBlockEntity machine){var state=STATES.remove(machine);if(state!=null&&state.sound!=null){state.sound.finish();Minecraft.getInstance().getSoundManager().stop(state.sound);}}
    public static void stopped(AssemblerBlockEntity machine){oneShot(machine,"block.assemblerstop",machine.volume(.25F),1.5F);}
    private static void oneShot(AssemblerBlockEntity machine,String id,float volume,float pitch){var level=machine.getLevel();var p=machine.getBlockPos();if(level!=null)level.playLocalSound(p.getX(),p.getY(),p.getZ(),HbmSounds.get(id),SoundSource.BLOCKS,volume,pitch,false);}
    private void update(AssemblerBlockEntity machine){var level=machine.getLevel();if(level==null)return;var client=Minecraft.getInstance();var pos=machine.getBlockPos();if(level.getGameTime()%20==0)frame=!level.getBlockState(pos.above(3)).isAir();if(machine.processing()&&client.player!=null&&client.player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<2500){if(sound==null||!client.getSoundManager().isActive(sound)){sound=new Loop(machine);client.getSoundManager().play(sound);}sound.keepAlive=0;sound.update();}else if(sound!=null){sound.finish();client.getSoundManager().stop(sound);sound=null;}for(var arm:arms){System.arraycopy(arm.angles,0,arm.previous,0,4);if(machine.processing())arm.update();else arm.returnToNull();if(!machine.muffled&&arm.previous[3]!=arm.angles[3]&&arm.angles[3]==-.75D)oneShot(machine,"block.assemblerstrike",machine.volume(.5F),1F);}prevRing=ring;if(machine.processing()){if(ring!=ringTarget){double delta=Math.abs(ringTarget-ring);if(delta<=ringSpeed)ring=ringTarget;if(ringTarget>ring)ring+=ringSpeed;if(ringTarget<ring)ring-=ringSpeed;if(ringTarget==ring){double sub=ringTarget>=360?-360D:360D;ringTarget+=sub;ring+=sub;prevRing+=sub;ringDelay=20+level.random.nextInt(21);}}else{if(ringDelay>0)ringDelay--;if(ringDelay<=0){ringTarget+=(level.random.nextDouble()*2-1)*135;ringSpeed=10D+level.random.nextDouble()*5;if(!machine.muffled)oneShot(machine,"block.assemblerstart",machine.volume(.25F),1.25F+level.random.nextFloat()*.25F);}}}}
    public static final class Arm {
        public final double[] angles=new double[4],previous=new double[4],target=new double[4],speed=new double[4];private final Random random=new Random();private int state,delay;private static final double[][] POSITIONS={{45,-15,-5},{15,15,-15},{25,10,-15},{30,0,-10},{70,-10,-25}};
        Arm(){resetSpeed();}private void resetSpeed(){speed[0]=speed[1]=speed[2]=15;speed[3]=.5;}
        void returnToNull(){Arrays.fill(target,0);speed[0]=speed[1]=speed[2]=3;speed[3]=.25;state=2;move();}
        void update(){resetSpeed();if(delay>0){delay--;return;}switch(state){case 0->{if(move()){delay=2;state=1;target[3]=-.75;}}case 1->{if(move()){state=2;target[3]=0;}}case 2->{if(move()){delay=2+random.nextInt(5);var next=POSITIONS[random.nextInt(POSITIONS.length)];System.arraycopy(next,0,target,0,3);state=0;}}}}
        private boolean move(){boolean changed=false;for(int i=0;i<4;i++){if(angles[i]==target[i])continue;changed=true;if(Math.abs(angles[i]-target[i])<=speed[i])angles[i]=target[i];else angles[i]+=target[i]>angles[i]?speed[i]:-speed[i];}return !changed;}
        public double angle(int index,float partial){return previous[index]+(angles[index]-previous[index])*partial;}
    }
    private static final class Loop extends AbstractTickableSoundInstance {
        final AssemblerBlockEntity machine;int keepAlive;
        Loop(AssemblerBlockEntity machine){super(HbmSounds.get("block.motor"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}
        void update(){pitch=.75F;var player=Minecraft.getInstance().player;volume=machine.volume(.5F)*(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}void finish(){stop();}
    }
}
