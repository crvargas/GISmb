import java.util.ArrayList;


public class Proyecto {
	
	private int id;
	private String nombre;
	private String fecha;
	private String posicionCamara;
	private int zoom;
	private ArrayList<CapaVectorial> capasVectoriales;
	
	public Proyecto(){}
	
	
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
	
	

}
