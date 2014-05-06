

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.intamap.InterpolateException;
import org.intamap.InterpolateRequest;
import org.intamap.InterpolateResponse;
import org.intamap.InterpolateService;
import org.intamap.generators.GeneratorException;
import org.intamap.generators.GeoTIFFGenerator;
import org.intamap.om.ObservationCollection;
import org.intamap.parsers.ParserException;
import org.intamap.parsers.StringParser;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;



/**
 * Servlet implementation class 
 */
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String rutaImagen = "/home/cristina/ImagenesServer/geoTIFF/geotiffs";   
    private String rutaImagenPng ="/home/cristina/ImagenesServer/geoPNG";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.setContentType("text/html");
        //PrintWriter out = response.getWriter();
        //out.println("<html><body><h1>Test Servlet </h1></body></html>");
		BaseDeDatos BDgis = new BaseDeDatos();
		String dato;
		String action = request.getParameter("action");
		System.out.println(action);

		try{
		if(request.getParameter("action").equals("obtenerCapas")){

			CapaVectorial cv = new CapaVectorial();
			cv = BDgis.obtenerCapaVectorial(1);
			BDgis.cerrarConexion();
	
        	Gson gs = new Gson();
        	dato = gs.toJson(cv);
        	
        	response.setContentType("application/json;charset=UTF-8");//Formato del contenido de respuesta del servlet.
            PrintWriter out = response.getWriter();
            out.print(dato);
            
		}
		else if(request.getParameter("action").equals("obtenerProyectos")){
			ArrayList<Proyecto> proyectosgis;
			proyectosgis = BDgis.obtenerProyectos();
			BDgis.cerrarConexion();
			
			Gson proyectosGson = new Gson();
			String proystring = proyectosGson.toJson(proyectosgis);
		
			response.setContentType("application/json;charset=UTF-8");//Formato del contenido de respuesta del servlet.
	        PrintWriter out = response.getWriter();
	        out.print(proystring);
		}
		//Prueba conexion intamap
		else if(request.getParameter("action").equals("interpolar"))
		{
			String exito="";
			try {
			    // parse csv file
			    File csvFile = new File("/home/cristina/Programacion/workspace/GISmbWS/example-observations.csv");
			    ObservationCollection observations = StringParser.parseCSV(csvFile, 27700);
			    // create request
			    InterpolateRequest solicitud = new InterpolateRequest(observations);
			    solicitud.setMethodName("automap");

			    // add statistics to predict
			    solicitud.addStatisticToPredict("Mean");
			    solicitud.addStatisticToPredict("Variance");

			    // send request http://intamap.geo.uu.nl:8180/intamap/WebProcessingService
			    // offline: http://intamap.aston.ac.uk:8080/intamap/WebProcessingService
			    InterpolateService service = new InterpolateService("http://intamap.geo.uu.nl:8180/intamap/WebProcessingService");
			    InterpolateResponse respuesta = service.sendRequest(solicitud);
			    // generate geotiffs
			    File outputFolder = new File("geotiffs");
			    if (!outputFolder.isDirectory()) outputFolder.mkdir();
			    GeoTIFFGenerator generator = new GeoTIFFGenerator();
			    ArrayList<File> generatedFiles = generator.generateGeoTIFFs(respuesta, outputFolder);
			    for (File file : generatedFiles) {
			    	exito = exito + " "+file.getName();
			        System.out.println("Generated " + file.getName());
			    }
			    
			    response.setContentType("text/plain;charset=UTF-8");//Formato del contenido de respuesta del servlet.
	            PrintWriter out = response.getWriter();
	            out.print(exito);
			}
			catch (ParserException e1) {
			    System.err.println("Error parsing CSV file. " + e1.getMessage());
			}
			catch (IOException e2) {
			    System.err.println("Could not open CSV file/write GeoTIFFs. " + e2.getMessage());
			}
			catch (InterpolateException e3) {
			    System.err.println("INTAMAP returned an error during interpolation. " + e3.getMessage());
			    e3.printStackTrace();
			}
			catch (GeneratorException e4) {
			    System.err.println("Error generating GeoTIFFs. " + e4.getMessage());
			}
		}
		else if(request.getParameter("action").equals("obtenerCapasProyecto")){
			//Obtiene las todas las capas que pertenecen al proyecto
			//int id_proyecto = request.;
			ArrayList<CapaVectorial> capasProyecto = new ArrayList<CapaVectorial>();
			int id = Integer.parseInt(request.getParameter("id"));
			capasProyecto = BDgis.obtenerCapasVectoriales(id);
			BDgis.cerrarConexion();
			
			JSONObject proyecto = new JSONObject();
			proyecto.put("id", id);
			
			//Usando libreria Gson
			Proyecto proyecto_Con_Capas = new Proyecto();
			proyecto_Con_Capas.setId(1);
			proyecto_Con_Capas.setCapasVectoriales(capasProyecto);
			
			Gson proyectoGson = new Gson();
			String proyectoJson = proyectoGson.toJson(proyecto_Con_Capas);
		
			response.setContentType("application/json;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        proyecto.toString();
	        out.print(proyectoJson);
		}
		else if(request.getParameter("action").equals("mostrarImagen")){
			//Hacer algo para enviarle la imagen (url) al cliente.
			Imagen imagenResultado = new Imagen();
			ParseadorGdal parseador = new ParseadorGdal();
			//1. Transformar la imagen de interes en formato google.tif
			String rutaTifSalida = parseador.transformarAProyeccionGoogle(
					"/home/cristina/ImagenesServer/geoTIFF/albers27.tif", "Out.tif");
			//2. Leer la información del geotiff (centro, noreste y suroeste).
			parseador.ejecutarGdalInfo(rutaTifSalida);
			imagenResultado.setNoreste(parseador.obtenerCoordenadasBorde(parseador.NORESTE));
			imagenResultado.setSuroeste(parseador.obtenerCoordenadasBorde(parseador.SUROESTE));
			imagenResultado.setCentro(parseador.obtenerCoordenadasBorde(parseador.CENTRO));
			
			//3. Transformar la imagen a png
			String rutaSalidaPng = parseador.transformarAPng(rutaTifSalida, "out.png");
			imagenResultado.setNombreArchivo("out.png");
			//String url = "http://werowero.com/wp-content/uploads/2012/04/springfield_view_1.jpg";
		
			//Trasformar el objeto imagen a JSON para enviarlo al cliente.
			Gson gsonImagen = new Gson();
			String imagenJSON = gsonImagen.toJson(imagenResultado);
			response.setContentType("application/json;charset=UTF-8");
			//response.setContentType("text/plain;charset=UTF-8");//Formato del contenido de respuesta del servlet.
	        PrintWriter out = response.getWriter();
	        out.print(imagenJSON);
	        //out.print("http://190.162.107.192:8080/imagenes/eslovaquia.png");
		}
		else if(request.getParameter("action").equals("eliminarProyecto")){
			//Eliminar el proyecto en cascada con el id del mismo
			int id=0;
			id = Integer.parseInt(request.getParameter("id"));
			boolean elimino = BDgis.eliminarProyecto(id);
			BDgis.cerrarConexion();
			
			//Gson respuestaGson = new Gson();
			//String respuestaJson = respuestaGson.toJson(elimino);
		
			//Como la respuesta solo es un true o false se lo envio en un texto plano y asi no crear un JSON
			//inecesario.
			response.setContentType("text/plain;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        out.print(elimino);
		}
		else if(request.getParameter("action").equals("eliminarCapaVectorial")){
			//Eliminar la capaVectorial en cascada con el id de la misma
			int id=0;
			id = Integer.parseInt(request.getParameter("id"));
			boolean elimino = BDgis.eliminarCapaVectorial(id);
			BDgis.cerrarConexion();
			response.setContentType("text/plain;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        out.print(elimino);
		}
		else if(request.getParameter("action").equals("eliminarFiguraGeometrica")){
			//Eliminar la capaVectorial en cascada con el id de la misma
			int id=0;
			id = Integer.parseInt(request.getParameter("id"));
			boolean elimino = BDgis.eliminarFiguraGeometrica(id);
			BDgis.cerrarConexion();
			response.setContentType("text/plain;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        out.print(elimino);
		}
		else{
			PrintWriter out = response.getWriter();
            out.print("No existe action solicitada");
		}
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		BaseDeDatos BDgis = new BaseDeDatos();
		try{
			if(request.getParameter("accion").equals("guardarProyecto")){
				Gson gsonProyecto = new Gson();
				String proyString = request.getParameter("proyecto");
				Proyecto proyecto = gsonProyecto.fromJson(proyString, Proyecto.class);
			
				int idProyectoGuardado = BDgis.guardarProyecto(proyecto);
				
				//enviar el id al cliente para que pueda acceder más tarde al proyecto de forma correcta.
				//1. Crear un objeto para que contenga como unico elemento el par (nombreId, valorId)
				JSONObject idProyectoJSON = new JSONObject();
				idProyectoJSON.put("idProyectoGuardado", idProyectoGuardado);
				//2. Hacerlo "visible" para que el cliente lo recupere.
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(idProyectoJSON);
			}
			else if(request.getParameter("accion").equals("guardarFiguraGeometrica"))
			{
				Gson gsonFigura = new Gson();
				String figuraStringJson = request.getParameter("FiguraGeometrica");
				FiguraGeometrica figura = gsonFigura.fromJson(figuraStringJson, FiguraGeometrica.class);
				int idCapa = Integer.parseInt(request.getParameter("idCapa"));
				String tipo = request.getParameter("tipoCapa");
				
				int r = BDgis.guardarFiguraGeometrica(figura, idCapa, tipo);
				//1. Crear un objeto para que contenga como unico elemento el par (nombreId, valorId)
				JSONObject respuesta = new JSONObject();
				respuesta.put("lineasModificadas", r);
				
				//2. Hacerlo "visible" para que el cliente lo recupere.
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(respuesta);
			}
			else if(request.getParameter("accion").equals("verificarPoligono")){
				Gson gsonFigura = new Gson();
				String figuraStringJson = request.getParameter("FiguraGeometrica");
				FiguraGeometrica figura = gsonFigura.fromJson(figuraStringJson, FiguraGeometrica.class);
				boolean esvalido = BDgis.verificarPoligono(figura);
				
				JSONObject respuesta = new JSONObject();
				respuesta.put("esValido", esvalido);
				
				//Hacerlo "visible" para que el cliente lo recupere.
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(respuesta);
				
				
			}	
			else if(request.getParameter("accion").equals("guardarCapaVectorial"))//Recibir la capa junto con el id del proyecto al que pertenece.
			{
				Gson gsonCapa = new Gson();
				String capaStringJson = request.getParameter("CapaVectorial");
				CapaVectorial capa = gsonCapa.fromJson(capaStringJson, CapaVectorial.class);
				int idProyecto = Integer.parseInt(request.getParameter("idProyecto"));
				//Guardar la capa en la base de datos y retornar el id de la capa creada
				int idCapaCreada = BDgis.guardarCapa(capa, idProyecto);
				//Para enviar el id al cliente a través de JSON
				JSONObject idProyectoJSON = new JSONObject();
				idProyectoJSON.put("idCapa", idCapaCreada);
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(idProyectoJSON);
			}
			//guardarAtributoNumerico
			else if(request.getParameter("accion").equals("guardarAtributoNumerico"))//Recibir el atributo junto con el id de de la figura que corresponde.
			{
				//Obtener el atributo númerico
				Gson gsonAtributoNumerico = new Gson();
				String atributoStringJson = request.getParameter("AtributoNumerico");
				AtributoNumerico atributoNumerico = gsonAtributoNumerico.fromJson(atributoStringJson, AtributoNumerico.class);
				
				ArrayList<Integer> idsFigurasGeomatricas = new ArrayList<Integer>();
				Gson gsonIdsFigurasGeometricas = new Gson();
				String gsonArrayIds = request.getParameter("idsFiguraGeometrica");
				idsFigurasGeomatricas = gsonIdsFigurasGeometricas.fromJson(gsonArrayIds, new TypeToken<ArrayList<Integer>>(){}.getType());
				//Guardar la capa en la base de datos y retornar el id de la capa creada
				int atributosGuardados=0;
				for(Integer i: idsFigurasGeomatricas){
					if(BDgis.guardarAtributoNumerico(i, atributoNumerico))
						atributosGuardados++;
				}
				//Para enviar al cliente el numero de atributos guardados a través de JSON
				JSONObject numAtribGuardados = new JSONObject();
				numAtribGuardados.put("atributosGuardados", atributosGuardados);
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(numAtribGuardados);
			}
			//guardarAtributoTexto
			else if(request.getParameter("accion").equals("guardarAtributoTexto"))//Recibir el atributo junto con el id de de la figura que corresponde.
			{
				//Obtener el atributo númerico
				Gson gsonAtribTexto = new Gson();
				String atributoStringJson = request.getParameter("AtributoTexto");
				AtributoTexto atributoTexto = gsonAtribTexto.fromJson(atributoStringJson, AtributoTexto.class);
				
				ArrayList<Integer> idsFigurasGeomatricas = new ArrayList<Integer>();
				Gson gsonIdsFigurasGeometricas = new Gson();
				String gsonArrayIds = request.getParameter("idsFiguraGeometrica");
				idsFigurasGeomatricas = gsonIdsFigurasGeometricas.fromJson(gsonArrayIds, new TypeToken<ArrayList<Integer>>(){}.getType());
				//Guardar la capa en la base de datos y retornar el id de la capa creada
				int atributosGuardados = 0;
				for(Integer i: idsFigurasGeomatricas){
					if(BDgis.guardarAtributoTexto(i, atributoTexto))
						atributosGuardados++;
				}
				
				JSONObject numAtribGuardados = new JSONObject();
				numAtribGuardados.put("atributosGuardados", atributosGuardados);
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(numAtribGuardados);
			}
			else if(request.getParameter("accion").equals("guardarAtributoLogico"))//Recibir el atributo junto con el id de de la figura que corresponde.
			{
				//Obtener el atributo númerico
				Gson gsonAtribLogico = new Gson();
				String atributoStringJson = request.getParameter("AtributoLogico");
				AtributoLogico atributoLogico = gsonAtribLogico.fromJson(atributoStringJson, AtributoLogico.class);
				
				ArrayList<Integer> idsFigurasGeomatricas = new ArrayList<Integer>();
				Gson gsonIdsFigurasGeometricas = new Gson();
				String gsonArrayIds = request.getParameter("idsFiguraGeometrica");
				idsFigurasGeomatricas = gsonIdsFigurasGeometricas.fromJson(gsonArrayIds, new TypeToken<ArrayList<Integer>>(){}.getType());
				//Guardar la capa en la base de datos y retornar el id de la capa creada
				int atributosGuardados = 0;
				for(Integer i: idsFigurasGeomatricas){
					if(BDgis.guardarAtributoLogico(i, atributoLogico))
						atributosGuardados++;
				}
				
				//Para enviar el id al cliente a través de JSON
				JSONObject numAtribGuardados = new JSONObject();
				numAtribGuardados.put("atributosGuardados", atributosGuardados);
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(numAtribGuardados);
			}
			else if(request.getParameter("accion").equals("actualizarValoresAtributos")){//Cuando el usuario desea guardar valores de atributos
				//Obtener la figura geometrica, que contiene, los arreglos de los atributos.
				Gson figuragson = new Gson();
				String figuraJson = request.getParameter("figuraGeometrica");
				FiguraGeometrica figura = figuragson.fromJson(figuraJson, FiguraGeometrica.class);
				int lineasModificadas = 0;
				BaseDeDatos bd = new BaseDeDatos();
				//1. Ir guardando los valores de los atributos Numericos:
				for(AtributoNumerico an: figura.getAtributosNumericos())
				{
					lineasModificadas = lineasModificadas + bd.actualizarValoresAtributosNumericos(an);
				}
				//2. Ir guardando los valores de los atributos Textos:
				for(AtributoTexto at: figura.getAtributosTexto())
				{
					lineasModificadas = lineasModificadas + bd.actualizarValoresAtributosTexto(at);
				}
				//3. Ir guardando los valores de los atributos Logicos:
				for(AtributoLogico al: figura.getAtributosLogicos())
				{
					lineasModificadas = lineasModificadas + bd.actualizarValoresAtributosLogicos(al);
				}
				//Para enviar el id al cliente a través de JSON
				//JSONObject lineasModifJson = new JSONObject();
				//lineasModifJson.put("lineasModificadas", lineasModificadas);
				//response.setContentType("application/json;charset=UTF-8");
		        //PrintWriter out = response.getWriter();
		        //out.print(lineasModifJson);
			}
			else if(request.getParameter("accion").equals("actualizarVisisbilidadCapa"))
			{
				ArrayList<CapaVectorial> capas = new ArrayList<CapaVectorial>();
				Gson gsonCapas = new Gson();
				String gsonCapasString = request.getParameter("capas");
				capas = gsonCapas.fromJson(gsonCapasString, new TypeToken<ArrayList<CapaVectorial>>(){}.getType());
				int n = 0;
				for(CapaVectorial c: capas)
				{
					n= n +BDgis.actualizarVisibilidadCapa(c);//Actualizar el campo de visibilidad de la capa.
				}
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(n));
			}
			else if(request.getParameter("accion").equals("guardarPosicionActualCamara"))
			{
				Gson gsonProyecto = new Gson();
				String gsonProyString = request.getParameter("proyecto");
				Proyecto proyecto = gsonProyecto.fromJson(gsonProyString, Proyecto.class);
				int r = BDgis.guardarPosicionActualCamara(proyecto);
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(r));
			}
			else if(request.getParameter("accion").equals("eliminarAtributosFiguraGeometrica"))
			{
				int lineasMod=0;
				Gson gsonFigura = new Gson();
				String jsonFigura = request.getParameter("figura");
				FiguraGeometrica figura = gsonFigura.fromJson(jsonFigura, FiguraGeometrica.class);
				if(figura.getAtributosNumericos()!=null && figura.getAtributosNumericos().size()>0){
					for(AtributoNumerico atribNum: figura.getAtributosNumericos()){
						lineasMod = lineasMod + BDgis.eliminarAtributoNumerico(atribNum.getId());
					}
				}
				if(figura.getAtributosTexto()!=null && figura.getAtributosTexto().size()>0){
					for(AtributoTexto atribTxt: figura.getAtributosTexto()){
						lineasMod = lineasMod + BDgis.eliminarAtributoTexto(atribTxt.getId());
					}
				}
				if(figura.getAtributosLogicos()!=null && figura.getAtributosLogicos().size()>0){
					for(AtributoLogico atribLog: figura.getAtributosLogicos()){
						lineasMod = lineasMod + BDgis.eliminarAtributoLogico(atribLog.getId());
					}
				}
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(lineasMod));	
				
			}
			else if(request.getParameter("accion").equals("modificarProyecto"))
			{
				int lineasMod = 0;
				Gson gsonProyecto = new Gson();
				String proyString = request.getParameter("proyecto");
				Proyecto proyectoModificado = gsonProyecto.fromJson(proyString, Proyecto.class);
			
				lineasMod = BDgis.modificarProyecto(proyectoModificado);
				
				//enviar la cantidad de lineas Modificadas, es decir si se hizo un cambio debe ser al menos 1.
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(lineasMod));					
			}
			else if(request.getParameter("accion").equals("modificarCapaVectorial"))
			{
				int lineasMod = 0;
				Gson gsonCapa = new Gson();
				String capaString = request.getParameter("capaVectorial");
				CapaVectorial capaModificada = gsonCapa.fromJson(capaString, CapaVectorial.class);
			
				lineasMod = BDgis.modificarCapaVectorial(capaModificada);
				
				//enviar la cantidad de lineas Modificadas, es decir si se hizo un cambio debe ser al menos 1.
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(lineasMod));			
			
			}
			else if(request.getParameter("accion").equals("modificarFiguraGeometrica"))
			{ 
				int lineasMod = 0;
				Gson gsonFigura = new Gson();
				String figuraString = request.getParameter("figuraGeometrica");
				String tipo = request.getParameter("tipoFigura");
				FiguraGeometrica figuraModificada = gsonFigura.fromJson(figuraString, FiguraGeometrica.class);		
				lineasMod = BDgis.modificarFiguraGeometrica(figuraModificada,tipo);
				
				//enviar la cantidad de lineas Modificadas, es decir si se hizo un cambio debe ser al menos 1.
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(lineasMod));	
				
			}
			else if(request.getParameter("accion").equals("modificarNombresAtributos"))
			{
				int lineasMod = 0;
				Gson gsonFigura = new Gson();
				String figuraString = request.getParameter("figuraGeometrica");
				FiguraGeometrica figura = gsonFigura.fromJson(figuraString, FiguraGeometrica.class);		
				int lineasModificadas = 0;
				BaseDeDatos bd = new BaseDeDatos();
				//1. Ir guardando los valores de los atributos Numericos:
				for(AtributoNumerico an: figura.getAtributosNumericos())
				{
					lineasModificadas = lineasModificadas + bd.modificarAtributoNumerico(an);
				}
				//2. Ir guardando los valores de los atributos Textos:
				for(AtributoTexto at: figura.getAtributosTexto())
				{
					lineasModificadas = lineasModificadas + bd.modificarAtributoTexto(at);
				}
				//3. Ir guardando los valores de los atributos Logicos:
				for(AtributoLogico al: figura.getAtributosLogicos())
				{
					lineasModificadas = lineasModificadas + bd.modificarAtributoLogico(al);
				}
				//enviar la cantidad de lineas Modificadas, es decir si se hizo un cambio debe ser al menos 1.
				response.setContentType("text/plain;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(String.valueOf(lineasMod));	
				
			}
			else if(request.getParameter("accion").equals("estadisticasBasicas"))
			{
				ArrayList<ResultadoEstadistica> resultadoEstadisticasCapa = new ArrayList<ResultadoEstadistica>();
				int idCapa = Integer.parseInt(request.getParameter("idCapa"));
				
				resultadoEstadisticasCapa = BDgis.calculosEstadicasBasicas(idCapa);
				Gson gsonResultadoEstadisticas = new Gson();
				String resultadojson = gsonResultadoEstadisticas.toJson(resultadoEstadisticasCapa);
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(resultadojson);
			}
			else if(request.getParameter("accion").equals("obtenerAtributosNumericosCapa"))
			{
				int idCapa = Integer.parseInt(request.getParameter("idCapa"));
				//Llamar al metodo que obtiene todos los nombres de los atributos numericos de la capa.
				ArrayList<String> nombresAtributosCapa = BDgis.obtenerNombresAtributosNumericosPorCapa(idCapa);
				Gson gsonNombres = new Gson();
				String arrayNombresAtributosJSON = gsonNombres.toJson(nombresAtributosCapa);
				response.setContentType("application/json;charset=UTF-8");
		        PrintWriter out = response.getWriter();
		        out.print(arrayNombresAtributosJSON);
				
			}
			else if(request.getParameter("accion").equals("interpolacion"))
			{
				//1. obtener el nombre del atributo y el id de la capa de trabajo
				String nombreAtributoAInterpolar = request.getParameter("nombreAtributo");
				int idCapa = Integer.parseInt(request.getParameter("idCapa"));
				String datosAInterpolar="";
				//3. Comunicación con INTAMAP, enviando el String generado.	
				try {
					//2. Obtener los valores de los parametros que se relacionen a la capa, junto con la ubicación.
					datosAInterpolar = BDgis.obtenerValoresAInterpolar(nombreAtributoAInterpolar, idCapa);
				    // parse string
				    ObservationCollection observations = StringParser.parseXYZ(datosAInterpolar,32719);
				    // create request
				    InterpolateRequest solicitud = new InterpolateRequest(observations);
				    solicitud.setMethodName("automap");

				    // add statistics to predict
				    solicitud.addStatisticToPredict("Mean");
				    solicitud.addStatisticToPredict("Variance");

				    // offline: http://intamap.aston.ac.uk:8080/intamap/WebProcessingService
				    // Utrecht--> send request http://intamap.geo.uu.nl:8180/intamap/WebProcessingService
				    //Muenster --> http://intamap.uni-muenster.de:8080/intamap/WebProcessingService
				    InterpolateService service = new InterpolateService("http://intamap.uni-muenster.de:8080/intamap/WebProcessingService");
				    InterpolateResponse respuesta = service.sendRequest(solicitud);
				    // generate geotiffs
				    File outputFolder = new File(rutaImagen);
				    if (!outputFolder.isDirectory())
				    	outputFolder.mkdir();
				    
				    GeoTIFFGenerator generator = new GeoTIFFGenerator();
				    ArrayList<File> generatedFiles = generator.generateGeoTIFFs(respuesta, outputFolder);
				    for (File file : generatedFiles) {
				        System.out.println("Generated " + file.getName());
				    }    
				    //4. Obtener los valores de la imagen tiff 
					Imagen imagenResultado = new Imagen();
					ParseadorGdal parseador = new ParseadorGdal();
					//1. Transformar la imagen de interes en formato google.tif
					//String rutaTifSalida = parseador.transformarAProyeccionGoogle(
					//	"/home/cristina/ImagenesServer/geoTIFF/geotiffs/mean.tif", "Out.tif");
					//2. Leer la información del geotiff (centro, noreste y suroeste).rutaImagen+"/mean.tif"
					parseador.ejecutarGdalInfo("/home/cristina/ImagenesServer/geoTIFF/geotiffs/mean.tif");
					imagenResultado.setNoreste(parseador.obtenerCoordenadasBorde(parseador.NORESTE));
					imagenResultado.setSuroeste(parseador.obtenerCoordenadasBorde(parseador.SUROESTE));
					imagenResultado.setCentro(parseador.obtenerCoordenadasBorde(parseador.CENTRO));
					
					//3. Transformar la imagen a png
					String rutaSalidaPng = parseador.transformarAPng("/home/cristina/ImagenesServer/geoTIFF/geotiffs/mean.tif", "out.png");
					imagenResultado.setNombreArchivo("out.png");
					imagenResultado.setRuta(rutaSalidaPng);//Tratar de enviar la ip del servidor, para poder acceder directo desde el cliente.
					//4. Calcular los valores de los niveles (5) que van en la barra de color.
					ArrayList<Double> valoresAtributos = BDgis.obtenerValoresAtributosNumericos(nombreAtributoAInterpolar, idCapa);
					double maximo = Estadistica.obtenerMaximo(valoresAtributos);
					double minimo = Estadistica.obtenerMinimo(valoresAtributos);
					double sumar = (maximo - minimo)/4;// (Maximo - Minimo)/(niveles-1)
					imagenResultado.setNivel1(minimo);
					imagenResultado.setNivel2(minimo+sumar);
					imagenResultado.setNivel3(imagenResultado.getNivel2()+sumar);
					imagenResultado.setNivel4(imagenResultado.getNivel3()+sumar);
					imagenResultado.setNivel5(maximo);
					//Trasformar el objeto imagen a JSON para enviarlo al cliente.
					Gson gsonImagen = new Gson();
					String imagenJSON = gsonImagen.toJson(imagenResultado);
					response.setContentType("application/json;charset=UTF-8");
					//response.setContentType("text/plain;charset=UTF-8");//Formato del contenido de respuesta del servlet.
			        PrintWriter out = response.getWriter();
			        out.print(imagenJSON);
				    
				}
				catch (ParserException e1) {
				    System.err.println("Error parsing String. " + e1.getMessage());
				}
				catch (IOException e2) {
				    System.err.println("Could not open String /write GeoTIFFs. " + e2.getMessage());
				}
				catch (InterpolateException e3) {
				    System.err.println("INTAMAP returned an error during interpolation. " + e3.getMessage());
				    e3.printStackTrace();
				}
				catch (GeneratorException e4) {
				    System.err.println("Error generating GeoTIFFs. " + e4.getMessage());
				}
				catch(Exception e){
					System.out.println("Error en interpolacion: "+e.getMessage());
					
				}
			}
		}
		catch(JsonSyntaxException error){
			error.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally
		{BDgis.cerrarConexion();}
	}

}
