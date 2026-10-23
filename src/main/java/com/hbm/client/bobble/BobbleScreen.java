package com.hbm.client.bobble;
import com.hbm.block.bobble.*;
import com.hbm.registry.HbmSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
/** Source 300x150 green panel and exact spacing/anagram, with normal user-selected font and no blur. */
public final class BobbleScreen extends Screen {
    private final BobbleBlockEntity bobble;
    BobbleScreen(BobbleBlockEntity bobble){super(Component.translatable("gui.hbm.bobblehead.title"));this.bobble=bobble;}
    @Override protected void init(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(HbmSounds.get("block.bobble"),1F));}
    private String text(String field,String fallback){return Component.translatableWithFallback(bobble.type.key(field),fallback,System.getProperty("user.name")).getString();}
    private void centered(GuiGraphics graphics,String text,int y,int color){graphics.drawString(font,text,(width-font.width(text))/2,y,color,true);}
    @Override public void renderBackground(GuiGraphics graphics,int mouseX,int mouseY,float partial){graphics.fillGradient(0,0,width,height,0xc0101010,0xd0101010);}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        renderBackground(graphics,mouseX,mouseY,partial);int left=(width-300)/2,top=(height-150)/2;
        graphics.fill(left,top,left+300,top+150,0xcc003300);int y=top+10;
        centered(graphics,Component.translatable("gui.hbm.bobblehead.title").getString(),y,0x00ff00);y+=10;
        String name=text("name",bobble.type.name);if(bobble.type==BobbleType.MELLOW)name=anagramIt(name,"GEORGEWILLIAMPATON");centered(graphics,name,y,0x009900);y+=20;
        if(bobble.type.contribution!=null){centered(graphics,Component.translatable("gui.hbm.bobblehead.contribution").getString(),y,0x00ff00);y+=10;for(String line:text("contribution",bobble.type.contribution).split("\\$")){centered(graphics,line,y,0x009900);y+=10;}y+=10;}
        if(bobble.type.inscription!=null){centered(graphics,Component.translatable("gui.hbm.bobblehead.inscription").getString(),y,0x00ff00);y+=10;for(String line:text("inscription",bobble.type.inscription).split("\\$")){centered(graphics,line,y,0x009900);y+=10;}}
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==256||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
    @Override public boolean isPauseScreen(){return false;}
    private static final class Pair<K,V>{K key;V value;Pair(K key,V value){this.key=key;this.value=value;}}
	// Animates the letters (from -> to) back and forth over 1.5 seconds
	private String anagramIt(String from, String to) {
		double t = Math.sin((double)System.currentTimeMillis() / 1500.0) * 0.75 + 0.5;

		char[] lettersFrom = from.toCharArray();
		char[] lettersTo = to.toCharArray();
		boolean[] hasPairedLetter = new boolean[lettersFrom.length];
		List<Pair<Double, Character>> letterTargets = new ArrayList<Pair<Double, Character>>();

		for(int i = 0; i < lettersFrom.length; i++) {
			char letterFrom = lettersFrom[i];
			for(int o = 0; o < lettersTo.length; o++) {
				char letterTo = lettersTo[o];
				if(letterFrom == letterTo && !hasPairedLetter[o]) {
					double v = lerp((double)i, (double)o, t);
					letterTargets.add(new Pair<Double,Character>(v, lettersFrom[i]));
					hasPairedLetter[o] = true;
					break;
				}
			}
		}

		for(int i = 0; i < letterTargets.size(); i++) {
			for (int j = i + 1; j < letterTargets.size(); j++) {
				if (letterTargets.get(i).key > letterTargets.get(j).key) {
					Pair<Double, Character> temp = letterTargets.get(i);
					letterTargets.set(i, letterTargets.get(j));
					letterTargets.set(j, temp);
				}
			}
		}

		String anagrammedText = "";
		for(Pair<Double, Character> in : letterTargets) {
			anagrammedText += in.value;
		}

		return anagrammedText;
	}

	private double lerp(double a, double b, double t) {
		t = Math.max(Math.min(t, 1), 0);
		return a * (1 - t) + b * t;
	}
}
