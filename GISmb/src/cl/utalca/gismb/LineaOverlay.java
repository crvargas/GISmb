package cl.utalca.gismb;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LineaOverlay extends Overlay {
	private ArrayList<Linea> lineas;
	private GeoPoint geoPuntoInicio;
	private GeoPoint geoPuntoFinal;
	private Projection proyeccion;
	private String hexColor = "#eda62b";

	public LineaOverlay() {
		// TODO Auto-generated constructor stub
	}
	
	public LineaOverlay(GeoPoint geoPuntoInicio, GeoPoint geoPuntoFinal){
		this.geoPuntoInicio = geoPuntoInicio;
		this.geoPuntoFinal = geoPuntoFinal;
	}
	//En caso que el usuario determine el color.
	public LineaOverlay(GeoPoint geoPuntoInicio, GeoPoint geoPuntoFinal, String color){
		this.geoPuntoInicio = geoPuntoInicio;
		this.geoPuntoFinal = geoPuntoFinal;
		if (color.length() > 0)
			this.hexColor= "#" + color;
		
	}
	//Usando todas las lineas de la capa
	public LineaOverlay(ArrayList<Linea> lineas){
		this.lineas = lineas;
	}
	@Override
	public void draw(Canvas canvas, MapView mapview, boolean shadow){
		super.draw(canvas, mapview, shadow);
		
			//Ejemplo seguido de: http://www.nosinmiubuntu.com/2012/05/google-maps-en-android-iv.html
		 	Paint   mPaint = new Paint();
	        mPaint.setDither(true);
	        
	        mPaint.setColor(Color.RED);
	        
	        //caracteristicas de la linea
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(4);

	        proyeccion = mapview.getProjection(); 
	        for(Linea l:lineas)
	        {
	        	Point p1 = new Point();
		        Point p2 = new Point();
		        //Considerando dos puntos en la linea!
		        proyeccion.toPixels(l.getPuntosLinea().get(0), p1);
		        proyeccion.toPixels(l.getPuntosLinea().get(1), p2);
		        
		        Path  path = new Path();
		        path.moveTo(p2.x, p2.y);//indica el punto de inicio
		        path.lineTo(p1.x,p1.y);//indica el punto de fin
		        
		        canvas.drawPath(path, mPaint);//dibuja la linea con los puntos y caracteristicas dadas.
	        }
	        
	        
	        
	        /**
	        proyeccion.toPixels(this.geoPuntoInicio, p1);
	        proyeccion.toPixels(this.geoPuntoFinal, p2);
	        **/
	        /**
	        Path  path = new Path();
	        path.moveTo(p2.x, p2.y);//indica el punto de inicio
	        path.lineTo(p1.x,p1.y);//indica el punto de fin
	        **/

	        //canvas.drawPath(path, mPaint);//dibuja la linea con los puntos y caracteristicas dadas.
	}

}
