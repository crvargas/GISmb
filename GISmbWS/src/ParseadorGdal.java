import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParseadorGdal 
{
	public final  String NORESTE = "Upper Right";
	public final  String SUROESTE = "Lower Left";
	public final  String CENTRO = "Center";
    private ArrayList<String> lineasGdalInfo;
    private String comandoGdalInfo = "gdalinfo ";
    private String comandoGdalwarp ="gdalwarp ";
    private String comandoGdalTransformar = "gdal_translate ";
    private String datumGoogle= "WGS84 ";
   // private String rutaArchivoEntradatif ="/home/cristina/ImagenesServer/geoTIFF/";
    private String rutaArchivoSalidaTif= "/home/cristina/ImagenesServer/geoTIFFGoogle/";
    private String rutaArchivoSalidaPng = "/home/cristina/ImagenesServer/geoPNG/";
    /***
     * Constructor por defecto.
     */
    public ParseadorGdal()
    {
        this.lineasGdalInfo = new ArrayList<String>();
    }
    
    
    public ParseadorGdal(String comandoGdalInfo)
    {
        this.lineasGdalInfo = new ArrayList<String>();
        comandoGdalInfo = comandoGdalInfo.trim();
        this.comandoGdalInfo = comandoGdalInfo + " ";
    }
    
    
    public ParseadorGdal(String comandoGdalInfo, String parametro)
    {
        this.lineasGdalInfo = new ArrayList<String>();
        comandoGdalInfo = comandoGdalInfo.trim();
        parametro = parametro.trim();
        this.comandoGdalInfo = comandoGdalInfo + " " + parametro + " ";
    }
    
    public String transformarAProyeccionGoogle(String rutaArchivo, String nombreImagenSalida)
    {
    	//Ejecutar este comando: gdalwarp -t_srs '+proj=utm +zone=11 +datum=WGS84' archivoEntrada.tif archivoSalida.tif  
    	 
    	try{
    		String comandoEjecutar = comandoGdalwarp + "-t_srs "+datumGoogle +" --config CHECK_WITH_INVERT_PROJ FALSE" + rutaArchivo +" " +rutaArchivoSalidaTif+nombreImagenSalida;
    		Process proceso = Runtime.getRuntime().exec(comandoEjecutar);
    		proceso.waitFor();
    		rutaArchivoSalidaTif = rutaArchivoSalidaTif+nombreImagenSalida;
    	}
    	catch(IOException e) {System.out.println("Error: " + e.getLocalizedMessage());} 
    	catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return rutaArchivoSalidaTif;
    }
   
    public ArrayList<String> ejecutarGdalInfo(String rutaArchivo)
    {
        try
        {
            Process p=Runtime.getRuntime().exec(comandoGdalInfo + rutaArchivo);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineasGdal = reader.readLine();
            lineasGdalInfo.add(lineasGdal);

            while(lineasGdal != null)
            {
                lineasGdal = reader.readLine();
                lineasGdalInfo.add(lineasGdal);
            }
        }
        catch(IOException e) {System.out.println("Error: " + e.getLocalizedMessage());} 
        catch (InterruptedException e) {e.printStackTrace();}
        
        return lineasGdalInfo;
    }
    
    public String[] obtenerCoordenadasBorde(String lineaRequerida)
    {
        String[] coordenadas = new String[2]; 
           
        try
        {
           String lineaDeCoordenadas = obtenerLinea(lineaRequerida);
           ArrayList<String> temporalSeparado = limpiarArreglo(lineaDeCoordenadas, lineaRequerida);
          
           coordenadas[0] = temporalSeparado.get(0);
           coordenadas[1] = temporalSeparado.get(1);
                                
        }
        catch(Exception e) {System.out.println("ERROR: " + e.getLocalizedMessage());}
       
        return coordenadas;
    }
    
    private String obtenerLinea(String lineaRequerida)
    {
        for(String s: lineasGdalInfo)
            if(s.contains(lineaRequerida))
                return s;
        
        return null;
    }

    private ArrayList<String> limpiarArreglo(String lineaCoordenadas, String lineaRequerida)
    {       
        lineaCoordenadas = lineaCoordenadas.replace(lineaRequerida, "");
        lineaCoordenadas = lineaCoordenadas.replace(")", "");
        lineaCoordenadas = lineaCoordenadas.replace("(", "");
       
        return borrarVacios(lineaCoordenadas.split(",| "));
    }
    
    private ArrayList<String> borrarVacios(String[] temporalSeparado)
    {
        ArrayList<String> teporalSeparadoFiltrado = new ArrayList<String>();
        for(int i = 0; i < temporalSeparado.length; i++)
        {
            if(temporalSeparado[i].length() > 0)
                teporalSeparadoFiltrado.add(temporalSeparado[i].trim());
        }
       
        return teporalSeparadoFiltrado;
    }
    
    public String transformarAPng(String rutaArchivo, String nombreImagenPng)
    {
    	//gdal_translate -of PNG archivoEntrada.tif archivoSalida.png
    	String ejecutar = comandoGdalTransformar + "-of "+"PNG " + rutaArchivo +" " +rutaArchivoSalidaPng+nombreImagenPng;
    	try{
    		Process proceso = Runtime.getRuntime().exec(ejecutar);
    		proceso.waitFor();
    		rutaArchivoSalidaPng = rutaArchivoSalidaPng+nombreImagenPng;
    	}
    	catch(IOException e) {System.out.println("Error: " + e.getLocalizedMessage());} 
    	catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error transformando a png: "+e.getMessage());
		}
    	return rutaArchivoSalidaPng;
    }

    
}

