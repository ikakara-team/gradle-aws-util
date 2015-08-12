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

import java.io.FileNotFoundException
import java.io.IOException

import org.gradle.api.GradleException
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.model.DeleteFunctionRequest

public class AwsLambdaDeleteFunctionTask extends ConventionTask {

  String functionName

  public AwsLambdaDeleteFunctionTask() {
    setDescription("Delete Lambda function.")
    setGroup("AWS")
  }

  @TaskAction
  public void deleteFunction() throws FileNotFoundException, IOException {
    if (!functionName) {
      throw new GradleException("functionName is required")
    }

    println "AwsLambdaDeleteFunctionTask task: name=${functionName}"

    LambdaAwsPluginExtension ext = project.extensions.getByType(LambdaAwsPluginExtension.class)
    AWSLambda lambda = ext.client

    DeleteFunctionRequest request = new DeleteFunctionRequest()
    .withFunctionName(functionName)
    lambda.deleteFunction(request)
    getLogger().info("Delete Lambda function requested: {}", functionName)
  }
}
