/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.json.processor

import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import spock.lang.Specification
import spock.lang.TempDir

class JsonProcessorSpec extends Specification {

    @TempDir
    public File folder

    void "run json processor" () {
        def apiPath = ['.', 'src', 'test', 'resources', 'openapi.yaml'].join(File.separator)
        def expJson = ['.', 'src', 'test', 'resources', 'openapi.json'].join(File.separator)
        def targetDir = [folder.absolutePath].join(File.separator)
        def targetPath = [targetDir, 'openapi.json'].join(File.separator)

        def options = [
            apiPath: new File(apiPath).canonicalPath,
            targetDir: targetDir
        ]

        when:
        def processor = new JsonProcessor()
        processor.run (options)

        then:
        def e = new File(expJson).text
        def a = new File(targetPath).text
        if (e != a) {
            printUnifiedDiff(new File(expJson), new File(targetPath))
        }

        e.replace('\r', '') == a.replace('\r', '')
    }

    private void printUnifiedDiff (File expected, File generated) {
        def patch = DiffUtils.diff (
            expected.readLines (),
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            expected.path,
            generated.path,
            expected.readLines (),
            patch,
            2
        )

        diff.each {
            println it
        }
    }
}
