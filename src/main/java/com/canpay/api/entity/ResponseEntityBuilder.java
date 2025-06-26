package com.canpay.api.entity;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

public class ResponseEntityBuilder<T> {
    private String resultMessage;
    private HttpStatus httpStatus;
    private T body;

    private ResponseEntityBuilder(Builder<T> builder) {
        this.resultMessage = builder.resultMessage;
        this.httpStatus = builder.httpStatus;
        this.body = builder.body;
    }

    public static class Builder<T> {
        private String resultMessage;
        private HttpStatus httpStatus;
        private T body;

        public Builder<T> resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }

        public Builder<T> httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder<T> body(T body) {
            this.body = body;
            return this;
        }

        public ResponseEntity<T> build() {
            return ResponseEntity
                    .status(httpStatus != null ? httpStatus : HttpStatus.OK)
                    .body(body);
        }

        public ResponseEntity<ResponseWrapper<T>> buildWrapped() {
            ResponseWrapper<T> wrapper = new ResponseWrapper<>();
            wrapper.setSuccess(httpStatus != null && httpStatus.is2xxSuccessful());
            wrapper.setMessage(resultMessage);
            wrapper.setData(body);
            return ResponseEntity
                    .status(httpStatus != null ? httpStatus : HttpStatus.OK)
                    .body(wrapper);
        }
    }
}
 class ResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;

    public ResponseWrapper() {}

    public ResponseWrapper(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

     public boolean isSuccess() {
         return success;
     }

     public void setSuccess(boolean success) {
         this.success = success;
     }

     public String getMessage() {
         return message;
     }

     public void setMessage(String message) {
         this.message = message;
     }

     public T getData() {
         return data;
     }

     public void setData(T data) {
         this.data = data;
     }
 }

