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

import groovy.lang.Closure

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer

import org.gradle.api.GradleException
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.model.InvocationType
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult
import com.amazonaws.services.lambda.model.LogType
import com.google.common.base.Charsets
import com.google.common.io.Files

public class AwsLambdaInvokeTask extends ConventionTask {

  String functionName
  InvocationType invocationType
  LogType logType = LogType.None
  String clientContext
  Object payload
  InvokeResult invokeResult

  public AwsLambdaInvokeTask() {
    setDescription("Invoke Lambda function.")
    setGroup("AWS")
  }

  @TaskAction
  public void deleteFunction() throws FileNotFoundException, IOException {
    if (!functionName) {
      throw new GradleException("functionName is required")
    }

    println "AwsLambdaInvokeTask task: name=${functionName}"

    LambdaAwsPluginExtension ext = project.extensions.getByType(LambdaAwsPluginExtension.class)
    AWSLambda lambda = ext.client

    InvokeRequest request = new InvokeRequest()
    .withFunctionName(functionName)
    .withInvocationType(getInvocationType())
    .withLogType(getLogType())
    .withClientContext(getClientContext())
    setupPayload(request)
    invokeResult = lambda.invoke(request)
    getLogger().info("Invoke Lambda function requested: {}", functionName)
  }

  private void setupPayload(InvokeRequest request) throws IOException {
    Object payload = getPayload()
    String str
    if (payload instanceof ByteBuffer) {
      request.setPayload((ByteBuffer) payload)
      return
    }
    if (payload instanceof File) {
      File file = (File) payload
      str = Files.toString(file, Charsets.UTF_8)
    } else if (payload instanceof Closure) {
      Closure<?> closure = (Closure<?>) payload
      str = closure.call().toString()
    } else {
      str = payload.toString()
    }
    request.setPayload(str)
  }
}
