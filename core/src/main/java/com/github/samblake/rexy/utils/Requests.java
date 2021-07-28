package com.github.samblake.rexy.utils;

import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.github.samblake.rexy.http.RexyHeader.HEADER_CONTENT_TYPE;
import static java.util.stream.Collectors.toList;

public class Requests {
	
	private Requests() {
	}
	
	public static String getContentType(RexyRequest request) {
		return request.getHeaders()
				.stream().filter(h -> h.getName().equalsIgnoreCase(HEADER_CONTENT_TYPE))
				.findFirst().map(RexyHeader::getValue).orElse(null);
	}
	
	public static List<RexyHeader> toHeaders(Map<String, String> headers) {
		return headers.entrySet().stream().map(RexyHeader::fromEntry).collect(toList());
	}
	
	public static String getOrigin(RexyRequest request) throws URISyntaxException {
		URI url = new URI(request.getUri());
		String port = url.getPort() == -1 ? "" : ":" + url.getPort();
		return url.getScheme() + "://" + url.getHost() + port;
	}
	
}