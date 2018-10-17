/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.quartz.utils.FindbugsSuppressWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto generated source generation class for java.
 */
public class SourceGen {
    private static final String TAB = "    ";
    private int l = 0;
    private Map<String, JsonObject> anonTypes = new HashMap<>();

    public SourceGen(int l) {
        this.l = l;
    }

    // auto gen start
    // auto-gen-code
    // auto gen end


    @FindbugsSuppressWarnings
    public String getSourceOf(JsonObject node, boolean pretty, boolean replaceLambda) {
        if (node == null) {
            return "";
        }

        SourceGenParams sourceGenParams = new SourceGenParams();
        sourceGenParams.setI(0);

        JsonArray wsArray = node.getAsJsonArray("ws");
        JsonArray ws = new JsonArray();

        if (wsArray != null) {
            for (JsonElement wsObj : wsArray) {
                ws.add(wsObj.getAsJsonObject().get("ws"));
            }
        }

        sourceGenParams.setWs(ws);

        sourceGenParams.setShouldIndent(pretty || !(ws != null && ws.size() > 0));

        if (replaceLambda && node.get("kind").getAsString().equals("Lambda")) {
            return "$ function LAMBDA $";
        }

        switch (node.get("kind").getAsString()) {
            case "CompilationUnit":
                return join(node.getAsJsonArray("topLevelNodes"), pretty, replaceLambda,
                        "", null, false, sourceGenParams) +
                        w("", sourceGenParams);
            case "MarkdownDocumentation":
                JsonArray markDownWS = node.getAsJsonArray("ws");
                StringBuilder docString = new StringBuilder();
                for (int i = 0; i < markDownWS.size(); i++) {
                    JsonObject docWS = markDownWS.get(i).getAsJsonObject();

                    docString.append(docWS.get("ws").getAsString())
                            .append(docWS.get("text").getAsString());

                    docString.append(node.getAsJsonArray("documentationLines")
                            .get(i).getAsJsonObject().get("text").getAsString());
                }

                if (node.has("parameters")) {
                    JsonArray parameters = node.getAsJsonArray("parameters");
                    for (int i = 0; i < parameters.size(); i++) {
                        JsonArray parameterWS = parameters.get(i).getAsJsonObject().getAsJsonArray("ws");
                        for (int j = 0; j < parameterWS.size(); j++) {
                            docString.append(parameterWS.get(j).getAsJsonObject().get("ws").getAsString())
                                    .append(parameterWS.get(j).getAsJsonObject().get("text").getAsString());
                        }
                    }
                }

                if (node.has("returnParameter")) {
                    JsonArray returnParamWS = node.getAsJsonObject("returnParameter").getAsJsonArray("ws");
                    for (int i = 0; i < returnParamWS.size(); i++) {
                        docString.append(returnParamWS.get(i).getAsJsonObject().get("ws").getAsString())
                                .append(returnParamWS.get(i).getAsJsonObject().get("text").getAsString());
                    }
                }

                return docString.toString();
            case "TypeDefinition":
                String typeDefString = "";
                if (node.has("ws")) {
                    List<JsonObject> wsCollection = new ArrayList<>();
                    List<JsonObject> mergedWS = new ArrayList<>();
                    collectWSFromNode(node, wsCollection);

                    Collections.sort(wsCollection, Comparator.comparingInt(a -> a.get("i").getAsInt()));

                    JsonObject prevWS = null;
                    for (int i = 0; i < wsCollection.size(); i++) {
                        if (prevWS == null) {
                            prevWS = wsCollection.get(i);
                            mergedWS.add(prevWS);
                        } else if (prevWS.get("i").getAsInt() != wsCollection.get(i).get("i").getAsInt()) {
                            mergedWS.add(wsCollection.get(i));
                            prevWS = wsCollection.get(i);
                        }
                    }

                    for (JsonObject wsItem : mergedWS) {
                        typeDefString += wsItem.get("ws").getAsString() + wsItem.get("text").getAsString();
                    }
                }

                return typeDefString;

            /* eslint-disable max-len */
            // auto gen start
            // auto-gen-code
            // auto gen end
            /* eslint-enable max-len */
            default:
                return "";
        }
    }

    @FindbugsSuppressWarnings
    public String w(String defaultWS, SourceGenParams sourceGenParams) {
        JsonArray ws = sourceGenParams.getWs();
        int i = sourceGenParams.getI();

        if (ws.size() > 0 &&
                (ws.size() >= (i + 1))) {
            String wsI = ws.get(i).getAsString();
            sourceGenParams.setI(i + 1);
            // Check if the whitespace have comments
            boolean hasComment = (wsI != null) && wsI.trim().length() > 0;
            if (hasComment || (!sourceGenParams.isShouldIndent() && wsI != null)) {
                return wsI;
            }
        }
        return defaultWS;
    }

    @FindbugsSuppressWarnings
    public String a(String afterWS, boolean shouldIndent) {
        if (shouldIndent) {
            return afterWS;
        }
        return "";
    }

    @FindbugsSuppressWarnings
    private String indent() {
        ++l;
        return "";
    }

    @FindbugsSuppressWarnings
    private String outdent(JsonObject node, boolean shouldIndent) {
        --l;
        if (shouldIndent) {
            if (node.has("documentationText")) {
                String[] indent = node.get("documentationText").getAsString().split("\n");
                if (indent != null && indent.length > 0) {
                    if (indent[indent.length - 1].equals(repeat(TAB, l))) {
                        // if documentation text already contains the correct dent
                        return "";
                    }
                }
            }
            return "\r\n" + repeat(TAB, l);
        }
        return "";
    }

