package com.hbm.item.boltgun;

import com.hbm.advancement.HbmAdvancements;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Original ItemBoltgun: real BOLT callback, strict four-ammo priority and no-armour10damage via source retry helper. */
public final class EngineeringBoltgunItem extends Item {
    public static final ResourceKey<DamageType> DAMAGE=ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.parse("hbm:engineering_boltgun"));
    public EngineeringBoltgunItem(){super(new Properties().stacksTo(1));}
    private static ItemStack[] ammunition(){var bolt=AutogenContent.item("bolt");return new ItemStack[]{new ItemStack(HbmContent.item("bolt_spike")),HbmMaterials.bySymbol("MAT_STEEL").make(bolt),HbmMaterials.bySymbol("MAT_TUNGSTEN").make(bolt),HbmMaterials.bySymbol("MAT_DURA").make(bolt)};}
    @Override public boolean onLeftClickEntity(ItemStack stack,Player player,Entity victim){if(!victim.isAlive())return false;for(var ammo:ammunition())for(int slot=0;slot<player.getInventory().getContainerSize();slot++){var held=player.getInventory().getItem(slot);if(held.isEmpty()||held.getItem()!=ammo.getItem()||held.getOrDefault(HbmHazards.LEGACY_METADATA.get(),held.getDamageValue()).intValue()!=ammo.getOrDefault(HbmHazards.LEGACY_METADATA.get(),ammo.getDamageValue()).intValue())continue;if(!player.level().isClientSide){player.level().playSound(null,victim.getX(),victim.getY(),victim.getZ(),HbmSounds.get("item.boltgun"),SoundSource.PLAYERS,1,1);player.getInventory().removeItem(slot,1);player.inventoryMenu.broadcastChanges();var source=new DamageSource(player.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE),player);LegacyEntityDamage.attackEntityFromIgnoreIFrame(victim,source,10);if(!victim.isAlive()&&victim instanceof ServerPlayer dead)HbmAdvancements.trigger(dead,"achGoFish");EngineeringBoltgunPackets.effect(player.level(),victim.getX(),victim.getY()+victim.getBbHeight()/2,victim.getZ(),victim.getX(),victim.getY(),victim.getZ());EngineeringBoltgunPackets.animate(player,InteractionHand.MAIN_HAND);}return true;}return false;}
    @Override public InteractionResult useOn(UseOnContext context){var world=context.getLevel();var player=context.getPlayer();var pos=context.getClickedPos();if(player==null||!(world.getBlockState(pos).getBlock() instanceof ToolableBlock target))return InteractionResult.PASS;var hit=context.getClickLocation().subtract(pos.getX(),pos.getY(),pos.getZ());if(target.onScrew(world,player,pos,context.getClickedFace(),(float)hit.x,(float)hit.y,(float)hit.z,ToolType.BOLT)&&!world.isClientSide){world.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.boltgun"),SoundSource.PLAYERS,1,1);player.inventoryMenu.broadcastChanges();var direction=context.getClickedFace();EngineeringBoltgunPackets.effect(world,pos.getX()+hit.x+direction.getStepX()*.25,pos.getY()+hit.y+direction.getStepY()*.25,pos.getZ()+hit.z+direction.getStepZ()*.25,pos.getX(),pos.getY(),pos.getZ());EngineeringBoltgunPackets.animate(player,context.getHand());}return InteractionResult.PASS;}
}
