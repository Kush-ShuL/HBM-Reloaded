package com.hbm.client.turret.himars;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.TurretControl;
import com.hbm.machine.turret.himars.*;
import java.util.Arrays;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original GUITurretHIMARS extends the actual shared11-slot GUI;modebutton exact151/16 and original18px atlas icons/tooltips. */
public final class HimarsTurretScreen extends SentryScreen<HimarsTurretMenu> {
 public HimarsTurretScreen(HimarsTurretMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/gui_turret_himars.png"));}
 private HimarsTurretEntity artillery(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof HimarsTurretEntity tile?tile:null;}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){super.renderBg(graphics,partial,mouseX,mouseY);var tile=artillery();if(tile==null)return;if(tile.mode==HimarsTurretEntity.MODE_MANUAL)drawLegacyRegion(graphics,151,16,210,0,18,18);}
 @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){super.render(graphics,mouseX,mouseY,partial);var tile=artillery();if(tile!=null&&legacyInfoHovered(mouseX,mouseY,151,16,18,18)){String key="turret.arty."+(tile.mode==HimarsTurretEntity.MODE_AUTO?"artillery_rocket":"manual_rocket");graphics.renderComponentTooltip(font,Arrays.stream(Component.translatable(key).getString().split("\\$",-1)).<Component>map(Component::literal).toList(),mouseX,mouseY);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,151,16,18,18)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new TurretControl(menu.position,0,5,""));return true;}return result;}
}
