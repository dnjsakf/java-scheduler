package websockets.exceptions;

import common.Const;

public class AlreadyUsedException extends ChatException {
    
    private static final long serialVersionUID = 1L;

    public AlreadyUsedException(String message) {
        super(message);
    }
    
    public AlreadyUsedException() {
        super(Const.ERROR_ALREADY_USED_MESSAGE);
    }
}
