package maven.DensityMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.*;

public class Red {
	private final static int f =10000;
	private final static int c=10000;
	private int filas;
	private int columnas;
	private Punto[][] puntos= new Punto[f][c];
	private ArrayList<Punto> sensores; 
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	public final static double offset=0.005;
	public int getColumnas() {
		return columnas;
	}
	public void setColumnas(int columnas) {
		this.columnas = columnas;
	}
	public int getFilas() {
		return filas;
	}
	public void setFilas(int filas) {
		this.filas = filas;
	}
	public ArrayList<Punto> getSensores() {
		return sensores;
	}
	public void setSensores(ArrayList<Punto> sensores) {
		this.sensores = sensores;
	}
	public Punto[][] getPuntos() {
		return puntos;
	}
	public void setPuntos(Punto[][] puntos) {
		this.puntos = puntos;
	}
	//constructor de red en función de las 4 esquinas del mapa
	public Red(double x1, double x2, double y1, double y2) {
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		if(x2-x1>y2-y1) {
			 filas=400;
			 columnas=Math.abs((int)Math.round(((x2-x1)/(y2-y1))*400));
		}
		else {
			 columnas=400;
			 filas=Math.abs((int)Math.round(((y2-y1)/(x2-x1))*400));
		}
		for(int i=0;  i<filas;i++) {
			for (int j=0; j<columnas;j++) {
				

				puntos[i][j]=new Punto(i, j);
				
			}
		}
		sensores=new ArrayList<Punto>();
	}
	
	
	
	/*Constructor de una red sin sensores, con número de filas y columnas
	 * especificado*/
	public Red(int f, int c) {
		filas=f;
		columnas=c;
		
		for(int i=0;  i<filas;i++) {
			for (int j=0; j<columnas;j++) {
				

				puntos[i][j]=new Punto(i, j);
				
			}
		}
		sensores=new ArrayList<Punto>();
	}
	//Pre: controla que los sensores no se salgan de la red, si están fuera no se añadirán
	/*Post: Pueblo una red de sensores*/
	public void asignarSensores(ArrayList<Punto> sensores2) {
		sensores=sensores2;
		for(Punto p : sensores2) {
			if(p.getFila()<filas&&p.getColumna()<columnas) {
				puntos[p.getFila()][p.getColumna()].setEsSensor(true);
				puntos[p.getFila()][p.getColumna()].setValor(p.getValor());
			puntos[p.getFila()][p.getColumna()].setSensorMasCercano(p);
			puntos[p.getFila()][p.getColumna()].setPintado(true);
			}
			
		}
	}
	//PRE:Asegurate que la lista de sensores no es vacia
	//Post:Al punto que le pases le asigna el sensor mas cercano, su valor y si esta dentro de su alcance
	public void asignoSensorMasCercano(Punto p) {
		
		Punto aux=  sensores.get(0);
		double distancia= aux.distancia(p);
		for(Punto p1 : sensores) {
			
			if(distancia>p.distancia(p1)){
				aux=p1;
				distancia=p.distancia(p1);
			}
		}
		p.setSensorMasCercano(aux);
		p.setValor(aux.getValor());
		p.setPintado( aux.getAlcance()>=distancia);
	}
	//A cada punto se le asigna su sensor más cercano
	//asignandole su valor
	public void pintarRed() {
		for(int i=0; i<filas; i++) {
			for(int j=0;j<columnas;j++) {
				if(!puntos[i][j].isEsSensor()) {
					asignoSensorMasCercano(puntos[i][j]);
				}
				else {
					puntos[i][j].setPintado(true);
					puntos[i][j].setEsSensor(true);
					puntos[i][j].setSensorMasCercano(puntos[i][j]);
				}
			}
		}
	}
	
