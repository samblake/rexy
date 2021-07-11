/**
 * <p>Matchers are used to see if an MBean should handle a HTTP request. A base matcher is used to match all requests
 * against the endpoint URL and HTTP method. Currently this is a simple regular expression based matcher. In the
 * future other matchers may be supplied to address the limitations of this approach.
 *
 * <p>Additional matchers can be supplied as part of the {@code matchers} JSON. The keys in the  {@code matchers}
 * object are the name of the matcher and the values are the configuration required by that matcher. If additional
 * matchers are supplied all matchers, including the base matcher, must match for the MBean to process the request.
 */
package com.github.samblake.rexy.module.jmx.matcher;