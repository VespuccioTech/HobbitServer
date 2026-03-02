package hobbit.business;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import hobbit.bean.Obstacle;
import hobbit.bean.Player;


public final class Message {

	private Message() { }

	public static JSONObject buildLoginResponse() {
		JSONObject res = new JSONObject();
		res.put("comando", "login");
		res.put("status", "ok");
		return res;
	}

	public static JSONObject buildSinglePosition(Player p) {
		JSONObject res = new JSONObject();
		res.put("comando", "posizione");
		res.put("nome", p.getName());
		res.put("x", p.getX());
		res.put("y", p.getY());
		return res;
	}

	public static JSONObject buildObstacles(List<Obstacle> obstacles) {
		JSONObject res = new JSONObject();
		res.put("comando", "ostacoli");

		JSONArray arr = new JSONArray();
		for (Obstacle o : obstacles) {
			JSONObject oo = new JSONObject();
			oo.put("tipo", o.getType());
			oo.put("x", o.getX());
			oo.put("y", o.getY());
			arr.put(oo);
		}
		res.put("posizioni", arr);
		return res;
	}

	public static JSONObject buildAllPositions(List<Player> players) {
		JSONObject res = new JSONObject();
		res.put("comando", "posizioneTutti");

		JSONArray arr = new JSONArray();
		for (Player p : players) {
			JSONObject u = new JSONObject();
			u.put("nome", p.getName());
			u.put("x", p.getX());
			u.put("y", p.getY());
			arr.put(u);
		}
		res.put("utenti", arr);
		return res;
	}
}