package zlink.tools.db;

public class DBException extends Exception{

	private static final long serialVersionUID = 1L;

	public DBException( String message ) {super( message );}

	public DBException(Exception e) {super(e);}
	
	public static void forward(Exception e) throws DBException {throw new DBException(e);}

}