    @FindbugsSuppressWarnings
    private String dent(boolean shouldIndent) {
        if (shouldIndent) {
            return "\r\n" + repeat(TAB, l);
        }
        return "";
    }

    @FindbugsSuppressWarnings
    private String repeat(String tab, int l) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < l; j++) {
            result.append(tab);
        }
        return result.toString();
    }

    @FindbugsSuppressWarnings
    public String join(JsonArray arr, boolean pretty, boolean replaceLambda,
                       String defaultWS, String separator, boolean suffixLast, SourceGenParams sourceGenParams) {
        StringBuilder str = new StringBuilder();
        for (int j = 0; j < arr.size(); j++) {
            JsonObject node = arr.get(j).getAsJsonObject();
            if (node.get("kind").getAsString().equals("Identifier")) {
                defaultWS = w(defaultWS, sourceGenParams);
                str.append(defaultWS);
            }
            str.append(getSourceOf(node, pretty, replaceLambda));
            if (separator != null && (suffixLast || j != (arr.size() - 1))) {
                defaultWS = w(defaultWS, sourceGenParams);
                str.append(defaultWS).append(separator);
            }
        }
        return str.toString();
    }

    @FindbugsSuppressWarnings
    public void collectWSFromNode(JsonObject node, List<JsonObject> wsCollection) {
        for (Map.Entry<String, JsonElement> child : node.entrySet()) {
            String childName = child.getKey();
            if (!"position".equals(childName) && !"parent".equals(childName)) {
                if (child.getValue().isJsonObject() && child.getValue().getAsJsonObject().has("kind")) {
                    collectWSFromNode(child.getValue().getAsJsonObject(), wsCollection);
                } else if (child.getValue().isJsonArray()) {
                    if ("ws".equals(childName)) {
                        for (JsonElement wsElement : child.getValue().getAsJsonArray()) {
                            wsCollection.add(wsElement.getAsJsonObject());
                        }
                    } else {
                        for (JsonElement wsElement : child.getValue().getAsJsonArray()) {
                            if (wsElement.isJsonObject() && wsElement.getAsJsonObject().has("kind")) {
                                collectWSFromNode(wsElement.getAsJsonObject(), wsCollection);
                            }
                        }
                    }
                }
            }
        }
    }

    @FindbugsSuppressWarnings
    private void modifyNode(JsonObject node, String parentKind) {
        String kind = node.get("kind").getAsString();

        if ("If".equals(kind)) {
            if (node.getAsJsonObject("elseStatement") != null) {
                node.addProperty("ladderParent", true);
            }

            if (node.has("ws") && node.getAsJsonArray("ws").size() > 1 &&
                    node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString().equals("else") &&
                    node.getAsJsonArray("ws").get(1).getAsJsonObject().get("text").getAsString().equals("if")) {
                node.addProperty("isElseIfBlock", true);
            }
        }

        if ("Transaction".equals(kind)
                && node.has("condition") && node.getAsJsonObject("condition").has("value")) {
            JsonObject retry = null;
            if (node.has("failedBody") &&
                    node.getAsJsonObject("failedBody").has("statements")) {
                for (JsonElement statement :
                        node.getAsJsonObject("failedBody").get("statements").getAsJsonArray()) {
                    if (statement.isJsonObject() && statement.getAsJsonObject().has("kind") &&
                            statement.getAsJsonObject().get("kind").getAsString().equals("retry")) {
                        retry = statement.getAsJsonObject();
                    }
                }
            }

            if (node.has("committedBody") &&
                    node.getAsJsonObject("committedBody").has("statements")) {
                for (JsonElement statement :
                        node.getAsJsonObject("committedBody").get("statements").getAsJsonArray()) {
                    if (statement.isJsonObject() && statement.getAsJsonObject().has("kind") &&
                            statement.getAsJsonObject().get("kind").getAsString().equals("retry")) {
                        retry = statement.getAsJsonObject();
                    }
                }
            }

            if (node.has("transactionBody") &&
                    node.getAsJsonObject("transactionBody").has("statements")) {
                for (JsonElement statement :
                        node.getAsJsonObject("transactionBody").get("statements").getAsJsonArray()) {
                    if (statement.isJsonObject() && statement.getAsJsonObject().has("kind") &&
                            statement.getAsJsonObject().get("kind").getAsString().equals("retry")) {
                        retry = statement.getAsJsonObject();
                    }
                }
            }

            if (retry != null) {
                retry.addProperty("count", node.getAsJsonObject("condition").get("value").getAsString());
            }
        }

        if (("XmlCommentLiteral".equals(kind) ||
                "XmlElementLiteral".equals(kind) ||
                "XmlTextLiteral".equals(kind) ||
                "XmlPiLiteral".equals(kind)) &&
                node.has("ws") &&
                node.getAsJsonArray("ws").get(0) != null &&
                node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString().contains("xml")
                && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString().contains("`")) {
            node.addProperty("root", true);
            node.addProperty("startLiteral", node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
        }

        if ("XmlElementLiteral".equals(parentKind) ||
                "XmlTextLiteral".equals(parentKind) ||
                "XmlPiLiteral".equals(parentKind)) {
            node.addProperty("inTemplateLiteral", true);
        }

        if ("XmlPiLiteral".equals(kind) && node.has("ws")) {
            JsonObject startTagWS = new JsonObject();
            startTagWS.addProperty("text", "<?");
            startTagWS.addProperty("ws", "");

            JsonObject endTagWS = new JsonObject();
            endTagWS.addProperty("text", "?>");
            endTagWS.addProperty("ws", "");

            if (node.has("root") &&
                    node.get("root").getAsBoolean() && node.getAsJsonArray("ws").size() > 1) {
                node.add("ws", addDataToArray(1, startTagWS, node.getAsJsonArray("ws")));
                node.add("ws", addDataToArray(2, endTagWS, node.getAsJsonArray("ws")));
            }

            if (!node.has("root") || !(node.has("root") && node.get("root").getAsBoolean())) {
                node.add("ws", addDataToArray(0, startTagWS, node.getAsJsonArray("ws")));
                node.add("ws",
                        addDataToArray(node.getAsJsonArray("ws").size(), endTagWS,
                                node.getAsJsonArray("ws")));
            }

            if (node.has("target") &&
                    node.getAsJsonObject("target").has("unescapedValue")) {
                JsonObject target = node.getAsJsonObject("target");
                for (int i = 0; i < target.getAsJsonArray("ws").size(); i++) {
                    if (target.getAsJsonArray("ws").get(i).getAsJsonObject().get("text").getAsString().contains("<?")
                            && target.getAsJsonArray("ws").get(i).getAsJsonObject().get("text")
                            .getAsString().contains(target.get("unescapedValue").getAsString())) {
                        target.addProperty("unescapedValue",
                                target.getAsJsonArray("ws").get(i).getAsJsonObject().get("text")
                                        .getAsString().replace("<?", ""));
                    }
                }
            }
        }

        if ("Annotation".equals(kind)
                && node.has("attachmentPoints")
                && node.getAsJsonArray("attachmentPoints").size() <= 0) {
            node.addProperty("noAttachmentPoints", true);
        }

        if ("AnnotationAttachment".equals(kind) &&
                node.getAsJsonObject("packageAlias").get("value").getAsString().equals("builtin")) {
            node.addProperty("builtin", true);
        }

        if ("Identifier".equals(kind)) {
            if (node.has("literal") && node.get("literal").getAsBoolean()) {
                node.addProperty("valueWithBar", "^\"" + node.get("value").getAsString() + "\"");
            } else {
                node.addProperty("valueWithBar", node.get("value").getAsString());
            }
        }

        if ("Import".equals(kind)) {
            if (node.getAsJsonObject("alias") != null
                    && node.getAsJsonObject("alias").get("value") != null
                    && node.getAsJsonArray("packageName") != null
                    && node.getAsJsonArray("packageName").size() != 0
                    && !node.getAsJsonObject("alias").get("value").getAsString()
                    .equals(node.getAsJsonArray("packageName").get(node
                            .getAsJsonArray("packageName").size() - 1).getAsJsonObject()
                            .get("value").getAsString())) {
                node.addProperty("userDefinedAlias", true);
            }

            if ((node.getAsJsonArray("packageName") != null
                    && node.getAsJsonArray("packageName").size() == 2
                    && node.getAsJsonArray("packageName").get(0).getAsJsonObject().get("value").getAsString()
                    .equals("transactions")
                    && node.getAsJsonArray("packageName").get(1).getAsJsonObject()
                    .get("value").getAsString().equals("coordinator"))
                    || (node.getAsJsonObject("alias") != null
                    && node.getAsJsonObject("alias").get("value") != null
                    && node.getAsJsonObject("alias").get("value").getAsString().startsWith("."))) {
                node.addProperty("isInternal", true);
            }
        }

        if ("CompilationUnit".equals(parentKind) && ("Variable".equals(kind) || "Xmlns".equals(kind))) {
            node.addProperty("global", true);
        }

        if ("VariableDef".equals(kind)
                && node.getAsJsonObject("variable") != null
                && node.getAsJsonObject("variable").getAsJsonObject("typeNode") != null
                && node.getAsJsonObject("variable").getAsJsonObject("typeNode")
                .get("kind").getAsString().equals("EndpointType")) {
            node.getAsJsonObject("variable").addProperty("endpoint", true);
            node.addProperty("endpoint", true);
        }

        if ("Variable".equals(kind)) {
            if ("ObjectType".equals(parentKind)) {
                node.addProperty("inObject", true);
            }

            if (node.has("typeNode")
                    && node.getAsJsonObject("typeNode").has("isAnonType")
                    && node.getAsJsonObject("typeNode").get("isAnonType").getAsBoolean()) {
                node.addProperty("isAnonType", true);
            }

            if (node.has("initialExpression")) {
                node.getAsJsonObject("initialExpression").addProperty("isExpression", true);

                if (node.getAsJsonObject("initialExpression").has("async") &&
                        node.getAsJsonObject("initialExpression").get("async").getAsBoolean()
                        && node.has("ws")) {
                    JsonArray ws = node.getAsJsonArray("ws");
                    for (int i = 0; i < ws.size(); i++) {
                        if (ws.get(i).getAsJsonObject().get("text").getAsString().equals("start")
                                && node.getAsJsonObject("initialExpression").has("ws")) {
                            node.getAsJsonObject("initialExpression").add("ws",
                                    addDataToArray(0, node.getAsJsonArray("ws").get(i),
                                            node.getAsJsonObject("initialExpression")
                                                    .getAsJsonArray("ws")));
                            node.getAsJsonArray("ws").remove(i);
                        }
                    }
                }
            }

            if (node.has("typeNode")
                    && node.getAsJsonObject("typeNode").has("nullable")
                    && node.getAsJsonObject("typeNode").get("nullable").getAsBoolean()
                    && node.getAsJsonObject("typeNode").has("ws")) {
                JsonArray ws = node.getAsJsonObject("typeNode").get("ws").getAsJsonArray();
                for (int i = 0; i < ws.size(); i++) {
                    if (ws.get(i).getAsJsonObject().get("text").getAsString().equals("?")) {
                        node.getAsJsonObject("typeNode").addProperty("nullableOperatorAvailable", true);
                        break;
                    }
                }
            }

            if (node.has("typeNode")
                    && node.getAsJsonObject("typeNode").has("ws")
                    && !node.has("ws")) {
                node.addProperty("noVisibleName", true);
            }

            if (node.has("ws")) {
                JsonArray ws = node.getAsJsonArray("ws");
                for (int i = 0; i < ws.size(); i++) {
                    if (ws.get(i).getAsJsonObject().get("text").getAsString().equals(";")) {
                        node.addProperty("endWithSemicolon", true);
                    }

                    if (ws.get(i).getAsJsonObject().get("text").getAsString().equals(",")) {
                        node.addProperty("endWithComma", true);
                    }
                }
            }
        }

        if ("Service".equals(kind)) {
            if (!node.has("serviceTypeStruct")) {
                node.addProperty("isServiceTypeUnavailable", true);
            }

            if (!node.has("anonymousEndpointBind")
                    && node.has("boundEndpoints")
                    && node.getAsJsonArray("boundEndpoints").size() <= 0) {
                boolean bindAvailable = false;
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("bind")) {
                        bindAvailable = true;
                        break;
                    }
                }

                if (!bindAvailable) {
                    node.addProperty("bindNotAvailable", true);
                }
            }
        }

        if ("Resource".equals(kind)
                && node.has("parameters")
                && node.getAsJsonArray("parameters").size() > 0
                && node.getAsJsonArray("parameters").get(0).getAsJsonObject().has("ws")) {
            for (JsonElement ws : node.getAsJsonArray("parameters").get(0).getAsJsonObject().getAsJsonArray("ws")) {
                if (ws.getAsJsonObject().get("text").getAsString().equals("endpoint")) {
                    JsonObject endpointParam = node.getAsJsonArray("parameters").get(0).getAsJsonObject();
                    String valueWithBar = endpointParam.get("name").getAsJsonObject().has("valueWithBar")
                            ? endpointParam.get("name").getAsJsonObject().get("valueWithBar").getAsString()
                            : endpointParam.get("name").getAsJsonObject().get("value").getAsString();

                    endpointParam.addProperty("serviceEndpoint", true);
                    endpointParam.get("name").getAsJsonObject().addProperty("value",
                            endpointParam.get("name").getAsJsonObject().get("value").getAsString().replace("$", ""));
                    endpointParam.get("name").getAsJsonObject().addProperty("valueWithBar",
                            valueWithBar.replace("$", ""));
                    break;
                }
            }
        }

        if ("ForkJoin".equals(kind)) {
            if (node.getAsJsonObject("joinBody") != null) {
                node.getAsJsonObject("joinBody").add("position",
                        node.getAsJsonObject("joinResultVar").getAsJsonObject("position"));
            }

            if (node.getAsJsonObject("timeoutBody") != null) {
                node.getAsJsonObject("timeoutBody").add("position",
                        node.getAsJsonObject("timeOutExpression").getAsJsonObject("position"));
            }
        }

        // Check if sorrounded by curlies
        if (("MatchPatternClause".equals(kind) || "MatchExpressionPatternClause".equals(kind))
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 2) {
            node.addProperty("withCurlies", true);
        }

        // Check if sorrounded by parantheses
        if ("ValueType".equals(kind)) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 2) {
                node.addProperty("withParantheses", true);
            }

            if (node.has("typeKind") && node.get("typeKind").getAsString().equals("nil") && node.has("ws")) {
                node.addProperty("emptyParantheses", true);
            }

            if (node.has("nullable") && node.get("nullable").getAsBoolean() && node.has("ws")) {
                for (int i = 0; i < node.get("ws").getAsJsonArray().size(); i++) {
                    if (node.get("ws").getAsJsonArray().get(i)
                            .getAsJsonObject().get("text").getAsString().equals("?")) {
                        node.addProperty("nullableOperatorAvailable", true);
                        break;
                    }
                }
            }
        }

        if ("UnionTypeNode".equals(kind) && node.has("ws")) {
            if (node.getAsJsonArray("ws").size() > 2) {
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("(")) {
                        node.addProperty("withParantheses", true);
                        break;
                    }
                }
            }

            JsonArray memberTypeNodes = node.get("memberTypeNodes").getAsJsonArray();
            for (int i = 0; i < memberTypeNodes.size(); i++) {
                if (memberTypeNodes.get(i).getAsJsonObject().has("nullable")
                        && memberTypeNodes.get(i).getAsJsonObject().get("nullable").getAsBoolean()) {
                    for (JsonElement ws : node.getAsJsonArray("ws")) {
                        if (ws.getAsJsonObject().get("text").getAsString().equals("?")) {
                            memberTypeNodes.get(i).getAsJsonObject()
                                    .addProperty("nullableOperatorAvailable", true);
                            break;
                        }
                    }
                }
            }
        }

        if ("Function".equals(kind)) {
            if (node.has("returnTypeNode")
                    && node.getAsJsonObject("returnTypeNode").has("ws")
                    && node.getAsJsonObject("returnTypeNode").getAsJsonArray("ws").size() > 0) {
                node.addProperty("hasReturns", true);
            }

            if (node.has("defaultableParameters")) {
                JsonArray defaultableParameters = node.getAsJsonArray("defaultableParameters");
                for (int i = 0; i < defaultableParameters.size(); i++) {
                    defaultableParameters.get(i).getAsJsonObject().addProperty("defaultable", true);
                    defaultableParameters.get(i).getAsJsonObject().getAsJsonObject("variable")
                            .addProperty("defaultable", true);
                }
            }

            // Sort and add all the parameters.
            JsonArray allParamsTemp = node.getAsJsonArray("parameters");
            allParamsTemp.addAll(node.getAsJsonArray("defaultableParameters"));
            List<JsonElement> allParamElements = new ArrayList<>();
            allParamsTemp.forEach(jsonElement -> {
                allParamElements.add(jsonElement);
            });

            Collections.sort(allParamElements, (a, b) -> {
                int comparator = 0;
                comparator = (((a.getAsJsonObject().getAsJsonObject("position").get("endColumn").getAsInt() >
                        b.getAsJsonObject().getAsJsonObject("position").get("startColumn").getAsInt())
                        && (a.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt() ==
                        b.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt())) ||
                        (a.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt() >
                                b.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt())) ? 1 : -1;
                return comparator;
            });

            JsonArray allParams = new JsonArray();

            allParamElements.forEach(jsonElement -> {
                allParams.add(jsonElement);
            });

            node.add("allParams", allParams);

            if (node.has("receiver")
                    && !node.getAsJsonObject("receiver").has("ws")) {
                if (node.getAsJsonObject("receiver").has("typeNode")
                        && node.getAsJsonObject("receiver").getAsJsonObject("typeNode").has("ws")
                        && node.getAsJsonObject("receiver")
                        .getAsJsonObject("typeNode").getAsJsonArray("ws").size() > 0) {
                    for (JsonElement ws : node.get("ws").getAsJsonArray()) {
                        if (ws.getAsJsonObject().get("text").getAsString().equals("::")) {
                            node.addProperty("objectOuterFunction", true);
                            if (node.getAsJsonObject("receiver")
                                    .getAsJsonObject("typeNode").getAsJsonArray("ws").get(0)
                                    .getAsJsonObject().get("text").getAsString().equals("function")) {
                                node.getAsJsonObject("receiver")
                                        .getAsJsonObject("typeNode").getAsJsonArray("ws").remove(0);
                            }
                            node.add("objectOuterFunctionTypeName", node.getAsJsonObject("receiver")
                                    .getAsJsonObject("typeNode").getAsJsonObject("typeName"));
                            break;
                        }
                    }
                } else {
                    node.addProperty("noVisibleReceiver", true);
                }
            }

            if (node.has("restParameters")
                    && (node.has("allParams")
                    && node.getAsJsonArray("allParams").size() > 0)) {
                node.addProperty("hasRestParams", true);
            }

            if (node.has("restParameters")
                    && node.getAsJsonObject("restParameters").has("typeNode")) {
                node.getAsJsonObject("restParameters").getAsJsonObject("typeNode").addProperty("isRestParam", true);
            }
        }

        if ("TypeDefinition".equals(kind) && node.has("typeNode")) {
            if (!node.has("ws")) {
                node.addProperty("notVisible", true);
            }

            if (node.has("name") &&
                    node.getAsJsonObject("name").get("value").getAsString().startsWith("$anonType$")) {
                this.anonTypes.put(node.getAsJsonObject("name").get("value").getAsString(),
                        node.getAsJsonObject("typeNode"));
            }

            if (node.getAsJsonObject("typeNode").get("kind").getAsString().equals("ObjectType")) {
                node.addProperty("isObjectType", true);
                if (node.has("ws")) {
                    JsonArray typeDefWS = node.getAsJsonArray("ws");
                    for (int i = 0; i < typeDefWS.size(); i++) {
                        if (typeDefWS.get(i).getAsJsonObject().get("text").getAsString().equals("abstract")) {
                            node.addProperty("isAbstractKeywordAvailable", true);
                        }
                    }
                }
            }

            if (node.getAsJsonObject("typeNode").get("kind").getAsString().equals("RecordType")) {
                node.addProperty("isRecordType", true);
                if (node.has("ws")) {
                    for (int i = 0; i < node.getAsJsonArray("ws").size(); i++) {
                        if (node.getAsJsonArray("ws").get(i)
                                .getAsJsonObject().get("text").getAsString().equals("record")) {
                            node.addProperty("isRecordKeywordAvailable", true);
                        }
                    }
                }
            }
        }

        if ("ObjectType".equals(kind) && node.has("initFunction")) {
            if (!node.getAsJsonObject("initFunction").has("ws")) {
                node.getAsJsonObject("initFunction").addProperty("defaultConstructor", true);
            } else {
                node.getAsJsonObject("initFunction").addProperty("isConstructor", true);
            }
        }

        if ("RecordType".equals(kind) && node.has("restFieldType")) {
            node.addProperty("isRestFieldAvailable", true);
        }

        if ("TypeInitExpr".equals(kind)) {
            if (node.getAsJsonArray("expressions").size() <= 0) {
                node.addProperty("noExpressionAvailable", true);
            }

            if (node.has("ws")) {
                for (int i = 0; i < node.getAsJsonArray("ws").size(); i++) {
                    if (node.getAsJsonArray("ws").get(i).getAsJsonObject().get("text").getAsString()
                            .equals("(")) {
                        node.addProperty("hasParantheses", true);
                        break;
                    }
                }
            }

            if (!node.has("type")) {
                node.addProperty("noTypeAttached", true);
            } else {
                node.add("typeName", node.getAsJsonObject("type").get("typeName"));
            }
        }

        if ("Return".equals(kind)
                && node.has("expression")
                && node.getAsJsonObject("expression").get("kind").getAsString().equals("Literal")) {
            if (node.getAsJsonObject("expression").get("value").getAsString().equals("()")) {
                node.addProperty("noExpressionAvailable", true);
            }

            if (node.getAsJsonObject("expression").get("value").getAsString().equals("null")) {
                node.getAsJsonObject("expression").addProperty("emptyParantheses", true);
            }
        }

        if ("Documentation".equals(kind)) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 1) {
                node.addProperty("startDoc",
                        node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
            }

            for (int j = 0; j < node.getAsJsonArray("attributes").size(); j++) {
                JsonObject attribute = node.getAsJsonArray("attributes").get(j).getAsJsonObject();
                if (attribute.has("ws")) {
                    for (int i = 0; i < attribute.getAsJsonArray("ws").size(); i++) {
                        String text = attribute.getAsJsonArray("ws").get(i).getAsJsonObject()
                                .get("text").getAsString();
                        if (text.contains("{{") && !attribute.has("paramType")) {
                            int lastIndex = text.lastIndexOf("{{");
                            String paramType = text.substring(0, lastIndex);
                            String startCurl = text.substring(lastIndex, text.length());
                            attribute.getAsJsonArray("ws").get(i).getAsJsonObject()
                                    .addProperty("text", paramType);
                            attribute.addProperty("paramType", paramType);

                            JsonObject ws = new JsonObject();
                            ws.addProperty("text", startCurl);
                            ws.addProperty("ws", "");
                            ws.addProperty("static", false);
                            attribute.add("ws", addDataToArray(++i, ws, attribute.getAsJsonArray("ws")));
                        }
                    }
                }
            }
        }

        // Tag rest variable nodes
        if (("Function".equals(kind) || "Resource".equals(kind)) && node.has("restParameters")) {
            node.getAsJsonObject("restParameters").addProperty("rest", true);
        }

        if ("PostIncrement".equals(kind)) {
            node.addProperty("operator",
                    (node.get("operatorKind").getAsString() + node.get("operatorKind").getAsString()));
        }

        if ("SelectExpression".equals(kind) && node.has("identifier")) {
            node.addProperty("identifierAvailable", true);
        }

        if ("StreamAction".equals(kind) && node.has("invokableBody") &&
                node.getAsJsonObject("invokableBody").has("functionNode")) {
            node.getAsJsonObject("invokableBody").getAsJsonObject("functionNode")
                    .addProperty("isStreamAction", true);
        }

        if ("StreamingInput".equals(kind) && node.has("alias")) {
            node.addProperty("aliasAvailable", true);
        }

        if ("IntRangeExpr".equals(kind)
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0) {
            if (node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("[")) {
                node.addProperty("isWrappedWithBracket", true);
            } else if (node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("(")) {
                node.addProperty("isWrappedWithParenthesis", true);
            }
        }

        if ("FunctionType".equals(kind)) {
            if (node.has("returnTypeNode")
                    && node.getAsJsonObject("returnTypeNode").has("ws")) {
                node.addProperty("hasReturn", true);
            }

            if (node.has("ws")
                    && node.getAsJsonArray("ws").size() > 0
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("(")) {
                node.addProperty("withParantheses", true);
            }
        }

        if ("Literal".equals(kind) && !"StringTemplateLiteral".equals(parentKind)) {
            if (node.has("ws")
                    && node.getAsJsonArray("ws").size() == 1
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().has("text")) {
                node.addProperty("value",
                        node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
            }

            if ((node.get("value").getAsString().equals("nil")
                    || node.get("value").getAsString().equals("null"))
                    && node.has("ws")
                    && node.getAsJsonArray("ws").size() < 3
                    && node.getAsJsonArray("ws").get(0) != null
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("(")) {
                node.addProperty("emptyParantheses", true);
            }
        }

        if ("Foreach".equals(kind) && node.has("ws")) {
            for (JsonElement ws : node.getAsJsonArray("ws")) {
                if (ws.getAsJsonObject().get("text").getAsString().equals("(")) {
                    node.addProperty("withParantheses", true);
                    break;
                }
            }
        }

        if ("Endpoint".equals(kind) && node.has("ws")) {
            for (JsonElement ws : node.getAsJsonArray("ws")) {
                if (ws.getAsJsonObject().get("text").getAsString().equals("=")) {
                    node.addProperty("isConfigAssignment", true);
                    break;
                }
            }
        }

        if ("UserDefinedType".equals(kind)) {
            if (node.has("ws") && node.has("nullable") && node.get("nullable").getAsBoolean()) {
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("?")) {
                        node.addProperty("nullableOperatorAvailable", true);
                        break;
                    }
                }
            }

            if (node.has("typeName") && node.getAsJsonObject("typeName").has("value")
                    && anonTypes.containsKey(node.getAsJsonObject("typeName").get("value").getAsString())) {
                node.addProperty("isAnonType", true);
                node.add("anonType",
                        anonTypes.get(node.getAsJsonObject("typeName").get("value").getAsString()));
                anonTypes.remove(node.getAsJsonObject("typeName").get("value").getAsString());
            }
        }

        if ("ArrayType".equals(kind)
                && node.has("dimensions")
                && node.get("dimensions").getAsInt() > 0
                && node.has("ws")) {
            String dimensionAsString = "";
            JsonObject startingBracket = null;
            JsonObject endingBracket = null;
            StringBuilder content = new StringBuilder();
            JsonArray ws = node.getAsJsonArray("ws");

            for (int j = 0; j < ws.size(); j++) {
                if (ws.get(j).getAsJsonObject().get("text").getAsString().equals("[")) {
                    startingBracket = ws.get(j).getAsJsonObject();
                } else if (ws.get(j).getAsJsonObject().get("text").getAsString().equals("]")) {
                    endingBracket = ws.get(j).getAsJsonObject();

                    dimensionAsString += startingBracket.get("text").getAsString() + content.toString()
                            + endingBracket.get("ws").getAsString()
                            + endingBracket.get("text").getAsString();

                    startingBracket = null;
                    endingBracket = null;
                    content = new StringBuilder();
                } else if (startingBracket != null) {
                    content.append(ws.get(j).getAsJsonObject().get("ws").getAsString())
                            .append(ws.get(j).getAsJsonObject().get("text").getAsString());
                }
            }

            node.addProperty("dimensionAsString", dimensionAsString);
        }

        if ("Block".equals(kind)
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0
                && node.getAsJsonArray("ws").get(0)
                .getAsJsonObject().get("text").getAsString().equals("else")) {
            node.addProperty("isElseBlock", true);
        }

        if ("FieldBasedAccessExpr".equals(kind)
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0
                && node.getAsJsonArray("ws").get(0)
                .getAsJsonObject().get("text").getAsString().equals("!")) {
            node.addProperty("errorLifting", true);
        }

        if ("StringTemplateLiteral".equals(kind) && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0
                && node.getAsJsonArray("ws").get(0).getAsJsonObject()
                .get("text").getAsString().contains("string")
                && node.getAsJsonArray("ws").get(0).getAsJsonObject()
                .get("text").getAsString().contains("`")) {
            node.addProperty("startTemplate",
                    node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
            literalWSAssignForTemplates(1, 2, node.getAsJsonArray("expressions"),
                    node.getAsJsonArray("ws"), 2);
        }

        if ("XmlCommentLiteral".equals(kind) && node.has("ws")) {
            int length = node.getAsJsonArray("ws").size();
            for (int i = 0; i < length; i++) {
                if (node.getAsJsonArray("ws").get(i).getAsJsonObject()
                        .get("text").getAsString().contains("-->")
                        && node.getAsJsonArray("ws").get(i).getAsJsonObject()
                        .get("text").getAsString().length() > 3) {
                    JsonObject ws = new JsonObject();
                    ws.addProperty("text", "-->");
                    ws.addProperty("ws", "");

                    node.getAsJsonArray("ws").get(i).getAsJsonObject().addProperty("text",
                            node.getAsJsonArray("ws").get(i).getAsJsonObject()
                                    .get("text").getAsString().replace("-->", ""));
                    node.add("ws", addDataToArray(i + 1, ws, node.getAsJsonArray("ws")));
                    break;
                }
            }

            if (node.has("root") && node.get("root").getAsBoolean()) {
                literalWSAssignForTemplates(2, 3, node.getAsJsonArray("textFragments"),
                        node.getAsJsonArray("ws"), 4);
            } else {
                literalWSAssignForTemplates(1, 2, node.getAsJsonArray("textFragments"),
                        node.getAsJsonArray("ws"), 2);
            }
        }

        if ("ArrowExpr".equals(kind)) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 0
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("(")) {
                node.addProperty("hasParantheses", true);
            }

            if (node.has("parameters")) {
                JsonArray parameters = node.getAsJsonArray("parameters");
                for (int i = 0; i < parameters.size(); i++) {
                    JsonObject parameter = parameters.get(i).getAsJsonObject();
                    parameter.addProperty("arrowExprParam", true);
                }
            }
        }

        if ("PatternStreamingInput".equals(kind)
                && node.has("ws")
                && node.getAsJsonArray("ws").get(0)
                .getAsJsonObject().get("text").getAsString().equals("(")) {
            node.addProperty("enclosedInParenthesis", true);
        }

        if ("SelectClause".equals(kind) && !node.has("ws")) {
            node.addProperty("notVisible", true);
        }

        if ("OrderByVariable".equals(kind)) {
            if (!node.has("ws")) {
                node.addProperty("noVisibleType", true);
            } else {
                node.addProperty("typeString", node.getAsJsonArray("ws")
                        .get(0).getAsJsonObject().get("text").getAsString());
            }
        }

        if ("Deprecated".equals(kind)
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0) {
            node.addProperty("deprecatedStart",
                    node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
        }
    }

    @FindbugsSuppressWarnings
    void literalWSAssignForTemplates(int currentWs, int nextWs,
                                     JsonArray literals, JsonArray ws, int wsStartLocation) {
        if (literals.size() == (ws.size() - wsStartLocation)) {
            for (int i = 0; i < literals.size(); i++) {
                if (literals.get(i).getAsJsonObject().get("kind").getAsString().equals("Literal")) {
                    if (!literals.get(i).getAsJsonObject().has("ws")) {
                        literals.get(i).getAsJsonObject().add("ws", new JsonArray());
                    }

                    if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        literals.get(i).getAsJsonObject().addProperty("value",
                                ws.get(currentWs).getAsJsonObject().get("text").getAsString());
                        // TODO: use splice
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);

                    } else if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("}}")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        if (ws.get(nextWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                            literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(nextWs));
                            literals.get(i).getAsJsonObject().addProperty("value",
                                    ws.get(nextWs).getAsJsonObject().get("text").getAsString());
                            literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);
                            // TODO: use splice
                            ws.remove(nextWs);
                        }
                        // TODO: use splice
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("endTemplateLiteral", true);
                    }

                    if (i == (literals.size() - 1)) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        literals.get(i).getAsJsonObject().addProperty("value",
                                ws.get(currentWs).getAsJsonObject().get("text").getAsString());
                        literals.get(i).getAsJsonObject().addProperty("lastNodeValue", true);
                        // TODO: use splice.
                        ws.remove(currentWs);
                    }
                }
            }
        } else if ((literals.size() - 1) == (ws.size() - wsStartLocation)) {
            for (int i = 0; i < literals.size(); i++) {
                if (literals.get(i).getAsJsonObject().get("kind").getAsString().equals("Literal")) {
                    if (!literals.get(i).getAsJsonObject().has("ws")) {
                        literals.get(i).getAsJsonObject().add("ws", new JsonArray());
                    }

                    if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        literals.get(i).getAsJsonObject().addProperty("value",
                                ws.get(currentWs).getAsJsonObject().get("text").getAsString());
                        //TODO: use splice.
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);
                    } else if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("}}")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        if (ws.get(nextWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                            literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(nextWs));
                            literals.get(i).getAsJsonObject().addProperty("value",
                                    ws.get(nextWs).getAsJsonObject().get("text").getAsString());
                            literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);
                            //TODO: use splice.
                            ws.remove(nextWs);
                        }
                        //TODO: use splice.
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("endTemplateLiteral", true);
                    }
                }
            }
        }
    }

    @FindbugsSuppressWarnings
    JsonArray addDataToArray(int index, JsonElement element, JsonArray ws) {
        int length = ws.size() + 1;
        JsonArray array = new JsonArray();
        boolean added = false;

        for (int i = 0; i < length; i++) {
            if (i == index) {
                array.add(element);
                added = true;
            } else if (added) {
                array.add(ws.get(i - 1));
            } else {
                array.add(ws.get(i));
            }
        }

        return array;
    }

    @FindbugsSuppressWarnings
    public JsonObject build(JsonObject json, JsonObject parent, String parentKind) {
        String kind = json.get("kind").getAsString();
        for (Map.Entry<String, JsonElement> child : json.entrySet()) {
            if (!child.getKey().equals("position") && !child.getKey().equals("ws")) {
                if (child.getValue().isJsonObject() &&
                        child.getValue().getAsJsonObject().get("kind") != null) {
                    json.add(child.getKey(), build(child.getValue().getAsJsonObject(), json, kind));
                } else if (child.getValue().isJsonArray()) {
                    JsonArray childArray = child.getValue().getAsJsonArray();
                    for (int j = 0; j < childArray.size(); j++) {
                        JsonElement childItem = childArray.get(j);
                        if ("CompilationUnit".equals(kind)
                                && childItem.getAsJsonObject().get("kind").getAsString().equals("Function")
                                && childItem.getAsJsonObject().has("lambda")
                                && childItem.getAsJsonObject().get("lambda").getAsBoolean()) {
                            childArray.remove(j);
                            j--;
                        } else if (childItem.isJsonObject()
                                && childItem.getAsJsonObject().get("kind") != null) {
                            childItem = build(childItem.getAsJsonObject(), json, kind);
                        }
                    }
                }
            }
        }
        modifyNode(json, parentKind);
        json.add("parent", parent);
        return json;
    }

    @FindbugsSuppressWarnings
    class SourceGenParams {
        private boolean shouldIndent = false;
        private int i;
        private JsonArray ws;

        public SourceGenParams() {
            this.ws = new JsonArray();
            this.i = 0;
            this.shouldIndent = false;
        }

        public boolean isShouldIndent() {
            return shouldIndent;
        }

        public int getI() {
            return i;
        }

        public JsonArray getWs() {
            return ws;
        }

        public void setI(int i) {
            this.i = i;
        }

        public void setShouldIndent(boolean shouldIndent) {
            this.shouldIndent = shouldIndent;
        }

        public void setWs(JsonArray ws) {
            this.ws = ws;
        }
    }
}
