package hobbit.boundary;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import hobbit.business.GameEngine;

public class WebSocketGateway extends WebSocketServer {

	private final GameEngine engine;

	public WebSocketGateway(InetSocketAddress addr) {
		super(addr);
		this.engine = new GameEngine();
	}

	@Override
	public void onStart() {
		System.out.println("Gateway WebSocket avviato. Porta: " + getPort());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("[GW] Nuova connessione: " + conn.getRemoteSocketAddress());
		// niente auto-login: aspettiamo il comando "login" dal client
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("[GW] Chiusura connessione: " + conn.getRemoteSocketAddress());
		engine.disconnectClient(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("[GW] Ricevuto raw: " + message);
		try {
			JSONObject req = new JSONObject(message);
			final String comando = req.optString("comando", "");

			switch (comando) {
			case "login":
				String nome = req.optString("nome", "Sconosciuto");
				engine.registerPlayer(conn, nome);
				break;

			case "muovi":
				String direzione = req.optString("direzione", "");
				engine.movePlayer(conn, direzione);
				break;

			case "posizione":
				engine.sendOwnPosition(conn);
				break;

			case "ostacoli":
				engine.sendObstacles(conn);
				break;

			case "posizioneTutti":
				// richiesta esplicita di lista di tutti (compatibilità)
				engine.broadcastState();
				break;

			default:
				System.out.println("[GW] Comando non riconosciuto: " + comando);
				// non inviamo errori by default per compatibilità
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("[GW] Errore parsing messaggio: " + message);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("[GW] Errore WebSocket:");
		ex.printStackTrace();
	}
}