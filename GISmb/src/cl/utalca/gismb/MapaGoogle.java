package cl.utalca.gismb;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.gson.Gson;


public class MapaGoogle extends FragmentActivity implements OnMapClickListener, OnClickListener, OnInfoWindowClickListener{
	private static final String point = "point";
	private static final String linestring = "linestring";
	private static final String polygon = "polygon";
	private static final int SHOW_CREAR_PUNTO = 1;
	private static final int SHOW_CREAR_LINEA_POLIGONO = 2;
	private static final int SHOW_CREAR_CAPA = 5;
	private static final int SHOW_LIST_FIGURAS_ELIMINAR=4;
	private static final int SHOW_LIST_FIGURAS_CREAR_ATRIBUTO=6;
	private static final int DIALOG_NO_HAY_CAPA_SELECCIONADA = 1;
	private static final int DIALOG_SELECCION_CAPA_DE_TRABAJO = 0;
	private static final int DIALOG_INGRESAR_NOMBRE_FIGURA = 2;
	private static final int DIALOG_NO_HAY_POSICION = 3;
	private static final int DIALOG_NO_EXISTEN_CAPAS = 4;
	private static final int DIALOG_SELECCION_CAPAS = 5;
	private static final int DIALOG_SELECCION_LAYER_MAPA = 6; 
	private static final int DIALOG_INGRESAR_POSICION_CAMARA = 7;
	private static final int DIALOG_MODIFICAR_CAPA_SELECCIONADA = 8;
	private static final int DIALOG_SELECCION_ATRIBUTO_NUMERICO = 9;
	MapView mapView;
	MapController mapControl;
	private Proyecto proyectoActual;//Proyecto en el que esta trabajando el usuario.
	private CapaVectorial capaSeleccionada;//Capa sobre la que esta agregando datos.
	private LocationListener locationListener;
	private Location posicionActual;
	private FiguraGeometrica figuraLocal;
	private int idItemSeleccionado = -1;
	private GoogleMap mMapa;
	boolean[] itemSeleccionado;
	private boolean modoIngresarFiguraClickMapa = false;
	private int posicionLayerSeleccionada;
	private Polygon poligonoDibujado;
	private PolygonOptions opcionesPoligono;
	private Polyline lineaDibujada;
	private PolylineOptions opcionesLinea;
	private boolean clickMapa =false;
	private HashMap<Marker, FiguraGeometrica> hashMarkerFigura;
	private ArrayList<Marker> markersTemporales;
	private GroundOverlayOptions imagenInterpolada;
	private final float transparencia = 0.5f;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	proyectoActual = new Proyecto();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_google);
        
        //Intentar obtener un mapa desde el SupportMapFragment
        mMapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa_main))
                .getMap();
        hashMarkerFigura = new HashMap<Marker, FiguraGeometrica>();
        mMapa.setOnInfoWindowClickListener(this);
        //Obtener el id del proyecto seleccionado de la lista con todos los proyectos.
        int idSeleccionado;
        String nombreProyecto;
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
        	String actividad = extras.getString("nombreActividad");
        	if(actividad.equals("activity_list_proyectos")||actividad.equals("activity_main"))
        	{	
        		idSeleccionado = extras.getInt("idProyectoSeleccionado");
        		nombreProyecto = extras.getString("nombreProyecto");
        		
            	//llamar a la funcion que obtiene todas las capas y las muestra sobre el mapa.
        		proyectoActual.setId(idSeleccionado);
        		proyectoActual.setNombre(nombreProyecto);
        		//cargarCapasProyectoActual();
            	//dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
            	if(actividad.equals("activity_list_proyectos"))//Solo la actividad con la lista de los proyectos pasa estos parametros.
            	{
        			String posicion = extras.getString("poscicionCamara");
        			int zoom = extras.getInt("zoom");
        			if(posicion!=null && !posicion.equals("null")){
        				proyectoActual.setZoom(zoom);
            			proyectoActual.setPosicionCamara(posicion);
            			cambiarPosicionCamara(obtenerCoordenadas(proyectoActual.getPosicionCamara()),proyectoActual.getZoom(),false);
        			}
        					
        		}
        	}
        	if(actividad.equals("activity_crear_proyecto"))
        	{
        		Gson gsonProyecto = new Gson();
        		String proyectoJson = extras.getString("proyectoGuardadoJson");
        		proyectoActual = gsonProyecto.fromJson(proyectoJson,Proyecto.class);
        		Toast.makeText(this, "Usted a creado un proyecto",Toast.LENGTH_LONG).show();
        	}
        	this.setTitle("Proyecto: "+proyectoActual.getNombre());
        }
        
        locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				posicionActual = location;
			}
		};		
    }
    
    private void cargarCapasProyectoActual()
    {
    	ArrayList<CapaVectorial> capas = obtenerCapasProyectoActual(proyectoActual.getId());
		proyectoActual.setCapasVectoriales(capas);
		actualizarCapaSeleccionada();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mapa_google, menu);
        return true;
    }
    
    private boolean verificarCapasProyecto(){
    	
		if(proyectoActual.getCapasVectoriales()!= null && proyectoActual.getCapasVectoriales().size()>0){
			return true;
		}
		else {			
			Toast.makeText(this, "El proyecto no tiene capas", Toast.LENGTH_LONG).show();
			return false;
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent intento;
    	
    	//Manejo del click sobre los itemes del menu
    	switch(item.getItemId())
    	{
    	/* Inicio: Seccion Menu relacionadas a las capas */
    	case R.id.menu_establecer_capa_trabajo:
    	//Rellenar las capas del Proyecto y luego hacer algo con ellas.
    		if(verificarCapasProyecto()){
    			cargarCapasProyectoActual();
    			showDialog(DIALOG_SELECCION_CAPA_DE_TRABAJO);    			
    		}		
    		return true;
    	case R.id.submenu_crearCapa:
    		Intent intent = new Intent(this,CrearCapaVectorial.class);
    		startActivityForResult(intent, SHOW_CREAR_CAPA);
    		return true;
    	case R.id.submenu_eliminarCapa:
    		//Elimina la capa actual seleccionada, como capa de trabajo.
    		if(capaSeleccionada!=null){
    			eliminarCapaVectorial();
    		}
    		else if(proyectoActual.getCapasVectoriales()!=null&&capaSeleccionada==null){
    			//Debe seleccionar una capa Primero
    		}
    		else if(proyectoActual.getCapasVectoriales()==null){
    			//El proyecto no tiene capas para eliminar
    		}
    		return true;
    	case R.id.submenu_modificarCapa:
    		if(capaSeleccionada!=null){
    			showDialog(DIALOG_MODIFICAR_CAPA_SELECCIONADA);
    		}else{
    			showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
    		}
    		return true;
    	case R.id.submenu_ocultar_mostrar_capas:
    		//showDialog(DIALOG_NO_EXISTEN_CAPAS);
    		showDialog(DIALOG_SELECCION_CAPAS);
    		return true;
    	
    	case R.id.menu_figura_geometrica:
    		//Llamar a las opciones que corresponden a figura geometrica
    		return true;

    	case R.id.menu_estadisticas:	
    		if(capaSeleccionada!=null)
    		{
    			intento = new Intent(this,EstadisticasBasicas.class);
    			Bundle datosextras = new Bundle();
    			datosextras.putInt("idCapaTrabajo", capaSeleccionada.getId());
    			intento.putExtra("datosCapa",datosextras);
    			startActivity(intento);
    		}
    		else{
 				showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
 			}
    		
    		
    		return true;
    	case R.id.menu_interpolacion:
    		//llamarServicioInterpolacion();
    		if(capaSeleccionada!=null){
    			showDialog(DIALOG_SELECCION_ATRIBUTO_NUMERICO);
    		}else{
    			showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
    		}
    		return true;
    	//Administracion de los atributos de las figuras geometricas (Crear, Modificar, Eliminar y Ingresar Valores)	
    	case R.id.submenu_atributos_crear:
 			//Ir a la actividad que permita crear los atributos de las figuras, que serán seleccionadas de una lista.
    		//Se le debe enviar el id de la capa seleccionada (o el objeto CapaVectorial)
 			if(capaSeleccionada!= null){
 				//Crear un JSON de la capa seleccionada.
 				Gson gsonCapa = new Gson();
 				String CapaJsonString = gsonCapa.toJson(capaSeleccionada, CapaVectorial.class);
 				//Agregarla en el intent para que 
 				Intent in = new Intent(this,ListFigurasGeometricas.class);
 	 			Bundle datos = new Bundle();
 				datos.putString("capaSeleccionada", CapaJsonString);
 				datos.putString("accion", "CrearAtributo");
 				in.putExtras(datos);
 				startActivity(in);
 				//startActivityForResult(in, SHOW_LIST_FIGURAS_CREAR_ATRIBUTO);
 			}
 			else{
 				Toast.makeText(this, "Debe seleccionar una capa Primero!"
						, Toast.LENGTH_LONG).show();
 			}
    		
    		return true;
    	case R.id.submenu_ingresar_valores_atributo:
    		if(capaSeleccionada!=null){
    			Intent in = new Intent(this,IngresarValorAtributos.class);
    			Bundle datosFiguras = new Bundle();
    			Gson capaJson = new Gson();
    			String capaSelccionadaJson = capaJson.toJson(capaSeleccionada);
    			datosFiguras.putString("figuras", capaSelccionadaJson);
    			in.putExtras(datosFiguras);
    			startActivity(in);
    		}
    		else{showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);}
    		
    		return true;
    	case R.id.submenu_atributos_modificar:
    		if(capaSeleccionada!=null){
    			Intent in = new Intent(this,ModificarAtributos.class);
    			Bundle datosFiguras = new Bundle();
    			Gson capaJson = new Gson();
    			String capaSelccionadaJson = capaJson.toJson(capaSeleccionada);
    			datosFiguras.putString("figuras", capaSelccionadaJson);
    			in.putExtras(datosFiguras);
    			startActivity(in);
    		}
    		else{showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);}
    		return true;
    	case R.id.submenu_atributos_eliminar:
    		if(capaSeleccionada!=null){
    			Intent in = new Intent(this,EliminarAtributosFigura.class);
    			Bundle datosFiguras = new Bundle();
    			Gson capaJson = new Gson();
    			String capaSelccionadaJson = capaJson.toJson(capaSeleccionada);
    			datosFiguras.putString("figurasCapa", capaSelccionadaJson);
    			in.putExtras(datosFiguras);
    			startActivity(in);
    		}
    		else{showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);}
    		return true;
    	case R.id.submenu_fg_crear_manual:
    		//Llamar a la actividad para crear figura
    		if(capaSeleccionada!= null && capaSeleccionada.getTipo().equals(point))
    		{
    			Intent i = new Intent(this,CrearPunto.class);
    			Bundle datosCapa = new Bundle(); 
    			datosCapa.putInt("idCapa", capaSeleccionada.getId());
    			datosCapa.putString("accion", "CrearFigura");
    			i.putExtras(datosCapa);
    			startActivityForResult(i, SHOW_CREAR_PUNTO);
    		}
    		else if(capaSeleccionada!= null && capaSeleccionada.getTipo().equals(linestring))
    		{
    			Intent i = new Intent(this,CrearLineaPoligono.class);
    			Bundle datosCapa = new Bundle(); 
    			datosCapa.putInt("idCapa", capaSeleccionada.getId());
    			datosCapa.putString("tipoFigura", linestring);
    			datosCapa.putString("accion", "CrearFigura");
    			i.putExtras(datosCapa);
    			startActivityForResult(i, SHOW_CREAR_LINEA_POLIGONO);
    			
    		}
    		else if(capaSeleccionada!= null && capaSeleccionada.getTipo().equals(polygon))
    		{
    			Intent i = new Intent(this,CrearLineaPoligono.class);
    			Bundle datosCapa = new Bundle(); 
    			datosCapa.putInt("idCapa", capaSeleccionada.getId());
    			datosCapa.putString("tipoFigura", polygon);
    			datosCapa.putString("accion", "CrearFigura");
    			i.putExtras(datosCapa);
    			startActivityForResult(i, SHOW_CREAR_LINEA_POLIGONO);
    			
    		}
    		else if(capaSeleccionada ==null)
    		{
    			showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
    		}
    		return true;
    	
    	case R.id.submenu_fg_obtener_posicion:
    		if(capaSeleccionada!=null && capaSeleccionada.getTipo().equals(point))
    		{
    			/**LatLng posicion = obtenerPosicion();
    			posicion.toString();
    			Bundle extras = new Bundle();
    			extras.putString("posicion", posicion.toString());**/
    			figuraLocal = new FiguraGeometrica();
    			LatLng posicionAct = obtenerPosicion();
    			if(posicionAct != null && capaSeleccionada!=null) {
    				figuraLocal.setGeometria(posicionAct.longitude+" "+posicionAct.latitude);
    				showDialog(DIALOG_INGRESAR_NOMBRE_FIGURA);
    			}
    			else if(posicionAct==null){
    				showDialog(DIALOG_NO_HAY_POSICION);
    			}
    			else if(capaSeleccionada==null){
    				showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
    			}
    		}
    		return true;
    	case R.id.submenu_eliminar_figuraGeometrica:
    		if(capaSeleccionada!= null){
 				//Crear un JSON de la capa seleccionada.
 				Gson gsonCapa = new Gson();
 				String CapaJsonString = gsonCapa.toJson(capaSeleccionada, CapaVectorial.class);
 				//Agregarla en el intent para que 
 				Intent in = new Intent(this,ListFigurasGeometricas.class);
 	 			Bundle datos = new Bundle();
 				datos.putString("capaSeleccionada", CapaJsonString);
 				datos.putString("accion", "EliminarFigura");
 				in.putExtras(datos);
 				startActivityForResult(in, SHOW_LIST_FIGURAS_ELIMINAR);
 			}
 			else{
 				showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
 			}
    		return true;
    	case R.id.submenu_modificar_figuraGeometrica:
    		//Ir a la actividad que permita seleccionar la figura que será modificada
    		//Se le debe enviar el id de la capa seleccionada (o el objeto CapaVectorial)
 			if(capaSeleccionada!= null){
 				//Crear un JSON de la capa seleccionada.
 				Gson gsonCapa = new Gson();
 				String CapaJsonString = gsonCapa.toJson(capaSeleccionada, CapaVectorial.class);
 				//Agregarla en el intent para que 
 				Intent in = new Intent(this,ListFigurasGeometricas.class);
 	 			Bundle datos = new Bundle();
 				datos.putString("capaSeleccionada", CapaJsonString);
 				datos.putString("accion", "ModificarFigura");
 				in.putExtras(datos);
 				startActivity(in);
 				//startActivityForResult(in, SHOW_LIST_FIGURAS_CREAR_ATRIBUTO);
 			}
 			else{
 				showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
 			}
    		
    		return true;
    	//Menu relacionado al mapa de google, como: posición y capas.
    	case R.id.submenu_capas_google:
    		showDialog(DIALOG_SELECCION_LAYER_MAPA);
    		return true;
    	case R.id.submenu_mi_localizacion:
    		mMapa.setMyLocationEnabled(true);
    		return true;
    	case R.id.submenu_ir_a:
    		showDialog(DIALOG_INGRESAR_POSICION_CAMARA);
    		return true;
    	case R.id.submenu_guardar_posicion_actual:
    		guardarPosicionActualCamara();
    		return true;
    	case R.id.submenu_fg_clickear_mapa:
    		if(capaSeleccionada!=null)
    		{
    			//1. dibujar botones en la interfaz que permitan saber cuando termine de dibujar
        		LinearLayout contenedorBotones =  (LinearLayout)findViewById(R.id.layout_boton_terminar);
        		contenedorBotones.setVisibility(LinearLayout.VISIBLE);			
        		//2. activar el modo de ingreso de figura geometrica por click en el mapa.
    			modoIngresarFiguraClickMapa = true;
    			//3. Activar el Listener que obtiene el click hecho sobre el mapa.
        		mMapa.setOnMapClickListener(this);
        		//4. Obtener los click en los botones aceptar cancelar.
    			findViewById(R.id.btn_aceptar).setOnClickListener(this);
    	        findViewById(R.id.btn_cancelar).setOnClickListener(this);
    		}
    		else{
    			showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
    		}
    		return true;
    	default:
            return super.onOptionsItemSelected(item);
    	}
    	
    }//Fin de onOptionsItemSelected
    /***
     * Metodo para manejar el click en los botones aceptar/cancelar que aparecen cuando se desea crear una linea o polígono.
     * 
     */
	@Override
	public void onClick(View v) {
		LinearLayout contenedorBotones = (LinearLayout)findViewById(R.id.layout_boton_terminar);
		CoordenadasUtil coorUtil = new CoordenadasUtil();
		switch(v.getId())
   		{
   			case R.id.btn_aceptar:
   				//Guardar la figura geometrica creada (poligono o linea)
   				if(capaSeleccionada.getTipo().equals(polygon))
   				{
   					List<LatLng> puntosFigura = poligonoDibujado.getPoints();
   	   				figuraLocal = new FiguraGeometrica();
   	   				figuraLocal.setGeometria(coorUtil.obtenerPuntosFiguraTexto(puntosFigura));
   	   				//Verificar si el poligono esta correcto
   	   				if(poligonoValido()){   	   					
   	   					showDialog(DIALOG_INGRESAR_NOMBRE_FIGURA);
   	   					limpiarMarkersTemporales();
   	   				}
   	   				else{
   	   					Toast.makeText(this, "Poligono Inválido!", Toast.LENGTH_LONG).show();
   	   				}
   				}
   				else if(capaSeleccionada.getTipo().equals(linestring)){
   					List<LatLng> puntosLinea = lineaDibujada.getPoints();
   					figuraLocal = new FiguraGeometrica();
   					figuraLocal.setGeometria(coorUtil.obtenerPuntosFiguraTexto(puntosLinea));
   					showDialog(DIALOG_INGRESAR_NOMBRE_FIGURA);
   				}
   				
   				break;
   			case R.id.btn_cancelar:
   				break;
   		}
		//Terminar el modo de ingresar figura clickeando el mapa.
		modoIngresarFiguraClickMapa = false;
		//Terminar de recibir el evento click sobre el mapa.
		mMapa.setOnMapClickListener(null);
		//Borrar los botones de la vista.
		contenedorBotones.setVisibility(LinearLayout.INVISIBLE);
		poligonoDibujado = null;
		opcionesPoligono = null;
		//En caso de ser linea:
		lineaDibujada = null;
		opcionesLinea = null;
		clickMapa = false;
		dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
		
	}
	private boolean poligonoValido(){
		ComunicacionServidor com = new ComunicacionServidor(this);
   	 	com.execute(AccionesServlet.VERIFICAR_POLIGONO,figuraLocal);
   	 	boolean esValido = false;
   	 	try {
			esValido = (Boolean)com.get();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
   	 	return esValido;
	}
    private void guardarPosicionActualCamara()
    {
    	Proyecto proyectoSinCapas = new Proyecto();
    	CameraPosition posicionCamara = mMapa.getCameraPosition();
    	LatLng punto = posicionCamara.target;
    	float zoom = posicionCamara.zoom;
    	double lat = punto.latitude;
    	double lon = punto.longitude;
    	
    	proyectoSinCapas.setId(proyectoActual.getId());
    	proyectoSinCapas.setPosicionCamara(lon+" "+lat);
    	proyectoSinCapas.setZoom((int)zoom);
    	
    	ComunicacionServidor comDB = new ComunicacionServidor(this);
    	comDB.execute(AccionesServlet.GUARDAR_POSICION_ACTUAL_CAMARA,proyectoSinCapas);
    	proyectoActual.setZoom(proyectoSinCapas.getZoom());
    }
    
    private void eliminarCapaVectorial()
    {
    	ComunicacionServidor cdb = new ComunicacionServidor(this);
		cdb.execute(AccionesServlet.ELIMINAR_CAPA_VECTORIAL,capaSeleccionada.getId());
		boolean eliminarCapa;
		try {
			eliminarCapa = (Boolean)cdb.get();
			if(eliminarCapa){//Re-obtenerlascapas y re-dibujarlas sobre el mapa.
				capaSeleccionada = null;
				idItemSeleccionado = -1;
				cargarCapasProyectoActual();
				//mMapa.clear();
				dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());	
			}
			else{
				Toast.makeText(getBaseContext(), "Se Produjo un error al eliminar la capa",Toast.LENGTH_SHORT).show();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    private void modificarCapaVectorial(CapaVectorial capaModificada){
    	ComunicacionServidor com = new ComunicacionServidor(this);
   	 	com.execute(AccionesServlet.MODIFICAR_CAPA_VECTORIAL,capaModificada);
   	 	int lineasModificadas;
   	 	try {
			lineasModificadas = (Integer)com.get();
			if(lineasModificadas>0){
				Toast.makeText(this, "Exito el proyecto fue modificado", Toast.LENGTH_SHORT).show();								
			}
			else{
				Toast.makeText(this, "Error modificando Proyecto", Toast.LENGTH_SHORT).show();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
   	 
    }
    
    /**
     * Maneja el resultado de las actividades llamadas que crean las figuras geometricas.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	//Comprobar que el resultado se esta obteniendo de la solicitud hecha a la actividad especifica.
    	if(requestCode==SHOW_CREAR_PUNTO)
    	{
    		//Si se guardo la figura correctamente, cargar nuevamente las capas para redibujarlas
    		if(resultCode==RESULT_OK && data.getExtras().getInt("resultadoGuardar")>0){
    			cargarCapasProyectoActual();
    			//mMapa.clear();
    			dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
    		}
    		else if(resultCode==RESULT_CANCELED)
    		{
    			//Se presiono cancelar.
    		}
    	}
    	if(requestCode==SHOW_CREAR_LINEA_POLIGONO)
    	{
    		
    		if(resultCode==RESULT_OK && data.getExtras().getInt("resultadoGuardar")>0){
    			cargarCapasProyectoActual();
    			//mMapa.clear();
    			dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
    		}
    		else if(resultCode==RESULT_CANCELED)
    		{
    			//Se presiono cancelar.
    		}
    	}
    	if(requestCode==SHOW_CREAR_CAPA)
    	{
    		if(resultCode==RESULT_OK && data!=null){
    			CapaVectorial nuevaCapa = new CapaVectorial();
    			nuevaCapa.setNombre(data.getStringExtra("NombreCapaVectorial"));
    			nuevaCapa.setTipo(data.getStringExtra("tipoCapaVectorial"));
    			ComunicacionServidor comunicarDB = new ComunicacionServidor(this);
    			comunicarDB.execute(AccionesServlet.GUARDAR_CAPA_VECTORIAL,nuevaCapa,proyectoActual.getId());
    			try {
					int idCapaCreada = (Integer)comunicarDB.get();
					nuevaCapa.setId(idCapaCreada);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Toast.makeText(this, "Se produjo un error al crear la capa", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					Toast.makeText(this, "Se produjo un error al crear la capa", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
    			proyectoActual.getCapasVectoriales().add(nuevaCapa);
    			
    		}
    	}
    	
    	if(requestCode==SHOW_LIST_FIGURAS_ELIMINAR){
    		if(resultCode==RESULT_OK){
    			//Sacar la figura eliminada del mapa
    			cargarCapasProyectoActual();
    			//mMapa.clear();
    			dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());		
    		}
    		
    	}
    	
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	
    }
    
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	AlertDialog.Builder builder; 
    	switch(id){
    	case DIALOG_SELECCION_CAPA_DE_TRABAJO:
    		builder = new AlertDialog.Builder(this);    		
    		builder.setTitle(R.string.seleccionar_capa)
    			   .setSingleChoiceItems(proyectoActual.obtenerNombreCapasVectoriales(), idItemSeleccionado, 
    		new DialogInterface.OnClickListener() {    			

				@Override
				public void onClick(DialogInterface dialog, int which) {
					capaSeleccionada = proyectoActual.getCapasVectoriales().get(which);
					idItemSeleccionado = which;
					dialog.cancel();					
				}				
    		}).setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cambiarTitulo();
					removeDialog(DIALOG_SELECCION_CAPA_DE_TRABAJO);					
				}
			});
    		return builder.create();
    	case DIALOG_SELECCION_LAYER_MAPA:
    		int layerSeleccionado1 = 0;
    		
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.seleccionar_capa)
    			   .setSingleChoiceItems(R.array.tipos_layers_mapa, layerSeleccionado1,
    					new DialogInterface.OnClickListener() {    			

    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						//Hacer algo cuando seleccione el tipo de layer que quiere ver.
    						posicionLayerSeleccionada = which;
    						dialog.cancel();
    					}				
    	    		})
    	    		.setOnCancelListener(new DialogInterface.OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface dialog) {
							//Llamar al metodo para que cambie a la layer que quiere ver el usuario.
							setLayerMapa(posicionLayerSeleccionada);	
							
						}
					})
					;
    		return builder.create();
    		
    	case DIALOG_SELECCION_ATRIBUTO_NUMERICO:
    		int idAtributoSeleccionado = -1;
    		final String[] nombresAtributos=obtenerNombresAtributos(capaSeleccionada.getId());
    		
    		builder = new AlertDialog.Builder(this);
    		
    		builder.setTitle(R.string.seleccion_atributo_numerico)
			   .setSingleChoiceItems(nombresAtributos, idAtributoSeleccionado,
					new DialogInterface.OnClickListener() {    			

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Tomar el atributo seleccionado y mandarlo a la interpolacion.
						//int posicionAtributoSeleccionado = which;
						String nombreAtributoSeleccionado = nombresAtributos[which];
						//me gutaría no llamar aqui al metódo, pero bueno......						
						llamarServicioInterpolacion(nombreAtributoSeleccionado, capaSeleccionada.getId());
						dialog.cancel();
					}				
	    		})
	    		.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						removeDialog(DIALOG_SELECCION_ATRIBUTO_NUMERICO);						
					}
				})
				;
    		return builder.create();
		
    	case DIALOG_INGRESAR_POSICION_CAMARA:
    		builder = new AlertDialog.Builder(this);
    		// Get the layout inflater
            LayoutInflater inf = getLayoutInflater();
            final View dialogVie = inf.inflate(R.layout.dialogo_ingresar_posicion_camara, null);
            //pasar null como padre, ya que va a formar parte del dialogo.
            builder.setView(dialogVie);
            builder.setTitle(R.string.ir_a)
            	   .setPositiveButton(R.string.aceptar, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//Obtener la latitud y la longitud escrita por el usuario.
									EditText editxtLatitud = (EditText)dialogVie.findViewById(R.id.editxt_latitud_dialog);
									EditText editxtLongitud = (EditText)dialogVie.findViewById(R.id.editxt_longitud_dialog);
									String lats = editxtLatitud.getText().toString();
									String lons = editxtLongitud.getText().toString();
									double latd = Double.parseDouble(lats);
									double lond = Double.parseDouble(lons);
									LatLng posicion = new LatLng(latd, lond);
									//Llamar al metodo que animara la camara hasta el punto de interes.
									cambiarPosicionCamara(posicion,8,true);
									editxtLatitud.setText("");
									editxtLongitud.setText("");
								}
							}).setNegativeButton(R.string.cancelar, new
									DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
										}
									});
            // Create the AlertDialog object and return it
    		return builder.create();
    	case DIALOG_NO_HAY_CAPA_SELECCIONADA:
    		// Use the Builder class for convenient dialog construction
            builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alerta_falta_seleccionar_capa_de_trabajo)
            	   .setIcon(android.R.drawable.ic_dialog_alert)
                   .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // LLamar al metodo del menu para que seleccione la capa
                    	   if(verificarCapasProyecto()){
                    		  // showDialog(DIALOG_SELECCION_CAPA_DE_TRABAJO); 
                    	   }    	   
						};
                   });
            // Create the AlertDialog object and return it
            return builder.create();
    	case DIALOG_INGRESAR_NOMBRE_FIGURA:
    		//TextView tvPosicionActual;
    		builder = new AlertDialog.Builder(this);
    		// Get the layout inflater
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialogo_ingresar_nombre_figurageom, null);
            //tvPosicionActual = (TextView)findViewById(R.id.tv_posicion_obtenida);
            //tvPosicionActual.setText(figuraLocal.getGeometria());
            //"inflar" y setear el layout para el dialogo
            //pasar null como padre, ya que va a formar parte del dialogo.
            builder.setView(dialogView);
            builder.setTitle(R.string.ingresar_nombre_figura)
            	   .setPositiveButton(R.string.aceptar, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//Guardar los valores valores del editext a las preferencias
									 EditText editextNombreFigura;
									 editextNombreFigura = (EditText)dialogView.findViewById(R.id.editxt_nombre_figura);
									 String nombre = editextNombreFigura.getText().toString();
									 figuraLocal.setNombre(nombre);
									 guardarFiguraGeometrica();
									 dialog.cancel();
								}
							}).setNegativeButton(R.string.cancelar, new
									DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									}).setOnCancelListener(new DialogInterface.OnCancelListener() {
										
										@Override
										public void onCancel(DialogInterface dialog) {
											removeDialog(DIALOG_INGRESAR_NOMBRE_FIGURA);
											
										}
									});
            // Create the AlertDialog object and return it
            return builder.create();
    	case DIALOG_NO_HAY_POSICION:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("No se pudo obtener Posición")
    		       .setMessage("Sugerencia: busque un lugar abierto, para obtener una posicion con el GPS")
    		       .setPositiveButton(R.string.ok, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//Cerrar el dialogo
									 
								}
							});
    		 return builder.create();
    	case DIALOG_NO_EXISTEN_CAPAS:
    		builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.proyecto_no_tiene_capas)
    			   .setPositiveButton(R.string.ok, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//Cerrar el dialogo
									 
								}
							});
    		return builder.create(); 	
    	case DIALOG_SELECCION_CAPAS:
    		int cant = proyectoActual.getCapasVectoriales().size();
    		itemSeleccionado = new boolean[cant];
    		
    		for(int i =0; i < cant; i++)
    		{
    			itemSeleccionado[i] = proyectoActual.getCapasVectoriales().get(i).isVisible();
    		}
    		
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.capas_visibles)
    		//recibe el array de string, en este caso el nombre de las capas, un arreglo de booleans 
    		//y el listener cuando se clickea un item
    			   .setMultiChoiceItems(proyectoActual.obtenerNombreCapasVectoriales(),itemSeleccionado,
    					   new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which,
									boolean isChecked) {
								// TODO Auto-generated method stub
								Toast.makeText(getBaseContext(), proyectoActual.getCapasVectoriales().get(which).getNombre(), Toast.LENGTH_SHORT).show();
								proyectoActual.getCapasVectoriales().get(which).setVisible(isChecked);								
							}
                       })
                     .setPositiveButton(R.string.aceptar, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{			
									ArrayList<CapaVectorial> capasActualizarVisibilidad = new ArrayList<CapaVectorial>();
									for(CapaVectorial c: proyectoActual.getCapasVectoriales())
									{
										CapaVectorial nuevaCapa = new CapaVectorial();
										nuevaCapa.setId(c.getId());
										nuevaCapa.setVisible(c.isVisible());
										capasActualizarVisibilidad.add(nuevaCapa);
									}
									
									try {
									//Dibujar todas las capas vectoriales del proyecto sobre el mapa, con la visibilidad actualizada
									ComunicacionServidor comunicar = new ComunicacionServidor(MapaGoogle.this);
									comunicar.execute(AccionesServlet.ACTUALIZAR_VISIBILIDAD_CAPA, capasActualizarVisibilidad);
									
										int cantidadModificada = (Integer)comunicar.get();
										if(cantidadModificada == capasActualizarVisibilidad.size())
										{
											cargarCapasProyectoActual();
											dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
										}
										else Toast.makeText(getBaseContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
									} 
									catch (InterruptedException e) {e.printStackTrace();} 
									catch (ExecutionException e) {e.printStackTrace();}	
									dialog.cancel();
								}
							})
							.setNegativeButton(R.string.cancelar, new
									DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											//Hacer algo cuando presiona el boton cancelar
											dialog.cancel();
										}
									}).setOnCancelListener(new DialogInterface.OnCancelListener() {
										
										@Override
										public void onCancel(DialogInterface dialog) {
											removeDialog(DIALOG_SELECCION_CAPAS);
											
										}
									});
    		return builder.create();
    	case DIALOG_MODIFICAR_CAPA_SELECCIONADA:
    		LayoutInflater infla = getLayoutInflater();
            final View viewdialog = infla.inflate(R.layout.dialog_modificar_nombre, null);
            EditText editextNombre;
			editextNombre = (EditText)viewdialog.findViewById(R.id.editxt_nombre);
			editextNombre.setText(capaSeleccionada.getNombre());
    		return new AlertDialog.Builder(this)
    		.setTitle(R.string.modificar_capa)
    		.setView(viewdialog)
    		.setPositiveButton(R.string.aceptar, new 
    						   DialogInterface.OnClickListener() {			
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//guardar el nuevo nombre de la capa
									 CapaVectorial capaEditada = new CapaVectorial();
									 capaEditada = capaSeleccionada;
									 EditText editextNombre;
									 editextNombre = (EditText)viewdialog.findViewById(R.id.editxt_nombre);
									 String nombre = editextNombre.getText().toString();
									 capaEditada.setNombre(nombre);
									 modificarCapaVectorial(capaEditada);
								}
							})
			.setNegativeButton(R.string.cancelar, new
									DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											dialog.cancel();
										}
									}).setOnCancelListener(new DialogInterface.OnCancelListener() {
										
										@Override
										public void onCancel(DialogInterface dialog) {
											removeDialog(DIALOG_MODIFICAR_CAPA_SELECCIONADA);
											
										}
									}).create();
    	}
    	return null;
    }

//	@Override
//	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
//		return false;
//	}

    private void cambiarPosicionCamara(LatLng posicion, int zoom, boolean animar){
    	CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(posicion, zoom);
    	
    	if(animar){
    		mMapa.animateCamera(camUpdate);
    	}else{
    		mMapa.moveCamera(camUpdate);
    	}
    }
    public void guardarFiguraGeometrica(){
    	ComunicacionServidor comunicar = new ComunicacionServidor(this);
    	//Si dependiendo de la capa seleccionada se guardara según el tipo y por lo tanto correctamente en la base de datos.
    	comunicar.execute(AccionesServlet.GUARDAR_FIGURA_GEOMETRICA,figuraLocal,capaSeleccionada.getId(),capaSeleccionada.getTipo());
    	try {
			int resultado = (Integer)comunicar.get();
			if(resultado>0){
				cargarCapasProyectoActual();
    			dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
				Toast.makeText(this, "Se guardó exitosamente", Toast.LENGTH_SHORT).show();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void cambiarTitulo()
    {
    	//Que Pasa si no selecciono ninguna?
    	if(capaSeleccionada!=null)
    	{
    		this.setTitle("Capa de Trabajo: "+capaSeleccionada.getNombre());
    	}
    }
    public ArrayList<CapaVectorial> obtenerCapasProyectoActual(int idProyecto)
    {
    	try {
	    	ComunicacionServidor comunicar = new ComunicacionServidor(this);
	    	//ArrayList<CapaVectorial> capas = comunicar.obtenerCapasVectorialesProyecto(idProyecto);
	    	comunicar.execute(AccionesServlet.OBTENER_CAPAS_VECTORIALES_PROYECTO, idProyecto);
	    	ArrayList<CapaVectorial> capas = (ArrayList<CapaVectorial>) comunicar.get();
			return capas;
    	}
    	catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    	
    	
    }
    public void dibujarCapasVectorialesProyecto(ArrayList<CapaVectorial> capas)
    {
    	mMapa.clear();
    	for(CapaVectorial c: capas)
    	{
    		if(c.isVisible())
    		{
    			if(c.getTipo().equals(point))
	        	{
	        		//llamar al metodo para dibujar marcadores.
	    			dibujarPuntos(c);
	        	}
	    		else if(c.getTipo().equals(linestring))
	    		{
	    			//llamar al metodo para dibujar lineas.
	    			dibujarLineas(c);
	    		}
	    		else if(c.getTipo().equals(polygon))
	    		{
	    			//llamar al metodo para dibujar poligonos.
	    			dibujarPoligono(c);
	    		}
    		}
    	}
    	dibujarImagenInterpolacion();
    }
    private void dibujarPuntos(CapaVectorial capa){
    	float color = (float)capa.getColor();//Esto esta guardado como entero en la base de datos.
    	
    	for(FiguraGeometrica punto: capa.getFiguras())
    	{	
    		//AdapterInfoWindowGISmb infoWindow = new AdapterInfoWindowGISmb(this, punto);
			LatLng coordenadas= obtenerCoordenadas(punto.getGeometria());
    		Marker marker = mMapa.addMarker(new MarkerOptions()
            .position(coordenadas)
            .title(punto.getNombre())
            .icon(BitmapDescriptorFactory.defaultMarker(color))
            );
    		
    		hashMarkerFigura.put(marker, punto);
        	//mMapa.setInfoWindowAdapter(infoWindow);
                //icon(BitmapDescriptorFactory.defaultMarker(Color.BLUE)));//Cambiar el color del marker.
    	}
    	
    }
    private void dibujarLineas(CapaVectorial capa){
    	int color = capa.getColor();
    	for(FiguraGeometrica linea: capa.getFiguras()){
    		ArrayList<LatLng> puntos = obtenerPuntosLinea(linea.getGeometria());
    		
    		mMapa.addPolyline(new PolylineOptions()
    										.addAll(puntos)
    										.color(color)
    										.width(5));
    	}
    	
    }
    private void dibujarPoligono(CapaVectorial capa){
    	int color = capa.getColor();
    	for(FiguraGeometrica poligono: capa.getFiguras()){
    		ArrayList<LatLng> puntos = obtenerPuntosPoligono(poligono.getGeometria());
    		
    		mMapa.addPolygon(new PolygonOptions()
    										.addAll(puntos)
    										.fillColor(color)
    										.strokeColor(Color.BLUE)
    										.strokeWidth(1f)
    										.visible(true));
    	}
    }
   
    private LatLng obtenerCoordenadas(String geometria)
    {
    	
    	String puntos = geometria.replace("POINT", "");
    	puntos = puntos.replace("(", "");
    	puntos = puntos.replace(")", "");
    	String[] coordenadas = puntos.split(" ");
    	
    	//En la base de datos están guardadas (x y) por lo tanto, latitud=y longitud=x
    	//en la posición [0] x, [1] y
    	double lat = Double.parseDouble(coordenadas[1]);
    	double lon = Double.parseDouble(coordenadas[0]);
    	
    	return new LatLng(lat,lon);
    }
  
    private  ArrayList<LatLng> obtenerPuntosLinea(String linestring){
    	ArrayList<LatLng> puntosLinea = new ArrayList<LatLng>();
    	
    	String puntos = linestring.replace("LINESTRING", "");
    	puntos = puntos.replace("(", "");
    	puntos = puntos.replace(")", "");
    	
    	String[] puntosCoordenadas = puntos.split(",");
    	
    	for(int i=0;i<puntosCoordenadas.length;i++)
    	{
    		String latLong[] = puntosCoordenadas[i].split(" ");
    		//base de datos-> (lon,lat) -> (x,y) -> latLon[1]=lat latLon[0]=lon
    		puntosLinea.add(new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[0])));
    	}
    	
    	return puntosLinea;
    }
    private  ArrayList<LatLng> obtenerPuntosPoligono(String polygon){
    	ArrayList<LatLng> puntosPoligono = new ArrayList<LatLng>();
    	
    	String puntos = polygon.replace("POLYGON", "");
    	puntos = puntos.replace("(", "");
    	puntos = puntos.replace(")", "");
    	
    	String[] puntosCoordenadas = puntos.split(",");
    	
    	for(int i=0;i<puntosCoordenadas.length;i++)
    	{
    		String latLong[] = puntosCoordenadas[i].split(" ");
    		//base de datos-> (lon,lat) -> (x,y) -> latLon[1]=lat latLon[0]=lon
    		puntosPoligono.add(new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[0])));
    	}
    	
    	return puntosPoligono;
    }
    
    public void obtenerImagen(Imagen imagen){
    	
    	//Metodo para probar colocar una imagen teniendo un Objeto Imagen.sobre googlemaps.
    	ComunicacionServidor com = new ComunicacionServidor(this);
    	LatLng ubicacionCentro;
    	String urlImagen;
		try {
			urlImagen = "http://"+com.IP+":8080"+"/imagenes/"+imagen.getNombreArchivo();
			
			ubicacionCentro = new LatLng(Double.parseDouble(imagen.getCentro()[1]),Double.parseDouble(imagen.getCentro()[0]));
			LatLng ubicacionNoreste = new LatLng(Double.parseDouble(imagen.getNoreste()[1]), Double.parseDouble(imagen.getNoreste()[0]));
			LatLng ubicacionSurOeste= new LatLng(Double.parseDouble(imagen.getSuroeste()[1]), Double.parseDouble(imagen.getSuroeste()[0]));
			LatLngBounds bordes = new LatLngBounds(ubicacionSurOeste, ubicacionNoreste);
					
			//.position(ubicacionCentro, 50000)
			com.execute(AccionesServlet.IMAGEN_INTERPOLACION,urlImagen);			 
			Bitmap bm = (Bitmap) com.get();
	    	BitmapDescriptor bitMapDescriptor = BitmapDescriptorFactory.fromBitmap(bm);

	    	//GroundOverlay imagMap =
	    	GroundOverlayOptions imagenInterpolacion = new GroundOverlayOptions();
	    	imagenInterpolacion.transparency(transparencia).positionFromBounds(bordes).image(bitMapDescriptor);	    	
	    	imagenInterpolada = imagenInterpolacion;
	    	dibujarImagenInterpolacion();
	    	cambiarPosicionCamara(ubicacionCentro, proyectoActual.getZoom(), true);
	    	//Barra con los valores de los niveles del minimo al maximo
	    	dibujarBarraColorMap(imagen);
    
    	}
    	catch (InterruptedException e) {e.printStackTrace();}
    	catch (ExecutionException e) {e.printStackTrace();}
    }
    
    private void dibujarImagenInterpolacion() {
    	if(imagenInterpolada != null) {
    		mMapa.addGroundOverlay(imagenInterpolada);

    	}
    }
    
    private void dibujarBarraColorMap(Imagen imagen) {
    	RelativeLayout mapa = (RelativeLayout) findViewById(R.id.layout_mapa);
    	View barraColorMap = getLayoutInflater().inflate(R.layout.barra_colores_interpolacion, null);
    	barraColorMap.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
    	mapa.addView(barraColorMap); 
    	//Setear los textos que contienen los numeros de los niveles o escala
    	TextView tv1,tv2,tv3,tv4,tv5;
    	tv1 = (TextView) (findViewById(R.id.tv_nivel_1));
    	tv1.setText("--"+new DecimalFormat("#.##").format(imagen.getNivel1()));
    	tv2 = (TextView) (findViewById(R.id.tv_nivel_2));
    	tv2.setText("--"+new DecimalFormat("#.##").format(imagen.getNivel2()));
    	tv3 = (TextView) (findViewById(R.id.tv_nivel_3));
    	tv3.setText("--"+new DecimalFormat("#.##").format(imagen.getNivel3()));
    	tv4 = (TextView) (findViewById(R.id.tv_nivel_4));
    	tv4.setText("--"+new DecimalFormat("#.##").format(imagen.getNivel4()));
    	tv5 = (TextView) (findViewById(R.id.tv_nivel_5));
    	tv5.setText("--"+new DecimalFormat("#.##").format(imagen.getNivel5()));
    }
    
    private LatLng obtenerPosicion()
    {
        LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String mejorProveedor = locMan.getBestProvider(criteria, true);
        locMan.requestLocationUpdates(mejorProveedor, 0, 0,locationListener);
        posicionActual = locMan.getLastKnownLocation(mejorProveedor);
        try
        {
        	if(posicionActual!=null){
        		LatLng posicionObtenida = new LatLng(posicionActual.getLatitude(), posicionActual.getLongitude());
        		return posicionObtenida;
        	}        	
        }
        catch(SecurityException e) {e.printStackTrace();}
        catch (IllegalArgumentException e) {e.printStackTrace();}
        catch (Exception e ) {e.printStackTrace();}
        return null;
    }

	@Override
	public void onMapClick(LatLng coordenadas) {
		if(modoIngresarFiguraClickMapa && capaSeleccionada != null) {
			if(capaSeleccionada.getTipo().equals(point))
			{
				figuraLocal = new FiguraGeometrica();
				figuraLocal.setGeometria(coordenadas.longitude+" "+coordenadas.latitude);
				showDialog(DIALOG_INGRESAR_NOMBRE_FIGURA);
				//mMapa.setOnMapClickListener(null);
			}  
			if(capaSeleccionada.getTipo().equals(polygon)){				
				//Ir Dibujando el poligono en el mapa.
				//clickMapa, es solo ocupada para limitar el inicio y el fin del dibujado del poligono				
				if(clickMapa)
				{
					if(poligonoDibujado!=null)
					{
						poligonoDibujado.remove();
						poligonoDibujado = null;		
					}
					opcionesPoligono.add(coordenadas);
					opcionesPoligono.strokeColor(Color.BLUE);
					opcionesPoligono.fillColor(Color.CYAN);
					poligonoDibujado = mMapa.addPolygon(opcionesPoligono);
					dibujarMarkerTemporal(coordenadas);
					Toast.makeText(this, "Latitud: " + coordenadas.latitude + " - Longitud: " + coordenadas.longitude, Toast.LENGTH_SHORT).show();
				}
				else{
					if(poligonoDibujado!=null)
					{
						poligonoDibujado.remove();
						poligonoDibujado = null;		
					}
					opcionesPoligono = new PolygonOptions().add(coordenadas);										
					dibujarMarkerTemporal(coordenadas);
					Toast.makeText(this, "Latitud: " + coordenadas.latitude + " - Longitud: " + coordenadas.longitude, Toast.LENGTH_SHORT).show();
					clickMapa = true;
				}
				
			}
			if(capaSeleccionada.getTipo().equals(linestring)){
				if(clickMapa)
				{
					if(lineaDibujada!=null)
					{
						lineaDibujada.remove();
						lineaDibujada = null;
					}
					opcionesLinea.add(coordenadas);
					opcionesLinea.color(Color.GREEN);
					lineaDibujada = mMapa.addPolyline(opcionesLinea);
					dibujarMarkerTemporal(coordenadas);
					Toast.makeText(this, "Latitud: " + coordenadas.latitude + " - Longitud: " + coordenadas.longitude, Toast.LENGTH_SHORT).show();
				}
				else{
					if(lineaDibujada!=null)
					{
						lineaDibujada.remove();
						lineaDibujada = null;
					}
					opcionesLinea = new PolylineOptions().add(coordenadas);
					dibujarMarkerTemporal(coordenadas);
					Toast.makeText(this, "Latitud: " + coordenadas.latitude + " - Longitud: " + coordenadas.longitude, Toast.LENGTH_SHORT).show();
					clickMapa = true;
				}
			}
		}

		else
			showDialog(DIALOG_NO_HAY_CAPA_SELECCIONADA);
					
	}
	
	private void dibujarMarkerTemporal(LatLng coordenadas) {
		if(markersTemporales == null)
			markersTemporales = new ArrayList<Marker>();
		Marker marker = mMapa.addMarker(new MarkerOptions()
        .position(coordenadas)        
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_temporal)));
		markersTemporales.add(marker);
	}
	
	private void limpiarMarkersTemporales() {		
		markersTemporales.clear();
		markersTemporales = null;
	}
	
	/**
	 * Metodo para cambiar la vista del mapa según el layer seleccionado por el usuario, el cual se recibe como
	 * parametro, luego de haber mostrado un dialogo para su seleccion (DIALOG_SELECCION_LAYER_MAPA)
	 * @param i posicion del array que esta en resource del proyecto, un xml.
	 */
	public void setLayerMapa(int i){
		if (i==0) {//Normal
            mMapa.setMapType(MAP_TYPE_NORMAL);
        } else if (i==1) {//Hibrido
            mMapa.setMapType(MAP_TYPE_HYBRID);
        } else if (i==2) {//Satelite
            mMapa.setMapType(MAP_TYPE_SATELLITE);
        } else if (i==3) {//Terreno
            mMapa.setMapType(MAP_TYPE_TERRAIN);
        } else {
            Log.i("LDA", "Error al setear la layer del mapa... ");
        }
	}
	
	private void llamarServicioInterpolacion(String nombreAtributo, int idCapa){
		ComunicacionServidor com = new ComunicacionServidor(this);
		com.execute(AccionesServlet.INTERPOLACION,nombreAtributo,idCapa);
		try {
			Imagen imagenGenerada = (Imagen)com.get();
			if(imagenGenerada==null) {
				Toast.makeText(this, "Hubo un error generando imagen :( ", Toast.LENGTH_SHORT).show();
			}
			else{
				obtenerImagen(imagenGenerada);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private String[] obtenerNombresAtributos(int idCapa)
	{
		String[] nombres={};
		ComunicacionServidor com = new ComunicacionServidor(this);
		com.execute(AccionesServlet.OBTENER_NOMBRES_ATRIBUTOS_NUMERICOS,idCapa);			
		try {
			nombres = (String[])com.get();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return nombres;
	}

	/**
	 * Metodo para manejar el click sobre el infoWindows que aparece al clickear el marker.
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		FiguraGeometrica figura = hashMarkerFigura.get(marker);		
		//Llamar a la actividad que muestre la información de la figura
		Gson gsonFig = new Gson();
		String jsonFigura = gsonFig.toJson(figura);
		Bundle extras = new Bundle();
		extras.putString("figura", jsonFigura);
		Intent intent = new Intent(this,InformacionFiguraGeometrica.class);
		intent.putExtras(extras);
		startActivity(intent);
	}

	private void actualizarCapaSeleccionada() {
		if(capaSeleccionada != null) {
			for(CapaVectorial capaVectorial: proyectoActual.getCapasVectoriales()) {
				if(capaVectorial.getId() == capaSeleccionada.getId())
					capaSeleccionada = capaVectorial;
			}	
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		cargarCapasProyectoActual();
		if(verificarCapasProyecto()) {			
			dibujarCapasVectorialesProyecto(proyectoActual.getCapasVectoriales());
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(verificarCapasProyecto()) {
			actualizarCapaSeleccionada();			
		}
	}
    
}
