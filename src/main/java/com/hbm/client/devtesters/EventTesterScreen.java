package com.hbm.client.devtesters;
import com.hbm.block.devtesters.EventTesterMenu;
import com.hbm.client.gui.LegacyContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
/** Source176x222 image/48slot window/no foreground labels; asymmetric92drag/91knobtravel and bound43 retained. */
public final class EventTesterScreen extends LegacyContainerScreen<EventTesterMenu> {
    private boolean dragging;
    public EventTesterScreen(EventTesterMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_test_storage.png"),176,222);}
    private void scroll(int row){if(minecraft==null||minecraft.player==null||minecraft.player.isSpectator())return;int next=Mth.clamp(row,0,EventTesterMenu.SCROLL_BOUNDS);if(next==menu.scroll())return;menu.predictScroll(next);if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,next);}
    private void drag(double y){int offset=Mth.clamp((int)y-topPos-25,0,92);scroll((int)(EventTesterMenu.SCROLL_BOUNDS*((double)offset/92D)));}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){super.renderBg(graphics,partial,mouseX,mouseY);int y=18+(int)((double)menu.scroll()/EventTesterMenu.SCROLL_BOUNDS*91D);drawLegacyRegion(graphics,154,y,dragging?188:176,0,12,15);}
    @Override public boolean mouseClicked(double x,double y,int button){if(button==0&&leftPos+153<=x&&leftPos+167>x&&topPos+17<y&&topPos+125>=y){dragging=true;drag(y);return true;}return super.mouseClicked(x,y,button);}
    @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){if(button==0&&dragging){drag(y);return true;}return super.mouseDragged(x,y,button,dx,dy);}
    @Override public boolean mouseReleased(double x,double y,int button){if(button==0)dragging=false;return super.mouseReleased(x,y,button);}
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(vertical!=0){scroll(menu.scroll()-(vertical>0?1:-1));return true;}return super.mouseScrolled(x,y,horizontal,vertical);}
}
