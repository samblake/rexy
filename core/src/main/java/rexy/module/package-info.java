/**
 * <p>Rexy it built around pluggable chain of interceptors knows as modules. When Rexy receives a request it is passed
 * to each module in order. Modifications can be made to the request as it is passed up the chain. When any of the
 * modules generate a response the flow is instantly reversed and the response will be passed back down the chain in
 * the opposite direction. Again modifications can be made to the response as it flows down the chain. When the initial
 * module is reached the response will be returned to the originating server. All modules are configured to some extent
 * by the Rexy JSON configuration file, however some (those that should be modified at runtime) can also be configured
 * by JMX.</p>
 *
 * <p>No modules are enabled by default. In order to enable a module it should be specified in the `modules` section in the
 * configuration. Modules can be configured in any order but the final two modules in the chain should probably always be
 * the mock module followed by the proxy module.</p>
 */
package rexy.module;