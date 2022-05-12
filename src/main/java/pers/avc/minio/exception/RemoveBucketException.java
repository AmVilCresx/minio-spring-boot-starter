package pers.avc.minio.exception;

public class RemoveBucketException extends MinioServerException{
    public RemoveBucketException(String errMsg) {
        super(errMsg);
    }

    public RemoveBucketException(String errMsg, Throwable e) {
        super(errMsg, e);
    }
}
