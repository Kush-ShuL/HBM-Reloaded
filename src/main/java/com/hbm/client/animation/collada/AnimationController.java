// Generated from X5846 by tools/port_animations.py; numerical logic preserved.
// Source SHA-256: 8ce48d8e6c5eef2e1b8a6ceacde8e107f27943410a509c41cf0d90d8f19f2f69
package com.hbm.client.animation.collada;

public class AnimationController {
	
	//Drillgon200: You know what? I'm pretty sure this class is entirely pointless and just acts as a stupid getter/setter for the wrapper.
	//TODO delete

	protected AnimationWrapper activeAnim = AnimationWrapper.EMPTY;

	public void setAnim(AnimationWrapper w) {
		activeAnim = w;
	}

	public void stopAnim() {
		activeAnim = AnimationWrapper.EMPTY;
	}

	public AnimationWrapper getAnim() {
		return activeAnim;
	}

}