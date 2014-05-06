package cl.utalca.gismb;



import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;

public class CapaVectorial {
	
	private int id;
	private String nombre;
	private String tipo;
	public ArrayList<FiguraGeometrica> figuras;
	private boolean visible = true;
	private int color;
	private final int transparenciaPorDefecto = 150;
	
	public CapaVectorial(){
		this.figuras = new ArrayList<FiguraGeometrica>();
		this.id = 0;
	}
	
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	public String getNombre(){
		return this.nombre;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	public String getTipo(){
		return this.tipo;
	}
	public void setTipo(String tipo){
		this.tipo = tipo;
	}
	
	public ArrayList<FiguraGeometrica> getFiguras() {
		return figuras;
	}

	public void setFiguras(ArrayList<FiguraGeometrica> figuras) {
		this.figuras = figuras;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	/**
	 * Genera un color aleatorio, usando Random entre 0 y 255, para los niveles de rojo, verde y azul (rgb)
	 * @return entero que representa el color de acuerdo a los diferentes niveles de rojo,verde y azul.
	 */
	public void generarColorCapa(){
		int nuevoColor;
		if(this.tipo.equals("point"))
		{
			//Este metodo esta hecho solo para cambiar el color del marker, que solo se le puede entregar un valor
			//entre 0 y 360.
			Random rand = new Random();
	    	nuevoColor = rand.nextInt(360);
		}
		else
		{
			Random rand = new Random();
	    	int red = rand.nextInt(255);
	    	int green = rand.nextInt(255);
	    	int blue = rand.nextInt(255);
			//nuevoColor = Color.rgb(red, green, blue);
			nuevoColor = Color.argb(transparenciaPorDefecto, red, green, blue);
		}
    	
    	this.setColor(nuevoColor);
    }
}
