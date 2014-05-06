package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

import android.os.Bundle;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View.OnClickListener;

import android.view.View;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class CrearProyecto extends Activity implements OnClickListener{
	int codigoSolucitud = 1;
	Spinner seleccion_tipo;
	private AdapterCapaVectorial capaAdapter;
	private ArrayList<CapaVectorial> capas;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        capas = new ArrayList<CapaVectorial>();
        setContentView(R.layout.activity_crear_proyecto);
        
        findViewById(R.id.btn_aceptar).setOnClickListener(this);
        findViewById(R.id.btn_cancelar).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crear_proyecto, menu);
        return true;
    }
    
    /** Called when the user touches the button */
    public void GuardarProyecto(){
    	int idProyectoGuardado;
    	//Extraer los datos para guardarlos
    	EditText nombreProyecto = (EditText)findViewById(R.id.editxt_nombreProyecto);
    	Proyecto proyecto = new Proyecto();
    	proyecto.setNombre(nombreProyecto.getText().toString());
    	//Obtener las capas vectoriales creadas que rellenan la lista
    	proyecto.setCapasVectoriales(capas);
    	
    	//Enviarlo a la clase que se comunica con el servidor
    	ComunicacionServidor cdb = new ComunicacionServidor(this);
    	cdb.execute(AccionesServlet.GUARDAR_PROYECTO,proyecto);
    	try {
			idProyectoGuardado = (Integer)cdb.get();
			if(idProyectoGuardado>0)
	    	{
	    		//Llamar a la actividad con el mapaGoogle para comenzar a trabajar en el proyecto.
	    		proyecto.setId(idProyectoGuardado);
	    		System.out.println("id Proyecto guardado= "+proyecto.getId());
	    		llamarActividadMapa(proyecto);
	    		
	    	}else{
	    		Toast.makeText(this, "Error Guardando el proyecto", Toast.LENGTH_LONG).show();
	    	}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    private void llamarActividadMapa(Proyecto p)
    {
    	//El proyecto recibido como argumento no tiene id
    	Intent intentConProyecto = new Intent(this, MapaGoogle.class);
    	Gson gsonProyecto = new Gson();
    	String jsonProyecto = gsonProyecto.toJson(p);
    	Bundle extrasBundle = new Bundle();
    	extrasBundle.putString("nombreActividad","activity_crear_proyecto");
    	extrasBundle.putString("proyectoGuardadoJson", jsonProyecto);
    	intentConProyecto.putExtras(extrasBundle);
    	//intentConProyecto.putExtra("idProyecto", p.getId()); el proyecto recibido no tiene id.
    	//enviar las capas que creó el usuario!
    	startActivity(intentConProyecto);
    	
    }
    /** Se llama cuando hace click en el boton para crear capa vectorial*/
    public void onClickCrearCapa(View v){
    	//Iniciar la actividad que recolectara los datos de la capa que desea crear
    	Intent intento = new Intent(this, CrearCapaVectorial.class);
    	startActivityForResult(intento, codigoSolucitud);
    }
    
    /**
     * Metodo para obtener los datos capturados por Crear_capa_vectorial inicializada por Crear_proyecto
     * @param codigoRequest identifica que actividad es la que manda el resultado, este fue enviado al iniciar la actividad.
     * @param resultCode retornado por la actividad que fue llamada.
     * @param datos retorna los datos capturados por la actividad llamada.
     */
    @Override
    protected void onActivityResult(int codigoRequest, int resultCode, Intent datos){
    	//Comprobar que el resultado se esta obteniendo de la solicitud hecha a la actividad especifica.
    	if(codigoRequest==codigoSolucitud){
    		//los datos fueron agregados a traves del metodo putExtras de un objeto intent.
    		CapaVectorial capa = new CapaVectorial();
    		capa.setNombre(datos.getStringExtra("NombreCapaVectorial"));	
    		capa.setTipo(datos.getStringExtra("tipoCapaVectorial"));
    		
    		capas.add(capa);
    		rellenarListaCapas();
    	}
    } 
    
    private void rellenarListaCapas()
    {
    	if(capas!=null)
    	{
    		//Toast.makeText(this, proyectos.get(0).getNombre(), Toast.LENGTH_LONG).show();
    		
    		 capaAdapter = new AdapterCapaVectorial(this, R.layout.item_capas_a_crear, capas);
    		 ListView lista = (ListView) findViewById(android.R.id.list);
    		 lista.setAdapter(capaAdapter);
    	}
    	else{
    		Toast.makeText(this, "lista vacia", Toast.LENGTH_SHORT).show();
    	}
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_aceptar:
				GuardarProyecto();
			case R.id.btn_cancelar:
				finish();//Finaliza la actividad y retorna a la anterior.
		}
	}
    
}
