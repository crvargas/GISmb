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

public class AdapterListAtributos extends BaseAdapter
{
	public AdapterListAtributos(){}
	private Activity actividad;
	private int indice;
	//Los tipos de atributos de una figuraGeometrica
	//private ArrayList<AtributoNumerico> atribNumericos;
	//private ArrayList<AtributoTexto> atribTextos;
	//private ArrayList<AtributoLogico> atribLogicos;
	private LayoutInflater inflater;
	private ArrayList<Object> atributos;
	private final int NUMERICO=0;
	private final int TEXTO = 1;
	private final int LOGICO = 2;
	
	//Constructor
	public AdapterListAtributos(Activity actividad)
	{
		super();
		this.actividad = actividad;
		atributos = new ArrayList<Object>();
		inflater = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public AdapterListAtributos(Context context, int textViewResourceId) {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent)
	{
		int tipo = getItemViewType(posicion);
		TextView tv1;
		EditText etxt1;
		if(convertView==null){
			switch(tipo){
			case NUMERICO:
				convertView = inflater.inflate(R.layout.item_list_atributo_num, null);
				tv1 = (TextView)convertView.findViewById(R.id.tv_nombre_atributo_num);
				AtributoNumerico num = (AtributoNumerico)atributos.get(posicion);
				tv1.setText(num.getNombre());
				etxt1 = (EditText)convertView.findViewById(R.id.editxt_valor_atributo_num);
				etxt1.setText(String.valueOf(num.getValor()));
				etxt1.addTextChangedListener(new TextWatcherGenerico(num, NUMERICO));
				break;
			case TEXTO:
				convertView = inflater.inflate(R.layout.item_list_atributo_texto, null);
				AtributoTexto text = (AtributoTexto)atributos.get(posicion);
				tv1 = (TextView)convertView.findViewById(R.id.tv_nombre_atributo_text);
				tv1.setText(text.getNombre());
				etxt1 = (EditText)convertView.findViewById(R.id.editxt_valor_atributo_text);
				etxt1.setText(text.getValor());
				etxt1.addTextChangedListener(new TextWatcherGenerico(text, TEXTO));
				break;
			case LOGICO:
				convertView = inflater.inflate(R.layout.item_list_atributo_log, null);
				AtributoLogico log = (AtributoLogico)atributos.get(posicion);
				tv1 = (TextView)convertView.findViewById(R.id.tv_nombre_atributo_log);
				tv1.setText(log.getNombre());
				ToggleButton tgbtn = (ToggleButton)convertView.findViewById(R.id.tgb_atributo_logico);
				tgbtn.setChecked(log.isValor());
				tgbtn.addTextChangedListener(new TextWatcherGenerico(log, LOGICO));
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
