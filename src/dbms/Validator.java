package dbms;


public interface Validator {
	public boolean isDBExist(); // use db_name from query
	public boolean isTableExist(); // use tableName from query
	public boolean areAttributesExist();// use tableName and attribute from query
	public boolean isAttributesTypeGotValue(); // use tableName and attribute from query
	public boolean isCondition();// use tableName and condition from query
	public boolean isSupportedType(String type);// check the type of the attributes
}
