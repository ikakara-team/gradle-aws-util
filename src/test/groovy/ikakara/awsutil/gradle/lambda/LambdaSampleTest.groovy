/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ikakara.awsutil.gradle.lambda

import groovy.transform.CompileStatic

import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.*
import org.junit.*

import static org.junit.Assert.*

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection

import ikakara.awsutil.gradle.AwsPlugin
import ikakara.awsutil.gradle.AwsPluginExtension

@CompileStatic
class LambdaSampleTest {
  private Project project
  private LambdaAwsPlugin plugin

  @Before
  public void setup() { // this code gets called for every @Test
    project = ProjectBuilder.builder().build()
    plugin = new LambdaAwsPlugin()
  }

  @Test
  public void testLambdaSample() {
    /*
    GradleConnector connector = GradleConnector.newConnector()
    connector.forProjectDirectory(new File("src/test/resources/basic-project"))
    ProjectConnection connection = connector.connect()
    try {
      BuildLauncher launcher = connection.newBuild()
      launcher.forTasks("build")
      launcher.run()
    } finally {
      connection.close()
    }
*/
    project.tasks.each { println "++++++++++++++++ testLambdaSample task: " + it }
  }

}
