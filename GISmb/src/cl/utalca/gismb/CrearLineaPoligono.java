package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;


public class CrearLineaPoligono extends ListActivity implements OnClickListener{
	int idCapaDeTrabajo;
	//TextView tvListaDePuntos;
	String tipoFigura;
	String puntosFigura="";
	AdapterListCrearFigura adapterCrearFigura;
	ArrayList<String> puntos = new ArrayList<String>();
	private String modo;
	private boolean modoModificarPorPunto = false;
	private String modificar = "ModificarFigura";
	private int posicionModificarPunto;
	private String crear = "CrearFigura";
    private FiguraGeometrica figuraAModificar;//Cuando se llega a la actividad a través de la opción modificar, y se tiene ya una figura.
    private String figuraJson;
    private String puntoAModificar;
    private ListView listViewPuntosFigura;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_linea_poligono);
        
        findViewById(R.id.btn_aceptar).setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
        //Agregar el punto a la lista
        findViewById(R.id.btn_ok_punto_linea).setOnClickListener(this);
        //Enviados desde la actividad MapaGoogle, para saber en que capa se agrega la figura geometrica.
        Bundle datosCapa = getIntent().getExtras();
        idCapaDeTrabajo = datosCapa.getInt("idCapa");
        tipoFigura = datosCapa.getString("tipoFigura");
        modo = datosCapa.getString("accion");
        if(modo.equals(modificar)){
        	figuraJson = datosCapa.getString("FiguraAEditar");
        	Gson gsonfig = new Gson();
        	figuraAModificar = gsonfig.fromJson(figuraJson, FiguraGeometrica.class);
        	llenarInterfazConDatosFigura(figuraAModificar);
        	this.setTitle(R.string.modificar_figura_geometrica);
        }
        //Cambiar Titulo según lo que este ingresando el usuario:
        cambiarTituloActividad();
        listViewPuntosFigura = this.getListView();
    }
    private void cambiarTituloActividad()
    {
    	if(tipoFigura.equals("linestring")){
    		setTitle(R.string.crear_linea);
    		if(modo.equals(modificar))
    		{setTitle(R.string.modificar);}
    	}
    	if(tipoFigura.equals("polygon"))
    	{
    		setTitle(R.string.crear_poligono);
    		if(modo.equals(modificar))
    		{setTitle(R.string.modificar);}
    	}
    }
    private void llenarInterfazConDatosFigura(FiguraGeometrica figura)
    {
    	EditText ediTextNombreLinea = (EditText)findViewById(R.id.etxt_nombre_figura);
    	ediTextNombreLinea.setText(figura.getNombre());
    	//Llenar la lista con los puntos que pertener a la figura linea
    	CoordenadasUtil coor = new CoordenadasUtil();
    	ArrayList<LatLng> puntosLinea = new ArrayList<LatLng>();
    	if(tipoFigura.equals("linestring"))
    	{
    		puntosLinea = coor.obtenerPuntosLinea(figura.getGeometria());
    	}
    	else if(tipoFigura.equals("polygon"))
    	{
    		puntosLinea = coor.obtenerPuntosPoligono(figura.getGeometria());
    		int largo = puntosLinea.size()-1;
    		puntosLinea.remove(largo);
    	}
    	for(LatLng coordenada: puntosLinea)
    	{
    		puntos.add(String.valueOf(coordenada.longitude)+" "+String.valueOf(coordenada.latitude));
    	}
    	adapterCrearFigura = new AdapterListCrearFigura(this, R.layout.item_capas_a_crear, puntos);
		ListView lista = (ListView) findViewById(android.R.id.list);
		lista.setAdapter(adapterCrearFigura);
    }
    
    @Override
    public void onListItemClick(ListView lv, View v, int posicion, long id)
    {		
    	EditText ediTextLatitud = (EditText) findViewById(R.id.etxt_latitud0);
		EditText ediTextLongitud = (EditText) findViewById(R.id.etxt_longitud0);
		puntoAModificar = adapterCrearFigura.getItem(posicion);
		String[] punto = puntoAModificar.split(" ");
		String longitud = punto[0];
		String latitud = punto[1];
		ediTextLatitud.setText(latitud);
		ediTextLongitud.setText(longitud);
    		 		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_crear_linea, menu);
        return true;
    }
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_aceptar:
				if(modo.equals(crear))
				{
					guardarLineaPoligono(obtenerValoresInterfaz());
				}
				else if(modo.equals(modificar))
				{
					FiguraGeometrica fig = obtenerValoresInterfaz();
					figuraAModificar.setNombre(fig.getNombre());
					figuraAModificar.setGeometria(fig.getGeometria());
					modificarLineaPoligono(figuraAModificar);
				}
				break;
			case R.id.btn_cancelar:
				setResult(RESULT_CANCELED);
				finish();//Finaliza la actividad y retorna a la anterior.
				break;
			case R.id.btn_ok_punto_linea:
				//rellenar la lista con el nuevo punto.
				rellenarListaPuntosLinea();
				break;
		}
		
	}
	private void rellenarListaPuntosLinea()
	{
		EditText ediTextLatitud = (EditText) findViewById(R.id.etxt_latitud0);
		EditText ediTextLongitud = (EditText) findViewById(R.id.etxt_longitud0);
		String latitud = ediTextLatitud.getText().toString();
		String longitud = ediTextLongitud.getText().toString();
		//puntosFigura = puntosFigura+latitud+" "+longitud;
		//Juntar los puntos que se van creando en la lista
		//tvListaDePuntos.setText(puntosFigura);
		if(!latitud.equals("") && !longitud.equals("") && CoordenadasUtil.puntosSonValidos(latitud, longitud))
		{
			if(modoModificarPorPunto) 
			{			
				puntos.set(posicionModificarPunto, longitud+" "+latitud);
				adapterCrearFigura = new AdapterListCrearFigura(this, R.layout.item_capas_a_crear, puntos);
	   		 	ListView lista = (ListView) findViewById(android.R.id.list);
	   		 	lista.setAdapter(adapterCrearFigura);
	   		 	modoModificarPorPunto = false;
			}
			
			else
			{
				//Guardar los puntos de la forma (longitud latitud) = (x y)
				puntos.add(longitud+" "+latitud);				 			
				adapterCrearFigura = new AdapterListCrearFigura(this, R.layout.item_capas_a_crear, puntos);
				ListView lista = (ListView) findViewById(android.R.id.list);
				lista.setAdapter(adapterCrearFigura);		    	
			}
			
			ediTextLatitud.setText("");
			ediTextLongitud.setText("");
		}
		
		else
			Toast.makeText(this, "Los valores ingresados no son validos", Toast.LENGTH_LONG).show();					
	}
		
	private FiguraGeometrica obtenerValoresInterfaz()
	{
		EditText ediTextNombreLinea = (EditText)findViewById(R.id.etxt_nombre_figura);
		
		FiguraGeometrica linea = new FiguraGeometrica();		
		linea.setNombre(ediTextNombreLinea.getText().toString());
		
		if(tipoFigura.equals("polygon")){
			if(cerrarPoligono()){//Para verificar que el usuario cerro o no el poligono
				puntos.add(puntos.get(0));
			}
			
		}
		
		puntosFigura = TextUtils.join(",", puntos);
		linea.setGeometria(puntosFigura);
		
		return linea;
	}
	private boolean cerrarPoligono(){
		if(puntos.get(0).equals(puntos.get(puntos.size() - 1))){
			return false;//Punto inicial y punto final son iguales, por lo tanto no hay que cerrar el poligono
		}else{
			return true;//los puntos inicial y final son distintos, por lo que hay que cerrar el poligono.
		}
	}
	public void guardarLineaPoligono(FiguraGeometrica figura)
	{
		//
		ComunicacionServidor comunicar = new ComunicacionServidor(this);
		if(tipoFigura.equals("linestring"))
		{
			comunicar.execute(AccionesServlet.GUARDAR_FIGURA_GEOMETRICA,figura,idCapaDeTrabajo,"linestring");
		}
		if(tipoFigura.equals("polygon"))
		{
			comunicar.execute(AccionesServlet.GUARDAR_FIGURA_GEOMETRICA,figura,idCapaDeTrabajo,"polygon");
		}
    	
    	try {
			int resultado = (Integer)comunicar.get();
			Intent dataIntent = new Intent();
			dataIntent.putExtra("resultadoGuardar", resultado);
			setResult(RESULT_OK, dataIntent);
			finish();//Finalizar la actividad.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int modificarLineaPoligono(FiguraGeometrica figura)
	{
		int resultado=-1;
		ComunicacionServidor comunicar = new ComunicacionServidor(this);
		if(tipoFigura.equals("linestring"))
		{
			comunicar.execute(AccionesServlet.MODIFICAR_FIGURA_GEOMETRICA,figura,"linestring");
		}
		if(tipoFigura.equals("polygon"))
		{
			comunicar.execute(AccionesServlet.MODIFICAR_FIGURA_GEOMETRICA,figura,"polygon");
		}
		try {
			resultado = (Integer)comunicar.get();
			finish();//Finalizar la actividad.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultado;
	}
	
	public boolean getModoModificarPorPunto()
	{
		return this.modoModificarPorPunto;
	}
    
	public void setModoModificarPorPunto(boolean modoModificarPorPunto)
	{
		this.modoModificarPorPunto = modoModificarPorPunto;
	}
	public int getPosicionModificarPunto() {
		return posicionModificarPunto;
	}
	public void setPosicionModificarPunto(int posicionModificarPunto) {
		this.posicionModificarPunto = posicionModificarPunto;
	}
	
	
}
