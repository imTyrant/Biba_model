package utils;

public class RuleConflictException extends Exception {
    public final static int READ_DOWN = 1;
    public final static int WRITE_UP = 2;
    public final static int LEVEL_NOT_EQUAL = 3;

    public final String STR_READ_DOWN = "READ DOWN";
    public final String STR_WRITE_UP = "WRITE UP";
    public final String STR_LEVEL_NOT_EQUAL = "LEVEL NOT EQUAL";

    public String errMsg;
    public int type;

    public RuleConflictException(int type) {
        super();
        this.type = type;
    }

    public RuleConflictException(String errMsg) {
        super();
        this.errMsg = errMsg;
    }

    public RuleConflictException(String errMsg, int type) {
        super();
        this.errMsg = errMsg;
        this.type = type;
    }

    public String parseError() {
        String reason = "";
        switch (type) {
            case READ_DOWN:
                reason = STR_READ_DOWN;
            case WRITE_UP:
                reason =  STR_WRITE_UP;
            case LEVEL_NOT_EQUAL:
                reason =  STR_LEVEL_NOT_EQUAL;
        }
        return reason;
    }
}