	//return a string that represent the matrix
	public String toString() {
		String aux="";
		
		for(int i=0;i<filas;i++) {
			for(int j=0;j<columnas;j++) {
				if(puntos[i][j].isPintado()) {
					aux=aux+puntos[i][j].toString()+"|";
				}
				else
				{
					aux=aux+"___|" ;
				}
				
			}
			aux=aux+"\n";
		}
		return aux;
	}

	
	//pre: the point "sensor" must be a sensor
	//POST: returns a list of points surrounding "sensor" in the matrix
	public ArrayList<Punto> bordeSensor( final Punto sensor){
		ArrayList<Punto> aux = new ArrayList<Punto>();
		if(sensor.getAlcance()>0) {
			
			for(int i=0;i<filas;i++) {
				//System.out.println("eoo");
				for(int j=0;j<columnas;j++) {
					//System.out.println("eoo");
					if(puntos[i][j].distancia(sensor)<=sensor.getAlcance()+offset&&puntos[i][j].distancia(sensor)>=sensor.getAlcance()-offset) {
						//System.out.println("eoo");
						aux.add(puntos[i][j]);
//						double coord1 = ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, i);
//						double coord2 = ServiciosCoordenadas.MatrizACoordenadaX(columnas, x1, x2,j);
//						System.out.println(i+", "+j+", "+", "+coord1+", "+coord2);
						if(puntos[i][j].distancia(puntos[i][j].getSensorMasCercano())>puntos[i][j].getSensorMasCercano().getAlcance()) {
							puntos[i][j].setValor(sensor.getValor());
							

						}
						puntos[i][j].setPintado(true);
					}
				}
			}
		}
		aux.sort(new Comparator<Punto>() {
			public int compare(Punto p1, Punto p2) {
				return (new Double(Red.angulo(sensor, p1)).compareTo(new Double (Red.angulo(sensor, p2))));
			}
		});
		return (aux);
	}
	public void rodearSensor(Punto sensor) {
		if(sensor.isEsSensor()) {
			for (Punto p: bordeSensor(sensor)) {
				p.setPintado(true);
				p.setValor(p.getValor());
			}
		}
	}
	public void rodearSensores() {
		for(Punto p : this.getSensores()) {
			rodearSensor(p);
		}
	}
	public double getX1() {
		return x1;
	}
	public void setX1(double x1) {
		this.x1 = x1;
	}
	public double getY1() {
		return y1;
	}
	public void setY1(double y1) {
		this.y1 = y1;
	}
	public double getX2() {
		return x2;
	}
	public void setX2(double x2) {
		this.x2 = x2;
	}
	public double getY2() {
		return y2;
	}
	public void setY2(double y2) {
		this.y2 = y2;
	}
	
	
	/*returns the angle between "sensor" & "punto(point)"*/
	public static double angulo(Punto sensor, Punto punto) {
		int x=punto.getColumna()-sensor.getColumna();
		int y=punto.getFila()-sensor.getFila();
		//primer cuadrante
		if(x>0&&y>0) {
			return(Math.atan((double)y/x));
		}
		//segundo cuadrante
		else if(x<0&&y>0) {
			return(Math.PI+Math.atan((double)y/x));
		}
		//tercer cuadrante
		else if(x<0&&y<0) {
			return(Math.PI+Math.atan((double)y/x));
		}
		//cuarto cuadrante
		else if(x>0&&y<0) {
			return(Math.atan((double)y/x)+2*Math.PI);
		}
		
		
		else if(y==0) {
			if(x>=0) {
				
				return 0;
				
			}
			else {
				return Math.PI;
			}
		}
		else if(x==0) {
			if(y>=0) {
				return Math.PI/2;
			}
			else {
				return 3*Math.PI/2;
			}
		}
		return 0;

	}


