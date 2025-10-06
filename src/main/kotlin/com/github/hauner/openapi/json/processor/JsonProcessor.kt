/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-json
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.json.processor

import io.openapiprocessor.api.OpenApiProcessor
import io.swagger.v3.core.util.Json
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

/**
 *  Entry point of the openapi-processor-json.
 *
 *  @author Martin Hauner
 */
class JsonProcessor : OpenApiProcessor {

    /**
     * provides the generatr name.
     */
    override fun getName(): String {
        return "json"
    }

    /**
     * runs the processor.
     *
     * the options map should contain the following key/value pairs:
     *
     * - apiPath: (required) the path to the openapi.yaml file and the main input for the processor.
     * - targetDir: (required) the output folder for generating the openapi.json file.
     *
     * @param options map of processor properties
     */
    override fun run(options: MutableMap<String, *>) {
        var apiPath: String? = options["apiPath"]?.toString()
        if (apiPath == null) {
            println("openapi-processor-json: missing apiPath!")
            return
        }

        apiPath = toURL(apiPath).toString()

        var targetDir: String? = options["targetDir"]?.toString()
        if (targetDir == null) {
            println("openapi-processor-json: missing targetDir!")
            return
        }

        targetDir = toURL(targetDir).toString()

        val opts = ParseOptions()
        val result: SwaggerParseResult = OpenAPIV3Parser()
                .readLocation(apiPath, null, opts)

        var json= Json.pretty(result.openAPI)
        json += "\n"

        val p = Paths.get(URL(targetDir).toURI())
        val dir = Files.createDirectories(p)
        val targetPath = dir.resolve("openapi.json")
        targetPath.toFile().writeText(json)
    }

    /**
     * convert source to a valid URL.
     *
     * if the source is an url string it converts it to an URL
     * if the source is not an URL it assumes a local path and prefixes it with file://(//) to
     * create a valid URL.
     *
     * @param source source path or url
     * @return an URL to the given source
     */
    private fun toURL(source: String): URL {
        try {
            return URL(source)
        } catch (ignore: Exception) {
            // catch
        }

        try {
            return Paths.get(source)
                .normalize ()
                .toUri ()
                .toURL ()
        } catch (e: Exception) {
            throw e
        }
    }


}
