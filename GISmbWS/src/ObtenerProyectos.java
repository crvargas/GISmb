

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ObtenerProyectos
 */
public class ObtenerProyectos extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ObtenerProyectos() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String dato;
		ArrayList<Proyecto> proyectosgis;
		BaseDeDatos BDgis = new BaseDeDatos();
		proyectosgis = BDgis.obtenerProyectos();
		BDgis.cerrarConexion();
		
		JSONArray jsonArr = new JSONArray();
		jsonArr.put(proyectosgis);
	
		response.setContentType("application/json");//Formato del contenido de respuesta del servlet.
        PrintWriter out = response.getWriter();
        dato = jsonArr.toString();
        out.print(dato);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
