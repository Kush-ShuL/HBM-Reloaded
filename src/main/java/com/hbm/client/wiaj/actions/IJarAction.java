package com.hbm.client.wiaj.actions;

import com.hbm.client.wiaj.JarScene;
import com.hbm.client.wiaj.WorldInAJar;

public interface IJarAction {

	/** Time taken by this action in ticks */
	public int getDuration();
	/** Perform this action */
	public void act(WorldInAJar world, JarScene scene);
}
