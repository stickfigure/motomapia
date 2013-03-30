/*
 * $Id$
 */

package com.motomapia.wikimapia;

/**
 * A RuntimeException which indicates some sort of problem with wikimapia.
 * 
 * @author Jeff Schnitzer
 */
public class WikimapiaException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public WikimapiaException()
	{
		super();
	}

	public WikimapiaException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WikimapiaException(String message)
	{
		super(message);
	}

	public WikimapiaException(Throwable cause)
	{
		super(cause);
	}
}