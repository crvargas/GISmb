package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterListCalculoEstadistica extends ArrayAdapter<ResultadoEstadistica>{
	
	private Activity actividad;
	private ArrayList<ResultadoEstadistica> resultadosPorAtributos;
	
	
	public AdapterListCalculoEstadistica(Activity activity, int id, ArrayList<ResultadoEstadistica> resultadosPorAtributos){
		super(activity,id,resultadosPorAtributos);
		this.actividad = activity;
		this.resultadosPorAtributos = resultadosPorAtributos;
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent){
		View view = convertView;
		
		if(view == null){
			LayoutInflater vi = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_list_estadisticas, null);
		}
		
		ResultadoEstadistica resultado = resultadosPorAtributos.get(posicion);
		
		if(resultado != null)
		{
			//1
			TextView nombreatrib = (TextView) view.findViewById(R.id.textViewNombreAtrib);
			nombreatrib.setText(" "+ resultado.getNombreAtributo());
			//2
			TextView tvminimo = (TextView) view.findViewById(R.id.textViewMinimo);
			tvminimo.setText(" "+ resultado.getMinimo());
			//3
			TextView tvmaximo = (TextView) view.findViewById(R.id.textViewMaximo);
			tvmaximo.setText(" "+ resultado.getMaximo());
			//4
			TextView tvmediana = (TextView) view.findViewById(R.id.textViewMediana);
			tvmediana.setText(" "+ resultado.getMediana());
			//5
			TextView tvmedia = (TextView) view.findViewById(R.id.textViewMedia);
			tvmedia.setText(" "+ resultado.getMedia());
			//6
			TextView tvvarianza = (TextView) view.findViewById(R.id.textViewVarianza);
			tvvarianza.setText(" "+ resultado.getVarianza());
			//7
			TextView tvdesvestandar = (TextView) view.findViewById(R.id.textViewDesvEstandar);
			tvdesvestandar.setText(" "+ resultado.getDesviacionEstandar());
			//8
			TextView tvsuma = (TextView) view.findViewById(R.id.textViewSuma);
			tvsuma.setText(" "+ resultado.getSuma());
			//9
			TextView tvn = (TextView) view.findViewById(R.id.textViewN);
			tvn.setText(" "+ resultado.getN());
			
		}
		return view;
	}

}
