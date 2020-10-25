/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.jvm.types;

/**d
 * {@code ResourceFunction} represents a resource function in Ballerina.
 *
 * @since 2.0
 */
public class ResourceFunction {

    public final AttachedFunction attachedFunction;
    public final String accessor;
    public final String resourcePath;

    public ResourceFunction(AttachedFunction attachedFunction, String accessor, String resourcePath) {
        this.attachedFunction = attachedFunction;
        this.accessor = accessor;
        this.resourcePath = resourcePath;
    }

    @Override
    public String toString() {
        return "resource " + accessor + " " + attachedFunction.toString();
    }
}
