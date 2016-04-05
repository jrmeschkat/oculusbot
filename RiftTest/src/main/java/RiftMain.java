import oculusbot.opengl.Window;

public class RiftMain {
	public static void main(String[] args) {
		Window w = new Window(1600,600);
		Rift rift = new Rift(new Scene());
		w.init();
		w.register(new MirrorWindow(rift.getMirrorFramebuffer(w.getWidth(), w.getHeight()), w.getWidth(), w.getHeight()));
		
		rift.init();
		while(!w.shouldClose()){
			rift.render();
			w.render();
		}
		rift.destroy();
		w.destroy();
	}
	
}
