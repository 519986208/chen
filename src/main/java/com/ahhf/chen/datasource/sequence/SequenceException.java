package com.ahhf.chen.datasource.sequence;

/**
 * 类SequenceException.java的实现描述：Sequence异常
 */
public class SequenceException extends RuntimeException {

    private static final long serialVersionUID = 1037280730252312667L;

    public SequenceException() {
        super();
    }

    public SequenceException(String message) {
        super(message);
    }

    public SequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SequenceException(Throwable cause) {
        super(cause);
    }

}
