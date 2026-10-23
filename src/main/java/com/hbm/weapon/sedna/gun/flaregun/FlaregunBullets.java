package com.hbm.weapon.sedna.gun.flaregun;
import com.hbm.bomb.conventional.grenade.FireStatus;
import com.hbm.entity.aircraft.c130.C130Content;
import com.hbm.entity.aircraft.c130.EntityC130.C130PayloadType;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.particle.SpentCasing.CasingType;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.EntityHitResult;

/** Three source signal rounds retain standard impact/ricochet callbacks and real C130 delivery. */
public final class FlaregunBullets {
    private FlaregunBullets(){}
    public static final BulletConfig FLARE=flare("normal",PistolAmmo.G26_FLARE,0x9E1616,"g26Flare");
    public static final BulletConfig SUPPLY=flare("supply",PistolAmmo.G26_FLARE_SUPPLY,0x3C80F0,"g26FlareSupply").setOnUpdate(entity->spawnPlane(entity,C130PayloadType.SUPPLIES));
    public static final BulletConfig WEAPON=flare("weapon",PistolAmmo.G26_FLARE_WEAPON,0x278400,"g26FlareWeapon").setOnUpdate(entity->spawnPlane(entity,C130PayloadType.WEAPONS));
    private static BulletConfig flare(String key,GunAmmo ammo,int color,String casingName){
        return new BulletConfig("hbm:g26_flare/"+key).setItem(ammo).setLife(100).setVel(2F).setGrav(.015D).setRenderRotations(false)
            .setOnImpact((bullet,impact)->{if(impact instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity living)FireStatus.data(living).fire+=200;})
            .setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(color).setScale(2F).register(casingName));
    }
    public static void initializeCasings(){
        for(var config:new BulletConfig[]{FLARE,SUPPLY,WEAPON}){var casing=com.hbm.item.casing.FlatCasingContent.stack(1,1);config.setCasing(casing,4);}
    }
    private static void spawnPlane(Entity entity,C130PayloadType payload){
        if(!(entity.level() instanceof ServerLevel level)||entity.tickCount!=40)return;
        var bullet=(EntityBulletBaseMK4)entity;var owner=bullet.getThrower();
        if(owner!=null)level.playSound(null,owner.getX(),owner.getY(),owner.getZ(),HbmSounds.get("hbm:item.techBleep"),SoundSource.PLAYERS,1F,1F);
        int x=Mth.floor(bullet.getX()),z=Mth.floor(bullet.getZ());
        C130Content.spawnAtSignal(level,x,sourceHeight(level,x,z),z,payload);
    }
    /** Original Chunk.heightMap tracks the first block with positive light opacity.
     * Native WORLD_SURFACE provides an upper bound; transparent decorations do not raise the signal. */
    private static int sourceHeight(ServerLevel level,int x,int z){
        int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);
        var pos=new BlockPos.MutableBlockPos(x,y-1,z);
        while(y>level.getMinBuildHeight()&&level.getBlockState(pos).getLightBlock(level,pos)==0){y--;pos.setY(y-1);}
        return y;
    }
}
