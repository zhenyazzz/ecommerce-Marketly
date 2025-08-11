package com.paymentservice.service;


import com.paymentservice.dto.CreatePaymentRequest;
import com.paymentservice.dto.PaymentResponse;

import com.paymentservice.event.OrderPaymentEvent;
import com.paymentservice.exception.PaymentNotFoundException;
import com.paymentservice.mapper.PaymentMapper;
import com.paymentservice.model.Payment;
import com.paymentservice.model.PaymentStatus;
import com.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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



    public PaymentResponse getPaymentById(UUID id){
        Payment payment= paymentRepository.findById(id)
                .orElseThrow(()->new PaymentNotFoundException("Payment with id: "+id+" not found"));
        return paymentMapper.toPaymentResponse(payment);
    }



    public List<PaymentResponse> getAllPayments(){
        List<Payment> payments= paymentRepository.findAll();
        return paymentMapper.toPaymentResponseList(payments);
    }



    @Transactional
    public  PaymentResponse createPayment(CreatePaymentRequest request){
        Payment payment= paymentMapper.toPayment(request);
        Payment savedPayment= paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }



    public List<PaymentResponse> getPaymentsByUserId(Long userId){
        List<Payment> payment= paymentRepository.findAllByUserId(userId);
        return paymentMapper.toPaymentResponseList(payment);
    }



    @KafkaListener(topics = "order-payment", groupId = "payment-group")
    @Transactional
    public void processOrderCreatedEvent(OrderPaymentEvent event) {
        log.info("Processing OrderCreatedEvent for orderId: {}", event.orderId());
        try {
            Payment payment = new Payment();
            payment.setUserId(event.userId());
            payment.setOrderId(event.orderId());
            payment.setAmount(event.amount());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(Instant.now());

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment created for orderId: {}, paymentId: {}", event.orderId(), savedPayment.getId());
        } catch (Exception e) {
            log.error("Failed to process OrderCreatedEvent for orderId: {}, error: {}", event.orderId(), e.getMessage());
            throw e; // Или обработайте иначе, например, отправьте событие об ошибке
        }
    }


    @Transactional
    public PaymentResponse cancelPayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + paymentId + " not found"));
        payment.setStatus(PaymentStatus.CANCELLED);
        Payment canceledPayment= paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(canceledPayment);
    }



    @Transactional
    public PaymentResponse retryPayment(UUID paymentId) {
        // Поиск существующего платежа
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + paymentId + " not found"));

        // Проверка статуса платежа
        if (existingPayment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only failed payments can be retried.");
        }

        // Создание новой записи для повтора оплаты
        Payment retryPayment = new Payment();
        retryPayment.setUserId(existingPayment.getUserId());
        retryPayment.setOrderId(existingPayment.getOrderId());
        retryPayment.setAmount(existingPayment.getAmount());
        retryPayment.setStatus(PaymentStatus.COMPLETED); // Имітація успішної оплати
        retryPayment.setCreatedAt(Instant.now());
       // Додайте, якщо поле існує

        // Сохранение новой записи
        Payment savedPayment = paymentRepository.save(retryPayment);
        log.info("Retried payment for paymentId: {}, new paymentId: {}", paymentId, savedPayment.getId());

        // Возвращаем ответ с использованием маппера
        return paymentMapper.toPaymentResponse(savedPayment);
    }





}
