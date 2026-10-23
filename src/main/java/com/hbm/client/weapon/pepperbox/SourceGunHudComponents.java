package com.hbm.client.weapon.pepperbox;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.hud.IHUDComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Original per-receiver and mirrored components, including both configurations of source built-in akimbo weapons. */
public final class SourceGunHudComponents {
    public static final IHUDComponent HUD_COMPONENT_DURABILITY=durability(false),HUD_COMPONENT_DURABILITY_MIRROR=durability(true);
    public static final IHUDComponent HUD_COMPONENT_AMMO=ammo(0,false,false),HUD_COMPONENT_AMMO_MIRROR=ammo(0,true,false),HUD_COMPONENT_AMMO_NOCOUNTER=ammo(0,false,true),HUD_COMPONENT_AMMO_SECOND=ammo(1,false,false);
    private static int x(GuiGraphics graphics,Player player,ItemStack stack,boolean mirrored){
        if(stack.getItem() instanceof ItemGunBaseNT gun&&gun.isBuiltInDualWield()&&player.getMainArm()==HumanoidArm.LEFT)mirrored=!mirrored;
        return graphics.guiWidth()/2+(mirrored?-(98+52):98);
    }
    private static IHUDComponent durability(boolean mirrored){return new IHUDComponent(){
        public int getComponentHeight(Player player,ItemStack stack){return 5;}
        public void renderHUDComponent(GuiGraphics graphics,Player player,ItemStack stack,int bottomOffset,int gunIndex){
            var gun=(ItemGunBaseNT)stack.getItem();int x=x(graphics,player,stack,mirrored),y=graphics.guiHeight()-21;
            int wear=(int)(50*ItemGunBaseNT.getWear(stack,gunIndex)/gun.getConfig(stack,gunIndex).getDurability(stack));
            graphics.blit(GunHudComponents.OVERLAY,x,y+16,94,0,52,3,256,256);graphics.blit(GunHudComponents.OVERLAY,x+1,y+16,95,3,50-wear,3,256,256);
        }
    };}
    private static IHUDComponent ammo(int receiver,boolean mirrored,boolean noCounter){return new IHUDComponent(){
        public int getComponentHeight(Player player,ItemStack stack){return 17;}
        public void renderHUDComponent(GuiGraphics graphics,Player player,ItemStack stack,int bottomOffset,int gunIndex){
            var gun=(ItemGunBaseNT)stack.getItem();var mag=gun.getConfig(stack,gunIndex).getReceivers(stack)[receiver].getMagazine(stack);
            int x=x(graphics,player,stack,mirrored)+(noCounter?14:0),y=graphics.guiHeight()-bottomOffset-18;
            if(!noCounter)graphics.drawString(LegacyFonts.get(),mag.reportAmmoStateForHUD(stack,player),x+17,y+6,0xFFFFFF,false);
            graphics.renderItem(mag.getIconForHUD(stack,player),x,y);
        }
    };}
    private SourceGunHudComponents(){}
}
