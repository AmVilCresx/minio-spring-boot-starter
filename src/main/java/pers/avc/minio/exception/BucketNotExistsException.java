package pers.avc.minio.exception;

public class BucketNotExistsException extends MinioServerException{
    public BucketNotExistsException(String errMsg) {
        super(errMsg);
    }

    public BucketNotExistsException(String errMsg, Throwable e) {
        super(errMsg, e);
    }
}
