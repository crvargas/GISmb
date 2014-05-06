package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterProyecto extends ArrayAdapter<Proyecto> {
	
	private Activity actividad;
	private ArrayList<Proyecto> proyectos;
	
	
	public AdapterProyecto(Activity activity, int id, ArrayList<Proyecto> listProyectos){
		super(activity,id,listProyectos);
		this.actividad = activity;
		this.proyectos = listProyectos;
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent){
		View view = convertView;
		
		if(view == null){
			LayoutInflater vi = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_list_proyectos, null);
		}
		
		Proyecto proyecto = proyectos.get(posicion);
		
		if(proyecto != null)
		{
			TextView nombretv = (TextView) view.findViewById(R.id.tv_item_nombre);
			nombretv.setText(" "+ proyecto.getNombre());
			
			TextView fechatv = (TextView) view.findViewById(R.id.tv_item_creacion);
			fechatv.setText(" "+proyecto.getFecha());
		}
		return view;
	}

}
