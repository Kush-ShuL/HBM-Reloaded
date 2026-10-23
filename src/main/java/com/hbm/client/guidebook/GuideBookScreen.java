package com.hbm.client.guidebook;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.item.guidebook.GuideBooks.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

/** Original GUIScreenGuide:272×182 cover/spread in512×512 atlases, source page/text/image coordinates. */
public final class GuideBookScreen extends LegacyScreen {
    private static final ResourceLocation BOOK=ResourceLocation.parse("hbm:textures/gui/book/book.png"),COVER=ResourceLocation.parse("hbm:textures/gui/book/book_cover.png");
    private final BookType type;private final int maxPage;private int page=-1,left,top;
    public GuideBookScreen(BookType type){super(Component.translatable("item.book_guide.name"));this.type=type;maxPage=(int)Math.ceil(type.pages.size()/2D)-1;}
    @Override protected void init(){super.init();left=(width-272)/2;top=(height-182)/2;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        renderBackground(graphics,mouseX,mouseY,partial);
        if(page<0){
            graphics.blit(COVER,left,top,0,0,272,182,512,512);
            float scale=type.titleScale;String[] lines=I18n.get(type.title).split("\\$");
            for(int i=0;i<lines.length;i++){String cover=lines[i];graphics.pose().pushPose();graphics.pose().scale(scale,scale,1);graphics.drawString(font,cover,(int)((left+(136-font.width(cover)/2*scale))/scale),(int)((top+50+i*10*scale)/scale),0xfece00,false);graphics.pose().popPose();}
            return;
        }
        graphics.blit(BOOK,left,top,0,0,272,182,512,512);
        if(page>0)graphics.blit(BOOK,left+24,top+155,overLeft(mouseX,mouseY)?26:3,207,18,10,512,512);
        if(page<maxPage)graphics.blit(BOOK,left+230,top+155,overRight(mouseX,mouseY)?26:3,194,18,10,512,512);
        for(int side=0;side<2;side++){
            int defacto=page*2+side;if(defacto>=type.pages.size())continue;GuidePage current=type.pages.get(defacto);
            for(GuideText box:current.texts){
                float scale=box.scale;String text=I18n.get(box.text);int widthScaled=(int)(box.width*scale);
                // Deliberately preserve source space-token wrapping and integer arithmetic, including formatting tokens.
                String[] words=text.split(" ");List<String> lines=new ArrayList<>();lines.add(words[0]);int indent=font.width(words[0]);
                for(int word=1;word<words.length;word++){indent+=font.width(" "+words[word]);if(indent<=widthScaled){int last=lines.size()-1;lines.set(last,lines.get(last)+" "+words[word]);}else{lines.add(words[word]);indent=font.width(words[word]);}}
                float titleScale=overrideScale(current.titleScale,current.title+".scale");float offset=box.yOffset==-1?(current.title==null?-10:6/titleScale):box.yOffset;
                graphics.pose().pushPose();graphics.pose().scale(1/scale,1/scale,1);
                for(int line=0;line<lines.size();line++)graphics.drawString(font,lines.get(line),(int)((left+20+side*130+box.xOffset)*scale),(int)((top+30+offset)*scale+12*line),4210752,false);
                graphics.pose().popPose();
            }
            if(current.title!=null){float scale=current.titleScale;String title=I18n.get(current.title);graphics.pose().pushPose();graphics.pose().scale(1/scale,1/scale,1);graphics.drawString(font,title,(int)((left+20+side*130+(50-font.width(title)/2/scale))*scale),(int)((top+20)*scale),current.titleColor,false);graphics.pose().popPose();}
            for(GuideImage image:current.images){int x=image.x==-1?50-image.sizeX/2:image.x;var replacement=GuideBookImages.actualItem(image);if(replacement.isEmpty())graphics.blit(image.image,left+20+x+side*130,top+image.y,0,0,image.sizeX,image.sizeY,image.sizeX,image.sizeY);else{graphics.pose().pushPose();graphics.pose().translate(left+20+x+side*130,top+image.y,0);graphics.pose().scale(image.sizeX/16F,image.sizeY/16F,1);graphics.renderItem(replacement,0,0);graphics.pose().popPose();}}
            String label=(defacto+1)+"/"+type.pages.size();graphics.drawString(font,label,left+44+side*185-side*font.width(label),top+156,4210752,false);
        }
    }
    private float overrideScale(float fallback,String tag){try{return 1/Float.parseFloat(I18n.get(tag));}catch(NumberFormatException ignored){return fallback;}}
    private boolean overLeft(double x,double y){return x>=left+24&&x<left+42&&y>=top+155&&y<top+165;}
    private boolean overRight(double x,double y){return x>=left+230&&x<left+248&&y>=top+155&&y<top+165;}
    private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(),1));}
    @Override public boolean mouseClicked(double x,double y,int button){if(page<0){page=0;click();return true;}if(overLeft(x,y)&&page>0){page--;click();return true;}if(overRight(x,y)&&page<maxPage){page++;click();return true;}return super.mouseClicked(x,y,button);}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
    // Source inherits GuiScreen's pausing behavior; it has no non-pausing override.
}
