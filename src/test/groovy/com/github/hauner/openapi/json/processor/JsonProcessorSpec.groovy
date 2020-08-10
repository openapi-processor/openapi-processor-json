/*
 * Copyright 2019 the original authors
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

import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class JsonProcessorSpec extends Specification {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    void "run json processor" () {
        def apiPath = ['.', 'src', 'test', 'resources', 'openapi.yaml'].join(File.separator)
        def expJson = ['.', 'src', 'test', 'resources', 'openapi.json'].join(File.separator)
        def targetDir = [folder.root.absolutePath].join(File.separator)
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
