package oculusbot.network.client;

import java.net.InetAddress;

import org.lwjgl.ovr.OVR;

import oculusbot.opengl.Callback;
import oculusbot.opengl.Window;
import oculusbot.opengl.renderable.MatCanvas;
import oculusbot.rift.MirrorWindow;
import oculusbot.rift.Rift;
import oculusbot.rift.Scene;
import oculusbot.video.ReceiveVideoThread;
import static org.lwjgl.glfw.GLFW.*;

public class OculusbotClientMain {
	private Rift rift;
	private Window w;
	private SendPositionDataThread sendData;
	private ReceiveVideoThread video;

	public OculusbotClientMain() {
		InetAddress serverIP = new ServerDiscovery().getServerIP(1337);
		String ip = serverIP.getHostAddress();
		video = new ReceiveVideoThread(1339);
		video.start();
		w = new Window(1600, 600);
		w.setCallback(new Callback() {

			public void keyPressed(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW_RELEASE) {
					if (key == GLFW_KEY_R) {
						rift.recenter();
					}
				}
			}
		});
		w.init();
		rift = new Rift(new Scene(video));
		w.register(
				new MirrorWindow(rift.getMirrorFramebuffer(w.getWidth(), w.getHeight()), w.getWidth(), w.getHeight()));
		rift.init();
		sendData = new SendPositionDataThread(ip, 1338, rift);

	}

	public void start() {
		sendData.start();
		//		w.register(new MatCanvas(video));
		//		w.start();

		while (!w.shouldClose()) {
			rift.render();
			w.render();
		}
		sendData.interrupt();
		video.interrupt();
	}

	public static void main(String[] args) {
		OculusbotClientMain ocm = new OculusbotClientMain();
		ocm.start();
	}

}
