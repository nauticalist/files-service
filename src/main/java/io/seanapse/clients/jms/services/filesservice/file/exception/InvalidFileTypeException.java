package io.seanapse.clients.jms.services.filesservice.file.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidFileTypeException extends RuntimeException {
    List<String> validExtensions;

    public InvalidFileTypeException(List<String> validExtensions) {
        this.validExtensions = validExtensions;
    }
}
