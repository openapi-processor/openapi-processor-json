[![][badge-license]][oap-license]
[![][badge-ci]][workflow-ci]

![openapi-processor-api logo](images/openapi-processor-json@1280x200.png)


# openapi-processor-json

a simple [OpenAPI][openapi] yaml to json converter.
 

# Getting Started

The openapi processor [gradle plugin][opa-gradle] is the easiest way to use the yaml to json processor. 

## configuring openapi-processor-json

The plugin will add an `openapiGeneratr` configuration block that is used to configure the openapi processors.
Configuration for a specific processor is placed inside it using the processor name (in this case `json`) as
configuration block name.   

        openapiProcessor {

            json {
                processor 'com.github.hauner.openapi:openapi-processor-json:1.0.0.Mx'
            
                apiPath = "$projectDir/src/api/openapi.yaml"
                targetDir = "$projectDir/build/openapi"
            }        

        }
        
- `apiPath`: (**required**) the path to the `openapi.yaml` file and the main input for the processor.

- `targetDir`: (**required**) the output folder for generating the `openapi.json` file.

# Sample

See [`openapi-processor-spring-mvc-sample`][oap-sample] for a complete working sample of a minimal openapi.yaml.


[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[badge-ci]: https://github.com/hauner/openapi-processor-json/workflows/ci/badge.svg

[workflow-ci]: https://github.com/hauner/openapi-processor-json/actions?query=workflow%3Aci

[openapi]: https://www.openapis.org/

[oap-license]: https://github.com/hauner/openapi-processor-json/blob/master/LICENSE
[oap-gradle]: https://github.com/hauner/openapi-processor-gradle
[oap-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
