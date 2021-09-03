/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.amazon.cloudformation.stackinstances;

import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.CreateStackInstancesResponse;
import software.amazon.awssdk.services.cloudformation.model.CreateStackSetResponse;
import software.amazon.cloudformation.Action;
import software.amazon.cloudformation.proxy.*;
import software.amazon.cloudformation.stackinstances.util.InstancesAnalyzer;
import software.amazon.cloudformation.stackinstances.util.StackInstancesPlaceHolder;

import java.util.UUID;

import static software.amazon.cloudformation.stackinstances.translator.RequestTranslator.createStackInstancesRequest;

public class CreateHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<CloudFormationClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final StackInstancesPlaceHolder placeHolder = new StackInstancesPlaceHolder();

        InstancesAnalyzer.builder().desiredModel(model).build().analyzeForCreate(placeHolder);
        if (model.getInstanceId() == null) {
            model.setInstanceId(UUID.randomUUID().toString());
        }
        return ProgressEvent.progress(model, callbackContext)
                .then(progress -> createStackInstances(proxy, proxyClient, progress, placeHolder.getCreateStackInstances(), logger))
                .then(progress -> ProgressEvent.defaultSuccessHandler(model));
    }
}
