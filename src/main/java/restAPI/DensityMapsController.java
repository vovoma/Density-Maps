package restAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	private static final String APPLICATION_JSON_NGSI = "application/ngsi+json";
	private static final String APPLICATION_JSON_LD = "application/ld+json";
	
	private HttpServletRequest context; 
	
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

	         // Reading file
	         String linea;
	         while((linea=br.readLine())!=null)
	            SwaggerJson=SwaggerJson+"\n"+linea;
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }finally{
	         // In the finally we close the file to be sure that
	         // it close although it creates an exception
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
	@RequestMapping(value = "/densityMap", method = RequestMethod.POST, headers="Accept=application/json", consumes = {"application/json"},  produces= {MediaType.APPLICATION_JSON_VALUE,APPLICATION_JSON_LD,APPLICATION_JSON_NGSI})
	@ResponseBody
	public ResponseEntity getDensityMap(@RequestBody String body, @RequestHeader("X-Authorization-s4c") String jwtHeader) throws IllegalArgumentException, UnsupportedEncodingException {		
		//HMAC
		Algorithm algorithmHS = Algorithm.HMAC512(SECRET);
		String  acceptHeader= context.getHeader("Accept");
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
		//extracting the sensors
		jArray= (JsonArray)jRead.get("sensors");
		ArrayList<maven.DensityMap.Punto> sensors= new ArrayList<maven.DensityMap.Punto>();
		for(JsonElement je : jArray)
		{
			
			JsonObject jo = (JsonObject) je;
			double y=jo.get("lattitude").getAsDouble();
			double x=jo.get("longitude").getAsDouble();
			maven.DensityMap.Punto sensor1 =  ServiciosCoordenadas.CoordenadaAMatriz(r1.getFilas(), r1.getColumnas(), y1, y2, x1, x2, x, y);
			sensor1.setEsSensor(true);
			sensor1.setValor(jo.get("value").getAsFloat());
			sensor1.setAlcance(10);
			sensors.add(sensor1);
		}
		r1.asignarSensores(sensors);
		r1.pintarRed();
		r1.rodearSensores();
		String json = r1.redToJson2();
		if(acceptHeader.equals("application/ld+json")) {
			json = transformJsonLd(json);
		}
		return ResponseEntity.status(HttpStatus.OK).body(json);
	}
	
	@RequestMapping(value = "/densityMap/reversed", method = RequestMethod.POST, headers="Accept=application/json", consumes = {"application/json"} , produces= {MediaType.APPLICATION_JSON_VALUE,APPLICATION_JSON_LD,APPLICATION_JSON_NGSI})
	@ResponseBody
	public ResponseEntity getDensityMapReverse(@RequestBody String body, @RequestHeader("X-Authorization-s4c") String jwtHeader) throws IllegalArgumentException, UnsupportedEncodingException {		
		//HMAC
		Algorithm algorithmHS = Algorithm.HMAC512(SECRET);
		String  acceptHeader= context.getHeader("Accept");
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
		//extracting the sensors
		jArray= (JsonArray)jRead.get("sensors");
		ArrayList<maven.DensityMap.Punto> sensors= new ArrayList<maven.DensityMap.Punto>();
		for(JsonElement je : jArray)
		{
			
			JsonObject jo = (JsonObject) je;
			double y=jo.get("lattitude").getAsDouble();
			double x=jo.get("longitude").getAsDouble();
			maven.DensityMap.Punto sensor1 =  ServiciosCoordenadas.CoordenadaAMatriz(r1.getFilas(), r1.getColumnas(), y1, y2, x1, x2, x, y);
			sensor1.setEsSensor(true);
			sensor1.setValor(jo.get("value").getAsFloat());
			sensor1.setAlcance(10);
			sensors.add(sensor1);
		}		
		r1.asignarSensores(sensors);
		r1.pintarRed();
		r1.rodearSensores();
		String json = r1.redToJson();
		if(acceptHeader.equals("application/ld+json")) {
			json = transformJsonLd(json);
		}
		return ResponseEntity.status(HttpStatus.OK).body(json);
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
	
	
	//add the context of geojson to the (not ld) json and returns the jsonld as a string
	private String transformJsonLd(String originalJson) {
		JsonObject jsonobj = new JsonObject();
		JsonParser jp = new JsonParser();
		jsonobj = jp.parse(originalJson).getAsJsonObject();
		jsonobj.addProperty("type", "Feature");
		
		JsonObject properties = new JsonObject();
		properties.addProperty("title", "sensor");
		properties.addProperty("description", "sensors");
		jsonobj.add("properties", properties);
		
		JsonObject geometry = new JsonObject();
		geometry.addProperty("type", "MultiPoint");
		JsonArray coordinates = new JsonArray();
		JsonArray sensorsRead =(JsonArray) jsonobj.get("sensors");
		JsonArray auxcorw = new JsonArray();
		for(JsonElement je : sensorsRead) {
			JsonObject jo = (JsonObject)je;
			JsonArray corrdread = (JsonArray)jo.get("coordinates");
			coordinates.add(corrdread.get(0));
			coordinates.add(corrdread.get(1));
			auxcorw.add(corrdread);
		}	
		geometry.add("coordinates",auxcorw);
		jsonobj.add("geometry", geometry);
		
		
		jsonobj.add("@context" , getContextGeoJson());
		return jsonobj.toString();
	}
	
	
	//Create the context used in geojson
	private JsonObject getContextGeoJson() {
		JsonObject context = new JsonObject();
			
		context.addProperty("geojson", "https://purl.org/geojson/vocab#");
		context.addProperty("Feature", "geojson:Feature");
		context.addProperty("FeatureCollection", "geojson:FeatureCollection");
		context.addProperty("GeometryCollection", "geojson:GeometryCollection");
		context.addProperty("LineString", "geojson:LineString");
		context.addProperty("MultiLineString", "geojson:MultiLineString");
		context.addProperty("MultiPoint", "geojson:MultiPoint");
		context.addProperty("MultiPolygon", "geojson:MultiPolygon");
		context.addProperty("Point", "geojson:Point");
		context.addProperty("Polygon", "geojson:Polygon");
		
		JsonObject aux = new JsonObject();
		aux.addProperty("@container", "@list" );
		aux.addProperty("@id", "geojson:bbox" );		
		context.add("bbox", aux);
		
		aux = new JsonObject();
		aux.addProperty("@container", "@list" );
		aux.addProperty("@id", "geojson:coordinates" );		
		context.add("coordinates", aux);
		
		aux = new JsonObject();
		aux.addProperty("@container", "@set" );
		aux.addProperty("@id", "geojson:features" );
		context.add("features",aux);
		
		context.addProperty("geometry", "geojson:geometry");
		context.addProperty("id", "@id");
		context.addProperty("properties", "geojson:properties");
		context.addProperty("type", "@type");
		context.addProperty("description", "http://purl.org/dc/terms/description");
		context.addProperty("title", "http://purl.org/dc/terms/title");
			
		return context;
	}
}
