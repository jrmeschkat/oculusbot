package oculusbot.network.client;

import oculusbot.rift.RenderThread;

public class OculusbotClientMain {
	
	private RenderThread render;

	public OculusbotClientMain() {
		render = new RenderThread(1600, 600);
		render.start();
	}


	public static void main(String[] args) {
		new OculusbotClientMain();
	}

}
