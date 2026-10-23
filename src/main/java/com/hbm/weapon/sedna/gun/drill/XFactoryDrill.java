package com.hbm.weapon.sedna.gun.drill;
import com.hbm.bomb.conventional.RubbleBurstEffects;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.fluid.HbmFluids;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import java.util.function.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.PacketDistributor;
/** Actual X5846 fuelled drill: one native SEDNA receiver, source player harvest and NT damage; no projectile is invented. */
public final class XFactoryDrill {
    public static final String D_REACH="D_REACH",F_DTNEG="F_DTNEG",F_PIERCE="F_PIERCE",I_AOE="I_AOE",I_HARVEST="I_HARVEST";
    public static ItemGunDrill create(){return new ItemGunDrill(WeaponQuality.UTILITY,new GunConfig()
        .dura(3000).draw(10).inspect(55).hideCrosshair(false).crosshair(Crosshair.L_CIRCUMFLEX)
        .rec(new Receiver(0).dmg(DrillSettings::damage).delay(20).dry(30).auto(true).jam(0)
            .mag(new MagazineLiquidEngine(0,4000,HbmFluids.GASOLINE,HbmFluids.GASOLINE_LEADED,HbmFluids.COALGAS,HbmFluids.COALGAS_LEADED))
            .offset(1,-.0625*2.5,-.25).canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(LAMBDA_DRILL_FIRE))
        .pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD).decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
        .anim(LAMBDA_DRILL_ANIMS).orchestra(DrillOrchestra::play));}
    public static final BiConsumer<ItemStack,LambdaContext> LAMBDA_DRILL_FIRE=(stack,context)->doStandardFire(stack,context,GunAnimation.CYCLE,true);
    public static void doStandardFire(ItemStack stack,LambdaContext context,GunAnimation animation,boolean calcWear){
        var player=context.getPlayer();if(player==null)return;int index=context.configIndex;
        if(animation!=null)ItemGunBaseNT.playAnimation(player,stack,animation,index);
        var primary=context.config.getReceivers(stack)[0];var magazine=primary.getMagazine(stack);
        var hit=LegacyEntityDamage.getMouseOver(player,getModdableReach(stack,5));
        if(hit instanceof EntityHitResult entityHit){
            float damage=primary.getBaseDamage(stack);var target=entityHit.getEntity();var source=player.damageSources().playerAttack(player);
            if(target instanceof LivingEntity living)LegacyEntityDamage.attackEntityFromNT(living,source,damage,true,true,.1F,getModdableDTNegation(stack,2),getModdablePiercing(stack,.15F));
            else target.hurt(source,damage);
        }else if(hit instanceof BlockHitResult blockHit&&hit.getType()==HitResult.Type.BLOCK){
            int area=player.isShiftKeyDown()?0:getModdableAoE(stack,1);var origin=blockHit.getBlockPos();
            breakExtraBlock(player.level(),origin,player,origin);
            for(int x=-area;x<=area;x++)for(int y=-area;y<=area;y++)for(int z=-area;z<=area;z++){
                if(x==0&&y==0&&z==0)continue;breakExtraBlock(player.level(),origin.offset(x,y,z),player,origin);
            }
            didPlink=false;
        }
        int consumption=XWeaponModManager.hasUpgrade(stack,0,228)?1000:10;
        magazine.useUpAmmo(stack,context.inventory,consumption);
        // Source deliberately does not increment wear here.
        if(calcWear)ItemGunBaseNT.setWear(stack,index,Math.min(ItemGunBaseNT.getWear(stack,index),context.config.getDurability(stack)));
    }
    public static boolean didPlink=false;
    public static void breakExtraBlock(Level world,BlockPos pos,Player player,BlockPos origin){
        if(world.isEmptyBlock(pos)||!(player instanceof ServerPlayer server))return;
        var state=world.getBlockState(pos);String id=BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if(!state.canHarvestBlock(world,pos,player)||(state.getDestroySpeed(world,pos)==-1F&&state.getDestroyProgress(player,world,pos)==0F)||id.equals("stone_keyhole")){
            if(!didPlink){world.playSound(null,player.getX(),player.getY(),player.getZ(),net.minecraft.sounds.SoundEvents.ITEM_BREAK,player.getSoundSource(),.5F,.8F+world.random.nextFloat()*.6F);didPlink=true;}return;
        }
        boolean harvested=server.gameMode.destroyBlock(pos);
        if(harvested){
            if(pos.equals(origin))server.connection.send(new ClientboundLevelEventPacket(2001,pos,Block.getId(state),false));
            else PacketDistributor.sendToPlayer(server,new RubbleBurstEffects(pos.getX(),pos.getY(),pos.getZ(),Block.getId(state)));
            if(!server.isCreative())server.causeFoodExhaustion(.02F); // Original harvestBlock .025 versus native .005.
        }
    }
    public static double getModdableReach(ItemStack stack,double base){return XWeaponModManager.eval(base,stack,D_REACH,DrillContent.GUN.get(),0);}
    public static float getModdableDTNegation(ItemStack stack,float base){return XWeaponModManager.eval(base,stack,F_DTNEG,DrillContent.GUN.get(),0);}
    public static float getModdablePiercing(ItemStack stack,float base){return XWeaponModManager.eval(base,stack,F_PIERCE,DrillContent.GUN.get(),0);}
    public static int getModdableAoE(ItemStack stack,int base){return XWeaponModManager.eval(base,stack,I_AOE,DrillContent.GUN.get(),0);}
    public static int getModdableHarvestLevel(ItemStack stack,int base){return XWeaponModManager.eval(base,stack,I_HARVEST,DrillContent.GUN.get(),0);}
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_DRILL_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().setPos(-1, 0, 0).addPos(0, 0, 0, 750, IType.SIN_DOWN));
		case CYCLE:
			double deploy = HbmAnimations.getRelevantTransformation("DEPLOY")[0];
			double speed = HbmAnimations.getRelevantTransformation("SPEED")[0];
			double spin = HbmAnimations.getRelevantTransformation("SPIN")[0] % 360; // seamlessly continue from the previous animation state
			return new BusAnimation()
					.addBus("DEPLOY", new BusAnimationSequence().setPos(deploy, 0, 0).addPos(1, 0, 0, (int) (500 * (1 - deploy)), IType.SIN_FULL).hold(1000).addPos(0, 0, 0, 500, IType.SIN_FULL))
					.addBus("SPIN", new BusAnimationSequence().setPos(spin, 0, 0).addPos(spin + 360 * 1.5, 0, 0, 1500).addPos(360 * 3, 0, 0, 750 + (int) (1000 * (1D - spin / 360D)), IType.SIN_DOWN))
					.addBus("SPEED", new BusAnimationSequence().setPos(speed, 0, 0).addPos(1, 0, 0, 500).hold(1000).addPos(0, 0, 0, 750 + (int) (1000 * (1D - spin / 360D)), IType.SIN_DOWN));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("DEPLOY", new BusAnimationSequence().addPos(0.25, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("SPIN", new BusAnimationSequence().addPos(360 * 1, 0, 0, 1500, IType.SIN_DOWN))
				.addBus("SPEED", new BusAnimationSequence().addPos(0.75, 0, 0, 250).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
		case INSPECT: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-45, 0, 0, 500, IType.SIN_FULL).hold(1000).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		}
		
		return null;
	};
	
    private XFactoryDrill(){}
}
