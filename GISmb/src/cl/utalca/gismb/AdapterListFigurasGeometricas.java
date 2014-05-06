package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AdapterListFigurasGeometricas extends ArrayAdapter<FiguraGeometrica>{
	
	private Activity actividad;
	private ArrayList<FiguraGeometrica> figurasGeometricas;
	private int modoSeleccion;//Multichoice o SingleChoice
	
	public AdapterListFigurasGeometricas(Activity activity, int id, ArrayList<FiguraGeometrica> listFigurasGeometricas,int modoSeleccion){
		super(activity,id,listFigurasGeometricas);
		this.actividad = activity;
		this.figurasGeometricas = listFigurasGeometricas;
		this.modoSeleccion = modoSeleccion;
		
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent){
		View view = convertView;
		
		if(view == null){
			LayoutInflater vi = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(modoSeleccion == ListView.CHOICE_MODE_MULTIPLE){
				view = vi.inflate(android.R.layout.simple_list_item_multiple_choice, null);
			}
			
			if(modoSeleccion== ListView.CHOICE_MODE_SINGLE)
			{
				view = vi.inflate(android.R.layout.simple_list_item_single_choice, null);
			}
		}
		
		FiguraGeometrica figura = figurasGeometricas.get(posicion);
		
		//--Modificar el item view para que se rellene con los datos de la figura geometrica.
		if(figura != null)
		{
			TextView tv = (TextView) view.findViewById(android.R.id.text1);
			tv.setText(figura.getNombre());
		}
		
		return view;
	}

}
