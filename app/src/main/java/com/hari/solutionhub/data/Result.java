package com.hari.solutionhub.data;

import androidx.annotation.NonNull;

import com.squareup.picasso.RequestHandler;

// A generic class that holds a result success w/ data or an error exception.
public class Result<T> {
    // Hide the private constructor to limit subclass types (Success, Error)


    private Result() {
    }

    @NonNull
    @Override
    public String toString() {
        if (this instanceof Result.Success){
            Result.Success  <T> success = ( Result.Success<T>) this;
            return "Success[data=" + success.getData().toString()+ "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error)this;
            return "Error[exception=" + error.getError().toString() + "]";

        }
        return "";

    }
    // Success sub-class
    public static final class Success<T> extends Result<T>{
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }
    //Error sub-class
    public static final class Error extends Result{
        private final Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
