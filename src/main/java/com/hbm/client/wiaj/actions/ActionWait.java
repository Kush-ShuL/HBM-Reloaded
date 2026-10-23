package com.hbm.client.wiaj.actions;

import com.hbm.client.wiaj.JarScene;
import com.hbm.client.wiaj.WorldInAJar;

public class ActionWait implements IJarAction {
	
	private int ticks;
	
	public ActionWait(int ticks) {
		this.ticks = ticks;
	}

	@Override
	public int getDuration() {
		return this.ticks;
	}

	@Override
	public void act(WorldInAJar world, JarScene scene) { }
}
