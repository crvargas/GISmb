package cl.utalca.gismb;

import com.google.android.gms.internal.aa;
import com.google.gson.Gson;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class InformacionFiguraGeometrica extends ListActivity {

	FiguraGeometrica figura;
	AdapterListAtributosInformacion adapterInformacionAtributos;
	ListView lista;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_figura_geometrica);
        Bundle extras = getIntent().getExtras();
        String figuraGeometrica = extras.getString("figura");
        Gson gsonfig = new Gson();
        figura = gsonfig.fromJson(figuraGeometrica, FiguraGeometrica.class);
       // lista = (ListView)findViewById(R.id.lista_atributos);
        
        llenarDatosInterfaz(figura);
    }
/**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_informacion_figura_geometrica, menu);
        return true;
    }
**/
    private void llenarDatosInterfaz(FiguraGeometrica figura){
    	TextView id = (TextView) findViewById(R.id.tv_id);
    	id.setText(figura.getId()+"");
    	TextView nombre = (TextView) findViewById(R.id.tv_nombre);
    	nombre.setText(figura.getNombre());
    	TextView capa = (TextView) findViewById(R.id.tv_capa);
    	capa.setText(figura.getNombreCapaVectorial());
    	//Rellenar la lista con los atributos.
    	adapterInformacionAtributos = new AdapterListAtributosInformacion(this);
    	adapterInformacionAtributos.agregarAtributosArray(figura);
    	//lista.setAdapter(adapterInformacionAtributos);
    	setListAdapter(adapterInformacionAtributos);
    }
    
}
