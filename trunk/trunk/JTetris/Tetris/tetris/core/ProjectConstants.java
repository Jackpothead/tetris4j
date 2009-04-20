package tetris.core;

import java.io.*;
import java.net.URL;

/**This class contains a group of project constants.<br>
 * Use <pre>import static tetris.code.ProjectConstants</pre>.*/
public class ProjectConstants
{
	/**True if project is still being debugged,
	 * false otherwise.*/
	public static final boolean DEBUG = false;
	
	/**Should the application start fullscreened?*/
	public static final boolean STARTFS = false;
	
	
	/**Prints something to the console, with a tab
	 * <br>and leading/trailing newlines.*/
	public static void pImportant(Object o)
	{
		System.out.printf("\n\t%s\n\n",o);
	}
	
	
	/**Returns a resource as an InputStream. First it
	 * <br>tries to create a FileInputStream from the parent
	 * <br>directory (if contents are unzipped) and then tries
	 * <br>to use getResourceAsStream if that fails.*/
	public static InputStream getResStream(String path)
	throws IOException
	{
		try{
			//This is actually helpful for those downloading it
			//with something other than Eclipse (Tortoise for example).
			//However this screws up with Eclipse.
			File f = new File("."+path);
			return new BufferedInputStream(
					new FileInputStream(f.getCanonicalFile()));
		}catch(Exception e)
		{
			//eclipse workaround.
			return ProjectConstants.class.getResourceAsStream(path);
		}
		
	}
	
	/**Returns a resource as a URL object, for certain file
	 * <br>parsing. Should accomodate Eclipse and other clients/IDEs
	 * <br>as well. Currently it loads resources from Eclipse, the
	 * <br>jar file, and from Tortoise.*/
	public static URL getResURL(String path)
	throws IOException
	{
		try{
			File f = new File("."+path);
			if(!f.exists())throw new Exception();
			return f.getCanonicalFile().toURI().toURL();
		}catch(Exception e)
		{
			//eclipse workaround.
			return ProjectConstants.class.getResource(path);
		}
		
	}
}
