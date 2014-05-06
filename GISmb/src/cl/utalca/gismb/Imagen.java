package cl.utalca.gismb;

public class Imagen {
	
	private String nombreArchivo;
	private String ruta;
	private String[] centro;
	private String[] noreste;
	private String[] suroeste;
	//Atributos para poner en la barra de colores, para la escala(colormap)
	private double nivel1, nivel2, nivel3, nivel4, nivel5;
	
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombre) {
		this.nombreArchivo = nombre;
	}
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public String[] getCentro() {
		return centro;
	}
	public void setCentro(String[] centro) {
		this.centro = centro;
	}
	public String[] getNoreste() {
		return noreste;
	}
	public void setNoreste(String[] noreste) {
		this.noreste = noreste;
	}
	public String[] getSuroeste() {
		return suroeste;
	}
	public void setSuroeste(String[] sureste) {
		this.suroeste = sureste;
	}
	public double getNivel1() {
		return nivel1;
	}
	public void setNivel1(double nivel1) {
		this.nivel1 = nivel1;
	}
	public double getNivel2() {
		return nivel2;
	}
	public void setNivel2(double nivel2) {
		this.nivel2 = nivel2;
	}
	public double getNivel3() {
		return nivel3;
	}
	public void setNivel3(double nivel3) {
		this.nivel3 = nivel3;
	}
	public double getNivel4() {
		return nivel4;
	}
	public void setNivel4(double nivel4) {
		this.nivel4 = nivel4;
	}
	public double getNivel5() {
		return nivel5;
	}
	public void setNivel5(double nivel5) {
		this.nivel5 = nivel5;
	}
	

}
