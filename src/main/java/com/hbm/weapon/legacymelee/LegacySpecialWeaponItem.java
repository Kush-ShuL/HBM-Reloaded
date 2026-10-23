package com.hbm.weapon.legacymelee;

import com.hbm.LegacyStartup;
import com.hbm.advancement.HbmAdvancements;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.bomb.conventional.RubbleProjectile;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.item.legacysword.LegacySwordBlocking;
import com.hbm.item.meteorgavel.SourceGavelAppearance;
import com.hbm.item.toolability.SteelTier;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Mechanical WeaponSpecial branches. All source hitEntity paths returnfalse; only Caber wears505. */
public final class LegacySpecialWeaponItem extends LegacySwordBase implements LegacySwordBlocking,SourceGavelAppearance {
    public enum Kind { HAMMER,SLEDGE,AXE,CABER,PCH,WRENCH_FLIPPED,MEMESPOON,WOOD_GAVEL,STOP,SOP,CHERNOBYL }
    public final Kind kind;
    @SuppressWarnings("unused") private final Random sourceRandom=new Random(); // source owns one java.util.Random per Item, never a shared stream
    public LegacySpecialWeaponItem(Kind kind,String name) {
        super(material(kind),name,movement(kind),rarity(kind));this.kind=kind;
    }
    private static Tier material(Kind kind){return switch(kind){case HAMMER,PCH->LegacyMeleeMaterials.HAMMER;case SLEDGE,AXE->LegacyMeleeMaterials.SLEDGE;case WRENCH_FLIPPED->LegacyMeleeMaterials.ELECTRIC;case STOP,SOP,CHERNOBYL->LegacyMeleeMaterials.ALLOY;case WOOD_GAVEL->Tiers.WOOD;default->SteelTier.INSTANCE;};}
    private static double movement(Kind kind){return switch(kind){case HAMMER->-.5;case SLEDGE,AXE->-.2;case WRENCH_FLIPPED->-.1;default->0;};}
    private static Rarity rarity(Kind kind){return switch(kind){case HAMMER->Rarity.RARE;case CABER->Rarity.UNCOMMON;case SLEDGE,AXE->Rarity.EPIC;default->Rarity.COMMON;};}
    public Rarity sourceRarity(){return rarity(kind);}
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker) {
        Level world=victim.level();
        switch(kind){
            case HAMMER->{if(!world.isClientSide)victim.setHealth(0);sound(world,victim,"weapon.bonk",3,1);}
            case CABER->{if(!world.isClientSide)world.explode(null,victim.getX(),victim.getY(),victim.getZ(),7.5F,Level.ExplosionInteraction.TNT);LegacySwordWear.damage(stack,505,attacker);}
            case SLEDGE->{victim.setDeltaMovement(victim.getDeltaMovement().add(attacker.getLookAngle().scale(5)));sound(world,victim,"weapon.bang",3,1);}
            case AXE->{victim.setHealth(victim.getHealth()/2);sound(world,victim,"weapon.slice",3,1);}
            case WOOD_GAVEL->sound(world,victim,"weapon.whack",3,1);
            case MEMESPOON->{if(!world.isClientSide&&attacker instanceof Player player){
                if(player.fallDistance>=2){sound(world,victim,"weapon.bang",3,.75F);victim.hurt(world.damageSources().playerAttack(player),50);}
                if(player.fallDistance>=20&&!player.getAbilities().instabuild){double y=victim.getY()+victim.getBbHeight()/2D;
                    var vnt=new ExplosionVNT(world,victim.getX(),y,victim.getZ(),15,player)
                            .setEntityProcessor(new EntityProcessorCrossSmooth(1,150).setupPiercing(25,.5F))
                            .setPlayerProcessor(new PlayerProcessorStandard());
                    LargeConventionalEffects.small(world,victim.getX(),y,victim.getZ());vnt.explode();
                }
            }}
            case STOP,SOP->sound(world,victim,"weapon.stop",1,1);
            // Source wrench knockback is guarded this==ModItems.wrench, not wrench_flipped.
            // PCH and CHERNOBYL have no source special hit branch.
            default->{}
        }
        return false;
    }
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){}
    @Override public InteractionResult useOn(UseOnContext context) {
        var world=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();
        if(kind==Kind.SLEDGE){
            if(removable(world,pos)&&player!=null){var rubble=new RubbleProjectile(world,pos.getX()+.5F,pos.getY(),pos.getZ()+.5F);rubble.setMetaBasedOnBlock(world.getBlockState(pos));
                rubble.setDeltaMovement(rubble.getDeltaMovement().add(player.getLookAngle().scale(5)));sound(world,rubble,"weapon.bang",3,1);
                if(!world.isClientSide){world.addFreshEntity(rubble);world.destroyBlock(pos,false);}
            }
            return InteractionResult.SUCCESS;
        }
        if(kind==Kind.AXE){
            sound(world,pos,"weapon.kapeng",3,1);
            if(!world.isClientSide)for(var target:List.of(pos,pos.above(),pos.below()))if(removable(world,target))world.destroyBlock(target,false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    private static boolean removable(Level level,BlockPos pos){var state=level.getBlockState(pos);return !state.isAir()&&state.getBlock().getExplosionResistance()<6000;}
    private static void sound(Level level,Entity entity,String sound,float volume,float pitch){play(level,entity.getX(),entity.getY(),entity.getZ(),sound,volume,pitch);}
    private static void sound(Level level,BlockPos pos,String sound,float volume,float pitch){play(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,sound,volume,pitch);}
    private static void play(Level level,double x,double y,double z,String sound,float volume,float pitch){if(level.isClientSide)level.playLocalSound(x,y,z,HbmSounds.get(sound),SoundSource.PLAYERS,volume,pitch,false);else level.playSound(null,x,y,z,HbmSounds.get(sound),SoundSource.PLAYERS,volume,pitch);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(entity instanceof ServerPlayer player){if(equipped(player,"jackt","shimmer_sledge"))HbmAdvancements.trigger(player,"fiend");else if(equipped(player,"jackt2","shimmer_axe"))HbmAdvancements.trigger(player,"fiend2");}}
    private static boolean equipped(Player player,String chest,String held){var armor=BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse("hbm:"+chest));var weapon=BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse("hbm:"+held));return armor.isPresent()&&weapon.isPresent()&&player.getItemBySlot(EquipmentSlot.CHEST).is(armor.get())&&player.getMainHandItem().is(weapon.get());}
    @Override public String sourceGavelTexture(){if(kind!=Kind.WOOD_GAVEL)throw new IllegalStateException("Not the source gavel render branch");return "wood";}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags) {
        String key="tooltip.hbm.legacy_melee.";
        switch(kind){
            case HAMMER->{lines.add(Component.translatable(key+"hammer0"));lines.add(Component.translatable(key+"hammer1"));}
            case CABER->{lines.add(Component.translatable(key+"caber0"));lines.add(Component.translatable(key+"caber1"));}
            case SLEDGE->{if(LegacyStartup.polaroidId==11)for(int n=0;n<5;n++)lines.add(Component.translatable(key+"sledge_joke"+n));else lines.add(Component.translatable(key+"sledge"));}
            case AXE->lines.add(Component.translatable(key+(LegacyStartup.polaroidId==11?"axe_joke":"axe")));
            case WRENCH_FLIPPED->lines.add(Component.translatable(key+"wrench_flipped"));
            case MEMESPOON->{lines.add(Component.translatable(key+"spoon0").withStyle(ChatFormatting.DARK_GRAY));lines.add(Component.translatable(key+"spoon1").withStyle(ChatFormatting.AQUA));for(int n=2;n<4;n++)lines.add(Component.translatable(key+"spoon"+n).withStyle(ChatFormatting.RED));}
            case WOOD_GAVEL->lines.add(Component.translatable(key+"wood_gavel"));
            default->{}
        }
    }
}
