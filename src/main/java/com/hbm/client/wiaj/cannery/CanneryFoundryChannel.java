package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.material.*;import com.hbm.item.autogen.ScrapsItem;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CanneryFoundryChannel extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:foundry_channel",1,0);
	}

	@Override
	public String getName() {
		return "cannery.foundryChannel";
	}
	
	public CanneryBase[] seeAlso() {
		return new CanneryBase[] {
				new CanneryCrucible()
		};
	}

	@Override
	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(5, 4, 4);
		JarScript script = new JarScript(world);
		
		JarScene scene0 = new JarScene(script);
		
		/// SETUP ///
		scene0.add(new ActionSetZoom(3, 0));
		
		for(int x = world.sizeX - 1; x >= 0 ; x--) {
			for(int z = 0; z < world.sizeZ; z++) {
				scene0.add(new ActionSetBlock(x, 0, z, JarSymbols.block("minecraft:brick_block")));
			}
		}
		
		scene0.add(new ActionWait(5));
		scene0.add(new ActionSetBlock(1, 1, 2, JarSymbols.block("minecraft:brick_block")));
		scene0.add(new ActionSetBlock(2, 1, 2, JarSymbols.block("minecraft:brick_block")));
		scene0.add(new ActionSetBlock(3, 1, 2, JarSymbols.block("minecraft:brick_block")));
		scene0.add(new ActionSetBlock(3, 1, 3, JarSymbols.block("minecraft:brick_block")));

		scene0.add(new ActionSetTile(3, 1, 1, new JarTiles.Basin() {{ slots[0] = JarSymbols.item("hbm:mold",0,12); }}));
		scene0.add(new ActionCreateActor(1, new ActorTileEntity(new JarMachineActors.Foundry(), new CompoundTag() {{ putInt("x", 3); putInt("y", 1); putInt("z", 1); }})));
		scene0.add(new ActionSetBlock(3, 1, 1, JarSymbols.block("hbm:foundry_basin")));
		scene0.add(new ActionSetTile(2, 1, 1, new JarTiles.Mold() {{ slots[0] = JarSymbols.item("hbm:mold",0,2); }}));
		scene0.add(new ActionCreateActor(2, new ActorTileEntity(new JarMachineActors.Foundry(), new CompoundTag() {{ putInt("x", 2); putInt("y", 1); putInt("z", 1); }})));
		scene0.add(new ActionSetBlock(2, 1, 1, JarSymbols.block("hbm:foundry_mold")));
		
		scene0.add(new ActionWait(5));
		scene0.add(new ActionSetBlock(3, 2, 3, JarSymbols.block("minecraft:brick_block")));
		scene0.add(new ActionSetTile(1, 2, 2, new JarTiles.Mold() {{ slots[0] = JarSymbols.item("hbm:mold",0,2); }}));
		scene0.add(new ActionCreateActor(3, new ActorTileEntity(new JarMachineActors.Foundry(), new CompoundTag() {{ putInt("x", 1); putInt("y", 2); putInt("z", 2); }})));
		scene0.add(new ActionSetBlock(1, 2, 2, JarSymbols.block("hbm:foundry_mold")));
		scene0.add(new ActionSetTile(2, 2, 2, new JarTiles.Channel()));
		scene0.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:foundry_channel")));
		scene0.add(new ActionSetTile(3, 2, 2, new JarTiles.Channel()));
		scene0.add(new ActionSetBlock(3, 2, 2, JarSymbols.block("hbm:foundry_channel")));

		scene0.add(new ActionSetTile(2, 2, 1, new JarTiles.Outlet()));
		scene0.add(new ActionSetBlock(2, 2, 1, JarSymbols.block("hbm:foundry_outlet"), 2));
		scene0.add(new ActionSetTile(3, 2, 1, new JarTiles.Outlet()));
		scene0.add(new ActionSetBlock(3, 2, 1, JarSymbols.block("hbm:foundry_outlet"), 2));
		scene0.add(new ActionWait(5));
		scene0.add(new ActionSetTile(3, 3, 3, new JarTiles.Tank() {{ type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("BLOCK").q(1); }}));
		scene0.add(new ActionSetBlock(3, 3, 3, JarSymbols.block("hbm:foundry_tank")));
		scene0.add(new ActionSetTile(3, 3, 2, new JarTiles.Outlet()));
		scene0.add(new ActionSetBlock(3, 3, 2, JarSymbols.block("hbm:foundry_outlet"), 2));
		
		scene0.add(new ActionWait(10));
		
		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -25, new Object[][] {{JarSymbols.text("cannery.foundryChannel.0")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), -5, -40, new Object[][] {{JarSymbols.text("cannery.foundryChannel.1")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.TOP)));

		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionSetTile(3, 2, 2, new JarTiles.Channel() {{ type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("INGOT").q(1); }}));
		
		scene0.add(new ActionWait(10));
		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -25, new Object[][] {{JarSymbols.text("cannery.foundryChannel.2")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(0));
		
		for(int i = 0; i < 60; i++) {
			final int j = i;
			scene0.add(new ActionSetTile(3, 1, 1, new JarTiles.Basin() {{ slots[0] = JarSymbols.item("hbm:mold",0,12); type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("BLOCK").q(j, 60); }}));
			scene0.add(new ActionWait(1));
		}
		
		JarScene scene1 = new JarScene(script);
		
		scene1.add(new ActionWait(10));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -25, new Object[][] {{JarSymbols.text("cannery.foundryChannel.3")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(0));

		scene1.add(new ActionWait(10));
		scene1.add(new ActionSetTile(2, 2, 2, new JarTiles.Channel() {{ type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("INGOT").q(1); }}));
		scene1.add(new ActionWait(10));
		
		for(int i = 0; i < 60; i++) {
			final int j = i;
			scene1.add(new ActionSetTile(2, 1, 1, new JarTiles.Mold() {{ slots[0] = JarSymbols.item("hbm:mold",0,2); type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("INGOT").q(j, 60); }}));
			scene1.add(new ActionSetTile(1, 2, 2, new JarTiles.Mold() {{ slots[0] = JarSymbols.item("hbm:mold",0,2); type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("INGOT").q(j, 60); }}));
			scene1.add(new ActionWait(1));
		}
		
		scene1.add(new ActionWait(2));
		scene1.add(new ActionSetTile(3, 3, 3, new JarTiles.Tank()));
		scene1.add(new ActionWait(20));
		
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -25, new Object[][] {{JarSymbols.text("cannery.foundryChannel.4")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		scene1.add(new ActionWait(40));
		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionWait(10));

		scene1.add(new ActionSetTile(2, 2, 2, new JarTiles.Channel()));
		scene1.add(new ActionSetTile(3, 2, 2, new JarTiles.Channel()));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -25, new Object[][] {{JarSymbols.item("minecraft:iron_shovel",1,0), " -> ", ScrapsItem.create(new MaterialStack(HbmMaterials.bySymbol("MAT_GOLD"), 1))}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		scene1.add(new ActionWait(40));
		scene1.add(new ActionRemoveActor(0));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionSetTile(2, 1, 1, new JarTiles.Mold() {{ slots[1] = JarSymbols.item("minecraft:gold_ingot",1,0); }}));
		scene1.add(new ActionSetTile(1, 2, 2, new JarTiles.Mold() {{ slots[1] = JarSymbols.item("minecraft:gold_ingot",1,0); }}));
		scene1.add(new ActionSetTile(3, 1, 1, new JarTiles.Basin() {{ slots[1] = JarSymbols.item("minecraft:gold_block",1,0); }}));

		script.addScene(scene0);
		script.addScene(scene1);
		
		return script;
	}
	
}
