package com.example.demo.enums;

public enum PaymentEvent {
    RECEIVE_PAYMENT_REQUEST,
    VALIDATE_PAYMENT_REQUEST,
    PROCESS_SUCCESSFUL_RESPONSE,
    PROCESS_UNKNOWN_RESPONSE,
    PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION,
    PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION_SUCCESSFUL_RESPONSE,
    PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION_FAILED_RESPONSE,
    PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION_UNKNOWN_RESPONSE,
    PROCESS_UNKNOWN_RESPONSE_MANUAL_RESOLUTION,
    PROCESS_UNKNOWN_RESPONSE_MANUAL_RESOLUTION_SUCCESSFUL_RESPONSE,
    PROCESS_UNKNOWN_RESPONSE_MANUAL_RESOLUTION_FAILED_RESPONSE,
    PROCESS_TIMEDOUT_RESPONSE,
    PROCESS_TIMEDOUT_SUCCESSFUL_RESPONSE,
    PROCESS_TIMEDOUT_FAILED_RESPONSE,
    PROCESS_FAILED_RESPONSE,
    PROCESS_PAYMENT_REQUEST
}
