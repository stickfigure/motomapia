package com.motomapia.wikimapia;

import lombok.Getter;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Adds the detail information for a place() call
 */
public class WikiPlaceDetail extends WikiPlace
{
	private static final long serialVersionUID = 1L;

	@Getter final String description;
	@Getter final String link;
	@Getter final String wikipedia;

	/**
	 * Assumes the node is a valid node (ie not error).
	 * @throws WikimapaiException if something is wrong with the data
	 */
	public WikiPlaceDetail(JsonNode placeNode) throws WikimapiaException
	{
		super(placeNode, false);

		this.description = placeNode.path("description").asText();
		this.link = placeNode.path("url").asText();
		this.wikipedia = placeNode.path("wikipedia").asText();
	}
}