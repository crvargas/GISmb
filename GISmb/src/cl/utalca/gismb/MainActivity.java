package cl.utalca.gismb;


import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;



public class MainActivity extends ListActivity{
	String[] opciones;
	private EditText editTextIP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        opciones = getResources().getStringArray(R.array.opciones_inicio);        
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, opciones));
    }
    
    @Override
    public void onListItemClick(ListView lv, View v, int posicion, long id){
    	
    	if(posicion == 0){
    		//Abrir último proyecto (en caso de haber ocultar)
    		AdministradorPreferencias adm = new AdministradorPreferencias(this);
    		int idProyecto = adm.getIdProyecto();//¿qué pasa si fue eliminado?? el telefono tendrá un id que ya no existe..
    		String nombreProyecto = adm.getNombreProyecto();
    		Intent i = new Intent(this, MapaGoogle.class);
    		//Agregar el dato que se desea enviar a la otra actividad
    		Bundle extras = new Bundle();
    		extras.putString("nombreActividad","activity_main");
    		extras.putInt("idProyectoSeleccionado",idProyecto);
    		extras.putString("nombreProyecto", nombreProyecto);
    		i.putExtras(extras);
    		startActivity(i);   		
    	}
    	if(posicion == 1){
    		//Ver todos los proeyectos 
    		Intent i = new Intent(this, ListProyectos.class);
    		startActivity(i);
    	}
    	if(posicion == 2){
    		//Crear proyecto
    		Intent i = new Intent(this, CrearProyecto.class);
    		startActivity(i);
    		//Toast.makeText(this, "has seleccionado" + opciones[posicion], Toast.LENGTH_SHORT).show();
    		
    	}
    	    		

    }	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	//Manejo del click sobre los itemes del menu
    	switch(item.getItemId())
    	{
    	case R.id.menu_main:
    	    showDialog(0);		
    	return true;
    	default:
            return super.onOptionsItemSelected(item);
    	}
    	
    }//Fin de onOptionsItemSelected

    @Override
    protected Dialog onCreateDialog(int id)
    {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	// Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogo_ip_servidor, null);
        //"inflar" y setear el layout para el dialogo
        //pasar null como padre, ya que va a formar parte del dialogo.
        builder.setView(dialogView);
        
    	switch (id)
    	{
    	case 0:
    		return builder
    				   .setTitle("Ingrese IP servidor:")
    				   .setPositiveButton(R.string.aceptar, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//Guardar los valores valores del editext a las preferencias
									editTextIP = (EditText)dialogView.findViewById(R.id.etxt_ip_servidor);
									String ipText = editTextIP.getText().toString();
									AdministradorPreferencias admin = new AdministradorPreferencias(getBaseContext());
									admin.setIP(ipText);
									
								}
							})
							.setNegativeButton(R.string.cancelar, new
									DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
										}
									})
							.create();
    		
    	}
    	return null;
    }

}
