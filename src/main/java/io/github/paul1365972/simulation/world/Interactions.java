package io.github.paul1365972.simulation.world;

import org.joml.Vector2d;

public class Interactions {
	
	public static final InteractionHandler INELASTIC1D = (t, o, deltaT) -> {
		if (Math.abs(t.pos.x - o.pos.x) < t.radius + o.radius) {
			double velocity = (t.pos.x * t.mass + o.pos.x * o.mass) / (t.mass + o.mass);
			
			t.pos.sub(t.vel.mul(deltaT, new Vector2d()));
			o.pos.sub(o.vel.mul(deltaT, new Vector2d()));
			t.vel.set(velocity);
			o.vel.set(velocity);
		}
	};
	
	public static final InteractionHandler INELASTIC2D = (t, o, deltaT) -> {
		Vector2d dif = t.pos.sub(o.pos, new Vector2d());
		if (dif.length() < t.radius + o.radius) {
			Vector2d momentum1 = t.vel.mul(t.mass, new Vector2d());
			Vector2d momentum2 = o.vel.mul(o.mass, new Vector2d());
			Vector2d velocity = momentum1.add(momentum2).mul(1 / (t.mass + o.mass));
			
			t.pos.sub(t.vel.mul(deltaT, new Vector2d()));
			o.pos.sub(o.vel.mul(deltaT, new Vector2d()));
			t.vel.set(velocity);
			o.vel.set(velocity);
		}
	};
	
	public static final InteractionHandler ELASTIC1D = (t, o, deltaT) -> {
		if (Math.abs(t.pos.x - o.pos.x) < t.radius + o.radius) {
			double mass = t.mass + o.mass;
			
			double velocity1 = ((t.mass - o.mass) * t.vel.x + 2 * o.mass * t.vel.x) / mass;
			double velocity2 = ((o.mass - t.mass) * o.vel.x + 2 * t.mass * o.vel.x) / mass;
			
			t.pos.sub(t.vel.mul(deltaT, new Vector2d()));
			o.pos.sub(o.vel.mul(deltaT, new Vector2d()));
			t.vel.x = velocity1;
			o.vel.x = velocity2;
		}
	};
	
	public static final InteractionHandler ELASTIC2D = (t, o, deltaT) -> {
		Vector2d dif = t.pos.sub(o.pos, new Vector2d());
		if (dif.length() < t.radius + o.radius) {
			double mass = t.mass + o.mass;
			
			Vector2d uNormal = dif.normalize(new Vector2d());
			Vector2d uTangent = uNormal.negate(new Vector2d()).perpendicular();
			
			double velocity1n = uNormal.dot(t.vel);
			double velocity1t = uTangent.dot(t.vel);
			double velocity2n = uNormal.dot(o.vel);
			double velocity2t = uTangent.dot(o.vel);
			
			double velocity1nP = ((t.mass - o.mass) * velocity1n + 2 * o.mass * velocity2n) / mass;
			double velocity2nP = ((o.mass - t.mass) * velocity2n + 2 * t.mass * velocity1n) / mass;
			
			Vector2d velocity1 = uNormal.mul(velocity1nP, new Vector2d()).add(uTangent.mul(velocity1t, new Vector2d()));
			Vector2d velocity2 = uNormal.mul(velocity2nP, new Vector2d()).add(uTangent.mul(velocity2t, new Vector2d()));
			
			t.pos.sub(t.vel.mul(deltaT, new Vector2d()));
			o.pos.sub(o.vel.mul(deltaT, new Vector2d()));
			t.vel.set(velocity1);
			o.vel.set(velocity2);
		}
	};
	
	public static double COS_1D = 0.5f;
	
	public static final InteractionHandler SEMIELASTIC1D = (t, o, deltaT) -> {
		if (Math.abs(t.pos.x - o.pos.x) < t.radius + o.radius) {
			double mass = t.mass + o.mass;
			double momentum = t.mass * t.vel.x + o.mass * o.vel.x;
			
			double velocity1 = (COS_1D * o.mass * (o.vel.x - t.vel.x) + momentum) / mass;
			double velocity2 = (COS_1D * t.mass * (t.vel.x - o.vel.x) + momentum) / mass;
			
			t.pos.sub(t.vel.mul(deltaT, new Vector2d()));
			o.pos.sub(o.vel.mul(deltaT, new Vector2d()));
			t.vel.x = velocity1;
			o.vel.x = velocity2;
		}
	};
	
	public static double COS_2D = 0.5f;
	
	public static final InteractionHandler SEMIELASTIC2D = (t, o, deltaT) -> {
		Vector2d dif = t.pos.sub(o.pos, new Vector2d());
		if (dif.length() < t.radius + o.radius) {
			double mass = t.mass + o.mass;
			
			Vector2d uNormal = dif.normalize(new Vector2d());
			Vector2d uTangent = uNormal.negate(new Vector2d()).perpendicular();
			
			double velocity1n = uNormal.dot(t.vel);
			double velocity1t = uTangent.dot(t.vel);
			double velocity2n = uNormal.dot(o.vel);
			double velocity2t = uTangent.dot(o.vel);
			
			double momentumt = t.mass * velocity1n + o.mass * velocity2n;
			
			double velocity1nP = (COS_2D * o.mass * (velocity2n - velocity1n) + momentumt) / mass;
			double velocity2nP = (COS_2D * t.mass * (velocity1n - velocity2n) + momentumt) / mass;
			
			Vector2d velocity1 = uNormal.mul(velocity1nP, new Vector2d()).add(uTangent.mul(velocity1t, new Vector2d()));
			Vector2d velocity2 = uNormal.mul(velocity2nP, new Vector2d()).add(uTangent.mul(velocity2t, new Vector2d()));
			
			t.pos.sub(t.vel.mul(deltaT, new Vector2d()));
			o.pos.sub(o.vel.mul(deltaT, new Vector2d()));
			t.vel.set(velocity1);
			o.vel.set(velocity2);
		}
	};
	
	private static double CONST_REPULSION = 2f;
	
	public static final InteractionHandler SOFT_CONST = (t, o, deltaT) -> {
		Vector2d dif = t.pos.sub(o.pos, new Vector2d());
		if (dif.length() < t.radius + o.radius) {
			Vector2d force = new Vector2d(dif).normalize().mul(CONST_REPULSION * deltaT);
			
			Vector2d velocity1 = force.mul(deltaT / t.mass, new Vector2d());
			Vector2d velocity2 = force.mul(deltaT / o.mass, new Vector2d()).negate();
			
			t.vel.add(velocity1);
			o.vel.add(velocity2);
		}
	};
	
	private static double REPULSION_FACTOR = 2f;
	
	public static final InteractionHandler SOFT_INV = (t, o, deltaT) -> {
		Vector2d dif = t.pos.sub(o.pos, new Vector2d());
		if (dif.length() < t.radius + o.radius) {
			Vector2d force = dif.normalize(new Vector2d()).mul(REPULSION_FACTOR / dif.length());
			
			Vector2d velocity1 = force.mul(deltaT / t.mass, new Vector2d());
			Vector2d velocity2 = force.mul(deltaT / o.mass, new Vector2d()).negate();
			
			t.vel.add(velocity1);
			o.vel.add(velocity2);
		}
	};
	
}
