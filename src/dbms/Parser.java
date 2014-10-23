package dbms;

import java.util.ArrayList;

public class Parser {
	private String inputQuery;
	private TableOperator tableOperator;
	private Validation validator;
	private Query query;
	public static String workSpacePath;
	
	private int charIndex;
	
	private String temp;
	private boolean b;
	private ArrayList<String> types;
	private ArrayList<String> names;
		
	
	public Parser(String workSpacePath) {
		  this.workSpacePath = workSpacePath;
		  query = new Query();
	}

	public boolean isDBSet() { return b; }	
	
	public void excuteQuery(String inputQuery) { 
		
		inputQuery = inputQuery.trim();
		inputQuery = inputQuery.replaceAll("\\s+", " ");
		this.inputQuery = inputQuery;
		names = new ArrayList<String>();
		types = new ArrayList<String>();
		
		query.setCondition(new Conditions());
		query.setAttribute(new Attributes());
		try{
			if(!inputQuery.contains(";") || inputQuery.charAt(inputQuery.length()-1)!=';' ||inputQuery.indexOf(';')!=inputQuery.length()-1)
				System.out.println("Not a Valid Query Statement!"); 
			else
				validateAndDetect();
		}catch(Exception e){
			System.out.println("ERROR!");
		} 
	}

	private boolean validateAndDetect() {
		if(inputQuery.startsWith("USE "))
			dealwithUseDB();
		else if(inputQuery.startsWith("CREATE DATABASE "))
			dealWithCreateDB(); 
		else if(inputQuery.startsWith("CREATE TABLE "))
			dealWithCreateTable();
		else if(inputQuery.startsWith("INSERT INTO "))
			dealWithInsertInto();
		else if(inputQuery.startsWith("DELETE FROM "))
			dealWithDelete();
		else if(inputQuery.startsWith("SELECT "))
			dealWithSelect();
		else if(inputQuery.startsWith("UPDATE "))
			dealWithUpdate();
		else System.out.println("Not a Valid Query Statement!");
		return true;
	}


	private boolean checkSpaces(int begin, int end) {
		for(int i=begin+1;i<end-1;i++){
			if(inputQuery.charAt(i)==' ')
				return false;
		}
		return true;
	}
	
	private boolean checkChars() {
		if(inputQuery.contains("=") || inputQuery.contains(">") || inputQuery.contains("<"))
			return false;
		return true;
	}

	private void checkCols() {
		for(int i=0;i<query.getAttribute().getNumberOfColumns()-1;i++){
			for(int j=1+i;j<query.getAttribute().getNumberOfColumns();j++){
				if(query.getAttribute().getColumn(i).equals(query.getAttribute().getColumn(j))){
					b = false;
					break;
				}
			}
		}
	}
	
	private void getSavedTableName(int begin) {
		temp = "";
		while(begin < inputQuery.length()-1 && inputQuery.charAt(begin)!=' '
				&&	inputQuery.charAt(begin)!=';'){
			temp+=inputQuery.charAt(begin);
			++begin;
		}
		query.setTableName(temp.trim());
		charIndex=begin + 1;		
	}

	private void dealwithUseDB() {
		//	temp = "USE ";
		//	b = true;
		//	for(int i=0;i<temp.length();i++){
		//		if(inputQuery.charAt(i)!=temp.charAt(i))
		//			b = false;
		//	}
			if(checkSpaces(4,inputQuery.length()) && inputQuery.length()-1>4){
				query.setDbName(inputQuery.substring(4, inputQuery.length()-1));
				// method maatrix ely hanadeeha
				validator = new Validation(query);
				b = validator.isDBExist();
			}
			else
				System.out.println("Not a Valid Query Statement!");
	}
	
	private void dealWithCreateDB() {
		//	temp = "CREATE DATABASE ";
		//	b = true;
		//	for(int i=0;i<temp.length();i++){
		//		if(inputQuery.charAt(i)!=temp.charAt(i))
		//			b = false;
		//	}
			if(checkSpaces(16,inputQuery.length()) && inputQuery.length()-1>16){
				query.setDbName(inputQuery.substring(16, inputQuery.length()-1));
				// method maatrix ely hanadeeha
				tableOperator = new TableOperator(query);
				tableOperator.createDB();
			}
			else
				System.out.println("Not a Valid Query Statement!");
	}
	
