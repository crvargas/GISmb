package cl.utalca.gismb;

import java.util.ArrayList;


public class Proyecto {

	private int id;
	private String nombre;
	private String fecha;
	private String posicionCamara;
	private int zoom;
	private ArrayList<CapaVectorial> capasVectoriales;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public ArrayList<CapaVectorial> getCapasVectoriales() {
		return capasVectoriales;
	}
	public void setCapasVectoriales(ArrayList<CapaVectorial> capasVectoriales) {
		this.capasVectoriales = capasVectoriales;
	}
	public String getPosicionCamara() {
		return posicionCamara;
	}
	public void setPosicionCamara(String posicionCamara) {
		this.posicionCamara = posicionCamara;
	}
	public int getZoom() {
		return zoom;
	}
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	public String[] obtenerNombreCapasVectoriales() 
	{
		if(this.capasVectoriales.size() > 0)
		{
			String[] nombresCapas = new String[this.capasVectoriales.size()];
			
			for(int i=0; i < this.capasVectoriales.size(); i++) 		
				nombresCapas[i] = this.capasVectoriales.get(i).getNombre();		
			
			return nombresCapas;
		}
		else return null;
	}
	
	
	
}
