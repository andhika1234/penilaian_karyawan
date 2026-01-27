package id.co.lua.pbj.penilaian_karyawan.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
public class InternalServiceErrorException extends RuntimeException {
    public InternalServiceErrorException(String message) {
        super(message);
    }

    public InternalServiceErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}