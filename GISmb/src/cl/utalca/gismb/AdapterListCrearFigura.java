package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AdapterListCrearFigura extends ArrayAdapter<String>{

	//private Activity actividad;
	private ArrayList<String> puntos;
	//private int indice;
	private CrearLineaPoligono appContext = null;
	
	public AdapterListCrearFigura(CrearLineaPoligono context, int textViewResourceId,ArrayList<String> puntos) {
		super(context, textViewResourceId,puntos);
		// TODO Auto-generated constructor stub
		this.appContext = context;
		this.puntos = puntos;
		
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent){
		View view = convertView;
		final int p = posicion;
		if(view == null){
			LayoutInflater vi = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_capas_a_crear, null);
		}
		
		final String punto = puntos.get(posicion);
		
		if(punto != null)
		{
			TextView nombretv = (TextView) view.findViewById(R.id.tv_NombreCapa);
			nombretv.setText(punto);
			
		}
		 
		//Manejo del click en el boton de eliminar
		ImageButton quitar = (ImageButton) view.findViewById(R.id.ibtn_descartarCapa);
		quitar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				puntos.remove(p);
				notifyDataSetChanged();
				
			}
		});
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				appContext.setModoModificarPorPunto(true);
				appContext.setPosicionModificarPunto(p);
				EditText ediTextLatitud = (EditText) appContext.findViewById(R.id.etxt_latitud0);
				EditText ediTextLongitud = (EditText) appContext.findViewById(R.id.etxt_longitud0);
				String[] puntos = punto.split(" ");
				String longitud = puntos[0];
				String latitud = puntos[1];
				ediTextLatitud.setText(latitud);
				ediTextLongitud.setText(longitud);				
			}
		});
		
		return view;
	}


}
