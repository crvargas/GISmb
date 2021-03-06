package cl.utalca.gismb;

import java.util.ArrayList;
import com.google.gson.Gson;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class IngresarValorAtributos extends ListActivity implements OnClickListener, OnItemSelectedListener{
	private ArrayList<FiguraGeometrica> figuras;
	private AdapterListAtributos adapterAtributos;
	private Button aceptar, cancelar;
	private Spinner spinnerFiguras;
	private AdapterSpinnerFigurasGeometricas adapterFiguras;
	private FiguraGeometrica figuraSeleccionada;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_valor_atributos);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {        	
        	Gson gsonCapa = new Gson();
        	String capaJson = extras.getString("figuras");
        	CapaVectorial capaSeleccionada = gsonCapa.fromJson(capaJson, CapaVectorial.class);
        	figuras = capaSeleccionada.getFiguras();
        	llenarSpinnerFiguras(figuras);
        } 
        
        //Personalizar los botones aceptar, cancelar.
        aceptar = (Button)findViewById(R.id.btn_aceptar);
        aceptar.setText(R.string.guardar);
        aceptar.setOnClickListener(this);
        cancelar = (Button)findViewById(R.id.btn_cancelar);
        cancelar.setText(R.string.salir);
        cancelar.setOnClickListener(this);                     
    }
    
    public void llenarSpinnerFiguras(ArrayList<FiguraGeometrica> figuras){
    	//Acceder al spinner de la Interfaz.
        spinnerFiguras = (Spinner) findViewById(R.id.spinner_figurasGeometricas);
    	//Personalizar el Spinner con las Figuras Geometricas      
        AdapterSpinnerFigurasGeometricas adapterFiguras = new 
        				AdapterSpinnerFigurasGeometricas(this, R.layout.item_spinner_figuras, figuras);
        spinnerFiguras.setAdapter(adapterFiguras);
        spinnerFiguras.setOnItemSelectedListener(this);
    }
    
    
    public void llenarListaAtributos(FiguraGeometrica figura){
    	adapterAtributos = new AdapterListAtributos(this);
    	adapterAtributos.agregarAtributosArray(figura);
    	setListAdapter(adapterAtributos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ingresar_valor_atributos, menu);
        return true;
    }
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_aceptar:
				//1. Guardar los valores que se ingresan por cada item de la lista de atributos
				ComunicacionServidor com = new ComunicacionServidor(this);
				com.execute(AccionesServlet.ACTUALIZAR_VALORES_ATRIBUTOS,figuraSeleccionada);
				//2. Actualizar los atributos de la figura seleccionada
				actualizarAtributosCambiados();
				break;
			case R.id.btn_cancelar:
				finish();//Finaliza la actividad y retorna a la anterior.
				break;
		}
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
	private void actualizarAtributosCambiados()
	{
		figuraSeleccionada.getAtributosNumericos().clear();
		figuraSeleccionada.getAtributosTexto().clear();
		figuraSeleccionada.getAtributosLogicos().clear();
		for(Object obj: obtenerObjetosDibujados())
		{
			if(obj instanceof AtributoNumerico){
				figuraSeleccionada.getAtributosNumericos().add((AtributoNumerico)obj);
			}
			else if(obj instanceof AtributoTexto){
				figuraSeleccionada.getAtributosTexto().add((AtributoTexto)obj);
			}
			else if(obj instanceof AtributoLogico){
				figuraSeleccionada.getAtributosLogicos().add((AtributoLogico)obj);
			}
		}
	}
	
    public ArrayList<Object> obtenerObjetosDibujados(){
    	int elementosListView = this.getListView().getCount();
    	ArrayList<Object> listaObjetos = new ArrayList<Object>();
    	for(int i = 0; i < elementosListView; i++){
    		Object objetoMisterioso = this.getListView().getItemAtPosition(i);
    		listaObjetos.add(objetoMisterioso);
    	}
    	
    	return listaObjetos;
    }
}
