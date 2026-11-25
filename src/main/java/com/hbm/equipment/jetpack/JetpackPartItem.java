package com.hbm.equipment.jetpack;

import com.hbm.armor.ArmorModStorage;
import java.util.Locale;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Each modular part has an independent registry ID rather than source metadata. */
public final class JetpackPartItem extends Item {
    public enum Part {
        TANK_COALGAS("COALGAS",1000), TANK_KEROSENE("KEROSENE",2000), TANK_JETFUEL("KEROSENE_REFORM",4000),
        EXTENSION_TANKS(null,6000), THRUSTERS(.1), THRUSTER_LARGE(.3), STABILIZER, WINGS;
        public final String tankType;
        public final int tankSize;
        public final double velocity;
        Part(){this(null,0,0);}
        Part(String tankType,int tankSize){this(tankType,tankSize,0);}
        Part(double velocity){this(null,0,velocity);}
        Part(String tankType,int tankSize,double velocity){this.tankType=tankType;this.tankSize=tankSize;this.velocity=velocity;}
        public String id(){return name().toLowerCase(Locale.ROOT);}
    }
    public final Part part;
    public JetpackPartItem(Part part){super(new Properties().stacksTo(1));this.part=part;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var held=player.getItemInHand(hand);var chest=player.getItemBySlot(EquipmentSlot.CHEST);
        boolean attached=!(chest.getItem() instanceof ModularJetpackItem);
        var pack=attached?ArmorModStorage.pryMod(chest,ArmorModStorage.CHEST,level.registryAccess()):chest;
        if(!(pack.getItem() instanceof ModularJetpackItem)||ModularJetpackItem.isPartInstalled(pack,part))return InteractionResultHolder.pass(held);
        if(part==Part.EXTENSION_TANKS&&ModularJetpackItem.getInstalledTank(pack)==null)return InteractionResultHolder.pass(held);
        if(!level.isClientSide){
            Part previous=part.tankType!=null?ModularJetpackItem.getInstalledTank(pack)
                    :part.velocity>0?ModularJetpackItem.getInstalledThruster(pack)
                    :part==Part.STABILIZER||part==Part.WINGS?ModularJetpackItem.getInstalledFlightModel(pack):null;
            if (part.tankType != null) ModularJetpackItem.setFuel(pack, 0);
            if(previous!=null){
                if (!player.getAbilities().instabuild) {
                    var returned = new ItemStack(VectorJetpackContent.PARTS.get(previous).get());
                    if (!player.getInventory().add(returned)) player.drop(returned, false);
                }
                ModularJetpackItem.togglePart(pack,previous,false);
            }
            ModularJetpackItem.togglePart(pack,part,true);
            if(attached)ArmorModStorage.applyMod(chest,pack,level.registryAccess());
            held.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(held,level.isClientSide);
    }
}
