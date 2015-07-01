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

import org.gradle.api.Project

import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.AWSLambdaClient

import ikakara.awsutil.gradle.AwsPluginExtension

@CompileStatic
class AwsLambdaPluginExtension {
  static final String NAME = "lambda"

  Project project
  String profileName
  String region

  private final AWSLambda client = initClient()

  AwsLambdaPluginExtension(Project project) {
    this.project = project
  }

  private AWSLambda initClient() {
    AwsPluginExtension aws = project.getExtensions().getByType(AwsPluginExtension.class)
    AWSLambda client = aws.createClient(AWSLambdaClient.class, profileName)
    client.setRegion(aws.getActiveRegion(region))
    return client
  }

}
