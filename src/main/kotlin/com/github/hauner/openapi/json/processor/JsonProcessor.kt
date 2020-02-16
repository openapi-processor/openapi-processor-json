/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.json.processor

import com.github.hauner.openapi.api.OpenApiProcessor
import io.swagger.v3.core.util.Json
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import java.io.File

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
        val apiPath: String? = options["apiPath"]?.toString()
        val targetDir: String? = options["targetDir"]?.toString()

        if (apiPath == null) {
            println("openapi-processor-json: missing apiPath!")
            return
        }

        if (targetDir == null) {
            println("openapi-processor-json: missing targetDir!")
            return
        }

        val opts = ParseOptions()
        val result: SwaggerParseResult = OpenAPIV3Parser()
                .readLocation(apiPath, null, opts)

        val json = Json.pretty(result.openAPI)
        val jsonn = json + "\n"

        val targetPath = listOf(targetDir, "openapi.json").joinToString(separator = File.separator)
        File(targetPath).writeText(jsonn)
    }

}
