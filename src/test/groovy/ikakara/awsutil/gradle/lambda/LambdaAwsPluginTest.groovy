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

import ikakara.awsutil.gradle.AwsPlugin
import ikakara.awsutil.gradle.AwsPluginExtension

@CompileStatic
class LambdaAwsPluginTest {
  private Project project
  private LambdaAwsPlugin plugin

  @Before
  public void setup() { // this code gets called for every @Test
    project = ProjectBuilder.builder().build()
    plugin = new LambdaAwsPlugin()
  }

  @Test
  public void applyLambdaPlugin() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    project?.pluginManager.apply 'ikakara.awsutil.lambda'

    // validate that the extensions are nested
    assert project[AwsPluginExtension.EXT_AWS] instanceof AwsPluginExtension
    assert project[AwsPluginExtension.EXT_AWS][LambdaAwsPluginExtension.EXT_LAMBDA] instanceof LambdaAwsPluginExtension

    //assert project.custom.foo == "bar"

    project.tasks.each { println "++++++++++++++++ applyLambdaPlugin task: " + it }
  }

  @Test
  public void canAddCreateTaskToProject() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    def task = project.task('lambdaCreateFunction', type: AwsLambdaCreateFunctionTask)
    assertTrue(task instanceof AwsLambdaCreateFunctionTask)

    project.tasks.each { println "++++++++++++++++ canAddCreateTaskToProject task: " + it }
  }

  @Test
  public void canAddDeleteTaskToProject() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    def task = project.task('lambdaDeleteFunction', type: AwsLambdaDeleteFunctionTask)
    assertTrue(task instanceof AwsLambdaDeleteFunctionTask)

    project.tasks.each { println "++++++++++++++++ canAddDeleteTaskToProject task: " + it }
  }

  @Test
  public void canAddInvokeTaskToProject() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    def task = project.task('lambdaInvokeFunction', type: AwsLambdaInvokeTask)
    assertTrue(task instanceof AwsLambdaInvokeTask)

    project.tasks.each { println "++++++++++++++++ canAddInvokeTaskToProject task: " + it }
  }

  @Test
  public void canAddMigrateTaskToProject() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    def task = project.task('lambdaMigrateFunction', type: AwsLambdaMigrateFunctionTask)
    assertTrue(task instanceof AwsLambdaMigrateFunctionTask)

    project.tasks.each { println "++++++++++++++++ canAddMigrateTaskToProject task: " + it }
  }

  @Test
  public void canAddUpdateCodeTaskToProject() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    def task = project.task('lambdaUpdateFunctionCode', type: AwsLambdaUpdateFunctionCodeTask)
    assertTrue(task instanceof AwsLambdaUpdateFunctionCodeTask)

    project.tasks.each { println "++++++++++++++++ canAddUpdateCodeTaskToProject task: " + it }
  }

  @Test
  public void canAddUpdateConfigTaskToProject() {
    assertNotNull(project)
    assertTrue(plugin instanceof LambdaAwsPlugin)

    def task = project.task('lambdaUpdateFunctionConfig', type: AwsLambdaUpdateFunctionConfigurationTask)
    assertTrue(task instanceof AwsLambdaUpdateFunctionConfigurationTask)

    project.tasks.each { println "++++++++++++++++ canAddUpdateConfigTaskToProject task: " + it }
  }
}
