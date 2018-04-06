package restAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import maven.DensityMap.Punto;
import maven.DensityMap.*;


@RestController
public class DensityMapsController<Punto> {

	private static final String SECRET = "_JWT_TOKEN_KEY_";
	private static final String APPLICATION_JSON_NGSI = "application/ngsi+json";
	private static final String APPLICATION_JSON_LD = "application/ld+json";
	
	@Autowired
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
	@RequestMapping(value = "/densityMap", method = RequestMethod.POST,  produces= {MediaType.APPLICATION_JSON_VALUE,APPLICATION_JSON_LD,APPLICATION_JSON_NGSI})
	@ResponseBody
	public ResponseEntity getDensityMap(@RequestBody String body, @RequestHeader("X-Authorization-s4c") String jwtHeader) throws IllegalArgumentException, UnsupportedEncodingException {		
		String  acceptHeader= context.getHeader("Accept");
		//We don't use the returned user_id but we do the JWT verification
		decodeUserIdFromJWT(jwtHeader);
		String json = Red.redConstructor(body);
		if(acceptHeader.equals("application/ld+json")) {
			json = transformJsonLd(json);
		}
		return ResponseEntity.status(HttpStatus.OK).body(json);
	}
	
	@RequestMapping(value = "/densityMap/reversed", method = RequestMethod.POST, produces= {MediaType.APPLICATION_JSON_VALUE,APPLICATION_JSON_LD,APPLICATION_JSON_NGSI})
	@ResponseBody
	public ResponseEntity getDensityMapReverse(@RequestBody String body, @RequestHeader("X-Authorization-s4c") String jwtHeader) throws IllegalArgumentException, UnsupportedEncodingException {		
		String  acceptHeader= context.getHeader("Accept");
		//We don't use the returned user_id but we do the JWT verification
		decodeUserIdFromJWT(jwtHeader);
		String json = Red.redConstructorReverse(body);
		if(acceptHeader.equals("application/ld+json")) {
			json = transformJsonLd(json);
		}
		return ResponseEntity.status(HttpStatus.OK).body(json);
	}

	private String decodeUserIdFromJWT(String jwtHeader) {
		jwtHeader = jwtHeader.replace("Bearer ","");
		String user_id = "";
		//HMAC
		try {
			Algorithm algorithmHS = Algorithm.HMAC512(SECRET);
			JWTVerifier verifier = JWT.require(algorithmHS)
			        .withIssuer("s4c.microservices.authorization")
			        .build(); //Reusable verifier instance
			DecodedJWT jwt = verifier.verify(jwtHeader);
			
		    //DecodedJWT jwt = JWT.decode(jwtHeader);
		    // user is inside the jwt in the sub field
		    String serializedUser = jwt.getSubject();
		    Gson gson = new GsonBuilder().serializeNulls().create();
			gson.serializeNulls();
			Object user = gson.fromJson(serializedUser, Object.class);
			LinkedTreeMap<Object, Object> user_map = (LinkedTreeMap<Object, Object>) user;
			user_id = (String) user_map.get("id").toString();
			byte[] encodedBytes = Base64.encodeBase64(user_id.getBytes());
			user_id = new String(encodedBytes);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return user_id;
	}
	
	
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
