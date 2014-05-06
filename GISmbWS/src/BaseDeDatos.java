import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class BaseDeDatos {
	
	private Connection conn;
	private String sql;
	
	/***
	 * Abrir la conexión a la Base de Datos
	 * @return conexión a la BD
	 */
	public BaseDeDatos(){
		
		try{
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost/GISandroidDB";
			
			this.conn = DriverManager.getConnection(url, "cristina", "cristina");
			
		}
		catch(ClassNotFoundException esql){
			System.out.println(esql.getMessage());
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
	}
	
	public void cerrarConexion(){
		try{
			this.conn.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
	}
	/**
	 * Metodo que retorna una capa_vectorial usando el id de la capa.
	 * @param id
	 * @param idProyecto
	 * @return
	 */
	public CapaVectorial obtenerCapaVectorial(int id){
		
		this.sql = "SELECT * FROM capa_vectorial WHERE id="+id;
		
		CapaVectorial cv = new CapaVectorial();
		
		try 
		{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(this.sql);
			
			while(rs.next()){
	    		cv.setId(rs.getInt("id"));
	    		cv.setNombre(rs.getString("nombre"));
	    		cv.setTipo(rs.getString("tipo"));
	    		cv.setVisible(rs.getBoolean("visible"));
	    		cv.setColor(rs.getInt("color"));
			}
    		
    		st.close();
        	rs.close();
        	
		}		
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
    	
		return cv;
	}
	/***
	 * Metodo para obtener todas las capas vectoriales de un proyecto gis existentes en la BD.
	 * @return lista de todas las capas por id de proyecto(arrayList)
	 */
	public ArrayList<CapaVectorial> obtenerCapasVectoriales(int idProyecto){
		this.sql = "select capa_vectorial.id, capa_vectorial.nombre, capa_vectorial.tipo, capa_vectorial.visible,capa_vectorial.color "+
				   "from capa_vectorial "+ 
				   "where fk_proyecto = "+idProyecto;
		
		
		ArrayList<CapaVectorial> capasv = new ArrayList<CapaVectorial>();
		
		try
		{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(this.sql);
			
			
			while(rs.next()){
				CapaVectorial cv = new CapaVectorial();
				cv.setId(rs.getInt("id"));
				cv.setNombre(rs.getString("nombre"));
				cv.setTipo(rs.getString("tipo"));
				cv.setVisible(rs.getBoolean("visible"));
				cv.setColor(rs.getInt("color"));
				capasv.add(cv);
			}
			System.out.println(capasv);
			for(CapaVectorial capa:capasv){
				//Llenar la capa con las figuras que posee
				capa.figuras = obtenerFigurasGeometricas(capa.getId(),capa.getTipo(), capa.getNombre());
			}
			st.close();
			rs.close();
		}
		catch(SQLException sqlexception)
		{
			System.out.println("Mensaje obtenerCapasProyecto exception SQL: "+sqlexception.getMessage());
		}
		
		return capasv;
	}
	/**
	 * Obtiene una figura geometrica transformada en el sistema de coordenadas google (900913).
	 * @param idCapa
	 * @param tipo: de figura POINT, LINESTRING, POLYGON.
	 * @param nombreCapa
	 * @return
	 */
	public ArrayList<FiguraGeometrica> obtenerFigurasGeometricas(int idCapa, String tipo, String nombreCapa){
		/* Uso la función st_transform(geometry,srid) Para asegurar que la geometría obtenida sea en
		 * el sistema de coordenadas que usa Google srid=900913.*/
		this.sql = "SELECT id, nombre, st_astext(st_transform(geom,900913)) AS geom "+
					"FROM figura_geometrica WHERE fk_capa_vectorial="+idCapa;

		ArrayList<FiguraGeometrica> figuras_capa = new ArrayList<FiguraGeometrica>();
		
		try
		{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(this.sql);
			
			
			while(rs.next()){
				FiguraGeometrica figura = new FiguraGeometrica();
				//Datos de la capa
				figura.setIdCapaVectorial(idCapa);
				figura.setNombreCapaVectorial(nombreCapa);
				//Datos de la figura
				figura.setId(rs.getInt("id"));
				figura.setNombre(rs.getString("nombre"));
				figura.setGeometria(rs.getString("geom"));
				//Obtener los atributos de  las figuras geometricas
				figura.setAtributosNumericos(obtenerAtributosNumericos(figura.getId()));
				figura.setAtributosTexto(obtenerAtributosTexto(figura.getId()));
				figura.setAtributosLogicos(obtenerAtributosLogico(figura.getId()));
				figuras_capa.add(figura);
			}
			st.close();
			rs.close();
		}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		
		return figuras_capa;
	}

	public ArrayList<AtributoNumerico> obtenerAtributosNumericos(int idFiguraGeometrica)
	{
		ArrayList<AtributoNumerico> atributosNumericos = new ArrayList<AtributoNumerico>();
		//Buscar los atributos de tipo numerico
			this.sql = "select * "+
					   "from atributo_numerico "+ 
				       "where fk_figura_geom = "+idFiguraGeometrica;
			try
			{
				Statement st = this.conn.createStatement();
				ResultSet rs = st.executeQuery(this.sql);
				while(rs.next()){
					AtributoNumerico atribNumerico = new AtributoNumerico();
					atribNumerico.setId(rs.getInt("id"));
					atribNumerico.setNombre(rs.getString("nombre"));
					atribNumerico.setValor(rs.getDouble("valor"));
					atributosNumericos.add(atribNumerico);
				}
				st.close();
				rs.close();
			}
			catch(SQLException sqlexception)
			{
				System.out.println(sqlexception.getMessage());
			}
			return atributosNumericos;
	}
			//Buscar los atributos de tipo Texto
	public ArrayList<AtributoTexto> obtenerAtributosTexto(int idFiguraGeometrica)
	{
			this.sql = "select * "+
					   "from atributo_texto "+ 
				       "where fk_figura_geom = "+idFiguraGeometrica;
			ArrayList<AtributoTexto> atributosTextos = new ArrayList<AtributoTexto>();
				try
				{
					Statement st = this.conn.createStatement();
					ResultSet rs = st.executeQuery(this.sql);
					while(rs.next()){
						AtributoTexto atribTexto = new AtributoTexto();
						atribTexto.setId(rs.getInt("id"));
						atribTexto.setNombre(rs.getString("nombre"));
						atribTexto.setValor(rs.getString("valor"));
						atributosTextos.add(atribTexto);
					}
					st.close();
					rs.close();
				}
			catch(SQLException sqlexception)
			{
				System.out.println(sqlexception.getMessage());
			}
				return atributosTextos;
	}
			//Buscar los atributos de tipo lógico.		
	public ArrayList<AtributoLogico> obtenerAtributosLogico(int idFiguraGeometrica)
	{
			this.sql = "select *"+
					   "from atributo_logico "+ 
					   "where fk_figura_geom = ?";
			
			ArrayList<AtributoLogico> atributosLogicos = new ArrayList<AtributoLogico>();
			try
			{
				//Statement st = this.conn.createStatement();
				PreparedStatement ps = this.conn.prepareStatement(this.sql);
				ps.setInt(1, idFiguraGeometrica);
				ResultSet rs = ps.executeQuery();
				while(rs.next()){
					AtributoLogico atribLogico = new AtributoLogico();
					atribLogico.setId(rs.getInt("id"));
					atribLogico.setNombre(rs.getString("nombre"));
					atribLogico.setValor(rs.getBoolean("valor"));
					atributosLogicos.add(atribLogico);
				}
				ps.close();
				rs.close();
			}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		
		return atributosLogicos;
	}	
	
	public int guardarCapa(CapaVectorial capa, int idProyecto){
		this.sql = "INSERT INTO capa_vectorial(nombre,tipo,color,fk_proyecto) " +
					"VALUES('"+capa.getNombre()+"','"+capa.getTipo()+"', "+capa.getColor()+", "+idProyecto+") returning id";
		//int n=0;
		int idCapaGuardada=-1;
		try{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			//n = st.executeUpdate(this.sql);
			idCapaGuardada = rs.getInt(1);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		return idCapaGuardada;
	}
	
	public int guardarFiguraGeometrica(FiguraGeometrica figura, int idCapa, String tipoCapa){
		int lineasModificadas=0;
		if(tipoCapa.equals("point"))
		{
			this.sql = "INSERT INTO figura_geometrica (geom,nombre,fk_capa_vectorial )" +
					" VALUES (st_geomfromtext('POINT(" +
					figura.getGeometria() +
					")',900913)," +
					"'"+figura.getNombre()+"',"+idCapa+")";
			try{
				Statement st = this.conn.createStatement();
				lineasModificadas = st.executeUpdate(this.sql);
				st.close();
			}
			catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
			}
			return lineasModificadas;
		}
		if(tipoCapa.equals("linestring"))
		{
			double largo = calcularLargoLinea(figura.getGeometria());
			this.sql = "INSERT INTO figura_geometrica (geom,nombre,fk_capa_vectorial,area_largo )" +
					" VALUES (st_geomfromtext('LINESTRING(" +
					figura.getGeometria() +
					")',900913)," +
					"'"+figura.getNombre()+"',"+idCapa+","+largo+")";
			try{
				Statement st = this.conn.createStatement();
				lineasModificadas = st.executeUpdate(this.sql);
				st.close();
			}
			catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
			}
			return lineasModificadas;
			
		}
		if(tipoCapa.equals("polygon"))
		{
			double area = calcularAreaPoligono(figura.getGeometria());
			this.sql = "INSERT INTO figura_geometrica (geom,nombre,fk_capa_vectorial,area_largo )" +
					" VALUES (st_geomfromtext('POLYGON((" +
					figura.getGeometria() +
					"))',900913)," +
					"'"+figura.getNombre()+"',"+idCapa+","+area+")";
			try{
				Statement st = this.conn.createStatement();
				lineasModificadas = st.executeUpdate(this.sql);
				st.close();
			}
			catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
			}
			return lineasModificadas;
			
		}
		else{
			return -1;
		}
		
	}
	
	/***
	 * Metodo para obtener todos los proyectos gis existentes en la BD.
	 * @return lista de todos los proyectos (arrayList)
	 */
	public ArrayList<Proyecto> obtenerProyectos(){
		this.sql = "SELECT id,nombre,fecha,zoom,st_astext(posicion_camara) AS posicion_camara FROM proyecto ORDER BY id DESC";
		
		ArrayList<Proyecto> proyectosgis = new ArrayList<Proyecto>();
		try 
		{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(this.sql);		
			
			while(rs.next()){
				Proyecto p = new Proyecto();
	    		p.setId(rs.getInt("id"));
	    		p.setNombre(rs.getString("nombre"));
	    		p.setFecha(rs.getDate("fecha").toString());
	    		p.setZoom(rs.getInt("zoom"));
	    		p.setPosicionCamara(rs.getString("posicion_camara"));
	    		proyectosgis.add(p);
			}
    		
    		st.close();
        	rs.close();
        	
		}
		
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		return proyectosgis;
	}
	/**
	 * Guarda un proyecto con sus caracteristicas y las capas que le corresponden
	 * @param proyecto Recibido desde el cliente.
	 * @return id del proyecto recien guardado en la base de datos: -1 quiere decir que hubo algun error.
	 */
	public int guardarProyecto(Proyecto proyecto)
	{
		int idProyectoGuardado;
		this.sql = "INSERT INTO proyecto(nombre,fecha) VALUES('"+proyecto.getNombre()+"',CURRENT_DATE) returning id";
		
		try{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			//int filas = st.executeUpdate(sql);
			
			if(rs.next())
			{
				//guardar las capas creadas. luego de guardar el proyecto.
				idProyectoGuardado = rs.getInt(1);
				System.out.println(idProyectoGuardado);
				for(CapaVectorial capa:proyecto.getCapasVectoriales())
				{
					guardarCapa(capa, idProyectoGuardado);
				}
				
				return idProyectoGuardado;
			}
			else{
				return -1;
			}
			
						
		}
		catch(SQLException sqlerr)
		{
			System.out.println(sqlerr.getMessage());
			return -1;
		}
		
		
		
	}
	/***
	 * Guarda el atributo numerico correspondiente a una figura geometrica.
	 * @param idFiguraGeometrica entero.
	 * @return true si se guarda con exito en la base de datos, de acuerdo al nuemero de lineas que cambian.
	 */
	public boolean guardarAtributoNumerico(int idFiguraGeometrica, AtributoNumerico atribNum)
	{
		this.sql = "INSERT INTO atributo_numerico(nombre,valor,fk_figura_geom) " +
				   "VALUES('"+atribNum.getNombre()+"'," +
						  "'"+atribNum.getValor()+"'," +
						  "'"+idFiguraGeometrica+"')";
		
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return true;
		}else{return true;}
	}
	
	public boolean guardarAtributoTexto(int idFiguraGeometrica, AtributoTexto atribTexto)
	{
		this.sql = "INSERT INTO atributo_texto(nombre,valor,fk_figura_geom) " +
				   "VALUES('"+atribTexto.getNombre()+"'," +
						  "'"+atribTexto.getValor()+"'," +
						  "'"+idFiguraGeometrica+"')";
		
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return true;
		}else{return true;}
	}
	
	public boolean guardarAtributoLogico(int idFiguraGeometrica, AtributoLogico atribLogico)
	{
		this.sql = "INSERT INTO atributo_logico(nombre,valor,fk_figura_geom) " +
				   "VALUES('"+atribLogico.getNombre()+"'," +
						  "'"+atribLogico.isValor()+"'," +
						  "'"+idFiguraGeometrica+"')";
		
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return true;
		}else{return true;}
	}
	
	//Metodos de Eliminación de Objetos:
	
	public boolean eliminarProyecto(int idProyecto)
	{
		this.sql = "DELETE FROM proyecto WHERE id="+idProyecto;
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return true;
		}else{return true;}
	}
	
	public boolean eliminarCapaVectorial(int idCapaVectorial)
	{
		this.sql = "DELETE FROM capa_vectorial WHERE id="+idCapaVectorial;
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return true;
		}else{return true;}
	}
	
	public boolean eliminarFiguraGeometrica(int idFiguraGeometrica)
	{
		this.sql = "DELETE FROM figura_geometrica WHERE id="+idFiguraGeometrica;
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		if(n>0){
			return true;
		}else{return true;}
	}
	
	public int eliminarAtributoNumerico(int idAtributo)
	{
		this.sql = "DELETE FROM atributo_numerico WHERE id="+idAtributo;
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	public int eliminarAtributoTexto(int idAtributo)
	{
		this.sql = "DELETE FROM atributo_texto WHERE id="+idAtributo;
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	public int eliminarAtributoLogico(int idAtributo)
	{
		this.sql = "DELETE FROM atributo_logico WHERE id="+idAtributo;
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	//Metodos para modificar objetos de las tablas
	
	public int modificarProyecto(Proyecto proyecto)
	{
		this.sql = "UPDATE proyecto SET nombre ='"+proyecto.getNombre()+"' WHERE id="+proyecto.getId();
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
		
	}
	public int modificarCapaVectorial(CapaVectorial capa)
	{
		this.sql = "UPDATE capa_vectorial SET nombre ='"+capa.getNombre()+"' WHERE id="+capa.getId();
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	
	public int modificarFiguraGeometrica(FiguraGeometrica figura, String tipo)
	{
		int lineasModificadas=0;
		if(tipo.equals("point"))
		{
			this.sql = "UPDATE figura_geometrica " +
					"SET geom=st_geomfromtext('POINT("+figura.getGeometria()+")',900913),nombre='"+figura.getNombre()+"' " +
					"WHERE id="+figura.getId()+"";
			
		}
		else if(tipo.equals("linestring"))
		{
			double largo = calcularLargoLinea(figura.getGeometria());
			this.sql = "UPDATE figura_geometrica " +
					"SET geom=st_geomfromtext('LINESTRING("+figura.getGeometria()+")',900913),nombre='"+figura.getNombre()+"', area_largo="+largo+" " +
					"WHERE id="+figura.getId()+"";	
		}
		else if(tipo.equals("polygon"))
		{
			double area = calcularAreaPoligono(figura.getGeometria());
			this.sql = "UPDATE figura_geometrica " +
					"SET geom=st_geomfromtext('POLYGON(("+figura.getGeometria()+"))',900913),nombre='"+figura.getNombre()+"',area_largo="+area+" " +
					"WHERE id="+figura.getId()+"";	
		}
		
		try{
			Statement st = this.conn.createStatement();
			lineasModificadas = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		return lineasModificadas;
		
	}
	
	public int actualizarValoresAtributosNumericos(AtributoNumerico atribNumerico)
	{
		int resultado=0;
		this.sql = "UPDATE atributo_numerico SET valor = ? WHERE id = ?";
		try
		{
			//Statement st = this.conn.createStatement();
			PreparedStatement ps = this.conn.prepareStatement(this.sql);
			ps.setDouble(1, atribNumerico.getValor());
			ps.setInt(2, atribNumerico.getId());
			int rs = ps.executeUpdate();
			resultado = rs;
			ps.close();
		}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		return resultado;
	}
	public int actualizarValoresAtributosTexto(AtributoTexto atribTexto)
	{
		int resultado=0;
		this.sql = "UPDATE atributo_texto SET valor = ? WHERE id = ?";
		try
		{
			//Statement st = this.conn.createStatement();
			PreparedStatement ps = this.conn.prepareStatement(this.sql);
			ps.setString(1, atribTexto.getValor());
			ps.setInt(2, atribTexto.getId());
			int rs = ps.executeUpdate();
			resultado = rs;
			ps.close();
		}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		return resultado;
	}
	public int actualizarValoresAtributosLogicos(AtributoLogico atribLogico)
	{
		int resultado=0;
		this.sql = "UPDATE atributo_logico SET valor = ? WHERE id = ?";
		try
		{
			//Statement st = this.conn.createStatement();
			PreparedStatement ps = this.conn.prepareStatement(this.sql);
			ps.setBoolean(1, atribLogico.isValor());
			ps.setInt(2,atribLogico.getId());
			int rs = ps.executeUpdate();
			resultado = rs;
			ps.close();
		}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		return resultado;
	}
	public int modificarAtributoNumerico(AtributoNumerico atribNum)
	{
		this.sql = "UPDATE atributo_numerico SET nombre ='"+atribNum.getNombre()+"' WHERE id="+atribNum.getId();
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	public int modificarAtributoTexto(AtributoTexto atribTxt)
	{
		this.sql = "UPDATE atributo_texto SET nombre ='"+atribTxt.getNombre()+"' WHERE id="+atribTxt.getId();
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	public int modificarAtributoLogico(AtributoLogico atriLog)
	{
		this.sql = "UPDATE atributo_logico SET nombre ='"+atriLog.getNombre()+"' WHERE id="+atriLog.getId();
		int n=0;
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n>0){
			return n;
		}else{return -1;}
	}
	
	public int actualizarVisibilidadCapa(CapaVectorial capa){
		int n =0;
		this.sql = "UPDATE capa_vectorial SET visible='"+capa.isVisible()+"' WHERE id="+capa.getId();
		try{
			Statement st = this.conn.createStatement();
			n = st.executeUpdate(this.sql);
			st.close();
		}
		catch(SQLException sqlexception){
			System.out.println(sqlexception.getMessage());
		}
		
		if(n > 0)
			return n;
		else return -1;
	}
	
	public int guardarPosicionActualCamara(Proyecto p){
		int n =0;
		this.sql = "UPDATE proyecto SET posicion_camara=st_geomfromtext('POINT(" +
				p.getPosicionCamara()+")',900913) ," +
						"zoom="+p.getZoom()+" WHERE id="+p.getId();
		
		try{
				Statement st = this.conn.createStatement();
				n = st.executeUpdate(this.sql);
				st.close();
		}
		catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
		}
		if(n > 0)
			return n;
		else return -1;
	}
	
	public double calcularAreaPoligono(String geometria)
	{
		String geometriafromtext= "POLYGON(("+geometria+"))";
		this.sql = "SELECT st_area(st_geomfromtext('"+geometriafromtext +"'))";
		double area=1.8;
		double areaRedondeada=0.0;
		try{
				Statement st = this.conn.createStatement();
				ResultSet rs = st.executeQuery(this.sql);
				while(rs.next())
				{
					area = rs.getDouble(1);//1 porque en la primera (y unica) columna estará el resultado del calculo.
				}
				st.close();
		}
		catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
		}
	    String num = Double.toString(area);
	    BigDecimal bd = new BigDecimal(num);
	    BigDecimal round = bd.setScale(5, RoundingMode.HALF_UP);
	       
	    areaRedondeada = round.doubleValue();
	       
		//NumberFormat formatoNumero = NumberFormat.getInstance();
		//formatoNumero.setMaximumFractionDigits(10);//Numero máximo de decimales.
		//formatoNumero.setRoundingMode(RoundingMode.HALF_UP);//Redondea el último decimal hacia arriba, si es >=0.5.
//		
		return areaRedondeada;
	}
	public double calcularLargoLinea(String geometria){
		double largoRedondeada=0.0;
		String geometriafromtext= "LINESTRING("+geometria+")";
		this.sql = "SELECT st_length(st_geomfromtext('"+geometriafromtext +"'))";
		double largo=1.8;
		
		try{
				Statement st = this.conn.createStatement();
				ResultSet rs = st.executeQuery(this.sql);
				while(rs.next())
				{
					largo = rs.getDouble(1);//1 porque en la primera (y unica) columna estará el resultado del calculo.
				}
				st.close();
		}
		catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
		}
	    String num = Double.toString(largo);
	    BigDecimal bd = new BigDecimal(num);
	    BigDecimal round = bd.setScale(5, RoundingMode.HALF_UP);
	    largoRedondeada = round.doubleValue();
	    return largoRedondeada;
	    //return largo;
	}
	
	public ArrayList<ResultadoEstadistica> calculosEstadicasBasicas(int idCapa)
	{
		ArrayList<ResultadoEstadistica> resultadoTodosAtributosCapa = new ArrayList<ResultadoEstadistica>();
		
		//1º Buscar los atributos de distinto nombre a los que se le calcularan las estadisticas.
		ArrayList<String> nombreAtributos = obtenerNombresAtributosNumericosPorCapa(idCapa);
		//2º Por cada nombre de atributo encontrado de la capa, buscar los valores y calcular las estadísticas
		for(String nombre: nombreAtributos){
			ResultadoEstadistica resultado = new ResultadoEstadistica();
			ArrayList<Double> valoresAtributo = obtenerValoresAtributosNumericos(nombre, idCapa);
			resultado.setNombreAtributo(nombre);
			resultado.setMaximo(Estadistica.obtenerMaximo(valoresAtributo));
			resultado.setMinimo(Estadistica.obtenerMinimo(valoresAtributo));
			resultado.setMediana(Estadistica.obtenerMediana(valoresAtributo));
			resultado.setMedia(Estadistica.obtenerMedia(valoresAtributo));
			resultado.setVarianza(Estadistica.obtenerVarianza(valoresAtributo));
			resultado.setDesviacionEstandar(Estadistica.obtenerDesviacionEstandar(valoresAtributo));
			resultado.setSuma(Estadistica.obtenerSuma(valoresAtributo));
			resultado.setN(valoresAtributo.size());
			resultadoTodosAtributosCapa.add(resultado);
		}
		return resultadoTodosAtributosCapa;
	}
	/**
	 * Busca en la BD, los nombres de los atributos numericos de las figuras que pertencen a la
	 * capa de trabajo actual del cliente.
	 * @param idCapa
	 * @return La lista de los nombres (no repetidos) de los atributos relacionados a una capa.
	 */
	public ArrayList<String> obtenerNombresAtributosNumericosPorCapa(int idCapa)
	{
		ArrayList<String> nombresAtributos = new ArrayList<String>();
		
		this.sql ="SELECT DISTINCT atr.nombre FROM capa_vectorial capa, figura_geometrica figura," +
				"atributo_numerico atr WHERE capa.id = figura.fk_capa_vectorial and figura.id = atr.fk_figura_geom AND capa.id="+idCapa;

		try{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(this.sql);
			while(rs.next())
			{
				nombresAtributos.add(rs.getString(1));//1 porque en la primera columna estará el resultado.
			}
			st.close();
			rs.close();
		}
		catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
		}
	       
		return nombresAtributos;
	}
	public ArrayList<Double> obtenerValoresAtributosNumericos(String nombre, int idCapa)
	{
		this.sql = "SELECT atr.valor FROM capa_vectorial capa, figura_geometrica figura,atributo_numerico atr " +
				"WHERE capa.id = figura.fk_capa_vectorial and figura.id = atr.fk_figura_geom AND capa.id="+idCapa+
				" AND atr.nombre='"+nombre+"'";
		
		ArrayList<Double> valoresAtributos = new ArrayList<Double>();
		try{
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery(this.sql);
			while(rs.next())
			{
				valoresAtributos.add(rs.getDouble(1));//1 porque en la primera columna estará el resultado.
			}
			st.close();
			rs.close();
		}
		catch(SQLException sqlexception){
				System.out.println(sqlexception.getMessage());
		}
		
		return valoresAtributos;
		
	}
	public String obtenerValoresAInterpolar(String nombreAtributo, int idCapa)
	{
		String datosAinterpolar ="";//De la forma (x,y,z\n) donde x=longitud, y= latitud y z=valor del atributo.
		ResultSet rs;
		
		this.sql = "SELECT st_x(fig.geom) ,st_y(fig.geom), " +
				"atr_num.valor FROM atributo_numerico atr_num, figura_geometrica fig " +
				"WHERE atr_num.fk_figura_geom = fig.id " +
				"AND fig.fk_capa_vectorial=? " +
				"AND atr_num.nombre = ?";
				/**
		this.sql = "SELECT st_x(st_transform(fig.geom,32719)) ,st_y(st_transform(fig.geom,32719)), " +
				"atr_num.valor FROM atributo_numerico atr_num, figura_geometrica fig " +
				"WHERE atr_num.fk_figura_geom = fig.id " +
				"AND fig.fk_capa_vectorial=? " +
				"AND atr_num.nombre = ?";**/
		
		try
		{
			PreparedStatement ps = this.conn.prepareStatement(this.sql);
			ps.setInt(1,idCapa);
			ps.setString(2,nombreAtributo);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				double x = rs.getDouble(1);
				double y = rs.getDouble(2);
				double valor = rs.getDouble(3);
				
				datosAinterpolar = datosAinterpolar + (x+","+y+","+valor+" ");
				
			}
			
			rs.close();
			ps.close();

		}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		catch(Exception e){
			System.out.println("Error al tratar de obtener los datos a interpolar "+e.getMessage());
		}
		
		return datosAinterpolar;
		
	}
	
	public boolean verificarPoligono(FiguraGeometrica poligono)
	{
		boolean esValido = false;
		String geometriaPoligono = poligono.getGeometria();
		ResultSet rs;
		this.sql = "SELECT st_isvalid(st_geometryfromtext('POLYGON(("+geometriaPoligono+"))'))";
		try
		{
			PreparedStatement ps = this.conn.prepareStatement(this.sql);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				esValido = rs.getBoolean(1);				
			}
			
			rs.close();
			ps.close();

		}
		catch(SQLException sqlexception)
		{
			System.out.println(sqlexception.getMessage());
		}
		catch(Exception e){
			System.out.println("Error al tratar de verificar el poligono "+e.getMessage());
		}
		return esValido;
	}
	
	
}
