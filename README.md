# Rexy

A lightweight REST mock/proxy server.

<a href="https://samblake.github.io/rexy">Documentation</a>

### Overview

Rexy is designed to allow dynamic testing of different scenarios during integration testing of REST endpoints. It is
typically configured as a proxy server with the standard behaviour to simply forward requests on to the actual API
endpoint. Rexy can then be configured to intercept certain requests and return specific responses. This can be helpful
for testing error scenarios that may be difficult or impossible to trigger on an ad-hoc bases on the API endpoint
itself. 

Details of the different APIs to proxy as well as presets for responses are configured by a simple JSON file. Runtime 
configuration is performed via JMX or via the Wexy module as a web application.

### Running

Start Rexy by running the `rexy.Rexy` class, optionally specifying a configuration file as an argument. Alternatively
build via Maven and run the resulting jar.

### FAQ

**Can Rexy be used to test APIs without proxying?**

Yes, it is not a requirement to specify an endpoint to proxy to. If a request is not intercepted a 502 response will be
returned. This can be useful for testing APIs when there is only a production endpoint. 

**Can Rexy be configured without JMX?**

Although it still uses JMX in the background the Wexy module provides a web interface for runtime configuration.

**Can non-JSON response presets be configured?**

Yes, they just need to be specified in their own files and referenced from the JSON configuration.