/*
 */

package com.motomapia;

import static com.motomapia.OfyService.ofy;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.csvreader.CsvWriter;
import com.motomapia.entity.Place;

/**
 * Downloads all the places in a bounding box as a POI csv file.  This is an example of a plain
 * servlet with Guice, not using Resteasy.
 * 
 * @author Jeff Schnitzer
 */
@Singleton
public class DownloadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	/** */
	private final static Logger log = LoggerFactory.getLogger(DownloadServlet.class);
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		float swLat = Float.parseFloat(req.getParameter("swLat"));
		float swLng = Float.parseFloat(req.getParameter("swLng"));
		float neLat = Float.parseFloat(req.getParameter("neLat"));
		float neLng = Float.parseFloat(req.getParameter("neLng"));
		
		if (log.isDebugEnabled())
			log.debug("Downloading box " + swLat + "," + swLng + " -- " + neLat + "," + neLng);
		
		BoundingBox bb = new BoundingBox(neLat, neLng, swLat, swLng);

		// The cost function causes it to never return
//		List<String> cells = GeocellManager.bestBboxSearchCells(bb, new CostFunction() {
//			@Override
//			public double defaultCostFunction(int numCells, int resolution)
//			{
//				// Here we ensure that we do not try to query more than 30 cells, the limit of a gae IN filter
//				return numCells > 30 ? Double.MAX_VALUE : 0;
//			}
//		});
		
		List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);
		
		resp.setContentType("text/csv");
		resp.setHeader("Content-Disposition", "attachment; filename=poi.csv");
		CsvWriter writer = new CsvWriter(resp.getWriter(), ',');
		
		int count = 0;
		
		for (Place place: ofy().load().type(Place.class).filter("cells in", cells))
		{
			writer.write("" + place.getCenter().getLatitude());
			writer.write("" + place.getCenter().getLongitude());
			String name = (place.getName() == null || place.getName().trim().length() == 0) ? "Unknown" : place.getName();
			writer.write(name);
			writer.endRecord();
			count++;
		}
		
		if (log.isDebugEnabled())
			log.debug("Downloaded " + count + " records");
	}
}