package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ListFigurasGeometricas extends ListActivity implements OnClickListener{
	private AdapterListFigurasGeometricas adapterListaFiguras; 
	private String capaVectorialJsonString;
	private String accion;
	private String crearAtributos = "CrearAtributo";
	private String eliminarFiguraGeometrica ="EliminarFigura";
	private String modificarFiguraGeometrica = "ModificarFigura";
	private static final String point = "point";
	private static final String linestring = "linestring";
	private static final String polygon = "polygon";
	private static final int SHOW_CREAR_PUNTO = 1;
	private static final int SHOW_CREAR_LINEA_POLIGONO = 2;
	private CapaVectorial capaVectorialdeTrabajo;
	private int idFiguraGeom;
	ListView lista;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_figuras_geometricas);
        
        
        lista = getListView();
        
        Button botonAceptar = (Button) findViewById(R.id.btn_aceptar);
        botonAceptar.setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
        //Obtener la capa vectorial enviada desde la actividad MapaGoogle,
        //actividad en la que esta el menu para crear los atributos o eliminar figuras
        Bundle datosDesdeActividadMapa = getIntent().getExtras();
        if(datosDesdeActividadMapa!=null){
        	 capaVectorialJsonString = datosDesdeActividadMapa.getString("capaSeleccionada");
             Gson gson = new Gson();
             capaVectorialdeTrabajo = gson.fromJson(capaVectorialJsonString, CapaVectorial.class);
             accion = datosDesdeActividadMapa.getString("accion");
             if(accion.equals(eliminarFiguraGeometrica))
             {
            	 botonAceptar.setText(R.string.eliminar_figura_geometrica);
            	 lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
             }
             if(accion.equals(crearAtributos))
             {
            	 botonAceptar.setText(R.string.crear_atributos);
            	 lista.setChoiceMode(2);
            	 
             }
             if(accion.equals(modificarFiguraGeometrica))
             {
            	 botonAceptar.setText(R.string.modificar);
            	 lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
             }
        }
      //Llamada al metodo que se encarga de rellenar la lista con las figuras geonetricas de la capa.
        rellenarListaCheck();
    }

    public void rellenarListaCheck(){
    	
    		ArrayList<FiguraGeometrica> figuras = capaVectorialdeTrabajo.getFiguras();
		
	    	if(figuras!=null)
	    	{
	    		if(accion.equals(crearAtributos))
	    		{
	    			adapterListaFiguras = new AdapterListFigurasGeometricas(this, android.R.layout.simple_list_item_multiple_choice, 
	    					figuras,ListView.CHOICE_MODE_MULTIPLE);
	    		}
	    		if(accion.equals(eliminarFiguraGeometrica))
	    		{
	    			adapterListaFiguras = new AdapterListFigurasGeometricas(this, android.R.layout.simple_list_item_single_choice, 
	    					figuras, ListView.CHOICE_MODE_SINGLE);
	    		}
	    		if(accion.equals(modificarFiguraGeometrica))
	    		{
	    			adapterListaFiguras = new AdapterListFigurasGeometricas(this, android.R.layout.simple_list_item_single_choice, 
	    					figuras, ListView.CHOICE_MODE_SINGLE);
	    		}
	    		
	    		setListAdapter(adapterListaFiguras);
	    	}
	    	else{
	    		Toast.makeText(this, "lista vacia", Toast.LENGTH_SHORT).show();
	    	}
    	} 
    

    public ArrayList<FiguraGeometrica> obtenerItemsSeleccionados()
    {
    	ArrayList<FiguraGeometrica> figurasSeleccionadas = new ArrayList<FiguraGeometrica>();
    	
		SparseBooleanArray sbArray = lista.getCheckedItemPositions();
		
		for(int i = 0; i < sbArray.size(); i++)
		{
			int itemPos = sbArray.keyAt(i);
			figurasSeleccionadas.add((FiguraGeometrica)lista.getAdapter().getItem(itemPos));
		}    		
    	return figurasSeleccionadas;
    }
    private ArrayList<Integer> obtenerIDsFigurasSeleccionadas()
    {
    	ArrayList<Integer> idSeleccion = new ArrayList<Integer>();
    	ArrayList<FiguraGeometrica> figuras = obtenerItemsSeleccionados();
    	for(FiguraGeometrica f: figuras)
    	{
    		idSeleccion.add(f.getId());
    	}
    	return idSeleccion;
    }
    @Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_aceptar:
				if(accion.equals(crearAtributos))
				{
					Bundle idsFigurasSeleccionadas = new Bundle();
					idsFigurasSeleccionadas.putIntegerArrayList("idFigurasSeleccionadas", obtenerIDsFigurasSeleccionadas());
					Intent intent = new Intent(this, CrearAtributo.class);
					intent.putExtra("seleccionFiguras", idsFigurasSeleccionadas);
					//intent.putIntegerArrayListExtra("idFigurasSeleccionadas", obtenerIDsFigurasSeleccionadas());
					startActivity(intent);	
				}
				if(accion.equals(eliminarFiguraGeometrica))
				{
					int posicionFiguraSeleccionada = lista.getCheckedItemPosition();
					idFiguraGeom = capaVectorialdeTrabajo.getFiguras().get(posicionFiguraSeleccionada).getId();
					ComunicacionServidor comunicar = new ComunicacionServidor(this);
					comunicar.execute(AccionesServlet.ELIMINAR_FIGURA_GEOMETRICA,idFiguraGeom);
					boolean eliminada;
					try {
						eliminada = (Boolean)comunicar.get();
						if(eliminada)
						{
							Intent posicionFigura = new Intent();
							posicionFigura.putExtra("posicionFigura", posicionFiguraSeleccionada);
							setResult(RESULT_OK,posicionFigura);
						}
						else{
							Toast.makeText(this, "No se pudo eliminar Figura", Toast.LENGTH_SHORT).show();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				if(accion.equals(modificarFiguraGeometrica))//Llamar a la actividad en la que puede editar la figura.
				{
					int posicionFiguraSeleccionada = lista.getCheckedItemPosition();
					FiguraGeometrica figuraAEditar = capaVectorialdeTrabajo.getFiguras().get(posicionFiguraSeleccionada);
					//Crear un JSON de la figura seleccionada.
	 				Gson gsonCapa = new Gson();
	 				String figuraJsonString = gsonCapa.toJson(figuraAEditar, FiguraGeometrica.class);
	 				//Agregarla en el intent para que 
	 				//Llamar a la actividad para crear figura
	 	    		if(capaVectorialdeTrabajo.getTipo().equals(point))
	 	    		{
	 	    			Intent i = new Intent(this,CrearPunto.class);
	 	    			Bundle datos = new Bundle(); 
	 	 				//Agregar a el intent la figura a editar
	 	    			datos.putString("FiguraAEditar", figuraJsonString);
	 	    			//accion para diferenciar entre crear y modificar
	 	 				datos.putString("accion", "ModificarFigura");
	 	 				//para saber de que tipo es la figura.
	 	 				datos.putString("tipoFigura", point);
	 	 				i.putExtras(datos);
	 	 				//startActivity(i);
	 	    			startActivityForResult(i, SHOW_CREAR_PUNTO);
	 	 				//startActivity(i);
	 	    		}
	 	    		else if(capaVectorialdeTrabajo.getTipo().equals(linestring))
	 	    		{
	 	    			Intent i = new Intent(this,CrearLineaPoligono.class);
	 	    			Bundle datos = new Bundle(); 
	 	 				//Agregar a el intent la figura a editar
	 	    			datos.putString("FiguraAEditar", figuraJsonString);
	 	    			//accion para diferenciar entre crear y modificar
	 	 				datos.putString("accion", "ModificarFigura");
	 	 				//para saber de que tipo es la figura.
	 	 				datos.putString("tipoFigura", linestring);
	 	 				i.putExtras(datos);
	 	 				//startActivity(i);
	 	    			//startActivityForResult(i, SHOW_CREAR_LINEA_POLIGONO);
	 	 				startActivity(i);
	 	    			
	 	    		}
	 	    		else if(capaVectorialdeTrabajo.getTipo().equals(polygon))
	 	    		{
	 	    			Intent i = new Intent(this,CrearLineaPoligono.class);
	 	    			Bundle datos = new Bundle(); 
	 	 				//Agregar a el intent la figura a editar
	 	    			datos.putString("FiguraAEditar", figuraJsonString);
	 	    			//accion para diferenciar entre crear y modificar
	 	 				datos.putString("accion", "ModificarFigura");
	 	 				//para saber de que tipo es la figura.
	 	    			datos.putString("tipoFigura", polygon);
	 	    			i.putExtras(datos);
	 	    			//startActivityForResult(i, SHOW_CREAR_LINEA_POLIGONO);
	 	    			startActivity(i);
	 	    			
	 	    		}
				}
				
			case R.id.btn_cancelar:
				finish();//Finaliza la actividad y retorna a la anterior.
				
		}	
	}//Fin del metodo onClick(View v)
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	//Comprobar que el resultado se esta obteniendo de la solicitud hecha a la actividad especifica.
    	if(requestCode==SHOW_CREAR_PUNTO)
    	{
    		//Si se guardo la figura correctamente, cargar nuevamente las capas para redibujarlas
    		if(resultCode==RESULT_OK && data.getExtras().getInt("resultadoGuardar")>0){
    			Intent dataIntent = new Intent();
    			dataIntent.putExtra("resultadoGuardar", data.getExtras().getInt("resultadoGuardar"));
    			setResult(RESULT_OK, dataIntent);
    			finish();//Finalizar la actividad.
    		}
    		else if(resultCode==RESULT_CANCELED)
    		{
    			//Se presiono cancelar.
    		}
    	}    	
    	
    }//Fin de onActivityResult(int requestCode, int resultCode, Intent data)
}