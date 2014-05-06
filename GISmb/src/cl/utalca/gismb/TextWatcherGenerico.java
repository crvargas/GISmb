package cl.utalca.gismb;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class TextWatcherGenerico implements TextWatcher {

	private Object objetoObservado;
	private final int NUMERICO = 0;
	private final int TEXTO = 1;
	private final int LOGICO = 2;
	private final int tipo;
	
	public TextWatcherGenerico(Object objetoObservado, int tipo){
		this.objetoObservado = objetoObservado;
		this.tipo = tipo;
	}
	@Override
	public void afterTextChanged(Editable s) {
		

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {		
		try
		{
			String texto = s.toString();
			
			switch(tipo)
			{
				case NUMERICO:
					AtributoNumerico atrNum = (AtributoNumerico) objetoObservado;
					atrNum.setValor(Double.parseDouble(texto));
					break;
				case TEXTO:
					AtributoTexto atrText = (AtributoTexto) objetoObservado;
					atrText.setValor(texto);
					break;	
				case LOGICO:
					AtributoLogico atrLog = (AtributoLogico) objetoObservado;
					atrLog.setValor(Boolean.parseBoolean(texto));
				default:
					break;
			}
		}
		catch(Exception e) {e.printStackTrace();}		
	}

}
