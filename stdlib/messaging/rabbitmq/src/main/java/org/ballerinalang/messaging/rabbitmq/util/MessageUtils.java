/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.messaging.rabbitmq.util;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import org.ballerinalang.jvm.JSONParser;
import org.ballerinalang.jvm.XMLFactory;
import org.ballerinalang.jvm.values.ArrayValue;
import org.ballerinalang.messaging.rabbitmq.RabbitMQConstants;
import org.ballerinalang.messaging.rabbitmq.RabbitMQUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Util class for RabbitMQ Message handling.
 *
 * @since 1.1.0
 */
public class MessageUtils {
    public static Object basicAck(Channel channel, int deliveryTag, boolean ackMode,
                                  boolean ackStatus, boolean multiple) {
        if (ackStatus) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.MULTIPLE_ACK_ERROR);
        } else if (ackMode) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.ACK_MODE_ERROR);
        } else {
            try {
                channel.basicAck(deliveryTag, multiple);
            } catch (IOException exception) {
                return RabbitMQUtils.returnErrorValue(RabbitMQConstants.ACK_ERROR + exception.getMessage());
            } catch (AlreadyClosedException exception) {
                return RabbitMQUtils.returnErrorValue(RabbitMQConstants.CHANNEL_CLOSED_ERROR);
            }
        }
        return null;
    }

    public static Object basicNack(Channel channel, int deliveryTag, boolean ackMode,
                                   boolean ackStatus, boolean multiple, boolean requeue) {
        if (ackStatus) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.MULTIPLE_ACK_ERROR);
        } else if (ackMode) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.ACK_MODE_ERROR);
        } else {
            try {
                channel.basicNack(deliveryTag, multiple, requeue);
            } catch (IOException exception) {
                return RabbitMQUtils.returnErrorValue(RabbitMQConstants.NACK_ERROR
                        + exception.getMessage());
            } catch (AlreadyClosedException exception) {
                return RabbitMQUtils.returnErrorValue(RabbitMQConstants.CHANNEL_CLOSED_ERROR);
            }
        }
        return null;
    }

    public static Object getTextContent(ArrayValue messageContent) {
        byte[] messageCont = messageContent.getBytes();
        try {
            return new String(messageCont, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException exception) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.TEXT_CONTENT_ERROR
                    + exception.getMessage());
        }
    }

    public static Object getFloatContent(ArrayValue messageContent) {
        try {
            return Float.parseFloat(new String(messageContent.getBytes(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException exception) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.FLOAT_CONTENT_ERROR
                    + exception.getMessage());
        }
    }

    public static Object getIntContent(ArrayValue messageContent) {
        try {
            return Integer.parseInt(new String(messageContent.getBytes(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException exception) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.INT_CONTENT_ERROR
                    + exception.getMessage());
        }
    }

    public static Object getJSONContent(ArrayValue messageContent) {

        try {
            return JSONParser.parse(new String(messageContent.getBytes(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException exception) {
            return RabbitMQUtils.returnErrorValue
                    (RabbitMQConstants.JSON_CONTENT_ERROR + exception.getMessage());
        }
    }

    public static Object getXMLContent(ArrayValue messageContent) {
        try {
            return XMLFactory.parse(new String(messageContent.getBytes(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException exception) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.XML_CONTENT_ERROR
                    + exception.getMessage());
        }
    }

    private MessageUtils() {
    }
}
