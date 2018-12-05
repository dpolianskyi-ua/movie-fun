package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class S3Store implements BlobStore {
    private AmazonS3Client s3Client;
    private String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metaData = new ObjectMetadata();
        if (blob.contentType != null) {
            metaData.setContentType(blob.contentType);
        }

        s3Client.putObject(photoStorageBucket,
                blob.name,
                blob.inputStream,
                metaData);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        S3Object object = s3Client.getObject(photoStorageBucket, name);
        Blob blob = new Blob(object.getKey(),
                (InputStream) object.getObjectContent(),
                object.getObjectMetadata().getContentType()
        );

        return Optional.of(blob);
    }
}
