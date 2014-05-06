package cl.utalca.gismb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;


public class AdapterListModificarAtributos extends BaseAdapter{

	public AdapterListModificarAtributos(){}
	private Activity actividad;
	private int indice;
	private LayoutInflater inflater;
	private ArrayList<Object> atributos;
	private final int NUMERICO=0;
	private final int TEXTO = 1;
	private final int LOGICO = 2;
	
	//Constructor
	public AdapterListModificarAtributos(Activity actividad)
	{
		super();
		this.actividad = actividad;
		atributos = new ArrayList<Object>();
		inflater = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public AdapterListModificarAtributos(Context context, int textViewResourceId) {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent)
	{
		int tipo = getItemViewType(posicion);
		EditText etxt1;
		if(convertView==null){
			switch(tipo){
			case NUMERICO:
				convertView = inflater.inflate(R.layout.item_list_modificar_atributo, null);
				etxt1 = (EditText)convertView.findViewById(R.id.editxt_modificar_nombre_atributo);
				AtributoNumerico num = (AtributoNumerico)atributos.get(posicion);
				etxt1.setText(num.getNombre());
				etxt1.addTextChangedListener(new TextWatcherModificarNombreAtributos(num, NUMERICO));
				break;
			case TEXTO:
				convertView = inflater.inflate(R.layout.item_list_modificar_atributo, null);
				AtributoTexto text = (AtributoTexto)atributos.get(posicion);
				etxt1 = (EditText)convertView.findViewById(R.id.editxt_modificar_nombre_atributo);
				etxt1.setText(text.getNombre());
				etxt1.addTextChangedListener(new TextWatcherModificarNombreAtributos(text, TEXTO));
				break;
			case LOGICO:
				convertView = inflater.inflate(R.layout.item_list_modificar_atributo, null);
				AtributoLogico log = (AtributoLogico)atributos.get(posicion);
				etxt1 = (EditText)convertView.findViewById(R.id.editxt_modificar_nombre_atributo);
				etxt1.setText(log.getNombre());
				etxt1.addTextChangedListener(new TextWatcherModificarNombreAtributos(log, LOGICO));
				break;
			}
		}
		return convertView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return atributos.size();
	}
	@Override         
	public int getViewTypeCount() { 
		
		return 3;         
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return atributos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
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

	public void agregarAtributosArray(FiguraGeometrica f){
		atributos.addAll(f.getAtributosNumericos());
		atributos.addAll(f.getAtributosTexto());
		atributos.addAll(f.getAtributosLogicos());
		notifyDataSetChanged();
}

}
