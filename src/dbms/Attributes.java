package dbms;

import java.util.ArrayList;

public class Attributes 
{
/////////////////////////////////// Instances
	private ArrayList<String> columns;
	private ArrayList<String> types;
	private ArrayList<String> values;
/////////////////////////////////// constructor
	public Attributes() 
	{
		columns = new ArrayList<String>();
		types= new ArrayList<String>();
		values= new ArrayList<String>();
	}
/////////////////////////////////// adding values
	public void addColumnWithType(String col,String type) 
	{
		columns.add(col);
		types.add(type);
	}
	public void addColumnAndValue(String col,String value) 
	{
		columns.add(col);
		values.add(value);
	}
	
	public void addCol(String col) 
	{
		columns.add(col);
	}
	public void addColWithTyptAndValue(String col,String type,String value){
		columns.add(col);
		types.add(type);
		values.add(value);
	}
	public void addValue(String value) { 
		values.add(value);
	}
/////////////////////////////////// get by index
	public String getType(int i)
	{
		return types.get(i);
	}
	public String getColumn(int i)
	{
		return columns.get(i);
	}
	public String getValue(int i)
	{
		return values.get(i);
	}
/////////////////////////////////// get size
	public int getNumberOfColumns() 
	{
		return columns.size();
	}
	public int getNumberOfTypes() 
	{
		return types.size();
	}

	public int getNumberOfValues() 
	{
		return values.size();
	}
////////////////Getters
public ArrayList<String> getTypes() 
{
return types;
}
public ArrayList<String> getValues() 
{
return values;
}
public ArrayList<String> getColumns() 
{
return columns;
}

	
	
	

}
