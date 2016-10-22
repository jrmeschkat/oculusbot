package oculusbot.network.client;

import oculusbot.rift.RenderThread;

/**
 * The main-class of the client. 
 * @author Robert Meschkat
 *
 */
public class OculusbotClientMain {
	
	private RenderThread render;

	/**
	 * Create a new client which will try to lookup the server IP.
	 */
	public OculusbotClientMain() {
		render = new RenderThread(1600, 600);
		render.start();
	}
	
	/**
	 * Create a new client with a static server IP.
	 * @param ip
	 */
	public OculusbotClientMain(String ip) {
		render = new RenderThread(1600, 600, ip);
		render.start();
	}


	public static void main(String[] args) {
		if(args.length > 1){
			if(args[0].equals("-d")){
				new OculusbotClientMain(args[1]);
			}
			
		} else{
			new OculusbotClientMain();
		}
	}

}
