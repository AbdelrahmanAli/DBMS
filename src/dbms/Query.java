package dbms;

public class Query 
{
///////////////////////////////////////// Instances
	private String tableName, dbName;
	private Attributes attribute;
	private Conditions condition;
///////////////////////////////////////// Constructor
	public Query() 
	{
		attribute = new Attributes();
		condition = new Conditions();
	}
///////////////////////////////////////// Getters
	public String getTableName()
	{
		return tableName;
	}
	public String getDbName() // used when creating a database 
	{
		return dbName;
	}
	public Attributes getAttribute() 
	{
		return attribute;
	}
	public Conditions getCondition() 
	{
		return condition;
	}
//////////////////////////////////////////// Setters
	public void setDbName(String dbName) 
	{
		this.dbName = dbName;
	}
	public void setTableName(String tableName) 
	{
		this.tableName = tableName;
	}
	public void setCondition(Conditions condition)
	{
		this.condition = condition;
	}
	public void setAttribute(Attributes attribute)
	{
		this.attribute = attribute;
	}
	
	

}
