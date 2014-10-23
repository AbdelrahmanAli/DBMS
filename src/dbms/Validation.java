package dbms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Validation implements Validator {
	private Query query;
	private File workSpace = new File(Parser.workSpacePath+"\\DBMS Workspace");

	public Validation(Query query) {
		this.query = query;
	}

	@Override
	public boolean isDBExist() {
		String DBname = query.getDbName();
		File file = new File(workSpace.getAbsolutePath() + "\\" + DBname);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isTableExist() 
	{
		String DBname = query.getDbName();
		String TableName = query.getTableName() + ".xml";
		File file = new File(workSpace.getAbsolutePath() + "\\" + DBname + "\\"+ TableName);
		
		if (file.exists()) return true;
		return false;
	}

	@Override
	public boolean areAttributesExist() {
		String DBname = query.getDbName();
		String TableName = query.getTableName() + ".xml";
		File file = new File(workSpace.getAbsolutePath() + "\\" + DBname + "\\"
				+ TableName);
		// table has been chosen from database
		ArrayList<String> columnsNames = null;
		try {
			columnsNames = parseFileName(file);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// get the names of the query attributes
		ArrayList<String> QueryAttributes = this.query.getAttribute().getColumns();
		if (QueryAttributes.get(0).equals("*")) {
			return true;
		}
		// i didn't know what you did in this case but if you fulfill the
		// columns arraylist , you can delete these three line of code
		for (String n : QueryAttributes) {// looping over all query attributes
			String str = n.trim();
			if (!(columnsNames.contains(str))) {
				return false;
			}
		}

		return true;
	}
	
	public ArrayList<String> parseFileName(File file) throws JDOMException, IOException{
		ArrayList<String> columnsNames = new ArrayList<String>();
		SAXBuilder builder = new SAXBuilder();
		Document doc = (Document) builder.build(file);
		// parsing the file
		// all the columns names of the table
		Element rootNode = doc.getRootElement();
		Element Attributes = rootNode.getChild("attributes");// node here as we have one attributes node
		List AttList = Attributes.getChildren("column");// list here as we have more than one column node
		for (int i = 0; i < AttList.size(); i++) {
			Element node = (Element) AttList.get(i);
			String name = node.getChildText("name").trim();
			columnsNames.add(name);
		}
		return columnsNames;
	}
	
	public ArrayList<String> parseFileType(File file) throws JDOMException, IOException{
		ArrayList<String> columnsTypes = new ArrayList<String>();
		SAXBuilder builder = new SAXBuilder();
		Document doc = (Document) builder.build(file);
		// parsing the file
		Element rootNode = doc.getRootElement();
		Element Attributes = rootNode.getChild("attributes");
		List AttList = Attributes.getChildren("column");
		for (int i = 0; i < AttList.size(); i++) {
			Element node = (Element) AttList.get(i);
			String type = node.getChildText("type").trim();
			columnsTypes.add(type);
		}
		return columnsTypes;
	}

	@Override
	public boolean isAttributesTypeGotValue(){
		String DBname = query.getDbName();
		String TableName = query.getTableName() + ".xml";
		File file = new File(workSpace.getAbsolutePath() + "\\" + DBname + "\\"
				+ TableName);
		// table has been chosen from database
		ArrayList<String> columnsNames = null;
		try {
			columnsNames = parseFileName(file);
		} catch (JDOMException | IOException e) {e.printStackTrace();}
		// all the columns names of the tables
		ArrayList<String> columnsTypes = null;
		try {
			columnsTypes = parseFileType(file);
		} catch (JDOMException | IOException e) {e.printStackTrace();}
		// all the columns types of the table
		ArrayList<String> QueryNames = this.query.getAttribute().getColumns();
		// get the columns names of the query
		ArrayList<String> QueryValues = this.query.getAttribute().getValues();
		// get the columns values of the query
		for (int i = 0; i < QueryNames.size(); i++) {
			String name = QueryNames.get(i).trim();// query column name 
			String value = QueryValues.get(i).trim();// query column value
			for (int j = 0; j < columnsNames.size(); j++) 
			{
				if (name.equalsIgnoreCase(columnsNames.get(j).trim())) 
				{
					String type = columnsTypes.get(j).trim();
					if(!checkValue(value,type)) return false ;
				}
			}
		}
		return true;
	}


	@Override
	public boolean isCondition() {
		String DBname = query.getDbName();
		String TableName = query.getTableName() + ".xml";
		File file = new File(workSpace.getAbsolutePath() + "\\" + DBname + "\\"+ TableName); // table has been chosen from database
		ArrayList<String> columnsNames = null;
		try {
			columnsNames = parseFileName(file);
		} catch (JDOMException | IOException e) {e.printStackTrace();}
		// table columns names
		ArrayList<String> columnsTypes = null;
		try {
			columnsTypes = parseFileType(file);
		} catch (JDOMException | IOException e) {e.printStackTrace();}
		// table columns types
		Conditions cond = this.query.getCondition();//get the condition 
		String CondName = cond.getColumnName();// condition column name 
		if (!(columnsNames.contains(CondName))) { 
			// check the existence of the column name
			return false;
		}
		String Op = cond.getOperator();// get the condition operator
		// check if the operator is supported
		if (!(Op.equals(">")) && !(Op.equals("<")) && !(Op.equals("=")) && !(Op.equals(">="))&& !(Op.equals("<="))) {
			return false;
		}
		String typeOfCol = "";// get the type of the condition column
		for (int i = 0; i < columnsNames.size(); i++) {
			if (CondName.equalsIgnoreCase(columnsNames.get(i))) {
				typeOfCol = columnsTypes.get(i);
			}
		}
		String val = cond.getValue();// get the condition value
		return checkValue(val,typeOfCol);
	}
	
	public boolean checkValue(String value,String type){
		if(value.equals("empty"))
		{
			return true;
		}
		else if (type.equalsIgnoreCase("int")) {
			
			try {
				int val = Integer.parseInt(value);
			} catch (Exception e) {
				return false;
			}
		} else if (type.equalsIgnoreCase("Boolean")) {
			if (!(value.equalsIgnoreCase("true")) && !(value.equalsIgnoreCase("false"))) {
				return false;
			}
		}
		else
		{
			try 
			{
				int val = Integer.parseInt(value);
				return false;
			}
			catch (Exception e) 
			{
			    if ((value.equalsIgnoreCase("true")) || (value.equalsIgnoreCase("false"))) 
			    {
				    return false;
			    }
			    return true;
			}
			
		}
		return true; // string
	}

	@Override
	public boolean isSupportedType(String type) {
		if (type.equalsIgnoreCase("int")) {
			return true;
		}
		if (type.equalsIgnoreCase("string")) {
			return true;
		}
		if (type.equalsIgnoreCase("boolean")) {
			return true;
		}
		return false;
	}

}
