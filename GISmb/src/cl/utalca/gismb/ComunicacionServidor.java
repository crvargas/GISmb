package cl.utalca.gismb;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

public class ComunicacionServidor extends AsyncTask<Object, Integer, Object>{
	public String IP;
	private Context contexto;
	private ProgressDialog dialProgress;
	
	public ComunicacionServidor(){}
	
	public ComunicacionServidor(Context contexto){
		this.contexto = contexto;
		AdministradorPreferencias adm = new AdministradorPreferencias(contexto);
		IP = adm.getIP();
	}
	
	public boolean ObtnerConexion(){
		return false;
	}
	
	public String obtenerCapas(){
    	//ejemplo seguido en: http://blogs.tunelko.com/2012/01/04/intercambio-de-datos-con-json-en-android-sdk/
    	StringBuilder builder = new StringBuilder();
    	HttpClient clientehttp = new DefaultHttpClient();
    	HttpGet peticion = new HttpGet("http://10.0.2.2:8080/GISmbWS/Servlet?action=obtenerCapas");
    	
    	try{
    		//Toast.makeText(this,"hola ya estoy en el TRY",Toast.LENGTH_LONG).show();
    		HttpResponse respuesta = clientehttp.execute(peticion);
    		
    		//despues de hacer el execute no funciona...se va al tercer catch.
    		//Toast.makeText(this,"hola ya estoy en el despues execute",Toast.LENGTH_LONG).show();
    		
    		StatusLine stline = respuesta.getStatusLine();
    		int codigoStline = stline.getStatusCode();
    		//Toast.makeText(this,"codigo Status:"+codigoStline,Toast.LENGTH_LONG).show();
    		
    		if(codigoStline == 200){
    			builder.append("entre al if");
    			HttpEntity entidad = respuesta.getEntity();
    			InputStream contenido = entidad.getContent();
    			BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
				
    			String linea;
    			while((linea = lector.readLine())!=null){
    				builder.append(linea+"\n");
    			}
				
    		}
    		else{
    			Log.e(MainActivity.class.toString(), "obtener capas JSON salio mal");
    		}
    	}
    	catch(HttpResponseException re){
    		return "estoy en el catch 1";
    	}
    	catch(ClientProtocolException cpe){
    		//Toast.makeText(this,"hola estoy en el CATCH 2",Toast.LENGTH_LONG).show();
    		System.out.println(cpe.getMessage());
    		return "estoy en el catch 2";
    	}
    	catch(IOException ioe){
    		//Toast.makeText(this,"hola estoy en el CATCH 3"+ioe.getMessage(),Toast.LENGTH_LONG).show();
    		ioe.printStackTrace();
    		return "estoy en el catch 3";
    	}
    	return builder.toString();
    			
    }
	
	public void CerrarConexion(){}
	
	public ArrayList<Proyecto> obtenerProyectos(){
		
		ArrayList<Proyecto> proyectos = new ArrayList<Proyecto>();
		
		StringBuilder builder = new StringBuilder();
    	HttpClient clientehttp = new DefaultHttpClient();
    	HttpGet peticion = new HttpGet("http://"+IP+":8080/GISmbWS/Servlet?action=obtenerProyectos");
    	
    	try{
    		HttpResponse respuesta = clientehttp.execute(peticion);
    		System.out.println("Ejecuto la petición con la url");
    		
    		StatusLine stline = respuesta.getStatusLine();
    		int codigoStline = stline.getStatusCode();
    		System.out.println("codigo estatus: "+codigoStline);
    		
    		if(codigoStline == 200){
    			HttpEntity entidad = respuesta.getEntity();
    			InputStream contenido = entidad.getContent();
    			BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
				
    			String linea;
    			while((linea = lector.readLine())!=null){
    				builder.append(linea+"\n");
    			}
    			Gson gsonProyectos = new Gson();
    			proyectos = gsonProyectos.fromJson(builder.toString(), new TypeToken<ArrayList<Proyecto>>(){}.getType());
    			
    		}
    		else{
    			Log.e(MainActivity.class.toString(), "obtener proyectos JSON salio mal");
    		}
    		
    		
    	}
    	catch(HttpResponseException re){
    		re.printStackTrace();
    	}
    	catch(ClientProtocolException cpe){
    		//Toast.makeText(this,"hola estoy en el CATCH 2",Toast.LENGTH_LONG).show();
    		System.out.println(cpe.getMessage());
    		cpe.printStackTrace();
    	}
    	catch(IOException ioe){
    		//Toast.makeText(this,"hola estoy en el CATCH 3"+ioe.getMessage(),Toast.LENGTH_LONG).show();
    		ioe.printStackTrace();
    	}
    	return proyectos;
    			
	}
	
