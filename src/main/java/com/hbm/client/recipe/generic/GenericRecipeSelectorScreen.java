package com.hbm.client.recipe.generic;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.recipe.generic.GenericMachineRecipe;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
/** Original176x132,8x5 recipe picker, one-row scrolling and literal blueprint pool gate. */
public final class GenericRecipeSelectorScreen extends com.hbm.client.gui.LegacyScreen {
    public static final String NULL_SELECTION="null";
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_recipe_selector.png");
    private final List<? extends GenericMachineRecipe> all;private final String pool;private final Screen previous;private final Consumer<String> submit;
    private final List<GenericMachineRecipe> visible=new ArrayList<>();private String selection;private EditBox search;private int left,top,page,size;private boolean submitted;
    public static void open(List<? extends GenericMachineRecipe> recipes,String selection,String installedPool,Screen previous,Consumer<String> submit){Minecraft.getInstance().setScreen(new GenericRecipeSelectorScreen(recipes,selection,installedPool,previous,submit));}
    public GenericRecipeSelectorScreen(List<? extends GenericMachineRecipe> recipes,String selection,String installedPool,Screen previous,Consumer<String> submit){super(Component.translatable("gui.recipe.setRecipe"));all=List.copyOf(recipes);this.selection=selection==null?NULL_SELECTION:selection;pool=installedPool;this.previous=previous;this.submit=submit;filter("");}
    private void filter(String term){visible.clear();for(var recipe:all)if((!recipe.isPooled()||(pool!=null&&recipe.isPartOfPool(pool)))&&recipe.displayName().getString().toLowerCase(Locale.US).contains(term.toLowerCase(Locale.US)))visible.add(recipe);page=0;size=Math.max(0,(int)Math.ceil((visible.size()-40)/8D));}
    @Override protected void init(){font=LegacyFonts.get();left=(width-176)/2;top=(height-132)/2;search=new EditBox(font,left+28,top+111,102,12,Component.empty());search.setBordered(false);search.setTextColor(-1);search.setTextColorUneditable(-1);search.setMaxLength(32);search.setResponder(this::filter);addRenderableWidget(search);}
    @Override public boolean isPauseScreen(){return false;}
    private boolean over(double x,double y,int bx,int by,int w,int h){return left+bx<=x&&left+bx+w>x&&top+by<y&&top+by+h>=y;}
    private void blit(GuiGraphics g,int x,int y,int u,int v,int w,int h){g.blit(TEXTURE,left+x,top+y,u,v,w,h,256,256);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){renderBackground(g,x,y,partial);blit(g,0,0,0,0,176,132);if(search.isFocused())blit(g,26,108,0,132,106,16);int[][] buttons={{152,18,0},{152,36,16},{152,90,32},{134,108,48},{8,108,64}};for(var button:buttons)if(over(x,y,button[0],button[1],16,16))blit(g,button[0],button[1],176,button[2],16,16);for(int i=page*8;i<Math.min(visible.size(),page*8+40);i++){int cell=i-page*8,bx=7+18*(cell%8),by=17+18*(cell/8);var recipe=visible.get(i);if(recipe.name().equals(selection))blit(g,bx,by,192,0,18,18);g.renderItem(recipe.icon(),left+bx+1,top+by+1);}var selected=all.stream().filter(r->r.name().equals(selection)).findFirst();selected.ifPresent(r->g.renderItem(r.icon(),left+152,top+72));super.render(g,x,y,partial);for(int i=page*8;i<Math.min(visible.size(),page*8+40);i++){int cell=i-page*8;if(over(x,y,7+18*(cell%8),17+18*(cell/8),18,18))g.renderComponentTooltip(font,visible.get(i).tooltip(),x,y);}if(over(x,y,151,71,18,18)&&selected.isPresent())g.renderComponentTooltip(font,selected.get().tooltip(),x,y);if(over(x,y,152,90,16,16))g.renderTooltip(font,Component.literal("Close"),x,y);if(over(x,y,134,108,16,16))g.renderTooltip(font,Component.literal("Clear search"),x,y);if(over(x,y,8,108,16,16))g.renderTooltip(font,Component.literal("Press ENTER to toggle focus"),x,y);}
    private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));}
    @Override public boolean mouseClicked(double x,double y,int button){if(button==0){if(over(x,y,152,18,16,16)){page=Math.max(0,page-1);return true;}if(over(x,y,152,36,16,16)){page=Math.min(size,page+1);return true;}if(over(x,y,134,108,16,16)){search.setValue("");search.setFocused(true);return true;}for(int i=page*8;i<Math.min(visible.size(),page*8+40);i++){int cell=i-page*8;if(over(x,y,7+18*(cell%8),17+18*(cell/8),18,18)){String next=visible.get(i).name();selection=next.equals(selection)?NULL_SELECTION:next;click();return true;}}if(over(x,y,151,71,18,18)&&!selection.equals(NULL_SELECTION)){selection=NULL_SELECTION;click();return true;}if(over(x,y,152,90,16,16)){onClose();return true;}}return super.mouseClicked(x,y,button);}
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){page=Math.clamp(page+(vertical>0?-1:vertical<0?1:0),0,size);return true;}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_ENTER){search.setFocused(!search.isFocused());return true;}if(search.isFocused()&&search.keyPressed(key,scan,modifiers))return true;switch(key){case GLFW.GLFW_KEY_UP->page--;case GLFW.GLFW_KEY_DOWN->page++;case GLFW.GLFW_KEY_PAGE_UP->page-=5;case GLFW.GLFW_KEY_PAGE_DOWN->page+=5;case GLFW.GLFW_KEY_HOME->page=0;case GLFW.GLFW_KEY_END->page=size;default->{if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}}page=Math.clamp(page,0,size);return true;}
    @Override public void removed(){if(!submitted){submitted=true;submit.accept(selection);click();}}
    @Override public void onClose(){minecraft.setScreen(previous);}
}
