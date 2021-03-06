/*
 * Copyright 2013 the original author or authors.
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
package ikakara.awsutil.gradle

import java.lang.reflect.Constructor

import groovy.transform.CompileStatic

import org.gradle.api.Project

import com.amazonaws.AmazonServiceException
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.auth.SystemPropertiesCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.RegionUtils
import com.amazonaws.regions.Regions
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.model.GetUserResult

import java.lang.reflect.InvocationTargetException

import ikakara.awsutil.gradle.AwsPlugin

@CompileStatic
class AwsPluginExtension {
  static final String EXT_AWS = "aws"
  static final AWSCredentialsProvider EMPTY = new AWSCredentialsProvider() {
    void refresh() {}
    AWSCredentials getCredentials() { return null }
  }

  Project project
  String profileName = "default"
  String region = Regions.US_EAST_1.getName()

  AwsPluginExtension(Project proj) {
    project = proj
  }

  AWSCredentialsProvider newCredentialsProvider(String name) {
    println "===============AwsPluginExtension.newCredentialsProvider: name=${name} profileName=${profileName}"

    return new AWSCredentialsProviderChain(
      new EnvironmentVariableCredentialsProvider(),
      new SystemPropertiesCredentialsProvider(),
      name ? new ProfileCredentialsProvider(name) : EMPTY,
      new ProfileCredentialsProvider(profileName),
      new InstanceProfileCredentialsProvider())
  }

  public <T extends AmazonWebServiceClient> T createClient(Class<T> serviceClass, String name) {
    if (!name) {
      if (!profileName) {
        throw new IllegalStateException("default profileName is null")
      }
      name = profileName
    }

    AWSCredentialsProvider credentialsProvider = newCredentialsProvider(name)
    return createClient(serviceClass, credentialsProvider, null)
  }

  static <T extends AmazonWebServiceClient> T createClient(Class<T> serviceClass, AWSCredentialsProvider credentials, ClientConfiguration config) {
    Constructor<T> constructor
    T client

    println "=============AwsPluginExtension.createClient: class=${serviceClass} cred=${credentials} config=${config}"

    try {
      if (credentials == null) {
        if(config == null) {
          constructor = serviceClass.getConstructor()
          client = constructor.newInstance()
        } else {
          constructor = serviceClass.getConstructor(ClientConfiguration.class)
          client = constructor.newInstance(config)
        }
      } else if (config == null) {
        constructor = serviceClass.getConstructor(AWSCredentialsProvider.class)
        client = constructor.newInstance(credentials)
      } else {
        constructor = serviceClass.getConstructor(AWSCredentialsProvider.class, ClientConfiguration.class)
        client = constructor.newInstance(credentials, config)
      }

      println "=============AwsPluginExtension.createClient: class=${serviceClass} cred=${credentials} config=${config} created_client=${client}"

      return client
    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException("Couldn't instantiate instance of " + serviceClass, e)
    }
  }

  Region getActiveRegion(String clientRegion) {
    if (clientRegion) {
      return RegionUtils.getRegion(clientRegion)
    }
    if (!region) {
      throw new IllegalStateException("default region is null")
    }
    return RegionUtils.getRegion(region)
  }

  String getActiveProfileName(String clientProfileName) {
    if (clientProfileName) {
      return clientProfileName
    }
    if (!profileName) {
      throw new IllegalStateException("default profileName is null")
    }
    return profileName
  }

  String getAccountId() {
    String arn = getUserArn() // ex. arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/Bob
    return arn.split(":")[4]
  }

  String getUserArn() {
    println "===========AwsPluginExtension.getUserArn: project=${project} profileName=${profileName} region=${region} ext=${project?.extensions}"

    AmazonIdentityManagement iam = createClient(AmazonIdentityManagementClient.class, profileName)
    try {
      GetUserResult getUserResult = iam.user
      return getUserResult.user.arn
    } catch (AmazonServiceException e) {
      if (e.getErrorCode().equals("AccessDenied") == false) {
        throw e
      }
      String msg = e.message
      int arnIdx = msg.indexOf("arn:aws")
      if (arnIdx == -1) {
        throw e
      }
      int arnSpace = msg.indexOf(" ", arnIdx)
      return msg.substring(arnIdx, arnSpace)
    }
  }
}
