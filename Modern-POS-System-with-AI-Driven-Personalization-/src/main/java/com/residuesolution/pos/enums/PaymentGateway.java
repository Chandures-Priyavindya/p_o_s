package com.residuesolution.pos.enums;

public enum PaymentGateway {
    INTERNAL,          // For cash payments, internal processing
    STRIPE,            // Stripe payment gateway
    PAYPAL,            // PayPal payment gateway
    SQUARE,            // Square payment gateway
    RAZORPAY,          // Razorpay (popular in Asia)
    AUTHORIZE_NET,     // Authorize.Net
    BRAINTREE,         // Braintree (PayPal owned)
    ADYEN,             // Adyen payment gateway
    WORLDPAY,          // Worldpay
    PAYU,              // PayU
    MOLLIE,            // Mollie
    KLARNA,            // Klarna
    APPLE_PAY,         // Apple Pay gateway
    GOOGLE_PAY,        // Google Pay gateway
    SAMSUNG_PAY,       // Samsung Pay gateway
    BANK_TRANSFER,     // Direct bank transfer
    CUSTOM             // Custom gateway integration
}