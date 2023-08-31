package io.playqd.mediaserver.util;

import io.playqd.mediaserver.api.rest.controller.RestApiResources;
import io.playqd.mediaserver.service.metadata.ImageSizeRequestParam;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.util.MimeType;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public abstract class ImageUtils {

    public static byte[] resize(byte[] data, int newWidth, int newHeight) {
        try {
            var image = ImageIO.read(new ByteArrayInputStream(data));

            if (image.getWidth() <= newWidth && image.getHeight() <= newHeight) {
                log.info("Original image size is less or equal to the new size. Resize is not required.");
                return data;
            }

            var imageType = ((image.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : image.getType());
            var resizedImage = new BufferedImage(150, 150, imageType);

            Graphics2D g2d = resizedImage.createGraphics();

            g2d.setComposite(AlphaComposite.Src);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(image, 0, 0, 150, 150, null);
            g2d.dispose();

            var out = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Resize image failed.", e);
            return new byte[]{};
        }
    }

    public static String buildImageDlnaProtocolInfo(MimeType mimeType) {
        return String.format("http-get:*:%s:DLNA.ORG_PN=JPEG_TN;DLNA.ORG_FLAGS=00900000000000000000000000000000",
                mimeType.toStringNoParameters());
    }

    public static String createAlbumArtResourceUri(String hostname, String albumId) {
        return createAlbumArtResourceUri(hostname, albumId, null, null);
    }

    public static String createAlbumArtResourceUri(String hostname, String albumId, String albumFolderImageFilename) {
        return createAlbumArtResourceUri(hostname, albumId, albumFolderImageFilename, null);
    }

    public static String createAlbumArtResourceUri(String hostname,
                                                   String albumId,
                                                   ImageSizeRequestParam imageSizeName) {
        return createAlbumArtResourceUri(hostname, albumId, null, imageSizeName);
    }

    public static String createAlbumArtResourceUri(String hostname,
                                                   String albumId,
                                                   String albumFolderImageFilename,
                                                   ImageSizeRequestParam imageSizeName) {
        var imageResourcePath = String.format("http://%s%s/%s", hostname, RestApiResources.ALBUM_ART_IMAGE, albumId);
        if (StringUtils.hasText(albumFolderImageFilename) || imageSizeName != null) {
            imageResourcePath = imageResourcePath + "?";
        }
        var params = new ArrayList<String>(2);
        if (StringUtils.hasText(albumFolderImageFilename)) {
            params.add("name=" + albumFolderImageFilename);
        }
        if (imageSizeName != null) {
            params.add("size=" + imageSizeName.name());
        }
        if (!params.isEmpty()) {
            imageResourcePath = imageResourcePath + String.join("&", params);
        }
        return imageResourcePath;
    }

    public static String createBrowsableObjectImageResourceUri(String hostname, String objectId) {
        return String.format("http://%s%s/%s", hostname, RestApiResources.BROWSABLE_IMAGE, objectId);
    }

    public static String createImageResourceUri(String hostname, String imageId) {
        return createImageResourceUri(hostname, imageId, null);
    }

    public static String createImageResourceUri(String hostname, String imageId, ImageSizeRequestParam imageSizeName) {
        if (imageSizeName != null) {
            return String.format("http://%s%s/%s?size=%s",
                    hostname, RestApiResources.IMAGE, imageId, imageSizeName.name());
        }
        return String.format("http://%s%s/%s", hostname, RestApiResources.IMAGE, imageId);
    }

}
