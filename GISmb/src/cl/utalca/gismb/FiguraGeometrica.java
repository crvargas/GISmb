package cl.utalca.gismb;
import java.util.ArrayList;


public class FiguraGeometrica {
	
	protected int id;
	protected String nombre;
	private String nombreCapaVectorial;
	private int idCapaVectorial;
	//Geometry
	private String geometria;
	
	private ArrayList<AtributoLogico> atributosLogicos;
	private ArrayList<AtributoNumerico> atributosNumericos;
	private ArrayList<AtributoTexto> atributosTexto;
	
	public String getGeometria() {
		return geometria;
	}
	public void setGeometria(String geometria) {
		this.geometria = geometria;
	}
	
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
	public ArrayList<AtributoLogico> getAtributosLogicos() {
		return atributosLogicos;
	}
	public void setAtributosLogicos(ArrayList<AtributoLogico> atributosLogicos) {
		this.atributosLogicos = atributosLogicos;
	}
	public ArrayList<AtributoNumerico> getAtributosNumericos() {
		return atributosNumericos;
	}
	public void setAtributosNumericos(ArrayList<AtributoNumerico> atributosNumericos) {
		this.atributosNumericos = atributosNumericos;
	}
	public ArrayList<AtributoTexto> getAtributosTexto() {
		return atributosTexto;
	}
	public void setAtributosTexto(ArrayList<AtributoTexto> atributosTexto) {
		this.atributosTexto = atributosTexto;
	}
	public String getNombreCapaVectorial() {
		return nombreCapaVectorial;
	}
	public void setNombreCapaVectorial(String nombreCapaVectorial) {
		this.nombreCapaVectorial = nombreCapaVectorial;
	}
	public int getIdCapaVectorial() {
		return idCapaVectorial;
	}
	public void setIdCapaVectorial(int idCapaVectorial) {
		this.idCapaVectorial = idCapaVectorial;
	}
	
	
	
}
