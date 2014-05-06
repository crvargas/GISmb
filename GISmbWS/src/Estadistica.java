
import java.util.ArrayList;
import java.util.Collections;

public class Estadistica 
{
	public static double obtenerMinimo(ArrayList<Double> valoresAtributos)
    {
        double minimo = 0.0;
        if(valoresAtributos != null && valoresAtributos.size() > 0)
        {
            minimo = valoresAtributos.get(0);
            if(valoresAtributos.size() > 1)
            {
                for(Double d: valoresAtributos)                
                    if(d < minimo)
                        minimo = d;                
            }
        }
        
        return minimo;
    }
    
    public static double obtenerMaximo(ArrayList<Double> valoresAtributos)
    {
        double maximo = 0.0;
        if(valoresAtributos != null && valoresAtributos.size() > 0)
        {
            maximo = valoresAtributos.get(0);
            if(valoresAtributos.size() > 1)
            {
                for(Double d: valoresAtributos)                
                    if(d > maximo)
                        maximo = d;                
            }
        }
        return maximo;
    }
    
    public static double obtenerMedia(ArrayList<Double> valoresAtributos)    
    {
        double promedio = 0.0;
        
        for(Double d: valoresAtributos)
            promedio = promedio + d;
        
        promedio = promedio / valoresAtributos.size();
        return promedio;
    }
    
    public static double obtenerVarianza(ArrayList<Double> valoresAtributos)
    {
        double varianza = 0.0;
        double promedio = obtenerMedia(valoresAtributos);
        
        if(valoresAtributos != null && valoresAtributos.size() > 0)
        {
            for(Double d: valoresAtributos)        
                varianza = varianza + Math.pow(d - promedio, 2);        
        
            varianza = varianza / (valoresAtributos.size() -1);
        }
        return varianza;
    }
    
    public static double obtenerDesviacionEstandar(ArrayList<Double> valoresAtributos)
    {
        double desviacionEstandar = Math.sqrt(obtenerVarianza(valoresAtributos));                
        
        return desviacionEstandar;
    }
    
    public static double obtenerMediana(ArrayList<Double> valoresAtributos)
    {
        Collections.sort(valoresAtributos);
        
        if(valoresAtributos.size() % 2 == 0) // es par
            return (double)(valoresAtributos.get(valoresAtributos.size()/2) + valoresAtributos.get((valoresAtributos.size()/2) - 1))/2;
        else
            return valoresAtributos.get((int)(valoresAtributos.size() / 2));
    }
    
    public static double obtenerSuma(ArrayList<Double> valoresAtributos)
    {
        double suma = 0.0;
        
        for(Double d: valoresAtributos)
            suma = suma + d;
        
        return suma;
    }
}
