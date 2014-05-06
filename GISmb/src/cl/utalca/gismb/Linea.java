package cl.utalca.gismb;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Linea {
	
	private ArrayList<GeoPoint> puntosLinea;
	
	public Linea(){}
	
	public Linea(ArrayList<GeoPoint> puntosLinea){
		this.puntosLinea = puntosLinea;
	}

	public ArrayList<GeoPoint> getPuntosLinea() {
		return puntosLinea;
	}

	public void setPuntosLinea(ArrayList<GeoPoint> puntosLinea) {
		this.puntosLinea = puntosLinea;
	}
	
	

}
