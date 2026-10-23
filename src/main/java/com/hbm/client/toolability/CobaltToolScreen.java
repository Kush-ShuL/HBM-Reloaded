package com.hbm.client.toolability;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.toolability.*;
import com.hbm.registry.HbmSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Original GUIScreenToolAbility's206x76 stretched panel, disabled switches, LEDs,99 presets and exact hit boxes. */
public final class CobaltToolScreen extends Screen {
    public static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/tool/gui_tool_ability.png");
    private static final String[] AREA={"","recursion","hammer","hammer_flat","explosion"},HARVEST={"","silktouch","luck","smelter","shredder","centrifuge","crystallizer","mercury"};
    private static final int[] AREA_LEVELS={1,7,4,4,4},HARVEST_LEVELS={1,1,6,1,1,1,1,1};
    private final ItemStack tool;private final CobaltPresets config;private int left,top,hoverArea=-1,hoverHarvest=-1,hoverButton=-1;private boolean closing;
    public CobaltToolScreen(ItemStack tool){super(Component.empty());this.tool=tool;config=CobaltPresets.read(tool);}
    @Override protected void init(){super.init();font=LegacyFonts.get();left=(width-206)/2;top=(height-76)/2;}
    @Override public boolean isPauseScreen(){return false;}
    private void blit(GuiGraphics graphics,int x,int y,int u,int v,int w,int h){graphics.blit(TEXTURE,x,y,u,v,w,h,256,256);}
    private void stretched(GuiGraphics graphics,int x,int y,int u,int v,int realWidth,int originalWidth,int height,int keepLeft,int keepRight){int middle=originalWidth-keepLeft-keepRight,realMiddle=realWidth-keepLeft-keepRight;blit(graphics,x,y,u,v,keepLeft,height);for(int i=0;i<realMiddle;i+=middle)blit(graphics,x+keepLeft+i,y,u+keepLeft,v,Math.min(middle,realMiddle-i),height);blit(graphics,x+keepLeft+realMiddle,y,u+keepLeft+middle,v,keepRight,height);}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        graphics.fillGradient(0,0,width,height,0xc0101010,0xd0101010);graphics.setColor(1,1,1,1);stretched(graphics,left,top,0,0,206,186,76,74,87);
        var active=config.active();hoverArea=switches(graphics,true,active.area(),active.recursionLevel,left+15,top+25,mouseX,mouseY);hoverHarvest=switches(graphics,false,active.harvest,active.harvestLevel,left+15,top+45,mouseX,mouseY);
        number(graphics,config.current+1,left+135,top+25);number(graphics,config.presets.size(),left+169,top+25);
        hoverButton=-1;for(int i=0;i<7;i++)if(inside(mouseX,mouseY,left+120+i*11,top+11,9,9)){hoverButton=i;blit(graphics,left+120+i*11,top+11,193+i*9,0,9,9);}
        Component tooltip=null;if(hoverArea>=0)tooltip=abilityLabel(true,hoverArea,hoverArea==(active.area())?active.recursionLevel:0);else if(hoverHarvest>=0)tooltip=abilityLabel(false,hoverHarvest,hoverHarvest==active.harvest?active.harvestLevel:0);else if(hoverButton>=0)tooltip=Component.translatable("gui.hbm.tool_ability.button."+hoverButton);
        if(tooltip!=null&&!tooltip.getString().isEmpty()){int tooltipWidth=Math.max(6,font.width(tooltip)),x=left+103-tooltipWidth/2,y=top+81;stretched(graphics,x-5,y-4,0,76,tooltipWidth+10,186,15,3,3);graphics.drawString(font,tooltip,x,y,0xffffffff,false);}
    }
    private int switches(GuiGraphics graphics,boolean area,int selected,int selectedLevel,int x,int y,int mouseX,int mouseY){
        var names=area?AREA:HARVEST;var levels=area?AREA_LEVELS:HARVEST_LEVELS;int hover=-1;
        for(int i=0;i<names.length;i++){boolean available=available(area,i),chosen=i==selected;blit(graphics,x+20*i,y,32*i+(available?16:0),area?91:107,16,16);if(levels[i]>1){int level=chosen?selectedLevel+1:0;if(level>10||level<0)level=-1;blit(graphics,x+20*i+17,y+1,188+level*2,70,2,14);}boolean hovered=inside(mouseX,mouseY,x+20*i,y,16,16);if(hovered)hover=i;if(chosen)blit(graphics,x+20*i-1,y-1,220,9,18,18);else if(available&&hovered)blit(graphics,x+20*i-1,y-1,238,9,18,18);}
        return hover;
    }
    private boolean available(boolean area,int index){return area?config.available.allowsArea(index):(config.active().area()!=4||index==0)&&config.available.allowsHarvest(index);}
    private static Component abilityLabel(boolean area,int index,int level){String name=(area?AREA:HARVEST)[index];var component=name.isEmpty()?Component.empty():Component.translatable("tool.ability."+name);String extension=area?switch(index){case 1->" ("+new int[]{3,4,5,6,7,9,10}[Math.clamp(level,0,6)]+")";case 2,3->" ("+(level+1)+")";case 4->" ("+new float[]{2.5F,5,10,15}[Math.clamp(level,0,3)]+")";default->"";}:index==2?" ("+AvailableAbilities.luckPower(level)+")":"";return component.append(extension);}
    private void number(GuiGraphics graphics,int number,int x,int y){number+=100;blit(graphics,x,y,(number/10%10)*10,123,10,15);blit(graphics,x+12,y,(number%10)*10,123,10,15);}
    private static boolean inside(double mx,double my,int x,int y,int width,int height){return x<=mx&&x+width>mx&&y<=my&&y+height>my;}
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button){
        var active=config.active();if(hoverArea>=0&&available(true,hoverArea)){boolean selected=(active.area())==hoverArea;if(!selected||config.available.maximumAreaLevel(hoverArea)>0)boop();active.recursionLevel=selected?(active.recursionLevel+1)%(config.available.maximumAreaLevel(hoverArea)+1):0;active.area=hoverArea;active.recursion=hoverArea==1;active.restrict(config.available);}
        if(hoverHarvest>=0&&available(false,hoverHarvest)){boolean selected=active.harvest==hoverHarvest;int maximum=config.available.maximumHarvestLevel(hoverHarvest);if(!selected||maximum>0)boop();active.harvestLevel=selected?(active.harvestLevel+1)%(maximum+1):0;active.harvest=hoverHarvest;}
        if(hoverButton>=0){switch(hoverButton){case 0->config.reset();case 1->{if(config.presets.size()>1){config.presets.remove(config.current);config.current=Math.min(config.current,config.presets.size()-1);}}case 2->{if(config.presets.size()<99)config.presets.add(++config.current,new CobaltPresets.Preset());}case 3->config.current=0;case 4->next(false);case 5->previous(false);case 6->onClose();default->{}}minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,.5F));}
        if(!inside(mouseX,mouseY,left,top,206,76))onClose();return true;
    }
    private void boop(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(HbmSounds.get("item.techBoop"),2));}
    private void next(boolean bound){if(bound)config.current=Math.min(config.current+1,config.presets.size()-1);else config.current=(config.current+1)%config.presets.size();}
    private void previous(boolean bound){if(bound)config.current=Math.max(config.current-1,0);else config.current=(config.current+config.presets.size()-1)%config.presets.size();}
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(vertical<0)previous(true);if(vertical>0)next(true);return true;}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
    @Override public void onClose(){if(closing)return;closing=true;config.apply(tool);PacketDistributor.sendToServer(new CobaltPayloads.Configure(config.save()));CobaltToolClient.notice(config.active());minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP,config.active().isNone()?.75F:1.25F,.25F));minecraft.setScreen(null);}
}
