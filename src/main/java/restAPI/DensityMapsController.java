package restAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.*;
import maven.DensityMap.Punto;
import maven.DensityMap.*;


@RestController
public class DensityMapsController<Punto> {

	private static final String SECRET = "_JWT_TOKEN_KEY_";
	
	
	public DensityMapsController() {
	}
    
	@RequestMapping(method = RequestMethod.GET, value = "/swagger", headers="Accept=application/json")
	public ResponseEntity  getSwagger() {
		File archivo = null;
	      FileReader fr = null;
	      BufferedReader br = null;
    	  String SwaggerJson="";


	      try {
	         // Apertura del fichero y creacion de BufferedReader para poder
	         // hacer una lectura comoda (disponer del metodo readLine()).
	    	 String absolutePath=new File("").getAbsolutePath();
	    	 System.out.println(absolutePath);
	    	 SwaggerJson="";
	         archivo = new File (absolutePath+File.separator+"src"+File.separator+"swaggerDM.json");
	         fr = new FileReader (archivo);
	         br = new BufferedReader(fr);

	         // Lectura del fichero
	         String linea;
	         while((linea=br.readLine())!=null)
	            SwaggerJson=SwaggerJson+"\n"+linea;
	        	 //System.out.println(linea);
	         //System.out.println(SwaggerJson);
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	      }
	     
	      return ResponseEntity.status(HttpStatus.OK).body(SwaggerJson);
		
	}	
	
	// Post Method
	/*
	 * ENTRY JSON:
	 * { "user_id": "user_id",
	 * 	 "rule" : {rule_JSON} }	* 
	 * 
	 */
	@RequestMapping(value = "/densityMap", method = RequestMethod.POST, headers="Accept=application/json", consumes = {"application/json"})
	@ResponseBody
	public ResponseEntity getDensityMap(@RequestBody String body, @RequestHeader("X-Authorization-s4c") String jwtHeader) throws IllegalArgumentException, UnsupportedEncodingException {		
		//HMAC
		Algorithm algorithmHS = Algorithm.HMAC512(SECRET);
		
		try {
		    DecodedJWT jwt = JWT.decode(jwtHeader);
		    String subject = jwt.getSubject();
		    System.out.println(subject);
		} catch (JWTDecodeException exception){
		    //Invalid token
		}
		//Extracting coordinates of the map
		double x1, y2, x2, y1;
		JsonParser parser = new JsonParser();
		JsonObject jRead = parser.parse(body).getAsJsonObject();
		JsonArray jArray = new JsonArray();
		jArray= (JsonArray) jRead.get("A");
		y2=(double) jArray.get(0).getAsDouble();
		x1=(double) jArray.get(1).getAsDouble();
		jArray= (JsonArray) jRead.get("B");
		y1=(double) jArray.get(0).getAsDouble();
		x2=(double) jArray.get(1).getAsDouble();
		Red r1= new Red(x1, x2, y1, y2);
//		System.out.println("x1: "+x1+" x2: "+x2+" y1: "+y1+" y2: "+y2);
		//extracting the sensors
		jArray= (JsonArray)jRead.get("sensors");
		ArrayList<maven.DensityMap.Punto> sensors= new ArrayList<maven.DensityMap.Punto>();
		for(JsonElement je : jArray)
		{
			
			JsonObject jo = (JsonObject) je;
			double y=jo.get("lattitude").getAsDouble();
//			System.out.println("lattitude: "+y);
			double x=jo.get("longitude").getAsDouble();
//			System.out.println("longitudede: "+x);
			maven.DensityMap.Punto sensor1 =  ServiciosCoordenadas.CoordenadaAMatriz(r1.getFilas(), r1.getColumnas(), y1, y2, x1, x2, x, y);
//			System.out.println("filaSensor:"+ sensor1.getFila());
//			System.out.println("columnaSensor:"+ sensor1.getColumna());
//			System.out.println("filas ,"+r1.getFilas()+" columnas, "+r1.getColumnas());
			sensor1.setEsSensor(true);
			sensor1.setValor(jo.get("value").getAsFloat());
			sensor1.setAlcance(10);
			sensors.add(sensor1);
		}
//		System.out.println(sensors.size());
		
		r1.asignarSensores(sensors);
		r1.pintarRed();
		r1.rodearSensores();
		return ResponseEntity.status(HttpStatus.OK).body(r1.redToJson2());
	}
	
