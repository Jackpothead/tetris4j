package utilities;

import java.util.ArrayList;

/**0-9;a-z Strings representing [unsigned] longs.*/
public class Az09Strings
{
	private static long [] pow;
	private static String chars = 
		"0123456789abcdefghijklmnopqrstuvwxyz";
	
	// Generates generic long array, although 
	// it's probably easier to just hardcode it.
	// Oh well.
	private static long[] GENERATE(long power)
	{	
		if(power < 2)
			throw new IllegalArgumentException();
		
		ArrayList<Long> aLong = new ArrayList<Long>();
		for(long base = 1,last = base;;)
		{	
			// May still overflow!
			last = base;
			base *= power;
			if(base < last)
				break;
			
			aLong.add(base);
		}
		
		// I have to manually copy arrays here?!
		long[] L_ = new long[aLong.size()+1];
		for(
			// Yeh looks messy but it works.
			int i0 = L_.length-2,i1=0;
			i0 >= 0;
			L_[i0]=aLong.get(i1++),i0--
		);
		L_[L_.length-1] = 1;
		
		return L_;
	}
	
	//J2se lacks library function to reverse strings.
	public static String REVERSE_STRING(String str0)
	{
		char[] out = new char[str0.length()];
		
		for(int i0 = out.length-1,i1 = 0;i1 < out.length;i1++,i0--)
			out[i1] = str0.charAt(i0);
		
		return new String(out);
	}
	
	/**Converts long to A-Z/0-9 string.*/
	public static String to0_9A_ZString(long long0)
	{
		if(long0 == 0)
			return "0";
		
		if(long0<0)
			long0 = -long0;
		
		char[] v = chars.toCharArray();
		if(pow==null)pow = GENERATE(36);
		StringBuffer b = new StringBuffer();
		
		for(int ia = 0;ia<pow.length;ia++)
		{
			//cheesy.
			if(long0==0)
			{
				b.append("0");
				continue;
			}
			
			if(long0 < pow[ia])
				continue;
			
			// Shouldn't overflow..
			int ib = (int) (long0/pow[ia]);
			b.append(v[ib]);
			long0 -= (pow[ia]*ib);
		}
		return b.toString();
	}
	
	/**Converts A-Z/0-9 String to Long.*/
	public static long toLong(String str0)
	{
		str0 = str0.toLowerCase();
		
		char[] ca0 = str0.toCharArray();
		
		if(pow==null)pow = GENERATE(36);
		
		//Length checking.
		if(str0.length()==0 || str0.length() > pow.length-1)
			throw new IllegalArgumentException();
		
		//Character checking.
		for(char c0 : ca0)
			if(!chars.contains(Character.toString(c0)))
					throw new IllegalArgumentException();
		
		int out = 0;
		
		// Woot algorithms done!
		for(int i0 = 0, i1 = pow.length - ca0.length
				;i0 < ca0.length; i0++,i1++)
		{
			out += pow[i1] * chars.indexOf(ca0[i0]);
		}
		
		// catches some (not all) overflows.
		if(out < 0)
			throw new IllegalArgumentException();
		
		return out;
	}
}
