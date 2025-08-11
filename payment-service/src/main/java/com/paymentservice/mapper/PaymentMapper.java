package com.paymentservice.mapper;

import com.paymentservice.dto.CreatePaymentRequest;
import com.paymentservice.dto.PaymentResponse;
import com.paymentservice.model.Payment;
import com.paymentservice.model.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = {PaymentStatus.class})
public interface PaymentMapper {

    @Mapping(target = "status",expression = "java(PaymentStatus.CREATED)")
    Payment toPayment(CreatePaymentRequest createPaymentRequest);

    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);

}
