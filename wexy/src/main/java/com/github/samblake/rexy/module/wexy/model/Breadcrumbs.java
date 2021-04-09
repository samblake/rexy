package com.github.samblake.rexy.module.wexy.model;

import com.github.samblake.rexy.module.wexy.model.Breadcrumbs.Breadcrumb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Breadcrumbs implements Iterable<Breadcrumb> {
	
	private final List<Breadcrumb> crumbs;
	
	public Breadcrumbs(Breadcrumb crumb) {
		crumbs = new ArrayList<>();
		crumbs.add(crumb);
	}
	
	public Breadcrumbs append(Breadcrumb crumb) {
		crumbs.add(crumb);
		return this;
	}
	
	@Override
	public Iterator<Breadcrumb> iterator() {
		return crumbs.iterator();
	}
	
	@Override
	public void forEach(Consumer<? super Breadcrumb> action) {
		crumbs.forEach(action);
	}
	
	@Override
	public Spliterator<Breadcrumb> spliterator() {
		return crumbs.spliterator();
	}
	
	public static class Breadcrumb {
		private final String name;
		private final String link;
		
		public Breadcrumb(String name, String link) {
			this.name = name;
			this.link = link;
		}
		
		public String getName() {
			return name;
		}
		
		public String getLink() {
			return link;
		}
	}
	
}