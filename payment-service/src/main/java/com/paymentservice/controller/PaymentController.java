package com.paymentservice.controller;


import com.paymentservice.dto.CreatePaymentRequest;
import com.paymentservice.dto.PaymentResponse;
import com.paymentservice.exception.PaymentNotFoundException;
import com.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RequestMapping("/api/payments")
@RestController
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        try {
            PaymentResponse response = paymentService.getPaymentById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        try {
            List<PaymentResponse> responses = paymentService.getAllPayments();
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createPayment(request);
            log.info("Payment created successfully for orderId: {}", request.orderId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Internal server error while creating payment for orderId {}: {}", request.orderId(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(@PathVariable Long userId) {
        try {
            List<PaymentResponse> responses = paymentService.getPaymentsByUserId(userId);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel/{paymentId}")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID paymentId) {
        try {
            PaymentResponse response = paymentService.cancelPayment(paymentId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/retry/{paymentId}")
    public ResponseEntity<PaymentResponse> retryPayment(@PathVariable UUID paymentId) {
        try {
            PaymentResponse response = paymentService.retryPayment(paymentId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
