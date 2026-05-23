package com.abhi.employeemanagement.service.impl;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import com.abhi.employeemanagement.service.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	private final String UPLOAD_DIR = "uploads/";

	@Override
	public String uploadFile(MultipartFile file) {

		try {

			String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

			Path uploadPath = Paths.get(UPLOAD_DIR);

			// CREATE FOLDER
			if (!Files.exists(uploadPath)) {

				Files.createDirectories(uploadPath);
			}

			Path filePath = uploadPath.resolve(fileName);

			Files.copy(

					file.getInputStream(),

					filePath,

					StandardCopyOption.REPLACE_EXISTING);

			return fileName;

		} catch (IOException e) {

			throw new RuntimeException("File upload failed");
		}
	}
}