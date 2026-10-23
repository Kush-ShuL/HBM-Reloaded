package com.hbm.client.weapon.pepperbox;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.hud.IHUDComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Source HUDComponentDurability + HUDComponentAmmo, preserving pixel coordinates and draw order. */
public final class GunHudComponents {
    public static final int PANEL_OFFSET=98,PANEL_WIDTH=52;
    public static final ResourceLocation OVERLAY=ResourceLocation.parse("hbm:textures/misc/overlay_misc.png");
    public static final IHUDComponent DURABILITY=new IHUDComponent(){
        public int getComponentHeight(Player player,ItemStack stack){return 5;}
        public void renderHUDComponent(GuiGraphics graphics,Player player,ItemStack stack,int bottomOffset,int gunIndex){
            var gun=(ItemGunBaseNT)stack.getItem();int x=graphics.guiWidth()/2+PANEL_OFFSET,y=graphics.guiHeight()-21;
            int wear=(int)(50*ItemGunBaseNT.getWear(stack,gunIndex)/gun.getConfig(stack,gunIndex).getDurability(stack));
            graphics.blit(OVERLAY,x,y+16,94,0,52,3,256,256);
            graphics.blit(OVERLAY,x+1,y+16,95,3,50-wear,3,256,256);
        }
    };
    public static final IHUDComponent AMMO=new IHUDComponent(){
        public int getComponentHeight(Player player,ItemStack stack){return 17;}
        public void renderHUDComponent(GuiGraphics graphics,Player player,ItemStack stack,int bottomOffset,int gunIndex){
            var gun=(ItemGunBaseNT)stack.getItem();var config=gun.getConfig(stack,gunIndex);
            int x=graphics.guiWidth()/2+PANEL_OFFSET,y=graphics.guiHeight()-bottomOffset-18;
            for(var receiver:config.getReceivers(stack)){
                var magazine=receiver.getMagazine(stack);
                graphics.drawString(LegacyFonts.get(),magazine.reportAmmoStateForHUD(stack,player),x+17,y+6,0xFFFFFF,false);
                graphics.renderItem(magazine.getIconForHUD(stack,player),x,y);y-=17;
            }
        }
    };
    private GunHudComponents(){}
}
