package pers.avc.minio.exception;

public class DuplicateBucketException extends MinioServerException{

    public DuplicateBucketException(String errMsg) {
        super(errMsg);
    }

    public DuplicateBucketException(String errMsg, Throwable e) {
        super(errMsg, e);
    }
}
