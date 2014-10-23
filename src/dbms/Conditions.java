package dbms;

public class Conditions 
{
////////////////////////////////////////////// Instances
	private String columnName;
	private String operator;
	private String value;
////////////////////////////////////////////// Nully Constructor	
	public Conditions() {}
////////////////////////////////////////////// Getters
	public String getColumnName() 
	{
		return columnName;
	}
	public String getOperator() 
	{
		return operator;
	}
	public String getValue() 
	{
		return value;
	}
////////////////////////////////////////////// Setters
	public void setValue(String value) 
	{
		this.value = value;
	}
	public void setoperator(String operator) 
	{
		this.operator = operator;
	}
	public void setColumnName(String columnName) 
	{
		this.columnName = columnName;
	}
	

}
