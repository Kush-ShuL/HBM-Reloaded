package com.hbm.client.wiaj.actions;

import com.hbm.client.wiaj.JarScene;
import com.hbm.client.wiaj.WorldInAJar;

import com.hbm.client.wiaj.JarSymbols.SourceBlock;

/**
 * Simple action that places one block instantly with no delay
 * @author hmb
 */
public class ActionSetBlock implements IJarAction {
	
	int x;
	int y;
	int z;
	SourceBlock b;
	int meta;
	
	public ActionSetBlock(int x, int y, int z, SourceBlock b) {
		this(x, y, z, b, 0);
	}
	
	public ActionSetBlock(int x, int y, int z, SourceBlock b, int meta) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.b = b;
		this.meta = meta;
	}

	@Override
	public void act(WorldInAJar world, JarScene scene) {
		world.setBlock(x, y, z, b, meta);
	}

	@Override
	public int getDuration() {
		return 0;
	}
}
