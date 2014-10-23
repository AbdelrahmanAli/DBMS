package dbms;

public interface DBMS 
{
	public boolean createDB(); //you will use from query dbName
	public boolean createTable(); //you will use from query tableName, attribute names and types
	public boolean select(); //you will use from query tableName, attribute names, condition
	public boolean update(); //you will use from query tableName, attribute names and values, condition
	public boolean delete(); //you will use from query tableName, condition
	public boolean insertInto(); // you will use from query tableName, attribute names and values
}
