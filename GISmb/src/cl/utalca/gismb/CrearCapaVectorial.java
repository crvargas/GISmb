package cl.utalca.gismb;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;

public class CrearCapaVectorial extends Activity implements OnClickListener{
	EditText editxtNombreCapa;
	Spinner spinnerTipoCapa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_capa_vectorial);
        
      //Rellenar el Spinner que selecciona la capa.
        Spinner spinner = (Spinner)findViewById(R.id.spinner_tipos_capas_vectorial);
        //Crear un Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.opciones_tipo_capas,android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        
        editxtNombreCapa = (EditText)findViewById(R.id.editxt_nombreCapaVectorial);
        spinnerTipoCapa = (Spinner)findViewById(R.id.spinner_tipos_capas_vectorial);
        
        findViewById(R.id.btn_aceptar).setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crear_capa_vectorial, menu);
        return true;
    }
    
    //Metodo que maneja el click de aceptar guardar capa.
    public void onClickAceptarCapa(){
    	Intent intentConDatos = new Intent();
    	intentConDatos.putExtra("NombreCapaVectorial", editxtNombreCapa.getText().toString());
    	intentConDatos.putExtra("tipoCapaVectorial",spinnerTipoCapa.getSelectedItem().toString());
    	setResult(Activity.RESULT_OK, intentConDatos);
    	finish();//finaliza la actividad actual.
    }
    
    @Override
   	public void onClick(View v) 
   	{
   		// TODO Auto-generated method stub
   		switch(v.getId())
   		{
   			case R.id.btn_aceptar:
   				//Guardar los atributos con relacionandolos con sus respectivas figuras geometricas
   				onClickAceptarCapa();
   				
   			case R.id.btn_cancelar:
   				finish();//Finaliza la actividad y retorna a la anterior.
   				
   		}
   		
   	}

    
}
