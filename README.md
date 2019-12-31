[![][badge-license]][generatr-license]
[![][badge-ci]][workflow-ci]

# openapi-generatr-json

a simple [OpenAPI][openapi] yaml to json generatr.
 

# Getting Started

The openapi generatr [gradle plugin][generatr-gradle] is the easiest way to use yaml to json generatr. 

## adding generatr-json

The plugin provides a `openapiGeneratr` dependency configuration that is used to add the generatr dependency.

        dependencies {
            // 'openapiGeneratr' is a custom configuration that is used by the gradle plugin.
            openapiGeneratr 'com.github.hauner.openapi:openapi-generatr-json:1.0.0.M1'
            
            // .... 
            // normal project dependencies
            // .... 
        }
        
## configuring generatr-json

The plugin will add an `openapiGeneratr` configuration block that is used to configure the generatrs.
Configuration for a specific generatr is placed inside it using the generatr name as configuration
block name.   

        openapiGeneratr {

            json {
                apiPath = "$projectDir/src/api/openapi.yaml"
                targetDir = "$projectDir/build/openapi"
            }        

        }
        
- `apiPath`: (**required**) the path to the `openapi.yaml` file and the main input for the generatr.

- `targetDir`: (**required**) the output folder for generating the `openapi.json` file.

# Sample

See [`openapi-generatr-spring-mvc-sample`][generatr-sample] for a complete working sample of a minimal
 openapi.yaml.


[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[badge-ci]: https://github.com/hauner/openapi-generatr-json/workflows/ci/badge.svg

[workflow-ci]: https://github.com/hauner/openapi-generatr-json/actions?query=workflow%3Aci

[openapi]: https://www.openapis.org/

[generatr-license]: https://github.com/hauner/openapi-generatr-json/blob/master/LICENSE
[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
