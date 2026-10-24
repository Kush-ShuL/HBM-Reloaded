package com.hbm.client.qmaw;
import com.hbm.client.qmaw.components.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
/** Original340x224 text manual, parser, history, links, composition, slider and final-page surprise. */
public final class GuiQMAW extends Screen {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/gui_wiki.png"),THE_MAN=ResourceLocation.parse("hbm:textures/gui/gui_wiki_flix.png");
 public String title,qmawID;public ItemStack icon;public List<List<ManualElement>> lines=new ArrayList<>();
 public final List<String> back=new ArrayList<>(),forward=new ArrayList<>();
 private final int xSize=340,ySize=224;private int guiLeft,guiTop,scrollProgress;private boolean dragging;
 public static final String EN_US="en_us";
 public GuiQMAW(QuickManualAndWiki page){super(Component.literal(page.name));qmawID=page.name;parseQMAW(page);}

	/** Chinese line-break opportunities, not added spaces or a replacement font. Link tokens never enter this method. */
	private static List<String> cjkTextParts(String text) {
		if(text.codePoints().noneMatch(code -> Character.UnicodeScript.of(code) == Character.UnicodeScript.HAN)) return List.of(text);
		StringBuilder visible = new StringBuilder();
		List<Integer> ends = new ArrayList<>();
		for(int i = 0; i < text.length();) {
			if(text.charAt(i) == '§' && i + 1 < text.length()) { i += 2; continue; }
			visible.append(text.charAt(i++)); ends.add(i);
		}
		java.text.BreakIterator iterator = java.text.BreakIterator.getLineInstance(Locale.SIMPLIFIED_CHINESE);
		iterator.setText(visible.toString());
		List<String> parts = new ArrayList<>();
		int start = 0; String formatting = "";
		for(int boundary = iterator.next(); boundary != java.text.BreakIterator.DONE; boundary = iterator.next()) {
			if(boundary < visible.length()) {
				char before = visible.charAt(boundary - 1), after = visible.charAt(boundary);
				// Keep command/number/parameter ASCII runs intact; avoid a closing punctuation mark at the start of a line.
				if((before >= '!' && before <= '~' && after >= '!' && after <= '~')
						|| "。，、；：？！）］｝》〉」』】〕〗”’％‰…".indexOf(after) >= 0
						|| "（［｛《〈「『【〔〖“‘".indexOf(before) >= 0) continue;
			}
			int end = boundary == visible.length() ? text.length() : ends.get(boundary - 1);
			String raw = text.substring(start, end);
			parts.add(formatting + raw);
			formatting = continuedFormatting(formatting, raw);
			start = end;
		}
		if(start < text.length()) parts.add(formatting + text.substring(start));
		return parts.isEmpty() ? List.of(text) : parts;
	}
	private static String continuedFormatting(String formatting, String text) {
		for(int i = 0; i + 1 < text.length(); i++) if(text.charAt(i) == '§') {
			char code = Character.toLowerCase(text.charAt(++i));
			if(code == 'r') formatting = "";
			else if("0123456789abcdef".indexOf(code) >= 0) formatting = "§" + code;
			else if("klmno".indexOf(code) >= 0 && formatting.indexOf("§" + code) < 0) formatting += "§" + code;
		}
		return formatting;
	}
	/** All existing manual locales use native lang; unmigrated resource pages retain literal fallback. */
	private static String localized(QuickManualAndWiki page, Map<String,String> literals, String field, String locale) {
		if(page.languageKey != null) {
			String key = page.languageKey + "." + field;
			var language = net.minecraft.locale.Language.getInstance();
			if(language.has(key)) return language.getOrDefault(key);
		}
		String value = literals.get(locale);
		return value != null ? value : literals.get(EN_US);
	}
	protected String compose(String contents) {
		var lang = Minecraft.getInstance().getLanguageManager();
		String langCode = lang.getSelected();
		int recursionBrake = 100;
		
		while(contents.contains("{{") && recursionBrake > 0) {
			
			int begin = contents.indexOf("{{");
			int end = contents.indexOf("}}");
			if(end < begin) break;
			
			String composeTag = contents.substring(begin + 2, end);
			QuickManualAndWiki qmaw = QMAWLoader.qmaw.get(composeTag);
			String substitute = composeTag;
			
			if(qmaw != null) {
				String localized = localized(qmaw, qmaw.contents, "content", langCode);
				if(localized != null) substitute = localized;
			}
			
			contents = contents.replace("{{" + composeTag + "}}", substitute);
			recursionBrake--;
		}
		
		return contents;
	}
	protected void parseQMAW(QuickManualAndWiki qmaw) {
		var lang = Minecraft.getInstance().getLanguageManager();

		this.title = localized(qmaw, qmaw.title, "title", lang.getSelected());
		if(title == null) this.title = "Missing Localization!";

		this.icon = qmaw.icon;

		String toParse = localized(qmaw, qmaw.contents, "content", lang.getSelected());
		if(toParse == null) toParse = "Missing Localization!";
		
		toParse = compose(toParse);

		int maxLineLength = xSize - 29;
		String prevToParse = "" + toParse;
		int maxIterations = 1000;
		int currentLineWidth = 0;

		while(!toParse.isEmpty() && maxIterations > 0) {
			if(this.lines.isEmpty()) this.lines.add(new ArrayList());
			List<ManualElement> currentLine = this.lines.get(this.lines.size() - 1);

			toParse = toParse.trim();

			maxIterations--;

			if(toParse.startsWith("<br>")) {
				toParse = toParse.substring(4);
				currentLine = new ArrayList();
				this.lines.add(currentLine);
				currentLineWidth = 0;
				continue;
			}

			// handle links
			if(toParse.startsWith("[[")) {
				int end = toParse.indexOf("]]");
				if(end != -1) {
					String link = toParse.substring(2, end);
					toParse = toParse.substring(end + 2);

					int pipe = link.indexOf("|");
					QComponentLink linkComponent;

					String suffix = toParse.startsWith(" ") ? " " : "";

					if(pipe == -1) {
						linkComponent = new QComponentLink(link, link + suffix);
					} else {
						linkComponent = new QComponentLink(link.substring(pipe + 1, link.length()), link.substring(0, pipe) + suffix);
					}

					// append to current line
					int width = linkComponent.getWidth();
					if(width + currentLineWidth <= maxLineLength) {
						currentLine.add(linkComponent);
						currentLineWidth += width;
					// new line
					} else {
						currentLine = new ArrayList();
						this.lines.add(currentLine);
						currentLine.add(linkComponent);
						currentLineWidth = width;
					}

					prevToParse = "" + toParse;
					continue;
				}
			}

			// handle standard text
			int delimit = toParse.length();

			int spaceIndex = toParse.indexOf(" ");
			if(spaceIndex != -1) delimit = Math.min(delimit, spaceIndex);
			int linkIndex = toParse.indexOf("[[");
			if(linkIndex != -1) delimit = Math.min(delimit, linkIndex);
			int brIndex = toParse.indexOf("<br>");
			if(brIndex != -1) delimit = Math.min(delimit, brIndex);

			if(delimit > 0) {
				String plain = toParse.substring(0, delimit) + (spaceIndex == delimit ? " " : "");
				toParse = toParse.substring(delimit);
				// Source words remain intact in other languages. Chinese has real line-break opportunities without spaces.
				List<String> parts = lang.getSelected().startsWith("zh") ? cjkTextParts(plain) : List.of(plain);
				for(String part : parts) {
					QComponentText textComponent = new QComponentText(part);
					int width = textComponent.getWidth();
					if(width + currentLineWidth <= maxLineLength) {
						currentLine.add(textComponent);
						currentLineWidth += width;
					} else {
						currentLine = new ArrayList();
						this.lines.add(currentLine);
						currentLine.add(textComponent);
						currentLineWidth = width;
					}
				}

				prevToParse = "" + toParse;
				continue;
			}

			if(toParse.equals(prevToParse)) break;
			prevToParse = "" + toParse;
		}
	}
 @Override protected void init(){guiLeft=(width-xSize)/2;guiTop=(height-ySize)/2;}
 public static void clickSound(){Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));}
 public void back(){if(back.isEmpty())return;var page=QMAWLoader.qmaw.get(back.getLast());if(page==null)return;clickSound();var next=new GuiQMAW(page);next.back.addAll(back);next.back.removeLast();next.forward.addAll(forward);next.forward.add(qmawID);minecraft.setScreen(next);}
 public void forward(){if(forward.isEmpty())return;var page=QMAWLoader.qmaw.get(forward.getLast());if(page==null)return;clickSound();var next=new GuiQMAW(page);next.back.addAll(back);next.back.add(qmawID);next.forward.addAll(forward);next.forward.removeLast();minecraft.setScreen(next);}
 private int slider(){return 25+(lines.size()<=1?0:(int)((double)scrollProgress/(lines.size()-1)*180));}
 private boolean end(){return lines.size()>1&&scrollProgress==lines.size()-1;}
 private void region(GuiGraphics g,int x,int y,int u,int v,int width,int height){g.blit(TEXTURE,guiLeft+x,guiTop+y,u,v,width,height,256,256);}
 @Override public void render(GuiGraphics g,int mx,int my,float partial){
  if(dragging)drag(my);g.fill(0,0,width,height,0xe0000000);region(g,0,0,0,0,170,ySize);region(g,170,0,52,0,30,ySize);region(g,200,0,52,0,140,ySize);
  if(!back.isEmpty())region(g,3,3,204,0,18,18);if(!forward.isEmpty())region(g,21,3,222,0,18,18);region(g,xSize-15,slider(),192,0,12,16);
  if(end()){g.blit(THE_MAN,guiLeft+60,guiTop+ySize-84,0,0,80,80,256,256);g.blit(THE_MAN,guiLeft+140,guiTop+ySize-60,0,80,77,39,256,256);}
  else{int x=guiLeft+7,y=guiTop+30,lineNum=0;for(var line:lines){if(++lineNum<=scrollProgress)continue;int maxHeight=line.stream().mapToInt(ManualElement::getHeight).max().orElse(0);if(y+maxHeight>guiTop+219)break;if(line.isEmpty())y+=font.lineHeight;int inset=0;for(var element:line){int ex=x+inset,ey=y+(maxHeight-element.getHeight())/2;boolean hover=ex<=mx&&ex+element.getWidth()>mx&&ey<my&&ey+element.getHeight()>=my;element.render(g,hover,ex,ey,mx,my,this);inset+=element.getWidth();}y+=maxHeight+2;}}
  int x=43,y=4;if(icon!=null&&!icon.isEmpty()){g.renderItem(icon,guiLeft+x,guiTop+y);g.renderItemDecorations(font,icon,guiLeft+x,guiTop+y);x+=18;y+=(16-font.lineHeight)/2;}g.drawString(font,title,guiLeft+x,guiTop+y+1,0xffffff,false);
 }
 private void drag(double y){double progress=Mth.clamp((y-(guiTop+33))/175D,0D,1D);scrollProgress=Mth.clamp((int)Math.round(Math.max(0,lines.size()-1)*progress),0,Math.max(0,lines.size()-1));}
 @Override public boolean mouseClicked(double x,double y,int button){
  if(inside(x,y,3,3,18,18)){back();return true;}if(inside(x,y,21,3,18,18)){forward();return true;}
  if(button==0&&inside(x,y,xSize-15,25,12,191)){dragging=true;drag(y);return true;}
  if(end()){if(inside(x,y,60,ySize-84,80,80))minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.parse("hbm:alarm.singer")),1F));return true;}
  if(button==0){int px=guiLeft+7,py=guiTop+30,lineNum=0;for(var line:lines){if(++lineNum<=scrollProgress)continue;int h=line.stream().mapToInt(ManualElement::getHeight).max().orElse(0);if(py+h>guiTop+219)break;if(line.isEmpty())py+=font.lineHeight;int inset=0;for(var element:line){int ex=px+inset,ey=py+(h-element.getHeight())/2;if(ex<=x&&ex+element.getWidth()>x&&ey<y&&ey+element.getHeight()>=y){element.onClick(this);return true;}inset+=element.getWidth();}py+=h+2;}}
  return super.mouseClicked(x,y,button);
 }
 private boolean inside(double x,double y,int dx,int dy,int w,int h){return x>=guiLeft+dx&&x<guiLeft+dx+w&&y>guiTop+dy&&y<=guiTop+dy+h;}
 @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){if(dragging&&button==0){drag(y);return true;}return super.mouseDragged(x,y,button,dx,dy);}
 @Override public boolean mouseReleased(double x,double y,int button){dragging=false;return super.mouseReleased(x,y,button);}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){scrollProgress=Mth.clamp(scrollProgress+(vertical>0?-1:vertical<0?1:0),0,Math.max(0,lines.size()-1));return true;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_LEFT){back();return true;}if(key==GLFW.GLFW_KEY_RIGHT){forward();return true;}if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
}
