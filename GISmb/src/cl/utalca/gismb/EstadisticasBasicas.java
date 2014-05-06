package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.app.ListActivity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class EstadisticasBasicas extends ListActivity{
	private ListView listaResultados;
	private AdapterListCalculoEstadistica adapterResultados;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica_basicas);
                
        Bundle datosExtras = getIntent().getBundleExtra("datosCapa");
        int idCapaTrabajo = datosExtras.getInt("idCapaTrabajo");
        rellenarListaEstadisticas(idCapaTrabajo);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_estadistica_basicas, menu);
        return true;
    }

    private void rellenarListaEstadisticas(int id)
    {
    	try {    		
    		ComunicacionServidor comdb = new ComunicacionServidor(this);
    		ArrayList<ResultadoEstadistica> resultados = new ArrayList<ResultadoEstadistica>();
    		comdb.execute(AccionesServlet.CALCULOS_ESTADISTICA_BASICA,id);
    	
			resultados = (ArrayList<ResultadoEstadistica>) comdb.get();
			listaResultados = this.getListView();
			listaResultados.setAdapter(null);
			
	    	if(resultados!=null)
	    	{
	    		adapterResultados = new AdapterListCalculoEstadistica(this, R.layout.item_list_estadisticas, resultados);	
	    		//adapterProyectos.sort(comparator)
	    		setListAdapter(adapterResultados);
	    		adapterResultados.notifyDataSetChanged();
	    		listaResultados.invalidateViews();
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
}
