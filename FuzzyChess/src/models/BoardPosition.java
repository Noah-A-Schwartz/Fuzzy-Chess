package models;

public class BoardPosition {
	private int x;
	private int y;
	public final static int TILE_SIZE = 80;
	public final static int OFFSET = 60;
	
	public BoardPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static BoardPosition convert(int screenX, int screenY) {
		int resX = Math.floorDiv(screenX-OFFSET, TILE_SIZE);
		int resY = Math.floorDiv(screenY-OFFSET, TILE_SIZE);
		return new BoardPosition(resX, resY);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int distance(BoardPosition destination) {
		return (int)(Math.sqrt(Math.pow((destination.getX() - x), 2) + Math.pow((destination.getY() - y), 2)));
	}
	
	public BoardPosition copy() {
		return new BoardPosition(x,y);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof BoardPosition))
			return false;
		BoardPosition other = (BoardPosition)o;
		return this.x == other.getX() && this.y == other.getY();
	}

	@Override
	public int hashCode() {
		int result = Integer.hashCode(x);
		result = 31 * result + Integer.hashCode(y);
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}
}
