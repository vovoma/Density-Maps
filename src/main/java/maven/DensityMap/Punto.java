package maven.DensityMap;

import java.util.ArrayList;

public class Punto {
	private int fila;
	private int columna;
	private boolean esSensor;
	private Punto sensorMasCercano;
	private float valor;
	private boolean pintado;
	private double alcance;
	public int getFila() {
		return fila;
	}
	public void setFila(int fila) {
		this.fila = fila;
	}
	public int getColumna() {
		return columna;
	}
	public void setColumna(int columna) {
		this.columna = columna;
	}
	public boolean isEsSensor() {
		return esSensor;
	}
	public void setEsSensor(boolean esSensor) {
		this.esSensor = esSensor;
	}
	public Punto getSensorMasCercano() {
		return sensorMasCercano;
	}
	public void setSensorMasCercano(Punto aux) {
		this.sensorMasCercano = aux;
	}
	public float getValor() {
		return valor;
	}
	public void setValor(float valor) {
		this.valor = valor;
	}
	public Punto(int f, int c) {
		
		fila=f;
		columna=c;
		esSensor=false;
		sensorMasCercano=null;
		valor=0;
		alcance=0;
		//borde=new ArrayList<Punto>();
	}
	//Punto's constructor with the row (f), column (c), isSensor (b), and the value
	public Punto(int f, int c, boolean b, float v) {
		
		fila=f;
		columna=c;
		esSensor=b;
		sensorMasCercano=null;
		valor=v;
		alcance=0;
//		borde=new ArrayList<Punto>();

	}
	//constructor for points that are sensors
	
	public Punto(int f, int c, boolean b, float v, int alc) {
	
	fila=f;
	columna=c;
	esSensor=b;
	sensorMasCercano=null;
	valor=v;
	alcance=alc;
	//borde=new ArrayList<Punto>();

}
	//this function returns the distance between 2 points in the matrix (red)
	public double distancia (Punto p) {
		return(Math.sqrt((p.getFila()-this.getFila())*(p.getFila()-this.getFila())+(p.getColumna()-this.getColumna())*(p.getColumna()-this.getColumna())));
	}
	public String toString() {
		return(/*"("+this.getFila()+","+this.getColumna()+")"+":"+*/this.getValor()+""/*+"pint"+this.isPintado()*/);
	}
	public boolean isPintado() {
		return pintado;
	}
	public void setPintado(boolean pintado) {
		this.pintado = pintado;
	}
	public double getAlcance() {
		return alcance;
	}
	public void setAlcance(double alcance) {
		this.alcance = alcance;
	}
	

	
}

