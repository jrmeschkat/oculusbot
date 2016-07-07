package oculusbot.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import oculusbot.network.NetworkConstants;
import oculusbot.network.NetworkThread;

public class BroadcastDiscoveryThread extends NetworkThread {

	public BroadcastDiscoveryThread(int port) {
		super(port);
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

		msg("Packet received from " + packet.getAddress().getHostAddress());
		String msg = new String(packet.getData()).trim();
		if (msg.equals(NetworkConstants.OB_REQUEST_SERVER_IP)) {
			send(NetworkConstants.OB_RESPONSE_SERVER_IP, packet);
			msg("Sent IP to " + packet.getAddress().getHostAddress());
		}
	}

}
