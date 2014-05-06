package cl.utalca.gismb;

import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CrearPunto extends Activity implements OnClickListener{
	int idCapaDeTrabajo;
	private String modo;
	private String modificar = "ModificarFigura";
	private String crear = "CrearFigura";
    private FiguraGeometrica figuraAModificar;
    private String figuraJson;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_punto);
        
        findViewById(R.id.btn_aceptar).setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
        
        Bundle datosCapa = getIntent().getExtras();
        idCapaDeTrabajo = datosCapa.getInt("idCapa");
        modo = datosCapa.getString("accion");
        if(modo.equals(modificar)){
        	figuraJson = datosCapa.getString("FiguraAEditar");
        	Gson gsonfig = new Gson();
        	figuraAModificar = gsonfig.fromJson(figuraJson, FiguraGeometrica.class);
        	llenarInterfazConDatosFigura(figuraAModificar);
        	this.setTitle(R.string.modificar_figura_geometrica);
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crear_punto, menu);
        return true;
    }*/
    private void llenarInterfazConDatosFigura(FiguraGeometrica figura)
    {
    	EditText e = (EditText)findViewById(R.id.etxt_nombre_punto);
		EditText lat = (EditText)findViewById(R.id.etxt_latitud);
    	EditText lon = (EditText)findViewById(R.id.etxt_longitud);
    	e.setText(figura.getNombre());
    	CoordenadasUtil coor = new CoordenadasUtil();
    	LatLng latlon = coor.obtenerCoordenadas(figura.getGeometria());
    	lat.setText(String.valueOf(latlon.latitude));
    	lon.setText(String.valueOf(latlon.longitude));
    }
    public void guardarPunto(FiguraGeometrica f){
    	//Obtener los valores de la vista
    	ComunicacionServidor comunicar = new ComunicacionServidor(this);
    	comunicar.execute(AccionesServlet.GUARDAR_FIGURA_GEOMETRICA,f,idCapaDeTrabajo,"point");
    	try {
			int resultado = (Integer)comunicar.get();
			Intent dataIntent = new Intent();
			dataIntent.putExtra("resultadoGuardar", resultado);
			setResult(RESULT_OK, dataIntent);
			finish();//Finalizar la actividad.
			//limpiarFormulario();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public FiguraGeometrica obtenerValoresInterfaz(){
    	FiguraGeometrica figura = new FiguraGeometrica();
    	EditText e = (EditText)findViewById(R.id.etxt_nombre_punto);
    	EditText lat = (EditText)findViewById(R.id.etxt_latitud);
    	EditText lon = (EditText)findViewById(R.id.etxt_longitud);
    	if(camposSonValidos(e, lat, lon))
    	{
    		figura.setNombre(e.getText().toString());
    		//Se guarda (longitud,latitud)= (x,y)
        	figura.setGeometria(lon.getText().toString()+" "+lat.getText().toString());    		
    	}
    	else{    		
    		figura = null;//Mostrar un dialogo de advertencia!        	
    	}
    	    	
    	return figura;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_aceptar:			
				FiguraGeometrica figuraActual = obtenerValoresInterfaz();
				if(figuraActual != null) {
					if(modo.equals(crear))
					{					
						guardarPunto(figuraActual);
						
					}else if(modo.equals(modificar))
					{
						modificarPunto(figuraActual);
					}
				}
				else
					Toast.makeText(this, "Los valores ingresados no son validos", Toast.LENGTH_LONG).show();
				break;			
			case R.id.btn_cancelar:
				setResult(RESULT_CANCELED, null);
				finish();//Finaliza la actividad y retorna a la anterior.
				
		}
		
	}
	private void modificarPunto(FiguraGeometrica cambios)
	{
		//Obtener los valores de la vista
		//FiguraGeometrica cambios = obtenerValoresInterfaz();
		figuraAModificar.setNombre(cambios.getNombre());
		figuraAModificar.setGeometria(cambios.getGeometria());
    	ComunicacionServidor comunicar = new ComunicacionServidor(this);
    	comunicar.execute(AccionesServlet.MODIFICAR_FIGURA_GEOMETRICA,figuraAModificar,"point");
    	try {
			int resultado = (Integer)comunicar.get();
			Intent dataIntent = new Intent();
			dataIntent.putExtra("resultadoGuardar", resultado);
			setResult(RESULT_OK, dataIntent);
			finish();//Finalizar la actividad.
			//limpiarFormulario();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void limpiarFormulario()
	{
		EditText e = (EditText)findViewById(R.id.etxt_nombre_punto);
		EditText lat = (EditText)findViewById(R.id.etxt_latitud);
    	EditText lon = (EditText)findViewById(R.id.etxt_longitud);
    	e.setText("");
    	lat.setText("");
    	lon.setText("");
	}
	
	private boolean camposSonValidos(EditText e, EditText lat, EditText lon)
	{
		if(e.getText().toString().equals("") || lat.getText().toString().equals("") || lon.getText().toString().equals("") ||
		   !CoordenadasUtil.puntosSonValidos(Double.parseDouble(lat.getText().toString()), Double.parseDouble(lon.getText().toString())))
		   return false;
		else
			return true;
			
	}
}
