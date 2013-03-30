/*
 */

package com.motomapia.test;

import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.CostFunction;


/**
 */
public class Endless
{
	public static void main(String[] args)
	{
		BoundingBox bb = new BoundingBox(38.912056, -118.40747, 35.263195, -123.88965);
		
		// This causes an endless loop
		List<String> cells = GeocellManager.bestBboxSearchCells(bb, new CostFunction() {
			@Override
			public double defaultCostFunction(int numCells, int resolution)
			{
				// Here we ensure that we do not try to query more than 30 cells, the limit of a gae IN filter
				return numCells > 30 ? Double.MAX_VALUE : 0;
			}
		});
		
		//List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);
		
		System.out.println("Cells are: " + cells);
	}
}