
public class StartServer {

	public static void main(String[] args) {
		Thread serverThread = new Thread(new ServerTask());
		serverThread.start();

	}

}
