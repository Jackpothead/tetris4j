package tetris.core;

import java.io.*;

/**Contains a group of project constants.*/
public class ProjectConstants
{
	/**True if project is still being debugged,
	 * false otherwise.*/
	public static final boolean DEBUG = true;
	
	public static void pImportant(Object o)
	{
		System.out.printf("\n\t%s\n\n",o);
	}
	
	public static InputStream getResStream(String path)
	throws IOException
	{
		try{
			File f = new File(".."+path);
			return new BufferedInputStream(new FileInputStream(f.getCanonicalFile()));
		}catch(Exception e)
		{
			return ProjectConstants.class.getResourceAsStream(path);
		}
		
	}
}
