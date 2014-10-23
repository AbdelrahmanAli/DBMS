package dbms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TableOperator implements DBMS
{
	private Query query; // this should contain attributes + values + types + dbName + tableName + condition
	private Validation validator;
	public TableOperator(Query query) 
	{
		this.query = query;
		validator = new Validation(query);
	}
	
	@Override
	public boolean createDB() 
	{
		File directory = new File(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName());
    	// create directory
    	boolean success = directory.mkdirs();
    	if (!success) {	System.out.println("Failed to create the database! May be its already exist.");	}
    	else{			System.out.println("Database "+query.getDbName()+" Created Successfully.");		}
		return success;
	}

	@Override
	public boolean createTable() 
	{
		if(!validator.isTableExist())
		{
			Element table = new Element(query.getTableName());
	
			boolean valid = true;
			
			Element attributes = new Element("attributes");

			for(int i = 0; i < query.getAttribute().getNumberOfColumns(); i++)
			{
				Element column = new Element("column");
				Element name = new Element("name").setText(query.getAttribute().getColumn(i));
                column.addContent(name);
				if(validator.isSupportedType(query.getAttribute().getType(i)))
				{
				    Element type = new Element("type").setText(query.getAttribute().getType(i));
				    column.addContent(type);
				}
				else
				{
				    System.out.println("Can't create the table. Unsupported type.");
				    valid = false;
				}
				if(valid)   attributes.addContent(column);
			   	else return valid;
			}
			table.addContent(attributes);
			
			Element records = new Element("records");
			table.addContent(records);
            
			Document doc = new Document();
			doc.setRootElement(table);
			
			// new XMLOutputter().output(doc, System.out);
			XMLOutputter xmlOutput = new XMLOutputter();
		 
			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			
			//create file to save in
			try 
			{
				System.out.println(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml");
				
				FileOutputStream stream = new FileOutputStream(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml");
			}catch (FileNotFoundException e1) {e1.printStackTrace();}
			try 
			{
				xmlOutput.output(doc, new FileWriter(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml"));
			} catch (IOException e) { e.printStackTrace();}
			System.out.println("Table Created Successfully.");
			
		}
		else
		{ 
		    System.out.println("Can't create the table. It's already exist.");   
		    return false;
		}
		return true;
	}

	@Override
	public boolean select() 
	{   
	    if(validator.isTableExist())
        {
            SAXBuilder builder = new SAXBuilder();
		
            File xmlFile = new File(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml");
		    
		    // converted file to document object
			Document document = null;
			try 
			{
				document = builder.build(xmlFile);
				// get root node from xml  
				Element table = document.getRootElement();
				Element records = table.getChild("records");
        		List<Element> recordList = (List<Element>) records.getChildren(); 
            	Element attributes = table.getChild("attributes"); // when implementing the condition
                List<Element> columns = (List<Element>) attributes.getChildren(); 
        		
        		boolean hasCondition = (query.getCondition()!=null);

        		System.out.println("-------------------");
        		
        		if(query.getAttribute()==null) // astrek
        		{
        		    if(hasCondition && !validator.isCondition())
        		    {
        		        System.out.println("Invalid Query. Please check the condition.");
        		        return false;
        		    }
        		    
        		    if(hasCondition)
        		    {
        		    	String type="";
        		    	for(Element column: columns)
    	   	            {
        		    		if(column.getChildText("name").equalsIgnoreCase(query.getCondition().getColumnName()))
        		    		{
        		    			type = column.getChildText("type");
        		    			break;
        		    		}
    	   	            }
        		    	
        		    	if(type.equalsIgnoreCase("boolean"))
        		    	{
        		    		if(!query.getCondition().getOperator().equals("="))
        		    		{
        		    			System.out.println("Invalid Query. Check the condition's operator.");
        		    			return false;
        		    		}
        		    	}
        		    }
        		    int printer = 0;
    		   		for(Element record: recordList)
    	   	        {   
    			        for(Element column: columns)
    			        {
    			            if (!hasCondition)
    			            {
    			              System.out.println(column.getChildText("name") + ": "+ record.getChildText( column.getChildText("name") ) );
    			              printer++;
    			            }
    			            else 
    			            {
    			                if(applyCondition(record))
    	    			        {
    	    			            printer++;
    	    			            System.out.println(column.getChildText("name") + ": "+ record.getChildText( column.getChildText("name") ) );
    	    			        }
    			            }
    			        }
    			        if(printer!=0) System.out.println("-------------------");
    			        printer = 0;
    	    	     }
        		 
        		}
			    else // without astrek
			    {
			    	if(validator.areAttributesExist())
			    	{
				       if(hasCondition && !validator.isCondition())
	        		    {
	        		        System.out.println("Invalid Query. Please check the condition.");
	        		        return false;
	        		    }
				       
				       if(hasCondition)
		       		    {
		       		    	String type="";
		       		    	for(Element column: columns)
		   	   	            {
		       		    		if(column.getChildText("name").equalsIgnoreCase(query.getCondition().getColumnName()))
		       		    		{
		       		    			type = column.getChildText("type");
		       		    			break;
		       		    		}
		   	   	            }
		       		    	
		       		    	if(type.equalsIgnoreCase("boolean"))
		       		    	{
		       		    		if(!query.getCondition().getOperator().equals("="))
		       		    		{
		       		    			System.out.println("Invalid Query. Check the condition's operator.");
		       		    			return false;
		       		    		}
		       		    	}
		       		    }
	        		    
	        		    int printer = 0;
						for(Element record: recordList)
				        {   
				            for (int i=0 ; i< query.getAttribute().getNumberOfColumns() ; i++)
				            {
				                if (!hasCondition)// no condition
				                {
				                   System.out.println(query.getAttribute().getColumn(i) + ": "+record.getChildText(query.getAttribute().getColumn(i)) );    
				                   printer++;
				                }
				                else
				                {
				                    if(applyCondition( record))// with condition
				                    {
				                        System.out.println(query.getAttribute().getColumn(i) + ": "+record.getChildText(query.getAttribute().getColumn(i)) );
				                        printer++;
				                    }
				                }
				            }
				            
				            if(printer!=0) System.out.println("-------------------");
				            printer = 0;
				        }
				     }
			    	else
			    	{
			    		System.out.println("Invalid Query.Columns doesn't exist.");
			    		return false;
			    	}
			    }
			}
			catch (JDOMException | IOException e) {e.printStackTrace();}
        }
        else
        {
            System.out.println("Invalid Query. Table doesn't exist.");   
		    return false;
        }
		return true;
	}
	
	public boolean applyCondition(Element record)
	{
	    boolean result = false ;
	    String value = record.getChildText(query.getCondition().getColumnName());
	    if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
	    {
	    	if(!query.getCondition().getOperator().equals("=")) return false;
	    }
	    switch(query.getCondition().getOperator())
	    {
	        case ">":
	        			result = value.compareToIgnoreCase(query.getCondition().getValue()) > 0 ;	
	                    break;
	        case "=":
	            	    result = value.compareToIgnoreCase(query.getCondition().getValue())  == 0 ;
	                    break;
	        case "<":
	            	    result = value.compareToIgnoreCase(query.getCondition().getValue()) < 0 ;
	                    break;
	        case "<=":
	            	    result = value.compareToIgnoreCase(query.getCondition().getValue()) <= 0 ;
	                    break;
	        case ">=":
	                    result = value.compareToIgnoreCase(query.getCondition().getValue())  >= 0 ;
	                    break;
	    }
	    return result;
	}

	@Override
	public boolean update() 
	{
		// converted file to document object
		Document document = null;		    
	    if(validator.isTableExist() && validator.areAttributesExist() && validator.isAttributesTypeGotValue())
        {
            SAXBuilder builder = new SAXBuilder();
		    File xmlFile = new File(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml");
			try 
			{
				document = builder.build(xmlFile);
				// get root node from xml  
				Element table = document.getRootElement();
				
				Element records = table.getChild("records");
        		List<Element> recordList = (List<Element>) records.getChildren(); 
        		
            	Element attributes = table.getChild("attributes"); // when implementing the condition
                List<Element> columns = (List<Element>) attributes.getChildren();
                
                
        		
        		boolean hasCondition = (query.getCondition()!=null);

        		if(hasCondition) // there is a condition
        		{
        		    if(validator.isCondition())
        		    {
        		    	String type="";
        		    	for(Element column: columns)
    	   	            {
        		    		if(column.getChildText("name").equalsIgnoreCase(query.getCondition().getColumnName()))
        		    		{
        		    			type = column.getChildText("type");
        		    			break;
        		    		}
    	   	            }
        		    	
        		    	if(type.equalsIgnoreCase("boolean"))
        		    	{
        		    		if(!query.getCondition().getOperator().equals("="))
        		    		{
        		    			System.out.println("Invalid Query. Check the condition's operator.");
        		    			return false;
        		    		}
        		    	}
        		    	
        		        for(Element record: recordList)
    	   	            {
    	   	                if(applyCondition(record) )
                            {
            	   	             for(int i = 0 ; i < query.getAttribute().getNumberOfColumns() ; i++)
            			         {
            			              record.getChild(query.getAttribute().getColumn(i)).setText(query.getAttribute().getValue(i));
            			                
            			         }
                            }
    	   	            }    			              
        		    }
        		    else {
                        System.out.println("Invalid Query. Please check the condition.");
        		        return false;
        		    }
        		}
        		else // no condition
        		{
        		    for(Element record: recordList)
    	   	        {
                         for(int i = 0 ; i < query.getAttribute().getNumberOfColumns() ; i++)
    			         {
    			            record.getChild( query.getAttribute().getColumn(i) ).setText(query.getAttribute().getValue(i));   
                         }
    	   	        } 
        		}
			}
			catch (JDOMException | IOException e) {e.printStackTrace();}
        }
        else
        {
            System.out.println("Invalid Query.");   
		    return false;
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		try 
		{
			xmlOutput.output(document, new FileWriter(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml"));
		} catch (IOException e) {e.printStackTrace();}
		
        System.out.println("Table updated successfully.");   
		return true;
	}

	@Override
	public boolean delete() 
	{  	
		// converted file to document object
		Document document = null;
		int deletedRecords = 0;
	    if(validator.isTableExist())
        {
            SAXBuilder builder = new SAXBuilder();
		    File xmlFile = new File(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml");
			try 
			{
				document = builder.build(xmlFile);
				// get root node from xml  
				Element table = document.getRootElement();
				
				Element records = table.getChild("records");
        		List<Element> recordList = (List<Element>) records.getChildren(); 
        		
            	Element attributes = table.getChild("attributes"); // when implementing the condition
                List<Element> columns = (List<Element>) attributes.getChildren(); 
        		
        		boolean hasCondition = (query.getCondition()!=null);
                
        		if(hasCondition) // there is a condition
        		{
        		    if(validator.isCondition())
        		    {
        		    	
            		    String type="";
            		    for(Element column: columns)
        	   	        {
            		    	if(column.getChildText("name").equalsIgnoreCase(query.getCondition().getColumnName()))
            		    	{
            		   			type = column.getChildText("type");
            		   			
            		   			break;
            		   		}
        	            }
            	    	
            	    	if(type.equalsIgnoreCase("boolean"))
           		    	{
           		    		if(!query.getCondition().getOperator().equals("="))
           		    		{
           		    			System.out.println("Invalid Query. Check the condition's operator.");
           		    			return false;
           		    		}
           		    	}
            		    
            	    	
            	    	List<Element> elements = new ArrayList<Element>();
            	    	Iterator iterator =recordList.iterator(); 
            	    	while(iterator.hasNext())
            	    	{
            	    	   Element subchild = (Element) iterator.next();
            	    	   if (applyCondition(subchild))
            	    	   {
            	    	       deletedRecords++;
            	    	       elements.add(subchild);
            	    	   }

            	    	}
            	    	  
            	    	for (Element element : elements)    element.getParent().removeContent(element);
            	    	recordList =  elements;
            	    	
        		    }
        		    else 
        		    {
                        System.out.println("Invalid Query. Please check the condition.");
        		        return false;
        		    }
        		}
        		else // no condition
        		{
        		    records.removeContent();
        		    deletedRecords++;
        		}
			}
			catch (JDOMException | IOException e) {e.printStackTrace();}
        }
        else
        {
            System.out.println("Invalid Query. Table doesn't exist.");   
		    return false;
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		try 
		{
			xmlOutput.output(document, new FileWriter(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml"));
		} catch (IOException e) {e.printStackTrace();}
		
        if(deletedRecords!=0) System.out.println("Record deleted successfully.");   
        else System.out.println("No records found.");
		return true;
		
	}
	
	public void addAllColumns(List<Element> columns)
	{
        for(Element column: columns)
    		{
        		boolean exist = false;
        		for(int i = 0 ; i < query.getAttribute().getNumberOfColumns() ; i++)
        		{
        			if(column.getChildText("name").equalsIgnoreCase(query.getAttribute().getColumn(i)))
        			{
        				exist = true;
        			}
        		}
        		if(!exist) query.getAttribute().addCol(column.getChildText("name"));
    		}

        for(int i = query.getAttribute().getNumberOfValues() ; i < query.getAttribute().getNumberOfColumns()  ; i++)
        {
        	query.getAttribute().addValue("empty");
        }
	}

	@Override
	public boolean insertInto() 
	{
	    // converted file to document object
		Document document = null;    
	    if(validator.isTableExist())
        {
	    	
            SAXBuilder builder = new SAXBuilder();
		    File xmlFile = new File(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml");

			try 
			{
				document = builder.build(xmlFile);
				// get root node from xml  
				Element table = document.getRootElement();
				
				Element records = table.getChild("records");
        		List<Element> recordList = (List<Element>) records.getChildren(); 
        		
            	Element attributes = table.getChild("attributes"); // when implementing the condition
                List<Element> columns = (List<Element>) attributes.getChildren(); 
    			
        		if(query.getAttribute().getNumberOfColumns() != columns.size()) // no columns
                {
        			addAllColumns(columns);
                }


		        if(validator.isAttributesTypeGotValue())
		        {
    				Element record  = new Element("record");
            	
                	for(int i = 0 ; i < query.getAttribute().getNumberOfValues() ; i++)
                	{
                	    Element column = new Element(query.getAttribute().getColumn(i)).setText(query.getAttribute().getValue(i));
                	    record.addContent(column);
                	}
            		records.addContent(record);
		        }
		        else
		        {
		            System.out.println("Invalid Values.");   
		            return false;
		        }
			}
			catch (JDOMException | IOException e) {e.printStackTrace();}
        }
        else
        {
            System.out.println("Invalid Query. Table doesn't exist.");   
		    return false;
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		try 
		{
			xmlOutput.output(document, new FileWriter(Parser.workSpacePath+"DBMS Workspace\\"+query.getDbName()+"\\"+query.getTableName()+".xml"));
		} catch (IOException e) {e.printStackTrace();}
		
		
        System.out.println("Record inserted successfully.");   
		return true;
	}



}


