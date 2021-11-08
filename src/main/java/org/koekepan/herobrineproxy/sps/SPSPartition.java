package org.koekepan.herobrineproxy.sps;

public class SPSPartition {
	private double[] xPoints;
	private double[] yPoints;
	
	public SPSPartition(double[] xPoints, double[] yPoints) {
		this.xPoints = xPoints;
		this.yPoints = yPoints;
	}
	
	public double[] getXPoints() {
		return this.xPoints;
	}
	
	public double[] getYPoints() {
		return this.yPoints;
	}
}
