#!/bin/bash

# Test script for Notification Service
# Make sure the service is running on port 8084

echo "Testing Notification Service..."

# Health check
echo "1. Testing health endpoint..."
curl -X GET http://localhost:8084/api/v1/notifications/health
echo -e "\n"

# Test sending a notification
echo "2. Testing notification sending..."
curl -X POST http://localhost:8084/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "type": "ORDER_CREATED",
    "channel": "EMAIL",
    "recipient": "test@example.com",
    "subject": "Test Order Confirmation",
    "templateName": "order-created",
    "templateData": {
      "orderId": "123e4567-e89b-12d3-a456-426614174000",
      "orderNumber": "#TEST123",
      "userName": "Test User",
      "totalAmount": "99.99",
      "orderStatus": "CREATED"
    },
    "userId": 123
  }'
echo -e "\n"

# Test sending user registration notification
echo "3. Testing user registration notification..."
curl -X POST http://localhost:8084/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "type": "USER_REGISTERED",
    "channel": "EMAIL",
    "recipient": "newuser@example.com",
    "subject": "Welcome to Marketly!",
    "templateName": "user-registered",
    "templateData": {
      "userName": "New User",
      "verificationToken": "abc123"
    },
    "userId": 456
  }'
echo -e "\n"

echo "Testing completed!" 