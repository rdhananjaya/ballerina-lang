/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.jvm;

import org.ballerinalang.jvm.types.BField;
import org.ballerinalang.jvm.types.BRecordType;
import org.ballerinalang.jvm.types.BType;
import org.ballerinalang.jvm.types.TypeConstants;
import org.ballerinalang.jvm.types.TypeFlags;
import org.ballerinalang.jvm.util.Flags;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all the utility methods related to iterators.
 *
 * @since 1.2.0
 */
public class IteratorUtils {

    public static int getTypeFlags(BType type) {
        boolean isPureType = type.isPureType();
        boolean isAnydata = type.isAnydata();

        if (isPureType && isAnydata) {
            return TypeFlags.asMask(TypeFlags.PURETYPE, TypeFlags.ANYDATA);
        }

        if (isAnydata) {
            return TypeFlags.ANYDATA;
        }

        if (isPureType) {
            return TypeFlags.PURETYPE;
        }

        return 0;
    }

    public static BRecordType createIteratorReturnNextType(BType type) {
        Map<String, BField> fields = new HashMap<>();
        fields.put("value", new BField(type, "value", Flags.PUBLIC + Flags.REQUIRED));
        return new BRecordType(TypeConstants.ITERATOR_NEXT_RETURN_TYPE, null, 0, fields, null, true,
                getTypeFlags(type));
    }
}
