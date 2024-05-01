package com.e_commerce.dao;

import com.e_commerce.Dto.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileUploadRepository extends JpaRepository<FileUpload, String> {


    Optional<FileUpload> findByUploadID(String docTypeId);
}
