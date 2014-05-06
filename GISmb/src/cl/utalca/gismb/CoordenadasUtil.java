package cl.utalca.gismb;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

public class CoordenadasUtil {
	
	public LatLng obtenerCoordenadas(String geometria)
    {
    	
    	String puntos = geometria.replace("POINT", "");
    	puntos = puntos.replace("(", "");
    	puntos = puntos.replace(")", "");
    	String[] coordenadas = puntos.split(" ");
    	
    	//En la base de datos están guardadas (x y) por lo tanto, latitud=y longitud=x
    	//en la posición [0] x, [1] y
    	double lat = Double.parseDouble(coordenadas[1]);
    	double lon = Double.parseDouble(coordenadas[0]);
    	
    	return new LatLng(lat,lon);
    }
  
    public  ArrayList<LatLng> obtenerPuntosLinea(String linestring){
    	ArrayList<LatLng> puntosLinea = new ArrayList<LatLng>();
    	
    	String puntos = linestring.replace("LINESTRING", "");
    	puntos = puntos.replace("(", "");
    	puntos = puntos.replace(")", "");
    	
    	String[] puntosCoordenadas = puntos.split(",");
    	
    	for(int i=0;i<puntosCoordenadas.length;i++)
    	{
    		String latLong[] = puntosCoordenadas[i].split(" ");
    		//base de datos-> (lon,lat) -> (x,y) -> latLon[1]=lat latLon[0]=lon
    		puntosLinea.add(new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[0])));
    	}
    	
    	return puntosLinea;
    }
    public  ArrayList<LatLng> obtenerPuntosPoligono(String polygon){
    	ArrayList<LatLng> puntosPoligono = new ArrayList<LatLng>();
    	
    	String puntos = polygon.replace("POLYGON", "");
    	puntos = puntos.replace("(", "");
    	puntos = puntos.replace(")", "");
    	
    	String[] puntosCoordenadas = puntos.split(",");
    	
    	for(int i=0;i<puntosCoordenadas.length;i++)
    	{
    		String latLong[] = puntosCoordenadas[i].split(" ");
    		//base de datos-> (lon,lat) -> (x,y) -> latLon[1]=lat latLon[0]=lon
    		puntosPoligono.add(new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[0])));
    	}
    	
    	return puntosPoligono;
    }
    public String obtenerPuntosFiguraTexto(List<LatLng> puntosFigura){
    	String puntosFiguraTexto="";
    	ArrayList<String> puntos = new ArrayList<String>();
    	for(LatLng latlon: puntosFigura){
    		puntos.add(latlon.longitude +" "+ latlon.latitude);	
    	}
    	puntosFiguraTexto = TextUtils.join(",", puntos);
    	
    	return puntosFiguraTexto;
    }

    public static boolean puntosSonValidos(double lat, double lon)
    {
    	if(lat <= 90.0 && lat >= -90.0 && lon <= 180.0 && lon >= -180.0)
    		return true;
    	else
    		return false;
    }
    
    public static boolean puntosSonValidos(String latitud, String longitud)
    {
    	double lat = Double.parseDouble(latitud);
    	double lon = Double.parseDouble(longitud);
    	
    	if(lat <= 90.0 && lat >= -90.0 && lon <= 180.0 && lon >= -180.0)
    		return true;
    	else
    		return false;
    }
}
