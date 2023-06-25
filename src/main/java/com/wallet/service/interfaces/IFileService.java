package com.wallet.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {

    String upload(MultipartFile multipartFile) throws IOException;

    String download(String fileName) throws IOException;
}
