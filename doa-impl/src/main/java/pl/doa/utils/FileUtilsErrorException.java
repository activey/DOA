package pl.doa.utils;

public class FileUtilsErrorException extends Exception {

    public FileUtilsErrorException() {
    }

    public FileUtilsErrorException(String message) {
        super(message);
    }

    public FileUtilsErrorException(Throwable cause) {
        super(cause);
    }

    public FileUtilsErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
