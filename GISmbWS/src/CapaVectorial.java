import java.util.ArrayList;


public class CapaVectorial {

	private int id;
	private String nombre;
	private String tipo;
	public ArrayList<FiguraGeometrica> figuras;
	private boolean visible = true;
	private int color;
	
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
	

	
}
