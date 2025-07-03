package com.example.message_service.service.util;

import com.example.message_service.model.Attachment;
import com.example.message_service.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j // Thêm logger để dễ dàng debug
public class FileStorageService {

    private final Path rootLocation;
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    // GỢI Ý 1: Inject đường dẫn từ file application.yml
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
        try {
            // GỢI Ý 3: Khởi tạo thư mục gốc khi service được tạo
            Files.createDirectories(rootLocation);
            log.info("Created root upload directory: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not initialize storage location", e);
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    /**
     * Chỉ tạo metadata cho attachment, không lưu file vật lý.
     * Dùng cho luồng gửi tin nhắn bất đồng bộ.
     */
    public Attachment createAttachmentMetadata(MultipartFile file, Message message) {
        String contentType = file.getContentType();
        String folder = "files"; // Đổi tên folder mặc định
        if (contentType != null) {
            if (contentType.startsWith("image/")) folder = "images";
            else if (contentType.startsWith("video/")) folder = "videos";
        }

        if ("videos".equals(folder) && file.getSize() > MAX_VIDEO_SIZE) {
            throw new RuntimeException("Video quá lớn. Tối đa 100MB.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uuidFileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            String encodedName = URLEncoder.encode(uuidFileName, StandardCharsets.UTF_8).replace("+", "%20");
            // Đường dẫn này là tương đối, để lưu vào DB
            String fileUrl = "/uploads/" + folder + "/" + encodedName;

            Attachment attachment = new Attachment();
            attachment.setFileUrl(fileUrl);
            attachment.setFileType(contentType);
            attachment.setFileSize(file.getSize());
            attachment.setMessage(message);
            attachment.setOriginalFileName(originalFileName);
            return attachment;
        } catch (Exception e) {
            throw new RuntimeException("Could not encode file name: " + originalFileName, e);
        }
    }

    /**
     * Lưu các file vật lý của một tin nhắn.
     * Đây là tác vụ chậm, được gọi bởi phương thức @Async.
     */
    public void storeFiles(MultipartFile[] files, List<Attachment> attachments) {
        if (files.length != attachments.size()) {
            log.warn("Mismatch between number of files and attachments metadata.");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            Attachment attachment = attachments.get(i);
            try {
                // Lấy đường dẫn từ metadata đã tạo
                // substring(1) để bỏ dấu "/" ở đầu
                Path destinationFile = this.rootLocation.resolve(Paths.get(attachment.getFileUrl().substring(1)))
                        .normalize().toAbsolutePath();

                // GỢI Ý 2: Kiểm tra bảo mật
                if (!destinationFile.getParent().startsWith(this.rootLocation.toAbsolutePath())) {
                    throw new RuntimeException("Cannot store file outside current directory.");
                }

                Files.createDirectories(destinationFile.getParent());
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.error("Failed to store file for attachment {}: {}", attachment.getId(), e.getMessage());
                // Không ném exception ở đây để các file khác vẫn có thể được lưu
            }
        }
    }

    /**
     * Lưu file avatar của người dùng và trả về đường dẫn tương đối.
     * Dùng cho luồng cập nhật avatar.
     */
    public String storeUserAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Không thể lưu một file rỗng.");
        }
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uuidFileName = UUID.randomUUID().toString() + fileExtension;
            Path subDir = Paths.get("avatars");
            Path destinationDir = this.rootLocation.resolve(subDir);

            Files.createDirectories(destinationDir);

            Path destinationFile = destinationDir.resolve(uuidFileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về đường dẫn tương đối để lưu vào DB
            return "/uploads/avatars/" + uuidFileName;
        } catch (IOException e) {
            log.error("Failed to store avatar file", e);
            throw new RuntimeException("Lỗi khi lưu file avatar.", e);
        }
    }
}