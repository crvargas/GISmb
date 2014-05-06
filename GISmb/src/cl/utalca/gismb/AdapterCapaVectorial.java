package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterCapaVectorial extends ArrayAdapter<CapaVectorial> 
{
	private Activity actividad;
	private ArrayList<CapaVectorial> capas;
	private int indice;
	
	public AdapterCapaVectorial(Activity activity, int id, ArrayList<CapaVectorial> listcapas){
		super(activity,id,listcapas);
		this.actividad = activity;
		this.capas = listcapas;
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent){
		View view = convertView;
		indice = posicion;
		final int p = posicion;
		if(view == null){
			LayoutInflater vi = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_capas_a_crear, null);
		}
		
		CapaVectorial capa = capas.get(indice);
		
		if(capa != null)
		{
			TextView nombretv = (TextView) view.findViewById(R.id.tv_NombreCapa);
			nombretv.setText(" "+ capa.getNombre());
			
		}
		 
		//Manejo del click en el boton de eliminar
		ImageButton quitar = (ImageButton) view.findViewById(R.id.ibtn_descartarCapa);
		//quitar.setFocusable(false);
		quitar.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				capas.remove(p);
				notifyDataSetChanged();
				
			}
		});
		 
		return view;
	}

	
	

}
