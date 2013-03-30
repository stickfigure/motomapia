package com.motomapia.util;


/**
 * Some basic stupid java tools.
 */
public class Utils
{
	/**
	 * Null safe equality comparison
	 */
	public static boolean safeEquals(Object o1, Object o2)
	{
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}
}