package hobbit.bean;

public class Player {
	private final String name;
	private volatile int x;
	private volatile int y;

	public Player(String name) {
		this.name = name;
		this.x = 0;
		this.y = 7;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}