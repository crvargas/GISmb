package cl.utalca.gismb;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AdapterListAtributosInformacion extends BaseAdapter
{
	public AdapterListAtributosInformacion(){}
	private Activity actividad;
	private int indice;
	
	private LayoutInflater inflater;
	private ArrayList<Object> atributos;
	private final int NUMERICO=0;
	private final int TEXTO = 1;
	private final int LOGICO = 2;
	
	//Constructor
	public AdapterListAtributosInformacion(Activity actividad)
	{
		super();
		this.actividad = actividad;
		atributos = new ArrayList<Object>();
		inflater = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public AdapterListAtributosInformacion(Context context, int textViewResourceId) {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent)
	{
		int tipo = getItemViewType(posicion);
		TextView tv1;
		TextView tv2;
		
		if(convertView==null){
			switch(tipo){
			case NUMERICO:
				convertView = inflater.inflate(R.layout.item_list_info_figura_geometrica, null);
				tv1 = (TextView)convertView.findViewById(R.id.tv_nombre_atributo);
				AtributoNumerico num = (AtributoNumerico)atributos.get(posicion);
				tv1.setText(num.getNombre());
				tv2 = (TextView)convertView.findViewById(R.id.tv_valor_atributo);
				tv2.setText(String.valueOf(num.getValor()));
				break;
			case TEXTO:
				convertView = inflater.inflate(R.layout.item_list_info_figura_geometrica, null);
				AtributoTexto text = (AtributoTexto)atributos.get(posicion);
				tv1 = (TextView)convertView.findViewById(R.id.tv_nombre_atributo);
				tv1.setText(text.getNombre());
				tv2 = (TextView)convertView.findViewById(R.id.tv_valor_atributo);
				tv2.setText(text.getValor());
				break;
			case LOGICO:
				convertView = inflater.inflate(R.layout.item_list_info_figura_geometrica, null);
				AtributoLogico log = (AtributoLogico)atributos.get(posicion);
				tv1 = (TextView)convertView.findViewById(R.id.tv_nombre_atributo);
				tv1.setText(log.getNombre());
				tv2 = (TextView)convertView.findViewById(R.id.tv_valor_atributo);
				tv2.setText(log.isValor()+"");
				break;
			}
		}
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