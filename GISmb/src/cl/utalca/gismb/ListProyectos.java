package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.internal.f;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


@SuppressLint("ParserError")
public class ListProyectos extends ListActivity {
	private AdapterProyecto adapterProyectos;
	private ListView listViewProyectos;
	private Proyecto proyectoSeleccionado;
	private int idItemSeleccionado =-1;
	private final static int DIALOGO_OPCIONES = 0;
	private final static int DIALOGO_MODIFICAR = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  

       setContentView(R.layout.activity_list_proyectos);
       
       listViewProyectos = this.getListView();
       //Obtener el click largo sobre la lista de proyectos.
       listViewProyectos.setLongClickable(true);
       listViewProyectos.setOnItemLongClickListener(new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int posicion, long arg3) {
			
			// TODO Auto-generated method stub
			proyectoSeleccionado = adapterProyectos.getItem(posicion);
			//Toast.makeText(getBaseContext(), "Nombre proyecto : "+
			//proyectoSeleccionado.getNombre(), Toast.LENGTH_LONG).show();
			idItemSeleccionado = posicion;
			//Llamar al dialogo para seleccionar eliminar o editar
			showDialog(DIALOGO_OPCIONES);
			return true;
		}
    	   
       });
       rellenarLista();
    }
/**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_proyectos, menu);
        return true;
    }
**/    
    public void rellenarLista(){
    	
    	try {    		
    		ComunicacionServidor comdb = new ComunicacionServidor(this);
    		ArrayList<Proyecto> proyectos = new ArrayList<Proyecto>();
    		comdb.execute(AccionesServlet.OBTENER_PROYECTOS);
    	
			proyectos = (ArrayList<Proyecto>) comdb.get();
			listViewProyectos = this.getListView();
			listViewProyectos.setAdapter(null);
	    	if(proyectos!=null)
	    	{
	    		adapterProyectos = new AdapterProyecto(this, R.layout.item_list_proyectos, proyectos);	
	    		//adapterProyectos.sort(comparator)
	    		setListAdapter(adapterProyectos);
	    		adapterProyectos.notifyDataSetChanged();
	    		listViewProyectos.invalidateViews();
	    	}
	    	else{
	    		Toast.makeText(this, "lista vacia", Toast.LENGTH_SHORT).show();
	    	}
    	} 
    	catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    @Override
    public void onListItemClick(ListView lv, View v, int posicion, long id)
    {		
    		//Guardar el id del proyecto que clickeo...
    		AdministradorPreferencias adm = new AdministradorPreferencias(getBaseContext());
    		adm.setIdProyecto(adapterProyectos.getItem(posicion).getId());
    		adm.setNombreProyecto(adapterProyectos.getItem(posicion).getNombre());
    		//--------------xxxxx------------
    		Intent i = new Intent(this, MapaGoogle.class);
    		//Agregar el dato que se desea enviar a la otra actividad
    		Bundle extras = new Bundle();
    		extras.putString("nombreActividad","activity_list_proyectos");
    		extras.putInt("idProyectoSeleccionado",adapterProyectos.getItem(posicion).getId());
    		extras.putString("nombreProyecto", adapterProyectos.getItem(posicion).getNombre());
    		extras.putInt("zoom", adapterProyectos.getItem(posicion).getZoom());
    		extras.putString("poscicionCamara", adapterProyectos.getItem(posicion).getPosicionCamara());
    		i.putExtras(extras);
    		startActivity(i); 		
    }
    
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	switch(id){
    	case 0:
    		return new AlertDialog.Builder(this)
    		.setTitle(R.string.seleccion_accion)
			.setItems(R.array.opciones_eliminar_modificar, new DialogInterface.OnClickListener() {    			

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					if(which==0)//Llamar al metodo modificar proyecto
					{
						modificarProyecto();
					}
					else if(which==1){//llamar al metodo que elimina el proyecto
						eliminarProyecto();
					}
					dialog.cancel();
				}				
    		})
    		.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					
					
				}
			})
    		.create(); 	
    	case 1:
    		LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_modificar_nombre, null);
            EditText editextNombre;
			editextNombre = (EditText)dialogView.findViewById(R.id.editxt_nombre);
			editextNombre.setText(proyectoSeleccionado.getNombre());
    		return new AlertDialog.Builder(this)
    		.setTitle(R.string.modficar_proyecto)
    		.setView(dialogView)
    		.setPositiveButton(R.string.aceptar, new 
    						   DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{									
									//guardar el nuevo nombre del proyecto
									 Proyecto proyectoEditado = new Proyecto();
									 proyectoEditado = proyectoSeleccionado;
									 EditText editextNombre;
									 editextNombre = (EditText)dialogView.findViewById(R.id.editxt_nombre);
									 String nombre = editextNombre.getText().toString();
									 proyectoEditado.setNombre(nombre);
									 guardarCambios(proyectoEditado);
								}
							})
			.setNegativeButton(R.string.cancelar, new
									DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											removerDialogo();
										}
									})
    		.create();
    	}
    	return null;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
    	

    }
    
    private void eliminarProyecto(){
    	
    	ComunicacionServidor comunicar = new ComunicacionServidor(this);
    	comunicar.execute(AccionesServlet.ELIMINAR_PROYECTO,proyectoSeleccionado.getId());
    	boolean elimino;
		try {
			elimino = (Boolean)comunicar.get();
			//Actualizar lista. Sin reconectarse a la base de dato, solo depende de la respuesta de la eliminacion
    		adapterProyectos.remove(proyectoSeleccionado);
    		adapterProyectos.setNotifyOnChange(elimino);
    		String exito = (String) getString(R.string.eliminado_exito);
    		Toast.makeText(this, exito+": "+proyectoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    private void modificarProyecto(){
    	
    	showDialog(DIALOGO_MODIFICAR);
    	
    }
    private void removerDialogo(){
    	removeDialog(DIALOGO_MODIFICAR);
    }
    private void guardarCambios(Proyecto proyectoConCambios){
    	 ComunicacionServidor com = new ComunicacionServidor(this);
    	 com.execute(AccionesServlet.MODIFICAR_PROYECTO,proyectoConCambios);
    	 int lineasModificadas;
    	 try {
 			lineasModificadas = (Integer)com.get();
 			if(lineasModificadas>0){
 				rellenarLista();
 				Toast.makeText(this, "Exito el proyecto fue modificado", Toast.LENGTH_SHORT).show();
 				removerDialogo();
 				
 			}
 			else{
 				Toast.makeText(this, "Error modificando Proyecto", Toast.LENGTH_SHORT).show();
 			}
 			//proyectoSeleccionado.setNombre(proyectoConCambios.getNombre());
 			//adapterProyectos.setNotifyOnChange(true);
 			//Actualizar lista. Despues de haber guardo el nuevo nombre.
     		/*adapterProyectos.remove(proyectoSeleccionado);
     		if(lineasModificadas>0){
     			adapterProyectos.setNotifyOnChange(true);
     		}
     		Toast.makeText(this, "El proyecto fue modificado", Toast.LENGTH_SHORT).show();*/
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	 
    }

}
