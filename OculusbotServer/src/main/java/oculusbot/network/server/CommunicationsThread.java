package oculusbot.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static oculusbot.network.NetworkConstants.*;
import oculusbot.network.NetworkThread;

public class CommunicationsThread extends NetworkThread {

	private Controller controller;

	public CommunicationsThread(int port, Controller controller) {
		super(port);
		this.controller = controller;
	}

	@Override
	protected void setup() {
		super.setup();
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doNetworkOperation() throws IOException {
		DatagramPacket packet = null;

		try {
			packet = receive();
		} catch (SocketTimeoutException e) {
			return;
		}

		String[] data = new String(packet.getData()).trim().split(" ");
		String msg = "";
		try {
			msg = data[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return;
		}

		msg("Packet received from " + packet.getAddress().getHostAddress() + ": " + msg);

		if (msg.equals(OB_REQUEST_SERVER_IP)) {
			msg("Sent IP to " + packet.getAddress().getHostAddress());
			send(OB_ACK, packet);
		}

		if (msg.equals(OB_REGISTER_CLIENT)) {
			msg("Registered client " + packet.getAddress().getHostAddress());
			controller.registerClient(packet.getAddress().getHostAddress());
			send(OB_ACK, packet);
		}

		if (msg.equals(OB_DEREGISTER_CLIENT)) {
			msg("Deregistered client " + packet.getAddress().getHostAddress());
			controller.deregisterClient(packet.getAddress().getHostAddress());
			send(OB_ACK, packet);
		}

		if (msg.equals(OB_SEND_KEY)) {
			try {
				msg("Received key " + packet.getAddress().getHostAddress() + ": " + data[1]);
				controller.keyReleased(Integer.parseInt(data[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				return;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}

			send(OB_ACK, packet);
		}

	}

}