	private void dealWithCreateTable() {
		//	temp = "CREATE TABLE ";
		//	b = true;
		//	for(int i=0;i<temp.length();i++){
		//		if(inputQuery.charAt(i)!=temp.charAt(i))
		//			b = false;
		//	}
		//	if(b)
				getTableName();
		//	else
		//		System.out.println("Not a Valid Query Statement!");
		}

	private void getTableName() {
		String s="";
			//int i = temp.length();
		int i = 13;
		while(i < inputQuery.length() && inputQuery.charAt(i)!='('){
			s+=inputQuery.charAt(i);
			++i;
		}
		//	s=s.trim();
		//	System.out.println(s);
		if(inputQuery.charAt(13+s.length())=='(' 
				&& (inputQuery.charAt(inputQuery.length()-2)==')' ||
				(inputQuery.charAt(inputQuery.length()-3)==')'
				&& inputQuery.charAt(inputQuery.length()-2)==' ') )
				&& checkSpaces(13,13+s.length())){
				query.setTableName(s.trim());
				checkAttributesFormat(13+s.length()+1);
		}
		else
			System.out.println("Not a Valid Query Statement!");
	}

	private void checkAttributesFormat(int begin) {
		int counter1 = 0,counter2 = 0;
		String s="";
		//	System.out.println(inputQuery.charAt(begin));
		//	if(inputQuery.charAt(begin)=='(')
		//		++begin;
		for(int i=begin;i<inputQuery.length()-2;i++){
			if(inputQuery.charAt(i)!=' ' && inputQuery.charAt(i)!=','){
				s+=inputQuery.charAt(i);
			}else if(inputQuery.charAt(i)==' ' && !(inputQuery.charAt(i-1)==',' 
					|| inputQuery.charAt(i+1)==',' || i==begin || i==inputQuery.length()-3)){
				++counter1;
				names.add(s);
				s="";
			}else if(inputQuery.charAt(i)==','){
				++counter2;
				types.add(s);
				s="";
			}
		}
		types.add(s);
		if(counter1==counter2+1)
			dealWithTableAttributes();
		else
			System.out.println("Not a Valid Query Statement!");	
	}

	private void dealWithTableAttributes() {
		b = true;
		for(int i=0;i<types.size();i++){
			if(!(types.get(i).equals("String") || types.get(i).equals("int") || types.get(i).equals("float")
					|| types.get(i).equals("boolean") || types.get(i).equals("double")))
				b = false;
		}
		if(b){
			for(int i=0;i<types.size();i++)	
				query.getAttribute().addColumnWithType(names.get(i), types.get(i));
			checkCols();
			if(b){
				tableOperator = new TableOperator(query);
				tableOperator.createTable();
			}
			else
				System.out.println("Not a Valid Query Statement!");
		}
		else
			System.out.println("Not a Valid Query Statement!");
	}
	
	private void dealWithInsertInto() {
		//	temp = "INSERT INTO ";
		//	b = true;
		//	for(int i=0;i<temp.length();i++){
		//		if(inputQuery.charAt(i)!=temp.charAt(i))
		//			b = false;
		//	}
		//	if(b){
		charIndex = 12;
		getSavedTableName(charIndex);
		if(charIndex < inputQuery.length() && inputQuery.charAt(charIndex)=='V'){
			//query.getAttribute().addCol(null);
			checkWordValue();
		}
		else if(charIndex < inputQuery.length() && inputQuery.charAt(charIndex)=='(')
			addCol();
		else
			System.out.println("Not a Valid Query Statement!");
	}

		

	private void addCol() {
		String s="";
		b = true;
		++charIndex;
		while(charIndex < inputQuery.length() && inputQuery.charAt(charIndex)!=')'){
			if(inputQuery.charAt(charIndex)==','){
				if(checkSpaces(charIndex-s.length(),charIndex))
					query.getAttribute().addCol(s.trim());
				else
					b=false;
				s="";
		}
		else
			s+=inputQuery.charAt(charIndex);
			++charIndex;
		}
		if(s!="")
			query.getAttribute().addCol(s.trim());
		names.add("-1");
		checkCols();
		if(charIndex < inputQuery.length() && b && inputQuery.charAt(++charIndex)==' '){
			++charIndex;
			checkWordValue();
		}
		else
			System.out.println("Not a Valid Query Statement!");	
	}
	
	private void checkWordValue() {
		temp = "VALUES (";
		b = true;
		for(int i=0;i<temp.length() && charIndex < inputQuery.length();i++){
			if(inputQuery.charAt(charIndex)!=temp.charAt(i))
				b = false;
			++charIndex;
		}
		if(charIndex < inputQuery.length() && b && (inputQuery.charAt(inputQuery.length()-2)==')' ||
				(inputQuery.charAt(inputQuery.length()-3)==')') && inputQuery.charAt(inputQuery.length()-2)==' '))
			addValues();
		else
			System.out.println("Not a Valid Query Statement!");
	}

