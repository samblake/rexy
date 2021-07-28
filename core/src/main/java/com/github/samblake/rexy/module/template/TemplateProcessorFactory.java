package com.github.samblake.rexy.module.template;

import com.github.samblake.rexy.http.request.RexyRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemplateProcessorFactory {
	private static final Logger logger = LogManager.getLogger(TemplateProcessorFactory.class);
	
	private static final PassThroughProcessor PASS_THROUGH_PROCESSOR = new PassThroughProcessor();
	
	private final List<TemplateProcessor> processors = new ArrayList<>();
	
	public void addProcessor(TemplateProcessor processor) {
		processors.add(processor);
	}
	
	public TemplateProcessor getProcessor(String template) {
		Optional<TemplateProcessor> processor = processors.stream()
				.filter(p -> p.matches(template))
				.findFirst();
		
		if (!processor.isPresent()) {
			logger.debug("No processor found for " + template);
		}
		
		return processor.orElse(PASS_THROUGH_PROCESSOR);
	}
	
	public String substitute(RexyRequest request, String template) {
		TemplateProcessor processor = getProcessor(template);
		return processor.substitute(request, template);
	}
	
}