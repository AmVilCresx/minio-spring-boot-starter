package pers.avc.minio.core;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.avc.minio.exception.*;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bucket及对象操作工具类
 *
 * @author AmVilCresx
 */
public class MinioObjectOperator {

    private final static Logger LOGGER = LoggerFactory.getLogger(MinioObjectOperator.class);

    private final MinioClient client;

    public MinioObjectOperator(MinioClient client) {
        this.client = client;
    }

    public MinioClient getClient() {
        return client;
    }

    /**
     * 判断桶是否存在
     *
     * @param bucketName 桶名称
     * @return boolean
     */
    public boolean bucketExists(String bucketName) {
        try {
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            LOGGER.error("判断bucket是否存在发生异常", e);
            throw new MinioServerException("判断bucket是否存在发生异常", e);
        }
    }

    /**
     * 创建Bucket，如果已经存在， 则抛异常
     *
     * @param bucketName 桶名称
     */
    public void createBucketIfNotExists(String bucketName) {
        if (this.bucketExists(bucketName)) {
            throw new DuplicateBucketException("该Bucket已存在");
        }
        try {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            LOGGER.error("创建bucket发生异常", e);
            throw new MinioServerException("创建bucket发生异常", e);
        }
    }

    /**
     * 移除 bucket
     *
     * @param bucketName 桶名称
     */
    public void removeBucket(String bucketName) {
        this.removeBucket(bucketName, false);
    }

    /**
     * 移除 bucket
     *
     * @param bucketName 桶名称
     */
    public void removeBucket(String bucketName, boolean removeObjectsIfNotEmpty) {
        this.removeBucket(bucketName, removeObjectsIfNotEmpty, null);
    }

    /**
     * 移除 bucket
     *
     * @param bucketName 桶名称
     * @param region     区域
     */
    public void removeBucket(String bucketName, boolean removeObjectsIfNotEmpty, String region) {
        if (!this.bucketExists(bucketName)) {
            throw new BucketNotExistsException("该Bucket不存在");
        }

        List<String> objectNames = listObjects(bucketName);
        if (!CollectionUtils.isEmpty(objectNames)) {
            if (!removeObjectsIfNotEmpty) {
                throw new RemoveBucketException("该Bucket中有对象，不可删除");
            }
            this.removeObjects(bucketName, objectNames);
        }

        try {
            RemoveBucketArgs.Builder builder = RemoveBucketArgs.builder().bucket(bucketName);
            if (StringUtils.hasText(region)) {
                builder.region(region);
            }
            client.removeBucket(builder.build());
        } catch (Exception e) {
            LOGGER.error("移除bucket发生异常", e);
            throw new MinioServerException("移除bucket发生异常", e);
        }
    }

    /**
     * 列出某个存储桶中的所有对象。
     *
     * @param bucketName 桶名称
     * @return 对象名称列表
     */
    public List<String> listObjects(String bucketName) {
        return listObjects(bucketName, true);
    }

