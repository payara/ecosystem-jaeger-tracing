# Jaeger Tracing Wrapper Library

This is a wrapper for Jaeger Tracing to be used with Payara Platform 5.194+, as an alternative implementation of Opentracing.

Version 1.1 : Works on 5.2021.1 or higher (MicroProfile 4.x)
Version 1.0 : Works on 5.194 - 5.2020.7

**ATTENTION**: Support for this repository is handled in the [Ecosystem Support repository](https://github.com/payara/ecosystem-support)

## Building

`mvn clean install`

## Configuration

Configuration can be done either with environment variables or programmatically in JaegerTracerWrapper

See https://github.com/jaegertracing/jaeger-client-java/blob/master/jaeger-core/README.md#configuration-via-environment 

- Define environment property `JAEGER_SERVICE_NAME` to override the default `jaeger-test` value.
- Define environment property `JAEGER_AGENT_HOST` to override the default `localhost`.
 
## Using with Payara Server

This must be added as a library to the server itself, not included with a deployed application.

Add it as a library with the asadmin command `add-library jaeger-tracer-lib.jar`

Tracing must be enabled with `set-requesttracing-configuration --enabled true --dynamic true`
