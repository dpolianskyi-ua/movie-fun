package org.superbiz.moviefun.albums;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static com.amazonaws.util.IOUtils.toByteArray;
import static java.lang.String.format;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        if (uploadedFile.getSize() > 0) {
            blobStore.put(
                    new Blob(
                            getCoverBlobName(albumId),
                            uploadedFile.getInputStream(),
                            uploadedFile.getContentType()
                    ));
        }

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Blob blob = blobStore.get(getCoverBlobName(albumId))
                .orElseGet(this::getDefaultCoverBlob);

        byte[] byteArray = toByteArray(blob.inputStream);

        return new HttpEntity<>(byteArray, getBlobHttpHeaders(blob, byteArray));
    }

    private HttpHeaders getBlobHttpHeaders(Blob blob, byte[] byteArray) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType(blob.contentType));
        headers.setContentLength(byteArray.length);

        return headers;
    }

    private Blob getDefaultCoverBlob() {
        return new Blob("default-cover",
                getClass().getClassLoader().getResourceAsStream("default-cover.jpg"),
                IMAGE_JPEG_VALUE);
    }

    private String getCoverBlobName(@PathVariable long albumId) {
        return format("covers/%d", albumId);
    }
}
