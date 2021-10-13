package org.koekepan.herobrineproxy.entity;

import java.util.UUID;

public class Entity {
	private int id;
	private UUID uuid;
	private double x;
	private double z;
	
	public Entity(int id, double x, double z) {
		this.x = x;
		this.z = z;
	}
	
	public void move(double moveX, double moveZ) {
		this.x += moveX;
		this.z += moveZ;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
}
