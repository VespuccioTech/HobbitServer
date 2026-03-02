package hobbit.business;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import hobbit.bean.Obstacle;
import hobbit.bean.Player;

public class GameEngine {

	private final Map<WebSocket, Player> sessions = new ConcurrentHashMap<>();
	private final List<Obstacle> obstacles = new CopyOnWriteArrayList<>();

	public GameEngine() {
		seedObstacles();
	}

	private void seedObstacles() {
		// Copia delle posizioni originarie -> stesso comportamento funzionale
		obstacles.add(new Obstacle("chest", 1, 1));
		obstacles.add(new Obstacle("rock", 6, 6));
		obstacles.add(new Obstacle("rock", 3, 4));
		obstacles.add(new Obstacle("rock", 4, 3));
	}

	// --- gestione connessioni / giocatori ---

	public void registerPlayer(WebSocket conn, String displayName) {
		Player p = new Player(displayName);
		sessions.put(conn, p);

		// risposta di login solo al client richiedente
		JSONObject loginOk = Message.buildLoginResponse();
		conn.send(loginOk.toString());

		System.out.println("[Engine] Player registrato: " + displayName);

		// notifica a tutti lo stato aggiornato
		broadcastState();
	}

	public void disconnectClient(WebSocket conn) {
		Player removed = sessions.remove(conn);
		if (removed != null) {
			System.out.println("[Engine] Rimosso: " + removed.getName());
			broadcastState();
		}
	}

	public void movePlayer(WebSocket conn, String direction) {
		Player p = sessions.get(conn);
		if (p == null)
			return;

		int candidateX = p.getX();
		int candidateY = p.getY();

		switch (direction) {
		case "su":
			candidateY--;
			break;
		case "giu":
			candidateY++;
			break;
		case "sinistra":
			candidateX--;
			break;
		case "destra":
			candidateX++;
			break;
		default:
			return;
		}

		// Regole di bordo
		if (candidateX < 0 || candidateX > 7 || candidateY < 0 || candidateY > 99)
			return;

		// creo variabili final per la lambda
		final int targetX = candidateX;
		final int targetY = candidateY;

		boolean collision = obstacles.stream().anyMatch(o -> o.getX() == targetX && o.getY() == targetY);

		if (collision)
			return;

		p.setX(targetX);
		p.setY(targetY);

		broadcastState();
	}

	// --- metodi informativi (risposte mirate) ---

	public void sendOwnPosition(WebSocket conn) {
		Player p = sessions.get(conn);
		if (p == null)
			return;

		JSONObject res = Message.buildSinglePosition(p);
		conn.send(res.toString());
	}

	public void sendObstacles(WebSocket conn) {
		JSONObject res = Message.buildObstacles(obstacles);
		conn.send(res.toString());
	}

	// Metodo pubblico per inviare lo stato corrente a TUTTI i client connessi
	public void broadcastState() {
		JSONObject payload = Message.buildAllPositions(sessions.values().stream().collect(Collectors.toList()));

		String txt = payload.toString();
		// invio a tutte le connessioni attive (controllo open)
		for (WebSocket ws : sessions.keySet()) {
			if (ws != null && ws.isOpen()) {
				ws.send(txt);
			}
		}
	}
}