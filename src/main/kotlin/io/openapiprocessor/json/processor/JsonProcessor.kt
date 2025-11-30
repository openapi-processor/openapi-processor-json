/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-json
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.json.processor

import io.openapiparser.OpenApiParser
import io.openapiprocessor.api.v2.OpenApiProcessor
import io.openapiprocessor.jackson.JacksonConverter
import io.openapiprocessor.jackson.JacksonJsonWriter
import io.openapiprocessor.jsonschema.reader.UriReader
import io.openapiprocessor.jsonschema.schema.DocumentLoader
import io.openapiprocessor.jsonschema.schema.DocumentStore
import java.io.File
import java.io.FileWriter

/**
 *  Entry point of the openapi-processor-json.
 */
class JsonProcessor : OpenApiProcessor
{
    /**
     * provides the processor name.
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
        val apiPath: String? = options["apiPath"]?.toString()
        if (apiPath == null) {
            println("openapi-processor-json: missing apiPath!")
            return
        }

        val targetDir: String? = options["targetDir"]?.toString()
        if (targetDir == null) {
            println("openapi-processor-json: missing targetDir!")
            return
        }

        val reader = UriReader()
        val converter = JacksonConverter()
        val loader = DocumentLoader(reader, converter)

        val documents = DocumentStore()
        val parser = OpenApiParser(documents, loader)

        val baseUri = toURI(apiPath)
        val result = parser.parse (baseUri)
        val bundled = result.bundle()

        val out = FileWriter(listOf(targetDir, "openapi.json").joinToString(File.separator))
        val writer = JacksonJsonWriter(out)

        writer.write(bundled)
    }
}