package com.hbm.entity.mob.dummy;

import com.hbm.entity.mob.glyphid.ai.LegacyMobController;
import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** Actual source EntityLiving dummy, not an aggressive EntityCreature/Monster or an armor stand. */
public final class EntityDummy extends Mob {
    private final LegacyMobController.IdleState idle=new LegacyMobController.IdleState();

    public EntityDummy(EntityType<? extends EntityDummy> type,Level world){
        super(type,world);
        // The inherited old non-goal idle method owns yaw, pitch and jumping.
        moveControl=new MoveControl(this){@Override public void tick(){}};
        lookControl=new LookControl(this){@Override public void tick(){}};
        jumpControl=new JumpControl(this){@Override public void tick(){}};
    }
    public static AttributeSupplier.Builder attributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,20D).add(Attributes.MOVEMENT_SPEED,.7D);}
    @Override protected void registerGoals(){}
    @Override protected void customServerAiStep(){
        // Native Mob.serverAiStep already increments noActionTime once.
        LegacyMobController.idle(this,idle,()->setNoActionTime(LegacyMobController.despawn(this,getNoActionTime(),()->true)));
        float forward=zza,strafe=xxa;setSpeed(.1F);zza=forward;xxa=strafe;
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){
        // Original EntityLiving Gaussian bonus, without modern triangular noise/5% left hand.
        var range=getAttribute(Attributes.FOLLOW_RANGE);
        if(range!=null)range.addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm","dummy_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        ((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);setLeftHanded(false);return group;
    }
    @Override public void checkDespawn(){/* Source idle invokes this at its original point, not again in the outer native world tick. */}
    @Override public InteractionResult mobInteract(Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(stack.getItem() instanceof ArmorItem armor)setItemSlot(armor.getEquipmentSlot(),stack.copy());
        // Source deliberately delegates even after copying: no success/consumption is invented.
        return super.mobInteract(player,hand);
    }
    @Override public Component getName(){return Component.literal((int)(getHealth()*10)/10F+" / "+(int)(getMaxHealth()*10)/10F);}
    @Override protected void dropEquipment(){}
}
