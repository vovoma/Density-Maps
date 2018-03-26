package maven.DensityMap;

public class ServiciosCoordenadas {
	//PRE: Las variables Xi, Yi representan los bordes del mapa, y 
	// x, y el punto que quiero convertir a formato matriz, f y c numero de filas y columnas
	//en la que dividiremos la matriz asumimos 100*100; y2>y1 && x2>x1
	//POST: Esta función dadas unas coordenadas cualquiera, devolvera un punto
	//análogo en la matriz
	
	public static  Punto CoordenadaAMatriz(int f, int c, double y1, double y2, double x1, double x2, double x, double y) {
		Punto p = new Punto(0,0);
		int fila=(int) Math.round(((f-1)*(y-y1))/(y2-y1));
		
		int columna=(int) Math.round(((c-1)*(x-x1))/(x2-x1));
		p.setColumna(Math.abs(columna));
		p.setFila(Math.abs(fila));
		return p;
	}
	public static double MatrizACoordenadaX(int c, double x1, double x2, int x) {
		return(((x)*(x2-x1)/c)+x1);
	}
	public static double MatrizACoordenadaY(int f, double y1, double y2, int y) {
		return(((y)*(y2-y1)/f)+y1);
	}
}