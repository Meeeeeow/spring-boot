package com.example.demo.configuration;

import java.time.LocalDateTime;
import java.util.EnumSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import com.example.demo.enums.PaymentEvent;
import com.example.demo.enums.PaymentStatus;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PaymentStatus, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentStatus, PaymentEvent> states) throws Exception {
        states
            .withStates()
                .initial(PaymentStatus.INITIALIZED)
                .states(EnumSet.allOf(PaymentStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentStatus, PaymentEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(PaymentStatus.INITIALIZED).target(PaymentStatus.PROCESSING)
                .event(PaymentEvent.VALIDATE_PAYMENT_REQUEST)
                .and()      
                .withExternal()
                .source(PaymentStatus.PROCESSING).target(PaymentStatus.COMPLETED)
                .event(PaymentEvent.PROCESS_PAYMENT_REQUEST)
                .guard(guard1())
                .and()
            .withExternal()
                .source(PaymentStatus.PROCESSING).target(PaymentStatus.FAILED)
                .event(PaymentEvent.PROCESS_PAYMENT_REQUEST)
                .guard(guard2())
                .and()
            .withExternal()
              	.source(PaymentStatus.PROCESSING).target(PaymentStatus.TIMEDOUT)
              	.event(PaymentEvent.PROCESS_TIMEDOUT_RESPONSE)
              	.and()
            .withExternal()
              	.source(PaymentStatus.PROCESSING).target(PaymentStatus.UNKNOWN)
              	.event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE)
              	.and()
            .withExternal()
              	.source(PaymentStatus.UNKNOWN_AUTOMATIC_DISPUTED).target(PaymentStatus.MANUALLY_DISPUTED)
              	.event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_MANUAL_RESOLUTION)
              	.and()
            .withExternal()
              	.source(PaymentStatus.MANUALLY_DISPUTED).target(PaymentStatus.COMPLETED)
              	.event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_MANUAL_RESOLUTION_SUCCESSFUL_RESPONSE)
              	.and()
            .withExternal()
              	.source(PaymentStatus.MANUALLY_DISPUTED).target(PaymentStatus.FAILED)
              	.event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_MANUAL_RESOLUTION_FAILED_RESPONSE)
              	.and()
            .withExternal()
              	.source(PaymentStatus.UNKNOWN).target(PaymentStatus.AUTOMATIC_DISPUTED)
              	.event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION)
              	.and()
            .withExternal()
                .source(PaymentStatus.AUTOMATIC_DISPUTED).target(PaymentStatus.COMPLETED)
                .event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION_SUCCESSFUL_RESPONSE)
                .and()
            .withExternal()
                .source(PaymentStatus.AUTOMATIC_DISPUTED).target(PaymentStatus.FAILED)
                .event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION_FAILED_RESPONSE)
                .and()
            .withExternal()
                .source(PaymentStatus.AUTOMATIC_DISPUTED).target(PaymentStatus.UNKNOWN_AUTOMATIC_DISPUTED)
                .event(PaymentEvent.PROCESS_UNKNOWN_RESPONSE_AUTOMATIC_RESOLUTION_UNKNOWN_RESPONSE)
                .and()
            .withExternal()
                .source(PaymentStatus.TIMEDOUT).target(PaymentStatus.FAILED)
                .event(PaymentEvent.PROCESS_TIMEDOUT_FAILED_RESPONSE)
                .and()             
            .withExternal()
                .source(PaymentStatus.PROCESSING).target(PaymentStatus.FAILED)
                .event(PaymentEvent.PROCESS_FAILED_RESPONSE);
    }
    @Bean
    public Guard<PaymentStatus, PaymentEvent> guard1() {
        return context -> {
            LocalDateTime now = LocalDateTime.now();
            System.out.println("I am from odd " + now);
            System.out.println( now.getSecond() % 2 != 0);
            return now.getSecond() % 2 != 0;
        };
    }

    @Bean
    public Guard<PaymentStatus, PaymentEvent> guard2() {
        return context -> {
            LocalDateTime now = LocalDateTime.now();
            System.out.println("I am fron even "  + now);
            System.out.println(now.getSecond() % 2 != 0);
            return now.getSecond() % 2 == 0;
        };
    }

}