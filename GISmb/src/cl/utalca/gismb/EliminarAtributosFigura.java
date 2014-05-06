package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class EliminarAtributosFigura extends ListActivity implements OnClickListener, OnItemSelectedListener{

	private ArrayList<FiguraGeometrica> figuras;
	private ArrayList<AtributoNumerico> atribNumericos;
	private ArrayList<AtributoTexto> atribTextos;
	private ArrayList<AtributoLogico> atribLogicos;
	private Spinner spinnerFiguras;
	private FiguraGeometrica figuraSeleccionada;
	private Button aceptar, cancelar;
	private AdapterListAtributosEliminar adapterListEliminar;
	ListView listaAtributos;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_atributos_figura);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {        	
        	Gson gsonCapa = new Gson();
        	String capaJson = extras.getString("figurasCapa");
        	CapaVectorial capaSeleccionada = gsonCapa.fromJson(capaJson, CapaVectorial.class);
        	figuras = capaSeleccionada.getFiguras();
        	llenarSpinnerFiguras(figuras);
        }
        
        aceptar = (Button)findViewById(R.id.btn_aceptar);
        cancelar = (Button)findViewById(R.id.btn_cancelar);
        aceptar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        
        listaAtributos = getListView();
        listaAtributos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
/**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_eliminar_atributos_figura, menu);
        return true;
    }
**/
    private void llenarSpinnerFiguras(ArrayList<FiguraGeometrica> figuras){
    	//Acceder al spinner de la Interfaz.
        spinnerFiguras = (Spinner) findViewById(R.id.spinner_figurasGeometricas2);
    	//Personalizar el Spinner con las Figuras Geometricas      
        AdapterSpinnerFigurasGeometricas adapterFiguras = new 
        				AdapterSpinnerFigurasGeometricas(this, R.layout.item_spinner_figuras, figuras);
        spinnerFiguras.setAdapter(adapterFiguras);
        spinnerFiguras.setOnItemSelectedListener(this);
    }
    
  //Metodos para responder a la selección en el Spinner, del usuario.
  	@Override
  	public void onItemSelected(AdapterView<?> adapter, View view, int posicion,long arg3) {
  		figuraSeleccionada = (FiguraGeometrica) adapter.getItemAtPosition(posicion);
  		llenarListaAtributos(figuraSeleccionada);

  	}

  	@Override
  	public void onNothingSelected(AdapterView<?> arg0) {
  		// TODO Auto-generated method stub
  		
  	}
  	 @Override
 	public void onClick(View v) {
 		// TODO Auto-generated method stub
 		switch(v.getId())
 		{
 			case R.id.btn_aceptar:
 				ArrayList<Object> seleccionados = obtenerItemsSeleccionados();
 				//Llena los arreglos locales por tipo.
 				obtenerAtributosSeleccionadosPorTipo(seleccionados);
 				//Crear una figura para "empaquetar" los atributos a eliminar, 
 				//para asi enviar un solo objeto JSON a la BD
 				FiguraGeometrica figuraConAtributosAeliminar = new FiguraGeometrica();
 				figuraConAtributosAeliminar.setAtributosNumericos(atribNumericos);
 				figuraConAtributosAeliminar.setAtributosTexto(atribTextos);
 				figuraConAtributosAeliminar.setAtributosLogicos(atribLogicos);
 				figuraConAtributosAeliminar.setId(figuraSeleccionada.getId());
 				//Llamar al metodo que se comunicara con la BD para eliminar.
 				eliminarAtributosFiguraGeometrica(figuraConAtributosAeliminar);
 				break;
 			case R.id.btn_cancelar:
 				finish();//Finaliza la actividad y retorna a la anterior.
 				break;
 		}
 	}

  	private void eliminarAtributosFiguraGeometrica(FiguraGeometrica figuraConAtributosAEliminar){
  		ComunicacionServidor comDB = new ComunicacionServidor(this);
  		comDB.execute(AccionesServlet.ELIMINAR_ATRIBUTOS_FIGURA_GEOMETRICA,figuraConAtributosAEliminar);
  		try {
			int respuesta = (Integer)comDB.get();
			int cantidadAtributos = 0;
			if(atribNumericos!=null){
				cantidadAtributos = cantidadAtributos + atribNumericos.size();
			}
			if(atribTextos!=null){
				cantidadAtributos = cantidadAtributos + atribTextos.size();
			}
			if(atribLogicos!=null){
				cantidadAtributos = cantidadAtributos + atribLogicos.size();
			}
			if(respuesta>0){
				//anda bien...
				System.out.println("borro los atributos, aunque no se si todos...");
				if(respuesta==cantidadAtributos){System.out.println("Todo salió excelente!!! borro todo los atributos");}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}
  	private void llenarListaAtributos(FiguraGeometrica figuraSeleccionada)
  	{
  		//Usar el adapter para llenar la lista con los distintos atributos.
  		adapterListEliminar = new AdapterListAtributosEliminar(this,android.R.layout.simple_list_item_multiple_choice,unirListasAtributos(figuraSeleccionada));
  		//adapterListEliminar.agregarAtributosArray(figuraSeleccionada);
  		setListAdapter(adapterListEliminar);
  	}
  	
  	private ArrayList<Object> unirListasAtributos(FiguraGeometrica f)
  	{
  		ArrayList<Object> atributosUnidos = new ArrayList<Object>();
  		atributosUnidos.addAll(f.getAtributosNumericos());
		atributosUnidos.addAll(f.getAtributosTexto());
		atributosUnidos.addAll(f.getAtributosLogicos());
		
		return atributosUnidos;
  	}
  	
  	public ArrayList<Object> obtenerItemsSeleccionados()
    {
    	ArrayList<Object> atributosSeleccionados = new ArrayList<Object>();
    	
    	for(int i = 0; i < listaAtributos.getChildCount(); i++)
    	{
    		CheckedTextView chtv = (CheckedTextView) listaAtributos.getChildAt(i);
    		
    		if(chtv.isChecked())
    		{
    			atributosSeleccionados.add(listaAtributos.getAdapter().getItem(i));
    		}
    	}
//    	SparseBooleanArray chequeados = listaAtributos.getCheckedItemPositions();//Retorna un array con las posiciones de los items seleccionados
//    	for(int i=0;i<chequeados.size();i++)
//    	{
//    		if(chequeados.get(i))
//    		{
//    			//Si el item esta seleccionado, agregarlo a la lista de atributos seleccionadas
//    			atributosSeleccionados.add(figuraSeleccionada.getAtributosNumericos().get(i));
//    		}
//    	}    	
    	return atributosSeleccionados;
    }

    private void obtenerAtributosSeleccionadosPorTipo(ArrayList<Object> chequeados){
    	atribNumericos = new ArrayList<AtributoNumerico>();
    	atribTextos = new ArrayList<AtributoTexto>();
    	atribLogicos = new ArrayList<AtributoLogico>();
    	for(Object obj : chequeados)
    	{
    		if(obj instanceof AtributoNumerico){
    			AtributoNumerico num = new AtributoNumerico();
    			num = (AtributoNumerico) obj;
    			atribNumericos.add(num);	
    		}
    		else if(obj instanceof AtributoTexto){
    			AtributoTexto atxt = new AtributoTexto();
    			atxt = (AtributoTexto) obj;
    			atribTextos.add(atxt);
    		}
    		else if(obj instanceof AtributoLogico){
    			AtributoLogico alog = new AtributoLogico();
    			alog = (AtributoLogico) obj;
    			atribLogicos.add(alog);
    		}
    		
    	}
    	
    }
    
}
