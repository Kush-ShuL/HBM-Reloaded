package com.hbm.explosion.vanillant;

import com.hbm.explosion.vanillant.interfaces.*;
import com.hbm.explosion.vanillant.standard.*;
import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** X5846's configurable VNT explosion pipeline. Each phase remains opt-in. */
public class ExplosionVNT {
    private IBlockAllocator blockAllocator;
    private IEntityProcessor entityProcessor;
    private IBlockProcessor blockProcessor;
    private IPlayerProcessor playerProcessor;
    private IExplosionSFX[] sfx;
    public final Level world;
    public final double posX, posY, posZ;
    public float size;
    public Entity exploder;
    public Explosion compat;

    public ExplosionVNT(Level world,double x,double y,double z,float size) {this(world,x,y,z,size,null);}
    public ExplosionVNT(Level world,double x,double y,double z,float size,Entity exploder) {
        this.world=world;this.posX=x;this.posY=y;this.posZ=z;this.size=size;this.exploder=exploder;
        compat=new Explosion(world,exploder,x,y,z,size,false,Explosion.BlockInteraction.DESTROY);
    }
    public void explode() {
        // Modern Explosion fields are final; recreate its event/callback view after caller changes.
        compat=new Explosion(world,exploder,posX,posY,posZ,size,false,Explosion.BlockInteraction.DESTROY);
        boolean blocks=blockAllocator!=null&&blockProcessor!=null;
        boolean entities=entityProcessor!=null&&playerProcessor!=null;
        HashSet<BlockPos> affectedBlocks=null;
        HashMap<Player,Vec3> affectedPlayers=null;
        if(blocks)affectedBlocks=blockAllocator.allocate(this,world,posX,posY,posZ,size);
        if(blocks)compat.getToBlow().addAll(affectedBlocks);
        if(entities)affectedPlayers=entityProcessor.process(this,world,posX,posY,posZ,size);
        if(entities)compat.getHitPlayers().putAll(affectedPlayers);
        if(blocks)blockProcessor.process(this,world,posX,posY,posZ,affectedBlocks);
        if(entities)playerProcessor.process(this,world,posX,posY,posZ,affectedPlayers);
        if(sfx!=null)for(IExplosionSFX fx:sfx)fx.doEffect(this,world,posX,posY,posZ,size);
    }
    public ExplosionVNT setBlockAllocator(IBlockAllocator value){blockAllocator=value;return this;}
    public ExplosionVNT setEntityProcessor(IEntityProcessor value){entityProcessor=value;return this;}
    public ExplosionVNT setBlockProcessor(IBlockProcessor value){blockProcessor=value;return this;}
    public ExplosionVNT setPlayerProcessor(IPlayerProcessor value){playerProcessor=value;return this;}
    public ExplosionVNT setSFX(IExplosionSFX... value){sfx=value;return this;}
    public ExplosionVNT makeStandard(){return setBlockAllocator(new BlockAllocatorStandard()).setBlockProcessor(new BlockProcessorStandard()).setEntityProcessor(new EntityProcessorStandard()).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectStandard());}
    public ExplosionVNT makeAmat(){return setBlockAllocator(new BlockAllocatorStandard(size<15?16:32)).setBlockProcessor(new BlockProcessorStandard().setNoDrop()).setEntityProcessor(new EntityProcessorStandard().withRangeMod(2F).withDamageMod(new CustomDamageHandlerAmat(50F))).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectAmat());}
}
