package com.ark.retailpulse.service.order;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String KEY_ID;
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Value("${razorpay.secret.key}")
    private String SECRET_KEY;


    private RazorpayClient razorpayClient;
    /**
     * Lazily initializes and returns the RazorpayClient instance.
     *
     * @return RazorpayClient
     * @throws RazorpayException if initialization fails
     */
    private RazorpayClient getRazorpayClient() throws RazorpayException {
        if (razorpayClient == null) {
            razorpayClient = new RazorpayClient(KEY_ID, SECRET_KEY);
        }
        return razorpayClient;
    }
    /**
     * Creates a Razorpay order with the specified amount and receipt.
     *
     * @param amount  the order amount
     * @param receipt the receipt ID
     * @return Razorpay Order object
     * @throws RazorpayException if the order creation fails
     */
    public com.razorpay.Order createRazorpayOrder(BigDecimal amount, String receipt) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // // Convert amount to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1);
        logger.info("Payment request: " + orderRequest.toString());

        try {
            return getRazorpayClient().orders.create(orderRequest);
        } catch (RazorpayException e) {
            logger.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new RazorpayException("Failed to create Razorpay order", e);
        }
    }

    /**
     * Verifies the payment signature using Razorpay's Utils.
     *
     * @param orderId   the order ID
     * @param paymentId the payment ID
     * @param signature the payment signature
     * @return true if the signature is verified
     * @throws RazorpayException if verification fails
     */

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        JSONObject params = new JSONObject();
        params.put("razorpay_order_id", orderId);
        params.put("razorpay_payment_id", paymentId);
        params.put("razorpay_signature", signature);

        try {
            return Utils.verifyPaymentSignature(params, SECRET_KEY);
        } catch (RazorpayException e) {
            throw new RuntimeException("Payment verification failed", e);
        }
    }
}
