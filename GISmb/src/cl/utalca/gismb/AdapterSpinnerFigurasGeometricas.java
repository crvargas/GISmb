package cl.utalca.gismb;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class AdapterSpinnerFigurasGeometricas extends ArrayAdapter<FiguraGeometrica> {

	private Context contexto;
	private ArrayList<FiguraGeometrica> figuras = null;
	private LayoutInflater layoutInflater;
	
	public AdapterSpinnerFigurasGeometricas(Context context, int idLayoutItem,ArrayList<FiguraGeometrica> figuras) {
		super(context, idLayoutItem, figuras);
		this.contexto = context;
		this.figuras = figuras;
	}

	//Este metodo sirve para poner el objeto que queda visible en la "primera" posición del spinner.
	@Override
	public View getView(int posicion, View convertView, ViewGroup parent){
		View view = convertView;
		
		if(view == null){
			LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.item_spinner_seleccionado_fig, null);
		}
		
		FiguraGeometrica figura = figuras.get(posicion);
		
		//--Modificar el item view para que se rellene con los datos de la figura geometrica.
		//if(figura != null)
		//{
			TextView tv = (TextView) view.findViewById(R.id.tv_spinner_seleccion_fg);
			tv.setText(figura.getNombre());
		//}
		
		return view;
	}
	//Para rellenar el resto del spinner
	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.item_spinner_figuras, parent, false);
        }
 
        /*
        if (row.getTag() == null)
        {
            FiguraGeometrica figura = new FiguraGeometrica();
            //redSocialHolder.setIcono((ImageView) row.findViewById(R.id.icono));
            //redSocialHolder.setTextView((TextView) row.findViewById(R.id.texto));
            //row.setTag(redSocialHolder);
        }
        */
 
        //rellenamos el layout con los datos de la fila que se está procesando
        FiguraGeometrica figura = figuras.get(position);     
        TextView tv = (TextView)row.findViewById(R.id.tv_spinner_figurasg);
        tv.setText(figura.getNombre());
        return row;
    }

	

	

}
