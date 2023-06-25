package com.wallet.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.wallet.service.interfaces.IFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService implements IFileService {

    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();                        // to get original file name
        fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.
        File file = this.convertToFile(multipartFile, fileName);
        // to convert multipartFile to File
        String TEMP_URL = this.uploadFile(file, fileName);                                   // to get uploaded file link
        file.deleteOnExit();                                                                // to delete the copy of uploaded file stored in the project folder
        return TEMP_URL;                     // Your customized response
    }

    @Override
    public String download(String fileName) throws IOException {
        String destFileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));     // to set random strinh for destination file name
        String destFilePath = "Z:\\New folder\\" + destFileName;                                    // to set destination file path

        ////////////////////////////////   Download  ////////////////////////////////////////////////////////////////////////
        Credentials credentials = GoogleCredentials.fromStream(new ClassPathResource("UploadFileConfig/upload-file-2ac29-firebase-adminsdk-config.json").getInputStream());
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Blob blob = storage.get(BlobId.of("upload-file-2ac29.appspot.com", fileName));
        blob.downloadTo(Paths.get(destFilePath));
        return "Successfully Downloaded!";
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("upload-file-2ac29.appspot.com", fileName);
        Credentials credentials = GoogleCredentials.fromStream(new ClassPathResource("UploadFileConfig/upload-file-2ac29-firebase-adminsdk-config.json").getInputStream());
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        byte[] fileBytes = IOUtils.toByteArray(new FileInputStream(file));
        // Xác định kiểu MIME của tệp tin
        Path filePath = Paths.get(file.getAbsolutePath());
        String mimeType = Files.probeContentType(filePath);
        // Kiểm tra kiểu MIME của tệp tin và cấu hình đúng loại tệp cho blob trên Firebase
        BlobInfo.Builder blobInfoBuilder = BlobInfo.newBuilder(blobId);
        if (mimeType != null) {
            if (mimeType.equals("image/png")) {
                blobInfoBuilder.setContentType("image/png");
            } else if (mimeType.equals("image/jpeg")) {
                blobInfoBuilder.setContentType("image/jpeg");
            }
        }
        BlobInfo blobInfo = blobInfoBuilder.build();

        storage.create(blobInfo, fileBytes);
        return String.format("https://firebasestorage.googleapis.com/v0/b/upload-file-2ac29.appspot.com/o/%s?alt=media", URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
