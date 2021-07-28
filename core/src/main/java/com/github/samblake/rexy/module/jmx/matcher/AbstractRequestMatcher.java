package com.github.samblake.rexy.module.jmx.matcher;

/**
 * An abstract request matcher that is associated with an MBean. Matching needs to be implemented by the subclass.
 *
 * @param <T> The type of MBean
 */
public abstract class AbstractRequestMatcher<T> implements RequestMatcher<T> {
	
	private final T mBean;
	
	public AbstractRequestMatcher(T mBean) {
		this.mBean = mBean;
	}
	
	@Override
	public T getMBean() {
		return mBean;
	}
	
	@Override
	public String toString() {
		return mBean.getClass().getSimpleName();
	}
	
}