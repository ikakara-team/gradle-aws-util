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

import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult

public class AwsLambdaUpdateFunctionCodeTask extends ConventionTask {
  String functionName
  File zipFile
  UpdateFunctionCodeResult updateFunctionCode


  public AwsLambdaUpdateFunctionCodeTask() {
    setDescription("Update Lambda function code.")
    setGroup("AWS")
  }

  @TaskAction
  public void updateFunctionCode() throws FileNotFoundException, IOException {
    if (!functionName) {
      throw new GradleException("functionName is required")
    }

    println "AwsLambdaUpdateFunctionCodeTask task: name=${functionName}"

    LambdaAwsPluginExtension ext = project.extensions.getByType(LambdaAwsPluginExtension.class)
    AWSLambda lambda = ext.client

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
      updateFunctionCode = lambda.updateFunctionCode(request)
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
}
