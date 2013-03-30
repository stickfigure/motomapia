package com.motomapia.util;

import javax.inject.Singleton;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Give us some finer control over Jackson's behavior
 */
@Singleton
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

	private final ObjectMapper defaultObjectMapper;

	public ObjectMapperProvider() {
		defaultObjectMapper = new ObjectMapper();

		defaultObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		//defaultObjectMapper.registerModule(new JodaModule());

	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return defaultObjectMapper;
	}
}