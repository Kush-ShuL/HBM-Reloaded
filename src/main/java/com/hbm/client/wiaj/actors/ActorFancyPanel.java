package com.hbm.client.wiaj.actors;
import com.hbm.client.wiaj.*;
import java.util.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
public class ActorFancyPanel implements ISpecialActor {
	
	static final ResourceLocation guiUtil =  ResourceLocation.parse("hbm:textures/gui/gui_utility.png");

	List<Object[]> lines = new ArrayList();
	net.minecraft.client.gui.GuiGraphics graphics;
	
	Font font;
	
	int x;
	int y;
	
	boolean consistentHeight = false;
	int lineDist = 2;
	int tallestElement = 0;
	static final int STACK_HEIGHT = 18;
	
	Orientation o = Orientation.CENTER;
	int colorBrighter = 0xFFCCCCCC;
	int colorDarker = 0xFF7D7D7D;
	int colorFrame = 0xFFA0A0A0;
	int colorBg = 0xFF302E36;
	
	public ActorFancyPanel(Font font, int x, int y, Object[][] raw, int autowrap) {
		
		this.font = font;
		this.x = x;
		this.y = y;
		
		if(autowrap <= 0) { //if autowrap is off, just add the lines 1:1
			for(Object[] o : raw) {
				lines.add(o);
			}
		} else {
			
			for(Object[] line : raw) {
				
				if(line.length == 1 && line[0] instanceof String) { //auto wrap can only apply to text-only lines
					List<String> frags = font.getSplitter().splitLines((String)line[0],autowrap,net.minecraft.network.chat.Style.EMPTY).stream().map(net.minecraft.network.chat.FormattedText::getString).toList();
					for(String s : frags) {
						lines.add(new Object[] { s });
					}
				} else {
					lines.add(line);
				}
			}
		}
	}
	
	public ActorFancyPanel enforceConsistentHeight() {
		this.consistentHeight = true;
		return this;
	}
	
	public ActorFancyPanel setLineDist(int dist) {
		this.lineDist = dist;
		return this;
	}
	
	public ActorFancyPanel setOrientation(Orientation o) {
		this.o = o;
		return this;
	}
	
	public ActorFancyPanel setColors(int brighter, int frame, int darker, int background) {
		this.colorBrighter = brighter;
		this.colorFrame = frame;
		this.colorDarker = darker;
		this.colorBg = background;
		return this;
	}
	
	public ActorFancyPanel setColors(int[] colors) {
		return setColors(colors[0], colors[1], colors[2], colors[3]);
	}
	
	public int getTallestElement() {
		if(this.tallestElement > 0) {
			return this.tallestElement;
		}
		
		for(Object[] line : this.lines) {
			for(Object element : line) {
				
				int height = getElementHeight(element);
				
				if(height > this.tallestElement) {
					this.tallestElement = height;
				}
			}
		}
		
		return this.tallestElement;
	}
	
	public int getElementHeight(Object element) {
		
		if(element instanceof String) {
			return this.font.lineHeight;
		}
		
		if(element instanceof ItemStack) {
			return STACK_HEIGHT;
		}
		
		if(element instanceof Object[]) {
			Object[] scaledStack = (Object[]) element; //[0] ItemStack, [1] double
			return (int) Math.ceil(STACK_HEIGHT * (double) scaledStack[1]);
		}
		
		return 0;
	}
	
	public int getElementWidth(Object element) {
		
		if(element instanceof String) {
			return this.font.width((String) element);
		}
		
		if(element instanceof ItemStack) {
			return STACK_HEIGHT;
		}
		
		if(element instanceof Object[]) {
			Object[] scaledStack = (Object[]) element; //[0] ItemStack, [1] double
			return (int) Math.ceil(STACK_HEIGHT * (double) scaledStack[1]);
		}
		
		return 0;
	}
	
	int blockHeight = 0;
	private int getBlockHeight() {
		
		if(this.blockHeight > 0) {
			return this.blockHeight;
		}
		
		for(Object[] line : this.lines) {
			
			if(this.blockHeight > 0) {
				this.blockHeight += this.lineDist;
			}
			
			int lineHeight = this.font.lineHeight;
			
			if(this.consistentHeight) {
				lineHeight = Math.max(lineHeight, getTallestElement());
			} else {
				
				for(Object o : line) {
					lineHeight = Math.max(lineHeight, getElementHeight(o));
				}
			}
			
			this.blockHeight += lineHeight;
		}
		
		return this.blockHeight;
	}
	
	int blockWidth = 0;
	private int getBlockWidth() {
		
		if(this.blockWidth > 0) {
			return this.blockWidth;
		}
		
		for(Object[] line : this.lines) {
			
			int lineWidth = 0;
			
			for(Object o : line) {
				
				if(lineWidth > 0) {
					lineWidth += 2;
				}
				
				lineWidth += getElementWidth(o);
			}
			
			if(lineWidth > this.blockWidth) {
				this.blockWidth = lineWidth;
			}
		}
		
		return this.blockWidth;
	}