	public Imagen mostrarImagen(){
		
		Imagen resultadoImagen = new Imagen();
		//String urlImagen="";
		
		StringBuilder builder = new StringBuilder();
    	HttpClient clientehttp = new DefaultHttpClient();
    	HttpGet peticion = new HttpGet("http://"+IP+":8080/GISmbWS/Servlet?action=mostrarImagen");
    	
    	try{
    		HttpResponse respuesta = clientehttp.execute(peticion);
    		System.out.println("Ejecuto la petición con la url");
    		
    		StatusLine stline = respuesta.getStatusLine();
    		int codigoStline = stline.getStatusCode();
    		System.out.println("codigo estatus: "+codigoStline);
    		
    		if(codigoStline == 200){
    			HttpEntity entidad = respuesta.getEntity();
    			InputStream contenido = entidad.getContent();
    			BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
				
    			String linea;
    			while((linea = lector.readLine())!=null){
    				builder.append(linea+"\n");
    			}
    			Gson imagenGson = new Gson();
    			resultadoImagen = imagenGson.fromJson(builder.toString(), Imagen.class);
    			//urlImagen = builder.toString();
    			
    		}
    		else{
    			Log.e(MainActivity.class.toString(), "obtener String URL Imagen salio mal");
    		} 		
    		
    	}
    	catch(HttpResponseException re){
    		re.printStackTrace();
    	}
    	catch(ClientProtocolException cpe){
    		//Toast.makeText(this,"hola estoy en el CATCH 2",Toast.LENGTH_LONG).show();
    		System.out.println(cpe.getMessage());
    		cpe.printStackTrace();
    	}
    	catch(IOException ioe){
    		//Toast.makeText(this,"hola estoy en el CATCH 3"+ioe.getMessage(),Toast.LENGTH_LONG).show();
    		ioe.printStackTrace();
    	}
    	return resultadoImagen;
    			
	}
	
	public ArrayList<CapaVectorial> obtenerCapasVectorialesProyecto(int id){		
		Proyecto proyecto = new Proyecto();
		StringBuilder builder = new StringBuilder();
    	HttpClient clientehttp = new DefaultHttpClient();
    	HttpGet peticion = new HttpGet("http://"+IP+":8080/GISmbWS/Servlet?action=obtenerCapasProyecto&id="+id);
    	
    	try{
    		HttpResponse respuesta = clientehttp.execute(peticion);
    		System.out.println("Ejecuto la petición con la url");
    		
    		StatusLine stline = respuesta.getStatusLine();
    		int codigoStline = stline.getStatusCode();
    		System.out.println("codigo estatus: "+codigoStline);
    		
    		if(codigoStline == 200){
    			HttpEntity entidad = respuesta.getEntity();
    			InputStream contenido = entidad.getContent();
    			BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
    			
    			String linea;
    			while((linea = lector.readLine())!=null){
    				builder.append(linea+"\n");
    			}
    			
    			Gson objetoJson = new Gson();
    			proyecto = objetoJson.fromJson(builder.toString(), Proyecto.class);
    			
    		}
    		else{
    			Log.e(MainActivity.class.toString(), "obtener proyectos JSON salio mal");
    		}
    		
    		
    	}
    	catch(HttpResponseException re){
    		re.printStackTrace();
    	}
    	catch(ClientProtocolException cpe){
    		System.out.println(cpe.getMessage());
    		cpe.printStackTrace();
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    	}
		
		return proyecto.getCapasVectoriales();
		
	}
	
