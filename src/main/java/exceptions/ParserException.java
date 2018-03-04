package exceptions;

import lombok.extern.log4j.Log4j;

/**
 * Created by Ameer on 3/3/18.
 */
@Log4j
public class ParserException extends Exception {
    private ErrorCodes errorCode;

    public ParserException(ErrorCodes errorCode, String errorMessage) {
        super(errorCode + " " + errorMessage);
        log.error(errorCode + " " + errorMessage);
    }

}
