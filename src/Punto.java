
public class Punto {
	private int row;
	private int column;
	private boolean isSensor;
	private Punto nearestSensor;
	private float value;
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public boolean isSensor() {
		return isSensor;
	}
	public void setSensor(boolean isSensor) {
		this.isSensor = isSensor;
	}
	public Punto getNearestSensor() {
		return nearestSensor;
	}
	public void setNearestSensor(Punto nearestSensor) {
		this.nearestSensor = nearestSensor;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}

}
