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
package org.ballerinalang.model.tree.expressions;

/**
 * @since 1.2.0
 */
public interface XMLNavigationAccess extends  VariableReferenceNode {
    enum NavAccessType {
        /**
         * Get element children.
         * xml/< elem > or xml/<*>
         */
        CHILD_ELEMS,
        /**
         * Get children
         * xml/*
         */
        CHILDREN,
        /**
         * Get descendents
         * xml/** /< elem >
         */
        DESCENDANTS;

        NavAccessType() {
        }

        public static NavAccessType fromInt(int number) {
            switch (number) {
                case 0:
                    return CHILD_ELEMS;
                case 1:
                    return CHILDREN;
                case 2:
                    return DESCENDANTS;
                default:
                    throw new IllegalArgumentException();
            }
        }

    }

    NavAccessType getNavAccessType();

}
