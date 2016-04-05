import oculusbot.opengl.Renderable;

public class Scene implements Renderable{
//	private TwoCamerasCanvas canvas;
	private Renderable canvas;
	
	public Scene(){
//		canvas = new Shape2D(new float[]{-0.5f, -0.5f, 0, 0, 1, 0.5f, -0.5f, 1, 0, 0, 0f, 0.75f, 0, 1, 0}, 3);
//		canvas = new SimpleRenderObject();
//		canvas = new TwoCamerasCanvasSwap();
		canvas = new TwoCamerasCanvas();
	}

	public void init() {
		canvas.init();
	}

	public void render() {
		canvas.render();
	}

	public void destroy() {
		canvas.destroy();
	}

	public void setEye(int eye) {
		if(canvas instanceof TwoCamerasCanvasSwap){
			((TwoCamerasCanvasSwap)canvas).setEye(eye);
		}
	}

}
