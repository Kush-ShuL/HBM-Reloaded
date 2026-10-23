package com.hbm.client.structureloot;
import com.hbm.world.structure.loot.LoreBookItem;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
/** Exact 272x182 GUIBookLore artwork, page bounds, 100px word wrapping and $ line breaks. */
public final class LoreBookScreen extends Screen {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/book/book_lore.png");
    private final CompoundTag tag;private final String key;private final int color,maxPage;private int left,top,page;
    public LoreBookScreen(ItemStack stack){super(stack.getHoverName());tag=LoreBookItem.data(stack);key=tag.getString("k");int cover=tag.getInt("cov_col");color=cover>0?cover:0x303030;maxPage=(int)Math.ceil(tag.getInt("p")/2D)-1;}
    @Override protected void init(){if(key.isEmpty()){onClose();return;}left=(width-272)/2;top=(height-182)/2;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        graphics.fillGradient(0,0,width,height,0xc0101010,0xd0101010);
        graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(TEXTURE,left,top,0,0,272,182,512,512);graphics.setColor(1,1,1,1);graphics.blit(TEXTURE,left+7,top+7,0,182,258,165,512,512);
        boolean overY=mouseY>=top+155&&mouseY<top+165;
        if(page>0)graphics.blit(TEXTURE,left+24,top+155,overY&&mouseX>=left+24&&mouseX<=left+42?295:272,13,18,10,512,512);
        if(page<maxPage)graphics.blit(TEXTURE,left+230,top+155,overY&&mouseX>=left+230&&mouseX<=left+248?295:272,0,18,10,512,512);
        for(int side=0;side<2;side++){int actual=page*2+side;if(actual>=tag.getInt("p"))continue;var args=new ArrayList<String>();var argTag=tag.getCompound("p"+actual);for(int index=1;;index++){String value=argTag.getString("a"+index);if(value.isEmpty())break;args.add(value);}String text=I18n.get("book_lore."+key+".page."+actual,args.toArray());String[] words=text.split(" ");if(words.length==0)continue;var lines=new ArrayList<String>();lines.add(words[0]);int indent=font.width(words[0]);for(int w=1;w<words.length;w++){if(words[w].equals("$")){if(w+1<words.length&&!words[w+1].equals("$")){lines.add(words[++w]);indent=font.width(words[w]);}else lines.add("");continue;}indent+=font.width(" "+words[w]);if(indent<=100){int last=lines.size()-1;lines.set(last,lines.get(last)+" "+words[w]);}else{lines.add(words[w]);indent=font.width(words[w]);}}for(int line=0;line<lines.size();line++)graphics.drawString(font,lines.get(line),left+20+side*130,top+20+9*line,0xff0f0f0f,false);}
    }
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button){if(mouseY<top+155||mouseY>=top+165)return false;boolean changed=false;if(page>0&&mouseX>=left+24&&mouseX<=left+42){page--;changed=true;}if(page<maxPage&&mouseX>=left+230&&mouseX<=left+248){page++;changed=true;}if(changed)minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));return changed;}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==256||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
}
