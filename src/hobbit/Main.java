package hobbit;

import java.net.InetSocketAddress;

import org.java_websocket.server.WebSocketServer;

import hobbit.boundary.WebSocketGateway;

public class Main {

	public static void main(String[] args) {
		String host = "0.0.0.0";
		int port = 8080;

		WebSocketServer server = new WebSocketGateway(new InetSocketAddress(host, port));
		server.start();

		System.out.println("Hobbit Server in ascolto su porta: " + port);
	}
}