	private void addValues() {
		String s="";
		b = true;
		while(charIndex < inputQuery.length()-1 && inputQuery.charAt(charIndex)!=')'){
			if(inputQuery.charAt(charIndex)==','){
				if(checkSpaces(charIndex-s.length(),charIndex))
					query.getAttribute().addValue(s.trim());
				else
					b=false;
				s="";
			}
			else
				s+=inputQuery.charAt(charIndex);
			++charIndex;
		}
		if(s!="")
			query.getAttribute().addValue(s.trim());
		if(names.size() == 1 && query.getAttribute().getNumberOfColumns()!=query.getAttribute().getNumberOfValues())
			b = false;
		if(b){
		//method 3abdou
			tableOperator = new TableOperator(query);
			tableOperator.insertInto();
		}
		else
			System.out.println("Not a Valid Query Statement!");
	}
	
	private void dealWithDelete() {
		charIndex = 12;
		getSavedTableName(charIndex);
		if(query.getTableName().contains("=") || query.getTableName().contains(">")
			|| query.getTableName().contains("<"))
			System.out.println("Not a Valid Query Statement!");
		else if(inputQuery.contains("WHERE"))
			checkWordWhere();
		else if(charIndex >=inputQuery.length() || inputQuery.charAt(charIndex)==';'){
			query.setCondition(null);
			tableOperator = new TableOperator(query);
			tableOperator.delete();
		}
	}	

	private void checkWordWhere() {
		temp = "WHERE ";
		b = true;
		for(int i=0;i<temp.length() && charIndex < inputQuery.length();i++){
			if(inputQuery.charAt(charIndex)!=temp.charAt(i))
				b = false;
			++charIndex;
		}
		if(charIndex < inputQuery.length() && b)
			addCol2();
		else
			System.out.println("Not a Valid Query Statement!");
	}

	private void addCol2() {
		String s="";
		b = true;
		while(charIndex < inputQuery.length()-1){
			if(inputQuery.charAt(charIndex)=='=' || inputQuery.charAt(charIndex)=='>'
					|| inputQuery.charAt(charIndex)=='<'){
				query.getCondition().setColumnName(s.trim());
				break;
			}
			else
				s+=inputQuery.charAt(charIndex);
			++charIndex;
		}
		if(s.contains("=") || s.contains(">") || s.contains("<"))
			b =false;
		s=""+inputQuery.charAt(charIndex);
	//	if(inputQuery.lastIndexOf('=')==charIndex || inputQuery.lastIndexOf('<')==charIndex
	//			|| inputQuery.lastIndexOf('>')==charIndex)
			query.getCondition().setoperator(s);
	//	else
	//		b = false;
		if(b && (inputQuery.charAt(charIndex)=='=' || inputQuery.charAt(charIndex)=='>'
				|| inputQuery.charAt(charIndex)=='<') && checkSpaces(charIndex-s.length(),charIndex)){
			//method 3abdou
			addValue2();
		}
		else
			System.out.println("Not a Valid Query Statement!");
	}

