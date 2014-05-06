package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterListAtributosEliminar extends BaseAdapter{
	
	//private LayoutInflater inflater;
	private Activity actividad;
	private ArrayList<Object> atributos;
	private final int NUMERICO=0;
	private final int TEXTO = 1;
	private final int LOGICO = 2;
	
	//Constructor
	public AdapterListAtributosEliminar(Activity actividad,int id, ArrayList<Object> listaAtributos)
	{
		//super(actividad, id, listaAtributos);
		super();
		this.actividad = actividad;
		this.atributos = listaAtributos;
		//inflater = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent)
	{
		int tipo = getItemViewType(posicion);
		TextView tv;
		
		//if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
			tv = (TextView) convertView.findViewById(android.R.id.text1);
			switch(tipo){
			case NUMERICO:
				AtributoNumerico num = (AtributoNumerico)atributos.get(posicion);
				tv.setText(num.getNombre());
				break;
			case TEXTO:
				AtributoTexto text = (AtributoTexto)atributos.get(posicion);
				tv.setText(text.getNombre());
				break;
			case LOGICO:
				AtributoLogico log = (AtributoLogico)atributos.get(posicion);
				tv.setText(log.getNombre());
				break;
			}
		//}
		return convertView;
	}
	@Override 
	public int getItemViewType(int posicion){
		if(atributos.get(posicion) instanceof AtributoNumerico){
			return NUMERICO;	
		}
		else if(atributos.get(posicion) instanceof AtributoTexto){
			return TEXTO;
		}
		else if(atributos.get(posicion) instanceof AtributoLogico){
			return LOGICO;
		}
		return -1;
	}
	@Override         
	public int getViewTypeCount() { 
		
		return 3;         
	}
	@Override
	public int getCount() {
		
		return atributos.size();
	}
	@Override
	public Object getItem(int arg0) {
		
		return atributos.get(arg0);
	}
	@Override
	public long getItemId(int position) {
		
		return position;
	}
	public void agregarAtributosArray(FiguraGeometrica f){
			atributos.addAll(f.getAtributosNumericos());
			atributos.addAll(f.getAtributosTexto());
			atributos.addAll(f.getAtributosLogicos());
			notifyDataSetChanged();
	}
	
	public ArrayList<Object> obtenerAtributos(){
		return atributos;
	}

}
