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

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import org.gradle.api.GradleException
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.model.CreateFunctionRequest
import com.amazonaws.services.lambda.model.CreateFunctionResult
import com.amazonaws.services.lambda.model.FunctionCode
import com.amazonaws.services.lambda.model.GetFunctionRequest
import com.amazonaws.services.lambda.model.GetFunctionResult
import com.amazonaws.services.lambda.model.Runtime
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationResult

public class AwsLambdaMigrateFunctionTask extends ConventionTask {
  String functionName
  String role
  Runtime runtime = Runtime.Nodejs
  String handler
  String functionDescription
  Integer timeout
  Integer memorySize
  File zipFile
  CreateFunctionResult createFunctionResult

  public AwsLambdaMigrateFunctionTask() {
    setDescription("Create / Update Lambda function.")
    setGroup("AWS")
  }

  @TaskAction
  public void createOrUpdateFunction() throws FileNotFoundException, IOException {
    if (!functionName) {
      throw new GradleException("functionName is required")
    }

    println "AwsLambdaMigrateFunctionTask task: name=${functionName}"

    LambdaAwsPluginExtension ext = project.extensions.getByType(LambdaAwsPluginExtension.class)
    AWSLambda lambda = ext.client

    try {
      GetFunctionResult getFunctionResult = lambda.getFunction(new GetFunctionRequest().withFunctionName(functionName))
      updateStack(lambda, getFunctionResult)
    } catch (AmazonServiceException e) {
      if (e.message.contains("does not exist")) {
        getLogger().warn("function {} not found", functionName)
        createFunction(lambda)
      } else {
        throw e
      }
    }
  }

  private void createFunction(AWSLambda lambda) throws IOException {
    RandomAccessFile raf
    FileChannel channel
    try {
      raf = new RandomAccessFile(zipFile, "r")
      channel = raf.channel

      MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
      buffer.load()
      CreateFunctionRequest request = new CreateFunctionRequest()
      .withFunctionName(functionName)
      .withRuntime(runtime)
      .withRole(role)
      .withHandler(handler)
      .withDescription(functionDescription)
      .withTimeout(timeout)
      .withMemorySize(memorySize)
      .withCode(new FunctionCode().withZipFile(buffer))
      createFunctionResult = lambda.createFunction(request)
      logger.info("Create Lambda function requested: {}", createFunctionResult.functionArn)
    } finally {
      if(raf) {
        raf.close()
      }
      if(channel) {
        channel.close()
      }
    }
  }

  private void updateStack(AWSLambda lambda, GetFunctionResult getFunctionResult) throws IOException {
    updateFunctionCode(lambda)
    updateFunctionConfiguration(lambda, getFunctionResult)
  }

  private void updateFunctionCode(AWSLambda lambda) throws IOException {
    RandomAccessFile raf
    FileChannel channel
    try {
      raf = new RandomAccessFile(zipFile, "r")
      channel = raf.channel

      MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
      buffer.load()
      UpdateFunctionCodeRequest request = new UpdateFunctionCodeRequest()
      .withFunctionName(functionName)
      .withZipFile(buffer)
      UpdateFunctionCodeResult updateFunctionCode = lambda.updateFunctionCode(request)
      getLogger().info("Update Lambda function requested: {}", updateFunctionCode.functionArn)
    } finally {
      if(raf) {
        raf.close()
      }
      if(channel) {
        channel.close()
      }
    }

  }

  private void updateFunctionConfiguration(AWSLambda lambda, GetFunctionResult getFunctionResult) {
    UpdateFunctionConfigurationRequest request = new UpdateFunctionConfigurationRequest()
    .withFunctionName(functionName)
    .withRole(role)
    .withHandler(handler)
    .withDescription(functionDescription)
    .withTimeout(timeout)
    .withMemorySize(memorySize)
    UpdateFunctionConfigurationResult updateFunctionConfiguration = lambda.updateFunctionConfiguration(request)
    getLogger().info("Update Lambda function configuration requested: {}", updateFunctionConfiguration.functionArn)
  }
}