	@Override
	public void drawForegroundComponent(GuiGraphics graphics,int w, int h, int ticks, float interp) {
        this.graphics=graphics; graphics.pose().pushPose();graphics.pose().translate(0,0,300);
		
		int height = this.getBlockHeight();
		int width = this.getBlockWidth();
		
		int posX = w / 2 + x;
		int posY = h / 2 + y;
		
		switch(o) {
		case TOP:
			posX -= width / 2;
			posY += 15;
			break;
		case BOTTOM:
			posX -= width / 2;
			posY -= height + 15;
			break;
		case LEFT:
			posX += 15;
			posY -= height / 2;
			break;
		case RIGHT:
			posX -= width + 15;
			posY -= height / 2;
			break;
		case CENTER:
			posX -= width / 2;
			posY -= height / 2;
			break;
		}

		
		
		this.drawRect(posX - 5, posY - 5, posX + width + 5, posY + height + 5, colorFrame);
		
		this.drawRect(posX - 5, posY - 5, posX - 4, posY + height + 4, colorBrighter);
		this.drawRect(posX - 5, posY - 5, posX + width + 4, posY - 4, colorBrighter);
		this.drawRect(posX + width + 2, posY - 2, posX + width + 3, posY + height + 3, colorBrighter);
		this.drawRect(posX - 2, posY + height + 2, posX + width + 3, posY + height + 3, colorBrighter);
		
		this.drawRect(posX - 3, posY - 3, posX - 2, posY + height + 2, colorDarker);
		this.drawRect(posX - 3, posY - 3, posX + width + 2, posY - 2, colorDarker);
		this.drawRect(posX + width + 4, posY - 4, posX + width + 5, posY + height + 5, colorDarker);
		this.drawRect(posX - 4, posY + height + 4, posX + width + 5, posY + height + 5, colorDarker);
		
		this.drawRect(posX - 2, posY - 2, posX + width + 2, posY + height + 2, colorBg);

		
		switch(o) {
		case TOP:
			this.drawArrow(posX + width / 2 - 7, posY - 15, 40, 14, 14, 10);
			break;
		case BOTTOM:
			this.drawArrow(posX + width / 2 - 7, posY + height + 5, 54, 14, 14, 10);
			break;
		case LEFT:
			this.drawArrow(posX - 15, posY + height / 2 - 7, 40, 0, 10, 14);
			break;
		case RIGHT:
			this.drawArrow(posX + width + 5, posY + height / 2 - 7, 50, 0, 10, 14);
			break;
		case CENTER: break;
		}
		
		int offsetY = 0;
		
		for(Object[] line : this.lines) {
			
			if(offsetY > 0) offsetY+= this.lineDist;
			int lineHeight = 0;
			for(Object element : line) lineHeight = Math.max(lineHeight, this.getElementHeight(element));
			
			int indent = 0;
			for(Object element : line) {
				
				if(indent > 0) {
					indent += 2;
				}
				
				drawElement(posX + indent, posY + offsetY + lineHeight / 2, element);
				indent += getElementWidth(element);
			}
			
			offsetY += lineHeight;
		}

        graphics.pose().popPose();
	}
	
	private void drawArrow(int posX, int posY, int sourceX, int sourceY, int sizeX, int sizeY) {
		this.drawTexturedModalRect(posX, posY, sourceX + 28 * 0, sourceY, sizeX, sizeY, this.colorBrighter);
		this.drawTexturedModalRect(posX, posY, sourceX + 28 * 1, sourceY, sizeX, sizeY, this.colorFrame);
		this.drawTexturedModalRect(posX, posY, sourceX + 28 * 2, sourceY, sizeX, sizeY, this.colorDarker);
		this.drawTexturedModalRect(posX, posY, sourceX + 28 * 3, sourceY, sizeX, sizeY, this.colorBg);
	}
	
	private void drawElement(int x, int y, Object element) {
		
		if(element instanceof String) {
			String text = (String) element;
			graphics.drawString(font,text,x,y-font.lineHeight/2,0xffffff,false);
			
		} else if(element instanceof ItemStack) {
			
			ItemStack stack = (ItemStack) element;

			if(!stack.isEmpty()&&stack.getCount()==0) {
				this.drawGradientRect(x - 1, y - 1 - 8, x + 17, y + 17, 0xffff0000, 0xffff0000);
				this.drawGradientRect(x, y - 8, x + 16, y + 16, 0xffb0b0b0, 0xffb0b0b0);
			}
            graphics.renderItem(stack,x,y-8);
            graphics.renderItemDecorations(font,stack,x,y-8);
		} //TODO: scaled stacks
	}
    @Override public void drawBackgroundComponent(JarRenderContext render,WorldInAJar world,int ticks,float interp){}
    @Override public void updateActor(JarScene scene){}
    @Override public void setActorData(CompoundTag data){}
    @Override public void setDataPoint(String tag,Object value){}
    protected void drawRect(int x0,int y0,int x1,int y1,int color){graphics.fill(x0,y0,x1,y1,color);}
    protected void drawGradientRect(int x0,int y0,int x1,int y1,int first,int second){graphics.fillGradient(x0,y0,x1,y1,first,second);}
    public void drawTexturedModalRect(int x,int y,int u,int v,int width,int height){drawTexturedModalRect(x,y,u,v,width,height,0xffffff);}
    public void drawTexturedModalRect(int x,int y,int u,int v,int width,int height,int color){graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,(color>>24&255)/255F);graphics.blit(guiUtil,x,y,u,v,width,height,256,256);graphics.setColor(1,1,1,1);}
    public enum Orientation{TOP,BOTTOM,LEFT,RIGHT,CENTER}
}