	public int guardarProyecto(Proyecto proyecto){
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar el proyecto a formato JSON para enviarlo al servidor.
		Gson gsonProyecto = new Gson();
		String proy = gsonProyecto.toJson(proyecto);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarProyecto"));
                pairs.add(new BasicNameValuePair("proyecto", proy));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Exitooo");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea+"\n");
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int idProyectoGuardado = jsonObject.getInt("idProyectoGuardado");
                	return idProyectoGuardado;
                }
                else{
                	
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}  
	
		
	}
	public int guardarFiguraGeometrica(FiguraGeometrica figura, int idCapa, String tipoCapa){
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la figurageometrica en Json para enviarla a Json
		Gson gsonfigura = new Gson();
		String figuraJson = gsonfigura.toJson(figura);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarFiguraGeometrica"));
                pairs.add(new BasicNameValuePair("FiguraGeometrica", figuraJson));
                pairs.add(new BasicNameValuePair("idCapa", idCapa+""));
                pairs.add(new BasicNameValuePair("tipoCapa", tipoCapa));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para guardar la figura");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea+"\n");
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int lineasModificadas = jsonObject.getInt("lineasModificadas");
                	return lineasModificadas;
                }
                else{
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  
		
	}
	public int guardarCapaVectorial(CapaVectorial capa, int idProyecto)
	{		
		capa.generarColorCapa();
		
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la capaVectorial en Json para enviarla a Json
		Gson gsonCapa = new Gson();
		String capaJson = gsonCapa.toJson(capa);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarCapaVectorial"));
                pairs.add(new BasicNameValuePair("CapaVectorial", capaJson));
                pairs.add(new BasicNameValuePair("idProyecto", idProyecto+""));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para guardar la capaVectorial, metodo guardarCapaVectorial ComunicacionDB");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea);
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int idCapaNueva = jsonObject.getInt("idCapa");
                	return idCapaNueva;
                }
                else{
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  
		
	}
	
	//Guardar varios atributos numericos a la vez.
	public int guardarAtributosNumericos(ArrayList<Integer> idsFiguras, AtributoNumerico atribNumerico){
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la atributo en Json para enviarla a Json
		Gson gsonatributo = new Gson();
		String atributoJson = gsonatributo.toJson(atribNumerico);
		
		//Enviar la lista con los ids
		Gson gsonIdsFiguras = new Gson();
		String ids = gsonIdsFiguras.toJson(idsFiguras);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarAtributoNumerico"));
                pairs.add(new BasicNameValuePair("AtributoNumerico", atributoJson));
                pairs.add(new BasicNameValuePair("idsFiguraGeometrica", ids+""));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para guardar el atributo");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea);
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int lineasModificadas = jsonObject.getInt("atributosGuardados");
                	return lineasModificadas;
                }
                else{
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  
		
	}
	public int guardarAtributosTextos(ArrayList<Integer> idsFiguras, AtributoTexto atribTexto){
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la atributo en Json para enviarla a Json
		Gson gsonatributo = new Gson();
		String atributoJson = gsonatributo.toJson(atribTexto);
		
		//Enviar la lista con los ids
		Gson gsonIdsFiguras = new Gson();
		String ids = gsonIdsFiguras.toJson(idsFiguras);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarAtributoTexto"));
                pairs.add(new BasicNameValuePair("AtributoTexto", atributoJson));
                pairs.add(new BasicNameValuePair("idsFiguraGeometrica", ids+""));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para guardar el atributo");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea);
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int lineasModificadas = jsonObject.getInt("atributosGuardados");
                	return lineasModificadas;
                }
                else{
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  
		
	}
	public int guardarAtributosLogicos(ArrayList<Integer> idsFiguras, AtributoLogico atribLogico){
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la atributo en Json para enviarla a Json
		Gson gsonatributo = new Gson();
		String atributoJson = gsonatributo.toJson(atribLogico);
		
		//Enviar la lista con los ids
		Gson gsonIdsFiguras = new Gson();
		String ids = gsonIdsFiguras.toJson(idsFiguras);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarAtributoLogico"));
                pairs.add(new BasicNameValuePair("AtributoLogico", atributoJson));
                pairs.add(new BasicNameValuePair("idsFiguraGeometrica", ids+""));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para guardar el atributo");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea);
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int lineasModificadas = jsonObject.getInt("atributosGuardados");
                	return lineasModificadas;
                }
                else{
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  
		
	}
	public int actualizarValoresAtributos(FiguraGeometrica figura)
	{
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la figura, que contiene los arreglos de atributos, en Json para enviarla al servidor
		Gson gsonfigura = new Gson();
		String figuraJson = gsonfigura.toJson(figura);
		//Enviarlo al servidor
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "actualizarValoresAtributos"));
                pairs.add(new BasicNameValuePair("figuraGeometrica", figuraJson));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para actualizarValoresAtributos, mensaje desde el cliente");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	/*
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea);
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int lineasModificadas = jsonObject.getInt("lineasModificadas");
                	return lineasModificadas;
                	*/
                }
                else{
                	return -1;}
                return 1;
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		/*catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  */
	}
	
	public boolean eliminarProyecto(int id)
	{
		String peticion = "http://"+IP+":8080/GISmbWS/Servlet?action=eliminarProyecto&id="+id;
		return comunicacionSinJSON(peticion);
	}
	
	public boolean eliminarCapaVectorial(int idCapa)
	{
		String peticion = "http://"+IP+":8080/GISmbWS/Servlet?action=eliminarCapaVectorial&id="+idCapa;
		return comunicacionSinJSON(peticion);
	}
	public boolean eliminarFiguraGeometrica(int idFigura)
	{
		String peticion = "http://"+IP+":8080/GISmbWS/Servlet?action=eliminarFiguraGeometrica&id="+idFigura;
		return comunicacionSinJSON(peticion);
	}
	
	public boolean comunicacionSinJSON(String rutaServicio)
	{
		StringBuilder builder = new StringBuilder();
    	HttpClient clientehttp = new DefaultHttpClient();
    	HttpGet peticion = new HttpGet(rutaServicio);
    	
    	try{
    		HttpResponse respuesta = clientehttp.execute(peticion);
    		System.out.println("Ejecuto la petición con la url");
    		
    		StatusLine stline = respuesta.getStatusLine();
    		int codigoStline = stline.getStatusCode();
    		System.out.println("codigo estatus: "+codigoStline);
    		
    		if(codigoStline == 200){
    			HttpEntity entidad = respuesta.getEntity();
    			InputStream contenido = entidad.getContent();
    			BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
    			
    			String linea;
    			while((linea = lector.readLine())!=null){
    				builder.append(linea);
    			}
    			//El metodo en el servidor debe retornar true o false para que la sgte parte funcione.
    			String resultado = builder.toString();
    			if(resultado.equals("true")){
    				return true;
    			}
    			else{
    				return false;
    			}
    			
    		}
    		else{
    			Log.e(MainActivity.class.toString(), "salio mal"+rutaServicio);
    			return false;
    		}
    		
    		
    	}
    	catch(HttpResponseException re){
    		re.printStackTrace();
    		return false;
    	}
    	catch(ClientProtocolException cpe){
    		System.out.println(cpe.getMessage());
    		cpe.printStackTrace();
    		return false;
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		return false;
    	}
		
		
	}
	/***
	 * Permite la establecer la conexión con el servidor a traves de HTTPClient
	 * Recibe los parametros que seran enviados. Ejemplo: la accion a ejecutar al servidor y los objetos.
	 * @return codigo de estado de la comunicacion
	 */
	public int comunicacionServidor(ArrayList<BasicNameValuePair> pairs)
	{
		//Metodo en construccion...no es ocupado por ahora...
		int codigo=0;
		StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                //
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto correctamente al servidor: Metodo comunicacionServidor");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea+"\n");
        			}
        			JSONObject jsonObject = new JSONObject(builder.toString());               	
                	int lineasModificadas = jsonObject.getInt("lineasModificadas");
                	return lineasModificadas;
                }
                else{
                return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 
		catch (JSONException e) {
			System.out.println("EXCEPTION JSON");
			e.printStackTrace();
			return -1;
		}  
	}
	
	public int actualizarVisibilidadCapa(ArrayList<CapaVectorial> capas)
	{
		//StringBuilder builder = new StringBuilder(); // Para leer la o las lineas de respuesta.
		//Transformar la atributo en Json para enviarla a Json
		Gson gsonListaCapas = new Gson();
		String capasJson = gsonListaCapas.toJson(capas);
				
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "actualizarVisisbilidadCapa"));
                pairs.add(new BasicNameValuePair("capas", capasJson));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para actualizar visibilidad de la capa");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();
                	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 			
	}
	public int guardarPosicionActualCamara(Proyecto proyecto)
	{
		Gson gsonProy = new Gson();
		String proyJSON = gsonProy.toJson(proyecto);
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "guardarPosicionActualCamara"));
                pairs.add(new BasicNameValuePair("proyecto", proyJSON));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para guardar posicion Actual camara");
                	//Para leer la linea de respuesta que debiese ser el id del proyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;
                	}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 	
		
	}
	public int eliminarAtributosFiguraGeometrica(FiguraGeometrica figura){
		Gson gsonFigura = new Gson();
		String figuraJSON = gsonFigura.toJson(figura);
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "eliminarAtributosFiguraGeometrica"));
                pairs.add(new BasicNameValuePair("figura", figuraJSON));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para eliminar Atributos FiguraGeometrica");
                	//Para leer la linea de respuesta eliminarAtributosFiguraGeometrica.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;
                	}
                
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 	
	}
	public int modificarProyecto(Proyecto proyectoModificado)
	{
		Gson gsonProeycto = new Gson();
		String proyectoJSON = gsonProeycto.toJson(proyectoModificado);
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "modificarProyecto"));
                pairs.add(new BasicNameValuePair("proyecto", proyectoJSON));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para modificar Proyecto");
                	//Para leer la linea de respuesta modificarProyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 	
	}
	public int modificarCapaVectorial(CapaVectorial capaModificada)
	{
		Gson gsonCapa = new Gson();
		String capaJson = gsonCapa.toJson(capaModificada);
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "modificarCapaVectorial"));
                pairs.add(new BasicNameValuePair("capaVectorial", capaJson));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para modificar Capa");
                	//Para leer la linea de respuesta modificarProyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 	
	}
	public int modificarFiguraGeometrica(FiguraGeometrica figura,String tipo)
	{
		Gson gsonfigura = new Gson();
		String figuraJson = gsonfigura.toJson(figura);
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "modificarFiguraGeometrica"));
                pairs.add(new BasicNameValuePair("figuraGeometrica", figuraJson));
                pairs.add(new BasicNameValuePair("tipoFigura", tipo));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para modificar Figura Geometrica");
                	//Para leer la linea de respuesta modificarProyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 	
	}
	public int modificarNombreAtributos(FiguraGeometrica figura){
		Gson gsonfigura = new Gson();
		String figuraJson = gsonfigura.toJson(figura);
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "modificarNombresAtributos"));
                pairs.add(new BasicNameValuePair("figuraGeometrica", figuraJson));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para modificar Nombres Atributos Figura Geometrica");
                	//Para leer la linea de respuesta modificarProyecto.
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
                	String linea = lector.readLine();              	
                	int lineasModificadas = Integer.parseInt(linea);
                	return lineasModificadas;
                }
                else{
                	return -1;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return -1;}
        catch (ClientProtocolException e) {e.printStackTrace();return -1;}
        catch (IOException e) {e.printStackTrace();return -1;} 	
		
	}
	public ArrayList<ResultadoEstadistica> calculoEstadisticasBasicas(int idCapa)
	{
		ArrayList<ResultadoEstadistica> resultados = new ArrayList<ResultadoEstadistica>();
		StringBuilder builder = new StringBuilder();
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "estadisticasBasicas"));
                pairs.add(new BasicNameValuePair("idCapa", Integer.toString(idCapa)));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para calculos Estadísticos");
                	//Para leer la respuesta
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));     
                	
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea+"\n");
        			}
        			Gson gsonResultados = new Gson();
        			resultados = gsonResultados.fromJson(builder.toString(), new TypeToken<ArrayList<ResultadoEstadistica>>(){}.getType());
        			//proyectos = gsonProyectos.fromJson(builder.toString(), new TypeToken<ArrayList<Proyecto>>(){}.getType());
                }
                else{
                	return null;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return null;}
        catch (ClientProtocolException e) {e.printStackTrace();return null;}
        catch (IOException e) {e.printStackTrace();return null;} 
		return resultados;
	}
	public String[] obtenerNombresAtributosNumericosPorCapa(int idCapa)
	{
		String[] nombres= {};
		StringBuilder builder = new StringBuilder();
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "obtenerAtributosNumericosCapa"));
                pairs.add(new BasicNameValuePair("idCapa", Integer.toString(idCapa)));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para obtener Nombres Atributos por capa");
                	//Para leer la respuesta
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));     
                	
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea+"\n");
        			}
        			Gson gson = new Gson();
        			nombres = gson.fromJson(builder.toString(),String[].class);
        			
                }
                else{
                	return null;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return null;}
        catch (ClientProtocolException e) {e.printStackTrace();return null;}
        catch (IOException e) {e.printStackTrace();return null;} 
		return nombres;
	}
	public Imagen Interpolacion(String nombreAtributo, int idCapa){
	
		Imagen imagenTiff = new Imagen();
		StringBuilder builder = new StringBuilder();
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "interpolacion"));
                pairs.add(new BasicNameValuePair("idCapa", Integer.toString(idCapa)));
                pairs.add(new BasicNameValuePair("nombreAtributo", nombreAtributo));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para hacer la interpolación por capa");
                	//Para leer la respuesta
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));     
                	
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea+"\n");
        			}
        			Gson gson = new Gson();
        			imagenTiff = gson.fromJson(builder.toString(),Imagen.class);
        			
                }
                else{
                	return null;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return null;}
        catch (ClientProtocolException e) {e.printStackTrace();return null;}
        catch (IOException e) {e.printStackTrace();return null;} 
		
		return imagenTiff;
    			
	}
	public boolean verificarPoligono(FiguraGeometrica poligono){
		boolean esValido = false;
		Gson gsonfigura = new Gson();
		String figuraJson = gsonfigura.toJson(poligono);
		StringBuilder builder = new StringBuilder();
		try
        {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://"+IP+":8080/GISmbWS/Servlet");
                ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                pairs.add(new BasicNameValuePair("accion", "verificarPoligono"));
                pairs.add(new BasicNameValuePair("FiguraGeometrica", figuraJson));
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse resp = httpClient.execute(httpPost);
                StatusLine status = resp.getStatusLine();
                int codigo = status.getStatusCode();
                
                if(codigo == 200){
                	System.out.println("Se conecto para verificar Poligono");
                	//Para leer la respuesta
                	HttpEntity httpE = resp.getEntity();
                	InputStream contenido = httpE.getContent();               	
                	BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));     
                	
                	String linea;
        			while((linea = lector.readLine())!=null){
        				builder.append(linea);
        			}
        			JSONObject jsonObject;
					jsonObject = new JSONObject(builder.toString());
					
                	esValido = jsonObject.getBoolean("esValido");
                }
                else{
                	return false;}         
        }
        catch (UnsupportedEncodingException e) {e.printStackTrace();return false;}
        catch (ClientProtocolException e) {e.printStackTrace();return false;}
        catch (IOException e) {e.printStackTrace();return false;} 
		catch (JSONException e) {
			e.printStackTrace();return false;
		}               	
		
		return esValido;
	}
	public Bitmap obtenerImagenPNGServidor(String urlImagen){
		URL url;
		
		try {
			url = new URL(urlImagen);
			return BitmapFactory.decodeStream(url.openConnection().getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public JSONArray datosJSONarray(String json){
		
		try{	
		JSONArray jsonarray = new JSONArray(json);
		return jsonarray;
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPreExecute(){
		    dialProgress = new ProgressDialog(contexto);
			dialProgress.setMessage("En Progreso...");
			dialProgress.show();			
	}
	@Override
	protected Object doInBackground(Object... params) {
		AccionesServlet accion = (AccionesServlet) params[0];
		
		switch (accion) {
		case OBTENER_PROYECTOS:
			return obtenerProyectos();
		case OBTENER_CAPAS_VECTORIALES_PROYECTO:
			return obtenerCapasVectorialesProyecto((Integer)params[1]);
		case GUARDAR_PROYECTO:
			return guardarProyecto((Proyecto)params[1]);
		case GUARDAR_FIGURA_GEOMETRICA:
			return guardarFiguraGeometrica((FiguraGeometrica)params[1],(Integer)params[2],(String)params[3]);
		case GUARDAR_CAPA_VECTORIAL:
			return guardarCapaVectorial((CapaVectorial)params[1], (Integer)params[2]);
		case GUARDAR_ATRIBUTOS_NUMERICOS:
			return guardarAtributosNumericos((ArrayList<Integer>)params[1], (AtributoNumerico)params[2]);
		case GUARDAR_ATRIBUTOS_TEXTO:
			return guardarAtributosTextos((ArrayList<Integer>)params[1], (AtributoTexto)params[2]);
		case GUARDAR_ATRIBUTOS_LOGICOS:
			return guardarAtributosLogicos((ArrayList<Integer>)params[1], (AtributoLogico)params[2]);
		case ACTUALIZAR_VISIBILIDAD_CAPA:
			return actualizarVisibilidadCapa((ArrayList<CapaVectorial>) params[1]);
		case ACTUALIZAR_VALORES_ATRIBUTOS:
			return actualizarValoresAtributos((FiguraGeometrica) params[1]);
		case ELIMINAR_PROYECTO:
			return eliminarProyecto((Integer) params[1]);
		case ELIMINAR_CAPA_VECTORIAL:
			return eliminarCapaVectorial((Integer) params[1]);
		case ELIMINAR_FIGURA_GEOMETRICA:
			return eliminarFiguraGeometrica((Integer) params[1]);
		case MOSTRAR_IMAGEN:
			return mostrarImagen();
		case GUARDAR_POSICION_ACTUAL_CAMARA:
			return guardarPosicionActualCamara((Proyecto)params[1]);
		case ELIMINAR_ATRIBUTOS_FIGURA_GEOMETRICA:
			return eliminarAtributosFiguraGeometrica((FiguraGeometrica)params[1]);
		case MODIFICAR_PROYECTO:
			return modificarProyecto((Proyecto) params[1]);
		case MODIFICAR_CAPA_VECTORIAL:
			return modificarCapaVectorial((CapaVectorial) params[1]);
		case MODIFICAR_FIGURA_GEOMETRICA:
			return modificarFiguraGeometrica((FiguraGeometrica) params[1],(String)params[2]);
		case MODIFICAR_NOMBRE_ATRIBUTO:
			return modificarNombreAtributos((FiguraGeometrica) params[1]);
		case CALCULOS_ESTADISTICA_BASICA:
			return calculoEstadisticasBasicas((Integer)params[1]);
		case OBTENER_NOMBRES_ATRIBUTOS_NUMERICOS:
			return obtenerNombresAtributosNumericosPorCapa((Integer)params[1]);
		case INTERPOLACION:
			return Interpolacion((String)params [1],(Integer)params[2]);
		case VERIFICAR_POLIGONO:
			return verificarPoligono((FiguraGeometrica)params[1]);
		case IMAGEN_INTERPOLACION:
			return obtenerImagenPNGServidor((String)params[1]);
		default:
			break;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Object result)
	{
		if(dialProgress.isShowing())
			dialProgress.dismiss();
	}
}
