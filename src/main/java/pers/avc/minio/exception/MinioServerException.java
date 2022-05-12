package pers.avc.minio.exception;

public class MinioServerException extends RuntimeException{

    public MinioServerException(String errMsg){
        super(errMsg);
    }

    public MinioServerException(String errMsg,Throwable e) {
        super(errMsg,e);
    }
}
