package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;

import static com.github.samblake.rexy.utils.Requests.getBaseUrl;

public class RequestTemplateProcessor extends ExpressionTemplateProcessor {
	private static final Logger logger = LogManager.getLogger(RequestTemplateProcessor.class);
	
	public RequestTemplateProcessor(String name) {
		super(name);
	}
	
	@Override
	protected String substituteExpression(RexyRequest request, String key) throws SubstituteException {
		try {
			switch (key) {
				case "origin":
					return getBaseUrl(request);
				case "path":
					return request.getPath();
				default:
					throw new SubstituteException("Invalid key: " + key);
			}
		}
		catch (URISyntaxException e) {
			throw new SubstituteException("Invalid URI: " + request.getUri(), e);
		}
	}
	
}