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
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationResult

public class AwsLambdaUpdateFunctionConfigurationTask extends ConventionTask {
  private String functionName
  private String role
  private String handler
  private String functionDescription
  private Integer timeout
  private Integer memorySize
  private UpdateFunctionConfigurationResult updateFunctionConfiguration

  public AwsLambdaUpdateFunctionConfigurationTask() {
    setDescription("Update Lambda function configuration.")
    setGroup("AWS")
  }

  @TaskAction
  public void createFunction() throws FileNotFoundException, IOException {
    if (!functionName) {
      throw new GradleException("functionName is required")
    }

    AwsLambdaPluginExtension ext = project.extensions.getByType(AwsLambdaPluginExtension.class)
    AWSLambda lambda = ext.client

    UpdateFunctionConfigurationRequest request = new UpdateFunctionConfigurationRequest()
    .withFunctionName(getFunctionName())
    .withRole(getRole())
    .withHandler(getHandler())
    .withDescription(getFunctionDescription())
    .withTimeout(getTimeout())
    .withMemorySize(getMemorySize())
    updateFunctionConfiguration = lambda.updateFunctionConfiguration(request)
    getLogger().info("Update Lambda function configuration requested: {}", updateFunctionConfiguration.getFunctionArn())
  }
}
