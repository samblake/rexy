package com.github.samblake.rexy.utils;

import java.util.Collection;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class Streams {
	
	private Streams() {
	}
	
	public static <T> Collection<T> flattern(Collection<? extends Collection<T>> itmes) {
		return itmes.stream().flatMap(Collection::stream).collect(toList());
	}
	
	public static <T> BinaryOperator<Collection<T>> collectionMerger() {
		return (c1, c2) -> concat(c1.stream(), c2.stream()).collect(toList());
	}
	
}
