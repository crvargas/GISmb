package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CrearAtributo extends Activity implements OnClickListener{

	private ArrayList<Integer> idsFigurasSeleccionadas;
	Spinner spinnerTipoAtributo;
	EditText editextNombreAtributo;
	String tipoAtributoSeleccionado;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_atributo);
        
        Bundle datosIdFiguras = getIntent().getBundleExtra("seleccionFiguras");
        
        idsFigurasSeleccionadas = datosIdFiguras.getIntegerArrayList("idFigurasSeleccionadas");
        
        spinnerTipoAtributo = (Spinner)findViewById(R.id.spinner_tipos_atributos);
        editextNombreAtributo = (EditText)findViewById(R.id.editxt_nombreAtributo);
        
        findViewById(R.id.btn_aceptar).setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crear_atributo, menu);
        return true;
    }

    @Override
   	public void onClick(View v) 
   	{
   		// TODO Auto-generated method stub
   		switch(v.getId())
   		{
   			case R.id.btn_aceptar:
   				//Guardar los atributos con relacionandolos con sus respectivas figuras geometricas
   				guardarAtributo();
   				
   			case R.id.btn_cancelar:
   				finish();//Finaliza la actividad y retorna a la anterior.
   				
   		}
   		
   	}
    
    private void guardarAtributo(){
    	String tipoAtrib = spinnerTipoAtributo.getSelectedItem().toString();
    	ComunicacionServidor comunicar = new ComunicacionServidor(this);
    	if(tipoAtrib.equals("numerico"))
    	{//si selecciona Numerico
			AtributoNumerico atribNumerico = new AtributoNumerico();
			atribNumerico.setNombre(editextNombreAtributo.getText().toString());
			comunicar.execute(AccionesServlet.GUARDAR_ATRIBUTOS_NUMERICOS,idsFigurasSeleccionadas,atribNumerico);
    	}
    	else if(tipoAtrib.equals("texto"))
    	{//si selecciona Texto "guardarAtributoTexto"
    		AtributoTexto atribTexto = new AtributoTexto();
    		atribTexto.setNombre(editextNombreAtributo.getText().toString());
    		comunicar.execute(AccionesServlet.GUARDAR_ATRIBUTOS_TEXTO,idsFigurasSeleccionadas,atribTexto);
    	}
    	else if(tipoAtrib.equals("logico"))
    	{//si selecciona Logico
    		AtributoLogico atribLogico = new AtributoLogico();
    		atribLogico.setNombre(editextNombreAtributo.getText().toString());
    		comunicar.execute(AccionesServlet.GUARDAR_ATRIBUTOS_LOGICOS,idsFigurasSeleccionadas,atribLogico);
    		
    	}
    	else
    	{
    		Toast.makeText(this, "Seleccione un tipo de Atributo", Toast.LENGTH_LONG).show();
    	}
    	
    	try {
			int resultado = (Integer)comunicar.get();
			Toast.makeText(this, "Número de atributos creados: "+resultado, Toast.LENGTH_LONG).show();
			finish();//Finalizar la actividad.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
}
