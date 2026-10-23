package com.hbm.client.ammopress;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.ammopress.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Original176x200 selector: twelve icons, one-column paging,25char search, exact half-alpha input ghosts. */
public final class AmmoPressScreen extends LegacyContainerScreen<AmmoPressMenu> {
 private final List<AmmoPressRecipe> recipes=new ArrayList<>();private EditBox search;private int index,size,selection;
 public AmmoPressScreen(AmmoPressMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_ammo_press.png"),176,200);selection=menu.machine instanceof AmmoPressEntity machine?machine.selectedRecipe:-1;}
 private List<AmmoPressRecipe> all(){return minecraft==null||minecraft.level==null?List.of():AmmoPressRecipes.recipes(minecraft.level.getRecipeManager());}
 private void filter(String text){String term=text.toLowerCase(Locale.US);recipes.clear();for(var recipe:all())if(term.isEmpty()||recipe.output().getHoverName().getString().toLowerCase(Locale.US).contains(term))recipes.add(recipe);index=0;size=Math.max(0,(int)Math.ceil((recipes.size()-12)/3D));}
 @Override protected void init(){super.init();search=new EditBox(font,leftPos+10,topPos+75,66,12,Component.empty());search.setTextColor(-1);search.setTextColorUneditable(-1);search.setBordered(false);search.setMaxLength(25);search.setResponder(this::filter);addRenderableWidget(search);filter("");}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,0xffffff,false);g.drawString(font,playerInventoryTitle,8,106,4210752,false);}
 private void click(){minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,1F));}
 private boolean inside(double mx,double my,int x,int y,int width,int height){return leftPos+x<=mx&&mx<leftPos+x+width&&topPos+y<my&&my<=topPos+y+height;}
 @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my){super.renderBg(g,partial,mx,my);if(inside(mx,my,7,17,9,54))drawTexturedModalRect(g,leftPos+7,topPos+17,176,0,9,54);if(inside(mx,my,88,17,9,54))drawTexturedModalRect(g,leftPos+88,topPos+17,185,0,9,54);if(search.isFocused())drawTexturedModalRect(g,leftPos+8,topPos+72,176,54,70,16);var all=all();for(int i=index*3;i<index*3+12&&i<recipes.size();i++){int at=i-index*3,x=leftPos+17+18*(at/3),y=topPos+18+18*(at%3);var recipe=recipes.get(i);var out=recipe.output();g.renderItem(out,x,y);g.pose().pushPose();g.pose().translate(0,0,300);drawTexturedModalRect(g,x-1,y-1,selection==all.indexOf(recipe)?194:212,0,18,18);g.pose().popPose();g.pose().pushPose();g.pose().translate(x+8,y+8,400);g.pose().scale(.5F,.5F,1);g.renderItemDecorations(font,out,0,0,Integer.toString(out.getCount()));g.pose().popPose();}if(selection>=0&&selection<all.size()){var recipe=all.get(selection);for(int i=0;i<9;i++){var input=recipe.cell(i);if(input==null||!menu.machine.getItem(i).isEmpty())continue;var examples=input.examples();if(examples.isEmpty())continue;var item=examples.get((int)(Math.abs(System.currentTimeMillis()/1000)%examples.size()));int x=leftPos+116+18*(i%3),y=topPos+18+18*(i/3);g.renderItem(item,x,y);g.renderItemDecorations(font,item,x,y);g.pose().pushPose();g.pose().translate(0,0,300);g.setColor(1,1,1,.5F);drawTexturedModalRect(g,x,y,116+18*(i%3),18+18*(i/3),18,18);g.setColor(1,1,1,1);g.pose().popPose();}}}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);for(var slot:menu.slots)if(slot.hasItem()&&isHovering(slot.x,slot.y,16,16,x,y))return;for(int i=index*3;i<index*3+12&&i<recipes.size();i++){int at=i-index*3;if(inside(x,y,16+18*(at/3),17+18*(at%3),18,18)){g.renderTooltip(font,recipes.get(i).output(),x,y);return;}}}
 @Override public boolean mouseClicked(double x,double y,int button){search.setFocused(inside(x,y,10,75,66,12));if(inside(x,y,7,17,9,54)){click();if(index>0)index--;return true;}if(inside(x,y,88,17,9,54)){click();if(index<size)index++;return true;}var all=all();for(int i=index*3;i<index*3+12&&i<recipes.size();i++){int at=i-index*3;if(inside(x,y,16+18*(at/3),17+18*(at%3),18,18)){int selected=all.indexOf(recipes.get(i));selection=selection==selected?-1:selected;PacketDistributor.sendToServer(new AmmoPressControl(menu.containerId,selection));click();return true;}}return super.mouseClicked(x,y,button);}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(inside(x,y,0,0,imageWidth,imageHeight)&&GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(),0)==GLFW.GLFW_RELEASE&&GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(),1)==GLFW.GLFW_RELEASE){boolean onSlot=false;for(var slot:menu.slots)if(isHovering(slot.x,slot.y,16,16,x,y)){onSlot=true;break;}if(!onSlot){if(vertical>0&&index>0)index--;if(vertical<0&&index<size)index++;return true;}}return super.mouseScrolled(x,y,horizontal,vertical);}
 @Override public boolean keyPressed(int key,int scan,int mods){if(search.isFocused()&&key!=GLFW.GLFW_KEY_ESCAPE){search.keyPressed(key,scan,mods);return true;}return super.keyPressed(key,scan,mods);}
}
