package retrofit.http.exception;

/**
 * 自定义服务器错误
 *
 */
public class ServerException extends RuntimeException {
    private static final long serialVersionUID = -8054910319339413140L;
    private int code;
    private String msg;

    public ServerException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
