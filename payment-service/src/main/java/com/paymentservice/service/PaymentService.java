package com.paymentservice.service;


import com.paymentservice.dto.CreatePaymentRequest;
import com.paymentservice.dto.PaymentResponse;
import com.paymentservice.event.OrderCreatedEvent;
import com.paymentservice.exception.PaymentNotFoundException;
import com.paymentservice.mapper.PaymentMapper;
import com.paymentservice.model.Payment;
import com.paymentservice.model.PaymentStatus;
import com.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;



    PaymentResponse getPaymentById(UUID id){
        Payment payment= paymentRepository.findById(id)
                .orElseThrow(()->new PaymentNotFoundException("Payment with id: "+id+" not found"));
        return paymentMapper.toPaymentResponse(payment);
    }



    List<PaymentResponse> getAllPayments(){
        List<Payment> payments= paymentRepository.findAll();
        return paymentMapper.toPaymentResponseList(payments);
    }



    @Transactional
    PaymentResponse createPayment(CreatePaymentRequest request){
        Payment payment= paymentMapper.toPayment(request);
        Payment savedPayment= paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }



    List<PaymentResponse> getPaymentsByUserId(Long userId){
        List<Payment> payment= paymentRepository.findAllByUserId(userId);
        return paymentMapper.toPaymentResponseList(payment);
    }



    @Transactional
    void processOrderCreatedEvent(OrderCreatedEvent event){
        /*log.info("Processing OrderCreatedEvent for orderId: {}", event.getOrderId());

        Payment payment = new Payment();
        payment.setUserId(event.getUserId());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());
        payment.setStatus(PaymentStatus.COMPLETED); // можно сделать имитацию оплаты
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        log.info("Payment created for orderId: {}", event.getOrderId());*/
    } // слушатель Kafka-события


    @Transactional
    PaymentResponse cancelPayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + paymentId + " not found"));
        payment.setStatus(PaymentStatus.CANCELLED);
        Payment canceledPayment= paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(canceledPayment);
    }



    PaymentResponse retryPayment(UUID paymentId){
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + paymentId + " not found"));

        if (existingPayment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only failed payments can be retried.");
        }

        Payment retry = new Payment();
        retry.setUserId(existingPayment.getUserId());
        retry.setOrderId(existingPayment.getOrderId());
        retry.setAmount(existingPayment.getAmount());
        retry.setStatus(PaymentStatus.COMPLETED); // имитация успешного повтора
        retry.setCreatedAt(Instant.now());

        Payment saved = paymentRepository.save(retry);
        log.info("Retried payment for paymentId: {}, new paymentId: {}", paymentId, saved.getId());
        return paymentMapper.toPaymentResponse(saved);
    }





}
