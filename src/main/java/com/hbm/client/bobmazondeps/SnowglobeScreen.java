package com.hbm.client.bobmazondeps;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.item.bobmazondeps.SnowglobeEntity;
import com.hbm.registry.HbmSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;

/** Original300x150 green commemorative panel, ordinary selected Font and no modern blur pass. */
public final class SnowglobeScreen extends LegacyScreen {
 private final SnowglobeEntity tile;
 public SnowglobeScreen(SnowglobeEntity tile){super(Component.translatable("gui.hbm.snowglobe.title"));this.tile=tile;}
 @Override protected void init(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(HbmSounds.get("block.bobble"),1));}
 private void centered(GuiGraphics graphics,Component text,int y,int color){graphics.drawString(font,text,(width-font.width(text))/2,y,color,true);}
 @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){renderBackground(graphics,mouseX,mouseY,partial);int left=(width-300)/2,top=(height-150)/2;graphics.fill(left,top,left+300,top+150,0xcc003300);int y=top+10;centered(graphics,title,y,0x00ff00);y+=10;centered(graphics,Component.translatableWithFallback(tile.type.key("name"),tile.type.label),y,0x009900);y+=20;
  if(tile.type.inscription!=null){centered(graphics,Component.translatable("gui.hbm.snowglobe.inscription"),y,0x00ff00);y+=10;var inscription=Component.translatableWithFallback(tile.type.key("inscription"),tile.type.inscription);for(var line:font.split(inscription,280)){graphics.drawString(font,line,(width-font.width(line))/2,y,0x009900,true);y+=10;}}
 }
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==256||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
 @Override public boolean isPauseScreen(){return false;}
}
