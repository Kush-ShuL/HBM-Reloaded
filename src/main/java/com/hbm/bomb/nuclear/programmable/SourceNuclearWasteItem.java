package com.hbm.bomb.nuclear.programmable;
import com.hbm.machine.reactor.WastePlateEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original ItemNuclearWaste, reusing reactors' already ported exact EntityItemWaste immunity/lifetime. */
public class SourceNuclearWasteItem extends Item {
 public SourceNuclearWasteItem(){this(new Properties());}
 protected SourceNuclearWasteItem(Properties properties){super(properties);}
 @Override public boolean hasCustomEntity(ItemStack stack){return true;}
 @Override public int getEntityLifespan(ItemStack stack,Level level){return Integer.MAX_VALUE;}
 @Override public Entity createEntity(Level level,Entity original,ItemStack stack){var waste=new WastePlateEntity(level,original.getX(),original.getY(),original.getZ(),stack);waste.setDeltaMovement(original.getDeltaMovement());waste.setPickUpDelay(10);original.discard();return waste;}
}
