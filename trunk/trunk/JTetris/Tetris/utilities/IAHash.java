package utilities;

import java.util.Arrays;

/**IAHash - Integer Array Hash.*/
public class IAHash
{
	public static String createSpecialHash(String str0)
	{
		if(str0==null)
			return "-";
		
		int length = str0.length();
		char[][] k = new char[length][length];
		
		for(int i = 0;i < length;i++)
		{
			str0 = new String(k[i] = SHIFT(str0).toCharArray());
		}
		
		return /*****/
		   	Integer.toHexString(Arrays.hashCode(k) +str0.hashCode()
		   			 +str0.length());
		
	}
	
	private static String SHIFT(String str0){
		if(str0 == null || str0.length() == 0)
			return null;
		
		return str0.substring(1) + str0.charAt(0);
	}
}
