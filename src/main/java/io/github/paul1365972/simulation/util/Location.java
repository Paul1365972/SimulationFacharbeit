package io.github.paul1365972.simulation.util;

public class Location {
	public final float x, y;
	
	public Location(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float distSq(Location other) {
		float dx = x - other.x;
		float dy = y - other.y;
		return dx * dx + dy * dy;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Location location = (Location) o;
		
		if (Float.compare(location.x, x) != 0) return false;
		return Float.compare(location.y, y) == 0;
	}
	
	@Override
	public int hashCode() {
		int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
		result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
		return result;
	}
}