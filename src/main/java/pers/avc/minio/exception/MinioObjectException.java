package pers.avc.minio.exception;

public class MinioObjectException extends MinioServerException{
    public MinioObjectException(String errMsg) {
        super(errMsg);
    }

    public MinioObjectException(String errMsg, Throwable e) {
        super(errMsg, e);
    }
}