	//returns a JsonElement that represents a sensor and his edge
	public JsonElement sensorToJson(Punto sensor) 
	{
		JsonArray coordinates= new JsonArray();
		//multiply by 10E6, round and divide by 10E6
		double coord1 = (double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, sensor.getFila())*1000000)/1000000;
		double coord2 =(double)Math.round(ServiciosCoordenadas.MatrizACoordenadaX(columnas, x1, x2, sensor.getColumna())*1000000)/1000000;
		coordinates.add(coord2);
		coordinates.add(coord1);
		JsonObject pj = new JsonObject();
		JsonArray ja1 = new JsonArray();
		JsonArray ja2 = new JsonArray();
		pj.add("coordinates", coordinates);
		pj.addProperty("value", sensor.getValor());
		double radio = Math.round(Math.random()*5+5);
		double coord11=(double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, bordeSensor(sensor).get(0).getFila())*1000000)/1000000;
		double coord22=(double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(columnas, x1, x2, bordeSensor(sensor).get(0).getColumna())*1000000)/1000000;
		pj.addProperty("range", radio);
		for(Punto p: this.bordeSensor(sensor)) {
			JsonArray aux = new JsonArray();
			aux.add((double)Math.round(ServiciosCoordenadas.MatrizACoordenadaX(columnas, x1, x2, p.getColumna())*1000000)/1000000);
			aux.add((double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, p.getFila())*1000000)/1000000);
			
			ja1.add(aux);
		}
		JsonArray aux = new JsonArray();
		aux.add(coord22);
		aux.add(coord11);
		ja1.add(aux);
		ja2.add(ja1);
		pj.add("edge", ja2);			
		return pj;
		
	}
	//returns a json that represents a sensor and his edge with the order of coordinates reversed
	public JsonElement sensorToJson2(Punto sensor) {
		
		JsonArray coordinates = new JsonArray();
		double coord1 = (double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, sensor.getFila())*1000000)/1000000;
		double coord2 =(double)Math.round(ServiciosCoordenadas.MatrizACoordenadaX(columnas, x1, x2, sensor.getColumna())*1000000)/1000000;
		coordinates.add(coord1);
		coordinates.add(coord2);
		double radio = Math.round(Math.random()*5+5);

		JsonObject pj = new JsonObject();
		JsonArray ja1 = new JsonArray();
		JsonArray ja2 = new JsonArray();
		pj.add("coordinates", coordinates);
		pj.addProperty("value", sensor.getValor());
		double coord11=(double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, bordeSensor(sensor).get(0).getFila())*1000000)/1000000;
		double coord22=(double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(columnas, x1, x2, bordeSensor(sensor).get(0).getColumna())*1000000)/1000000;
		pj.addProperty("range", radio);

		for(Punto p: this.bordeSensor(sensor)) {
			JsonArray aux = new JsonArray();
			
			aux.add((double)Math.round(ServiciosCoordenadas.MatrizACoordenadaY(filas, y1, y2, p.getFila())*1000000)/1000000);
			aux.add((double)Math.round(ServiciosCoordenadas.MatrizACoordenadaX(columnas, x1, x2, p.getColumna())*1000000)/1000000);
			ja1.add(aux);
		}
		JsonArray aux = new JsonArray();
		aux.add(coord11);
		aux.add(coord22);
		ja1.add(aux);
		ja2.add(ja1);
		pj.add("edge", ja2);			
		return pj;
	}
	//returns a String
	public String redToJson() {
		String red="";
		JsonObject jo = new JsonObject();
		JsonArray ja= new JsonArray();
		 for(Punto sensor: this.sensores) 
		 {
         	ja.add(sensorToJson(sensor));;
         }
		 jo.add("sensors", ja);
		 red=jo.toString();
		return red;
	}
	public String redToJson2() {
		String red="";
		JsonObject jo = new JsonObject();
		JsonArray ja= new JsonArray();
		 for(Punto sensor: this.sensores) 
		 {
         	ja.add(sensorToJson2(sensor));;
         }
		 jo.add("sensors", ja);
		 red=jo.toString();
		return red;
	}
	public static String redConstructor(String json) {
		double x1, y2, x2, y1;
		JsonParser parser = new JsonParser();
		JsonObject jRead = parser.parse(json).getAsJsonObject();
		JsonArray jArray = new JsonArray();
		jArray= (JsonArray) jRead.get("A");
		y2=(double) jArray.get(0).getAsDouble();
		x1=(double) jArray.get(1).getAsDouble();
		jArray= (JsonArray) jRead.get("B");
		y1=(double) jArray.get(0).getAsDouble();
		x2=(double) jArray.get(1).getAsDouble();
		Red r1= new Red(x1, x2, y1, y2);
		jArray= (JsonArray)jRead.get("sensors");
		ArrayList<Punto> sensors= new ArrayList<Punto>();
		JsonArray coordinates= new JsonArray();
		
		
		for(JsonElement je : jArray)
		{
			
			JsonObject jo = (JsonObject) je;
			coordinates=(JsonArray)jo.getAsJsonArray("coordinates");
			double y=coordinates.get(0).getAsDouble();
			
			double x=coordinates.get(1).getAsDouble();
			
			Punto sensor1 =  ServiciosCoordenadas.CoordenadaAMatriz(r1.getFilas(), r1.getColumnas(), y1, y2, x1, x2, x, y);
			
			sensor1.setEsSensor(true);
			sensor1.setValor(jo.get("value").getAsFloat());
			sensor1.setAlcance(10);
			sensors.add(sensor1);
			
		}
		r1.asignarSensores(sensors);
		
		r1.pintarRed();
		r1.rodearSensores();
		return r1.redToJson2();
		
	}
	public static String redConstructorReverse(String json) {
		double x1, y2, x2, y1;
		JsonParser parser = new JsonParser();
		JsonObject jRead = parser.parse(json).getAsJsonObject();
		JsonArray jArray = new JsonArray();
		jArray= (JsonArray) jRead.get("A");
		y2=(double) jArray.get(0).getAsDouble();
		x1=(double) jArray.get(1).getAsDouble();
		jArray= (JsonArray) jRead.get("B");
		y1=(double) jArray.get(0).getAsDouble();
		x2=(double) jArray.get(1).getAsDouble();
		Red r1= new Red(x1, x2, y1, y2);
		jArray= (JsonArray)jRead.get("sensors");
		ArrayList<Punto> sensors= new ArrayList<Punto>();
		JsonArray coordinates= new JsonArray();
		
		
		for(JsonElement je : jArray)
		{
			
			JsonObject jo = (JsonObject) je;
			coordinates=(JsonArray)jo.getAsJsonArray("coordinates");
			double y=coordinates.get(0).getAsDouble();
			
			double x=coordinates.get(1).getAsDouble();
			
			Punto sensor1 =  ServiciosCoordenadas.CoordenadaAMatriz(r1.getFilas(), r1.getColumnas(), y1, y2, x1, x2, x, y);
			
			sensor1.setEsSensor(true);
			sensor1.setValor(jo.get("value").getAsFloat());
			sensor1.setAlcance(10);
			sensors.add(sensor1);
			
		}
		r1.asignarSensores(sensors);
		
		r1.pintarRed();
		r1.rodearSensores();
		return r1.redToJson();
		
	}
}
