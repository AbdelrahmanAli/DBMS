package dbms;

import java.io.File;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

public class EntryPoint 
{
	public static void main(String[] args) 
	{
//		try 
//		{
//			System.out.println(EntryPoint.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
//		}
//		catch (URISyntaxException e) {e.printStackTrace();}
		
		// get desktop path
		FileSystemView filesys = FileSystemView.getFileSystemView();
		String desktopPath = filesys.getHomeDirectory()+"";
		
		//check work space
		File workSpace = new File(filesys.getHomeDirectory()+"\\DBMS Workspace");
		boolean success = workSpace.mkdirs();
    	if (success) {	System.out.println("Workspace Created.");	}
    	else{System.out.println("Workspace successfully found.");	}
		
		
	    String query;
	    Parser parser = new Parser(desktopPath + "\\");
		boolean databaseIsSet = false;
		Scanner in = new Scanner(System.in);
	    while(true)
	    {
	        System.out.println("Enter a query: ");
	        query = in.nextLine();
	        if(query.startsWith("USE ") || !databaseIsSet)
	        {
	        	if(query.equalsIgnoreCase("Exit")) break;
	        	
                parser.excuteQuery(query);
                databaseIsSet = parser.isDBSet();
	        }
	        else
	        {
			    if(query.equalsIgnoreCase("Exit")) break;
			    parser.excuteQuery(query);
	        }
	    }
	}
}