	private void addValue2() {
		String s="";
		b = true;
		++charIndex;
		while(charIndex < inputQuery.length()-1){
			s+=inputQuery.charAt(charIndex);
			++charIndex;
		}
		if(checkSpaces(charIndex-s.length(),charIndex) && s!="" && !s.contains("=")
				&& !s.contains(">") && !s.contains("<")){
			query.getCondition().setValue(s.trim());
			//method3abodu
			tableOperator = new TableOperator(query);
			tableOperator.delete();
		}
		else
			System.out.println("Not a Valid Query Statement!");
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private boolean checkElement(String element){
		element = element.trim();
		if(element.startsWith("'")){
			if(element.endsWith("'"))return true; 
			else return false;
		}else{
			if(element.contains(" ")) return false;
		}
		return true;
	}
	
	private String getElement(String element){
		element = element.trim();
		if(element.startsWith("'")){
			if(element.endsWith("'")) element = element.substring(1,element.length()-1); 
		}
		return element;
	}
	
	
	private boolean setColumns(int from, int to){
		boolean oneAtr = false, repeated = false;
		String [] columns = inputQuery.substring(from,to).split(",");
		if(columns[0].trim().equals("*") && columns[0].trim().length()==1 && columns.length==1) {query.setAttribute(null); return true;}
		for(int i=0; i<columns.length; i++){
			if(columns[i].trim().contains("*")) return false;
			if(checkElement(columns[i])){
				for(int j = i-1; j>=0 && !repeated; j--) if(getElement(columns[i]).equals(getElement(columns[j]))) repeated = true;
				if(!repeated)query.getAttribute().addCol(getElement(columns[i])); 	
			}else return false;
			oneAtr = true;
			repeated = false;
		}
		return oneAtr;
	}
	
	private int checkSet(String set){
		//0>>invalid //1>>int //2>>double //3>>boolean //4>>string
		set = set.trim();
		if(set.indexOf("=")>0 && set.indexOf("=")<set.length()-1 && !set.contains(">") && !set.contains("<") && set.indexOf("=",set.indexOf("=")+1)<0){
			String value = set.substring(set.indexOf("=")+1, set.length());
			value = value.trim();
			if(value.startsWith("'") && value.endsWith("'")) return 4;
			else if(!value.contains("'") && !value.contains(" ")){
				if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) return 3;
				for(int i=0; i<value.length(); i++){
					if( (value.charAt(i)<48 || value.charAt(i)>57) && value.charAt(i)!=46 ) return 0;
				}
				if(!value.contains(".")) return 1;
				else {
					if(value.split(".").length>2) return 0;
				}	return 2;
			}else return 0;			
		}else return 0;
	}
		
	private boolean setSets(int from, int to){
		String column = "", type = "", value= "";
		boolean oneAtr = false;
		String [] sets = inputQuery.substring(from,to).split(",");
		for(int i=0; i<sets.length; i++){
			sets[i] = sets[i].trim();
			if(checkSet(sets[i])>0) column = sets[i].substring(0, sets[i].indexOf("=")).trim();
			if(checkSet(sets[i])==1){ /*INT*/ type = "int"; value = sets[i].substring(sets[i].indexOf("=")+1,sets[i].length()).trim();}
			else if(checkSet(sets[i])==2){ /*DOUBLE */ type = "double"; value = sets[i].substring(sets[i].indexOf("=")+1,sets[i].length()).trim();}
			else if(checkSet(sets[i])==3){ /*BOOLEAN*/ type = "boolean"; value = sets[i].substring(sets[i].indexOf("=")+1,sets[i].length()).trim();}
			else if(checkSet(sets[i])==4){ /*STRING */ type = "string";
				value = sets[i].substring(sets[i].indexOf("="),sets[i].length()).trim();
				value = value.substring(value.indexOf("'")+1, value.indexOf("'", value.indexOf("'")+1));
			}else return false;
			if(!column.equals("") && !type.equals("") && !value.equals("")) { query.getAttribute().addColWithTyptAndValue(column, type, value); oneAtr = true; }
		}
		return oneAtr;
	}
	
	private boolean setTableName(int from, int to){
		if(checkElement(inputQuery.substring(from,to))) query.setTableName(getElement(inputQuery.substring(from,to)));
		else return false;
		return true;
	}
	
	private int checkCondition(String condition){
		condition = condition.trim(); int x=0;
		if(condition.indexOf("=")>0 || condition.indexOf(">")>0 || condition.indexOf("<")>0 && (condition.indexOf("=")<condition.length()-1 || condition.indexOf(">")<condition.length()-1 || condition.indexOf("<")<condition.length()-1)){
			if(condition.contains("=") &&  condition.indexOf("=",condition.indexOf("=")+1)<0 ){
				if(condition.contains(">=") &&  condition.indexOf(">")==condition.indexOf(">=") && condition.indexOf(">", condition.indexOf(">=")+1)<0  && !condition.contains("<")) return 5;
				else if(condition.contains("<=") &&  condition.indexOf("<")==condition.indexOf("<=") && condition.indexOf("<", condition.indexOf("<=")+1)<0  && !condition.contains(">")) return 4;
				else if(condition.indexOf("=", condition.indexOf("=")+1)<0  && !condition.contains("<") && !condition.contains(">")) return 3;
				else return 0;
			}else if(condition.contains("<>") &&  condition.indexOf("<")==condition.indexOf("<>") &&  condition.indexOf(">")==(condition.indexOf("<>")+1) && condition.indexOf(">", condition.indexOf("<>")+2)<0 && condition.indexOf("<", condition.indexOf("<>")+1)<0) return 6;
			else if (condition.contains(">") && !condition.contains("<") && condition.indexOf(">", condition.indexOf(">")+1)<0) return 2;
			else if (condition.contains("<") && !condition.contains(">") && condition.indexOf("<", condition.indexOf("<")+1)<0) return 1;
			else return 0;
		}else return 0;
	}
	