	@RequestMapping(value = "/densityMap", method = RequestMethod.POST, headers="Accept=application/json", consumes = {"application/json"})
	@ResponseBody
	public ResponseEntity getDensityMapReverse(@RequestBody String body, @RequestHeader("X-Authorization-s4c") String jwtHeader) throws IllegalArgumentException, UnsupportedEncodingException {		
		//HMAC
		Algorithm algorithmHS = Algorithm.HMAC512(SECRET);
		
		try {
		    DecodedJWT jwt = JWT.decode(jwtHeader);
		    String subject = jwt.getSubject();
		    System.out.println(subject);
		} catch (JWTDecodeException exception){
		    //Invalid token
		}
		//Extracting coordinates of the map
		double x1, y2, x2, y1;
		JsonParser parser = new JsonParser();
		JsonObject jRead = parser.parse(body).getAsJsonObject();
		JsonArray jArray = new JsonArray();
		jArray= (JsonArray) jRead.get("A");
		y2=(double) jArray.get(0).getAsDouble();
		x1=(double) jArray.get(1).getAsDouble();
		jArray= (JsonArray) jRead.get("B");
		y1=(double) jArray.get(0).getAsDouble();
		x2=(double) jArray.get(1).getAsDouble();
		Red r1= new Red(x1, x2, y1, y2);
//		System.out.println("x1: "+x1+" x2: "+x2+" y1: "+y1+" y2: "+y2);
		//extracting the sensors
		jArray= (JsonArray)jRead.get("sensors");
		ArrayList<maven.DensityMap.Punto> sensors= new ArrayList<maven.DensityMap.Punto>();
		for(JsonElement je : jArray)
		{
			
			JsonObject jo = (JsonObject) je;
			double y=jo.get("lattitude").getAsDouble();
//			System.out.println("lattitude: "+y);
			double x=jo.get("longitude").getAsDouble();
//			System.out.println("longitudede: "+x);
			maven.DensityMap.Punto sensor1 =  ServiciosCoordenadas.CoordenadaAMatriz(r1.getFilas(), r1.getColumnas(), y1, y2, x1, x2, x, y);
//			System.out.println("filaSensor:"+ sensor1.getFila());
//			System.out.println("columnaSensor:"+ sensor1.getColumna());
//			System.out.println("filas ,"+r1.getFilas()+" columnas, "+r1.getColumnas());
			sensor1.setEsSensor(true);
			sensor1.setValor(jo.get("value").getAsFloat());
			sensor1.setAlcance(10);
			sensors.add(sensor1);
		}
//		System.out.println(sensors.size());
		
		r1.asignarSensores(sensors);
		r1.pintarRed();
		r1.rodearSensores();
		return ResponseEntity.status(HttpStatus.OK).body(r1.redToJson());
	}

//	
//	private String decodeUserIdFromJWT(String jwtHeader) {
//		String user_id = "";
//		//HMAC
//		try {
//			Algorithm algorithmHS = Algorithm.HMAC512(SECRET);
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//		    DecodedJWT jwt = JWT.decode(jwtHeader);
//		    // user is inside the jwt in the sub field
//		    String serializedUser = jwt.getSubject();
//		    Gson gson = new GsonBuilder().serializeNulls().create();
//			gson.serializeNulls();
//			Object user = gson.fromJson(serializedUser, Object.class);
//			LinkedTreeMap<Object, Object> user_map = (LinkedTreeMap<Object, Object>) user;
//			user_id = (String) user_map.get("id");
//		} catch (JWTDecodeException exception){
//		    //Invalid token
//		}
//		return jwtHeader;
//	}
}