    /**
     * 列出某个存储桶中的所有对象。
     *
     * @param bucketName 桶名称
     * @param recursive  是否地递归查找
     * @return 对象名称列表
     */
    public List<String> listObjects(String bucketName, boolean recursive) {
        List<String> objectNames = new ArrayList<>();
        try {
            Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(recursive)
                    .build());
            for (Result<Item> item : objects) {
                objectNames.add(item.get().objectName());
            }
            return objectNames;
        } catch (Exception e) {
            LOGGER.error("获取bucket中的对象列表发生异常", e);
            throw new MinioServerException("获取bucket中的对象列表发生异常", e);
        }
    }

    /**
     * 向 Bucket 放入对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称, 建议带上后缀名（如果有）
     * @param is         输入流
     */
    public void putObject(String bucketName, String objectName, InputStream is) {
        this.putObject(bucketName, objectName, is, null);
    }

    /**
     * 向 Bucket 放入对象
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称, 建议带上后缀名（如果有）
     * @param is          输入流
     * @param contentType 媒体类型
     */
    public void putObject(String bucketName, String objectName, InputStream is, String contentType) {
        try {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(is, is.available(), -1);
            if (StringUtils.hasText(contentType)) {
                builder.contentType(contentType);
            }

            client.putObject(builder.build());
        } catch (Exception e) {
            LOGGER.error("Put对象发生异常:", e);
            throw new MinioObjectException("Put对象发生异常", e);
        }
    }

    /**
     * 获取桶里的对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 输入流
     */
    public FilterInputStream getObject(String bucketName, String objectName) {
        return getObject(bucketName, objectName, null);
    }

    /**
     * 获取桶里的对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param region     区
     * @return 输入流
     */
    public FilterInputStream getObject(String bucketName, String objectName, String region) {
        try {
            GetObjectArgs.Builder builder = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName);
            if (StringUtils.hasText(region)) {
                builder.region(region);
            }
            return client.getObject(builder.build());
        } catch (Exception e) {
            LOGGER.error("Get对象发生异常:", e);
            throw new MinioObjectException("Get对象发生异常", e);
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName       桶名称
     * @param objectName       对象名称
     * @param fileNameWithPath 文件名称（含路径）
     */
    public void uploadObject(String bucketName, String objectName, String fileNameWithPath) {
        this.uploadObject(bucketName, objectName, fileNameWithPath, null);
    }

    /**
     * 上传文件
     *
     * @param bucketName       桶名称
     * @param objectName       对象名称
     * @param fileNameWithPath 文件名称（含路径）
     * @param contentType      媒体类型
     */
    public void uploadObject(String bucketName, String objectName, String fileNameWithPath, String contentType) {
        try {
            UploadObjectArgs.Builder builder = UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .filename(fileNameWithPath);
            if (StringUtils.hasText(contentType)) {
                builder.contentType(contentType);
            }
            client.uploadObject(builder.build());
        } catch (Exception e) {
            LOGGER.error("Upload对象发生异常:", e);
            throw new MinioObjectException("Upload对象发生异常", e);
        }
    }

    /**
     * 移除桶里对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称,如果有多层目录结构，需要传全路径
     */
    public void removeObject(String bucketName, String objectName) {
        this.removeObject(bucketName, objectName, null);
    }

    /**
     * 移除桶里对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称,如果有多层目录结构，需要传全路径
     * @param region     区
     */
    public void removeObject(String bucketName, String objectName, String region) {
        try {
            RemoveObjectArgs.Builder builder = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName);
            if (StringUtils.hasText(region)) {
                builder.region(region);
            }
            client.removeObject(builder.build());
        } catch (Exception e) {
            LOGGER.error("Remove对象发生异常:", e);
            throw new MinioObjectException("Remove对象发生异常", e);
        }
    }

    /**
     * 根据名称列表，批量移除桶中的对对象
     * @param bucketName 桶名称
     * @param objectNames 名称列表
     */
    public void removeObjects(String bucketName, List<String> objectNames) {
        List<DeleteObject> targetDeltes = objectNames.stream().map(DeleteObject::new).collect(Collectors.toList());
        try {
            Iterable<Result<DeleteError>> results = client.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(bucketName)
                    .objects(targetDeltes)
                    .build());
            // 惰性地删除多个对象。需要迭代返回的Iterable来执行删除。
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                LOGGER.error("错误信息：{}", error);
            }
        } catch (Exception e) {
            LOGGER.error("批量Remove对象发生异常:", e);
            throw new MinioObjectException("批量Remove对象发生异常", e);
        }
    }

    /**
     * 获取对象的 可下载 的链接
     * @return url String
     */
    public String presignedObjectUrl(String bucketName, String objectName) {
        try {
            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .method(Method.GET)
                    .build());
        } catch (Exception e) {
            LOGGER.error("获取对象的下载链接发生异常:", e);
            throw new MinioObjectException("获取对象的下载链接发生异常", e);
        }
    }
}