	private boolean setCondition(int from, int to){
		String columnName = "",operator = "", value = ""; boolean flag =false;
		String condition= inputQuery.substring(from,to).trim();
		if(!inputQuery.contains("WHERE")){ query.setCondition(null); return true; }
		else if(!inputQuery.contains(" WHERE "))return false;
		else{
			if(checkCondition(condition)>0){
				for(int i=0; i<condition.length() && condition.charAt(i)!='<' && condition.charAt(i)!='>' && condition.charAt(i)!='='; i++) columnName += condition.charAt(i);
				if(checkElement(columnName)) columnName = getElement(columnName).trim();
				else return false;
			}
			if(checkCondition(condition)>2 && checkCondition(condition)<6){
				value = condition.substring(condition.indexOf("=")+1, condition.length()).trim();
				if(checkElement(value)) value=getElement(value); else return false;
				if(checkCondition(condition)==3) operator = "=";
				else if (checkCondition(condition)==4) operator = "<=";
				else if (checkCondition(condition)==5) operator = ">=";
			}else if(checkCondition(condition)==2 || checkCondition(condition)==6){
				value = condition.substring(condition.indexOf(">")+1, condition.length()).trim();
				if(checkElement(value)) value=getElement(value); else return false;
				if(checkCondition(condition)==2) operator = ">";
				else if (checkCondition(condition)==6) operator = "<>";
			}else if(checkCondition(condition)==1) {
				value = condition.substring(condition.indexOf("<")+1, condition.length()).trim();
				if(checkElement(value)) value=getElement(value); else return false;
				operator = "<";
			}else return false;
		}
		if(columnName=="" || value==null || operator=="" || columnName==" " || value==" " || operator==" ") return false;
		else {query.getCondition().setColumnName(columnName); query.getCondition().setoperator(operator); query.getCondition().setValue(value);}
		return true;
	}
///////////////////////////////////////////////////////////////////////////////////////////////
//SELECTION     ///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////	
	private boolean checkSelect(){
		if(inputQuery.contains(" FROM ")){
			if(inputQuery.contains(" WHERE ")){
				if(inputQuery.indexOf(" FROM ")>inputQuery.indexOf(" WHERE ")) return false;
			}
		}else return false;
		return true;
	}
	
	private void dealWithSelect() {
		
		int endTableName = inputQuery.length()-1;
		if(inputQuery.contains(" WHERE ")) endTableName = inputQuery.indexOf(" WHERE ");
		
		if(!checkSelect())System.out.println("Not a Valid Query Statement!"); 
		else if(!setColumns(7, inputQuery.indexOf(" FROM "))) 	System.out.println("Not a Valid Query Statement!"); 
		else if(!setTableName(inputQuery.indexOf("FROM")+4, endTableName)) System.out.println("Not a Valid Query Statement!"); 
		else if(!setCondition(inputQuery.indexOf("WHERE")+5, inputQuery.length()-1)) System.out.println("Not a Valid Query Statement!"); 
		else {
			TableOperator tableOperator = new TableOperator(query);
			tableOperator.select();
		}	
		charIndex = 0;
	}
///////////////////////////////////////////////////////////////////////////////////////////////
// UPDATE   ///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

	private boolean checkUpdate(){
		if(inputQuery.contains(" SET ")){
			if(inputQuery.contains(" WHERE ")){
				if(inputQuery.indexOf(" SET ")>inputQuery.indexOf(" WHERE ")) return false;
			}
		}else return false;
		return true;
	}
		
	private void dealWithUpdate(){
		int endSet = inputQuery.length()-1;
		if(inputQuery.contains(" WHERE ")) endSet = inputQuery.indexOf(" WHERE ");
		
		if(!checkUpdate())System.out.println("Not a Valid Query Statement!"); 
		else if(!setTableName(7, inputQuery.indexOf(" SET "))) System.out.println("Not a Valid Query Statement!"); 
		else if(!setSets( inputQuery.indexOf("SET")+3, endSet))System.out.println("Not a Valid Query Statement!"); 
		else if(!setCondition(inputQuery.indexOf("WHERE")+5, inputQuery.length()-1)) System.out.println("Not a Valid Query Statement!"); 
		else {
			TableOperator tableOperator = new TableOperator(query);
			tableOperator.update();
		}	
		charIndex = 0;
	}
	
}
