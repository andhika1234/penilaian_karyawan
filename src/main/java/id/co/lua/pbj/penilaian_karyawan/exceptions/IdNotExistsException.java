package id.co.lua.pbj.penilaian_karyawan.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "id_not_exists")
public class IdNotExistsException extends RuntimeException {
}