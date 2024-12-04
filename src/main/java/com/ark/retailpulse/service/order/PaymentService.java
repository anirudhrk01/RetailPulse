package com.ark.retailpulse.service.order;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String KEY_ID;

    @Value("${razorpay.secret.key}")
    private String SECRET_KEY;


    private RazorpayClient razorpayClient;

    private RazorpayClient getRazorpayClient() throws RazorpayException {
        if (razorpayClient == null) {
            razorpayClient = new RazorpayClient(KEY_ID, SECRET_KEY);
        }
        return razorpayClient;
    }

    public com.razorpay.Order createRazorpayOrder(BigDecimal amount, String receipt) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1);
        return getRazorpayClient().orders.create(orderRequest);
    }

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
