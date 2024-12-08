package com.ark.retailpulse.exception;

public class OtpResendLimitExceededException extends RuntimeException{

     public OtpResendLimitExceededException(String message){
         super(message);
     }
}
