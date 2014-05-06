package cl.utalca.gismb;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AdministradorPreferencias extends Activity{
	
	public final static String NOMBRE_PREFERENCIAS = "MisPreferencias";
	public final static String IP_SERVIDOR = "ipservidor";
	public final static String ID_ULTIMO_PROYECTO_ABIERTO = "idUltimoProyectoAbierto";
	public final static String NOMBRE_PROYECTO= "nombreProyecto";
	private Context contexto;
	//private SharedPreferences preferencias;
	
	public AdministradorPreferencias(Context contexto)
	{
		this.contexto = contexto;
	}
	
	public String getIP()
	{
    	SharedPreferences pref = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, MODE_PRIVATE);
		String ip = pref.getString(IP_SERVIDOR,"0");
    	return ip;
    }
	
	public void setIP(String ipNueva)
	{
		SharedPreferences preferencias = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putString(IP_SERVIDOR, ipNueva);
		editor.commit();
	}
	//Para recordar cual fue el último proyecto que se reviso o trabajo...
	public int getIdProyecto()
	{
    	SharedPreferences pref = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, MODE_PRIVATE);
		int idProyecto = pref.getInt(ID_ULTIMO_PROYECTO_ABIERTO, 0);
    	return idProyecto;
    }
	public void setIdProyecto(int idProyectoAbierto)
	{
		SharedPreferences preferencias = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putInt(ID_ULTIMO_PROYECTO_ABIERTO, idProyectoAbierto);
		editor.commit();
	}
	public String getNombreProyecto()
	{
    	SharedPreferences pref = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, MODE_PRIVATE);
		String nombreProyecto = pref.getString(NOMBRE_PROYECTO, "0");
    	return nombreProyecto;
    }
	public void setNombreProyecto(String nombreProyecto)
	{
		SharedPreferences preferencias = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putString(NOMBRE_PROYECTO, nombreProyecto);
		editor.commit();
	}

}
