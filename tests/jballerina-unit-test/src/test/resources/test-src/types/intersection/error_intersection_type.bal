// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// Detail types
type Detail record {
    string x;
};

type DetailTwo record {
    string x;
    string y;
};

type DetailThree record {
    int z;
};

// Error Types
type DetailFour map<string>;

type ErrorOne error<Detail>;

type ErrorTwo error<DetailTwo>;

type ErrorThree error<DetailThree>;

type ErrorFour error<record { string z;}>;

type ErrorFive error<DetailFour>;

//ErrorIntersections
type IntersectionError ErrorOne & ErrorTwo;

type IntersectionErrorTwo ErrorOne & ErrorTwo & ErrorThree;

type IntersectionErrorThree ErrorOne & ErrorFour;

type IntersectionErrorFour ErrorOne & ErrorFive;

function testIntersectionForExistingDetail() {
    var err = IntersectionError("message", x = "x", y = "y");
    assertEquality(err.detail().x, "x");
    assertEquality(err.detail().y, "y");
}

function testIntersectionForExisitingAndNewDetail() {
    var err = IntersectionErrorTwo("message", x = "x", y = "y", z = 10);
    assertEquality(err.detail().x, "x");
    assertEquality(err.detail().y, "y");
    assertEquality(err.detail().z, 10);
}

function testIntersectionForAnonymousDetail() {
    var err = IntersectionErrorThree("message", x = "x", z = "z");
    assertEquality(err.detail().x, "x");
    assertEquality(err.detail().z, "z");
}

function testIntersectionForDetailRecordAndDetailMap() {
    var err = IntersectionErrorFour("message", x = "x", z = "z");
    assertEquality(err.detail().x, "x");
    assertEquality(err.detail()["z"], "z");
}


const ASSERTION_ERROR_REASON = "AssertionError";

function assertEquality(any|error actual, any|error expected) {
    if expected is anydata && actual is anydata && expected == actual {
        return;
    }

    if expected === actual {
        return;
    }

    panic error(ASSERTION_ERROR_REASON,
                message = "expected '" + expected.toString() + "', found '" + actual.toString () + "'");
}
