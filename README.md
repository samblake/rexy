# Rexy
A lightweight REST mock/proxy server.

### Overview

Rexy is designed to allow dynamic testing of different scenarios during integration testing of REST endpoints. It is
typically configured as a proxy server with the standard behaviour to simply forward requests on to the actual API
endpoint. Rexy can then be configured to intercept certain requests and return specific responses. This can be helpful
for testing error scenarios that may be difficult or impossible to trigger on an ad-hoc bases on the API endpoint
itself. 

Details of the different APIs to proxy as well as presets for responses are configured by a simple JSON file. Runtime 
configuration is performed via JMX or via [Wexy](https://github.com/samblake/wexy) as a web application.

### Running

Start Rexy by running the `rexy.Rexy` class, optionally specifying a configuration file as an argument.

### Modules

Rexy it built around pluggable chain of interceptors knows as modules. When Rexy receives a request it is passed to 
each module in order. Modifications can be made to the request as it is passed up the chain. When any of the modules 
generate a response the flow is instantly reversed and the response will be passed back down the chain in the opposite
direction. Again modifications can be made to the response as it flows down the chain. When the initial module is 
reached the response will be returned to the originating server. All modules are configured to some extent by the Rexy 
JSON configuration file, however some (those that should be modified at runtime) can also be configured by JMX.

The two JMX modules supplied with Rexy are:

 * Mock - Allows returning of mocked responses, either specified in an ad-hoc manor or via presets
 * Delay - Introduces a fixed delay in processing the request

The other modules are:

 * Proxy - Forwards the request to the specified server 
 * Cors - Generates a preflight response for OPTIONS requests
 * Removal - Removes a header from the request

No modules are enabled by default. In order to enable a module it should be specified in the `modules` section in the
configuration. Modules can be configured in any order but the final two modules in the chain should probably always be 
the mock module followed by the proxy module.

### Configuration

On startup the configuration will be loaded from the classpath, falling back to the filesystem if that fails. The 
default path is `rexy.json`. A custom path can be supplied as a command line argument when running Rexy.

Rexy contains examples for two APIs, the metaweather API (http://www.metaweather.com/api) and the chucknorris.io API 
(https://api.chucknorris.io).

### FAQ

**Can Rexy be used to test APIs without proxying?**

Yes, it is not a requirement to specify an endpoint to proxy to. If a request is not intercepted a 502 response will be
returned. This can be useful for testing APIs when there is only a production endpoint. 

**Can Rexy be configured without JMX?**

Although it still uses JMX in the background the [Wexy module](https://github.com/samblake/wexy) provides a web 
interface for runtime configuration.

**Can non-JSON response presets be configured?**

Yes, they just need to be specified in their own files and referenced from the JSON configuration.