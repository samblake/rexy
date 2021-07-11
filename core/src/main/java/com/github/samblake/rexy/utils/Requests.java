package com.github.samblake.rexy.utils;

import com.github.samblake.rexy.http.RexyHeader;
import com.github.samblake.rexy.http.request.RexyRequest;

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
	
}