// Copyright 2013 Cloudera Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cloudera.impala.authorization;

import org.apache.sentry.provider.file.ResourceAuthorizationProvider;

import com.google.common.base.Preconditions;

/*
 * Class that contains configuration details for Impala authorization.
 */
public class AuthorizationConfig {
  private final String serverName_;
  private final String policyFile_;
  private final String policyProviderClassName_;

  public AuthorizationConfig(String serverName, String policyFile,
      String policyProviderClassName) {
    serverName_ = serverName;
    policyFile_ = policyFile;
    policyProviderClassName_ = policyProviderClassName;
  }

  /*
   * Validates the authorization configuration and throws an AuthorizationException
   * if any problems are found. If authorization is disabled, config checks are skipped.
   */
  public void validateConfig() throws IllegalArgumentException {
    // If authorization is not enabled, config checks are skipped.
    if (!isEnabled()) {
      return;
    }

    if (serverName_ == null || serverName_.isEmpty()) {
      throw new IllegalArgumentException("Authorization is enabled but the server name" +
          " is null or empty. Set the server name using the impalad --server_name flag."
          );
    }
    if (policyFile_ == null || policyFile_.isEmpty()) {
      throw new IllegalArgumentException("Authorization is enabled but the policy file" +
          " path was null or empty. Set the policy file using the " +
          "--authorization_policy_file impalad flag.");
    }
    if (policyProviderClassName_ == null || policyProviderClassName_.isEmpty()) {
      throw new IllegalArgumentException("Authorization is enabled but the " +
          "authorization policy provider class name is null or empty. Set the class " +
          "name using the --authorization_policy_provider_class impalad flag.");
    }
    Class<?> providerClass = null;
    try {
      // Get the Class object without performing any initialization.
      providerClass = Class.forName(policyProviderClassName_, false,
          this.getClass().getClassLoader());
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(String.format("The authorization policy " +
          "provider class '%s' was not found.", policyProviderClassName_), e);
    }
    Preconditions.checkNotNull(providerClass);
    if (!ResourceAuthorizationProvider.class.isAssignableFrom(providerClass)) {
      throw new IllegalArgumentException(String.format("The authorization policy " +
          "provider class '%s' must be a subclass of '%s'.",
          policyProviderClassName_,
          ResourceAuthorizationProvider.class.getName()));
    }
  }

  /*
   * Returns true if authorization is enabled.
   * If either serverName or policyFile is set (not null or empty), authorization
   * is considered enabled.
   */
  public boolean isEnabled() {
    return (serverName_ != null && !serverName_.isEmpty()) ||
           (policyFile_ != null && !policyFile_.isEmpty());
  }

  /*
   * Returns an AuthorizationConfig object that has authorization disabled.
   */
  public static AuthorizationConfig createAuthDisabledConfig() {
    return new AuthorizationConfig(null, null, null);
  }

  /*
   * The server name to secure.
   */
  public String getServerName() { return serverName_; }

  /*
   * The policy file path.
   */
  public String getPolicyFile() { return policyFile_; }

  /*
   * The full class name of the authorization policy provider. For example:
   * org.apache.sentry.provider.file.HadoopGroupResourceAuthorizationProvider.
   */
  public String getPolicyProviderClassName() { return policyProviderClassName_; }
}
