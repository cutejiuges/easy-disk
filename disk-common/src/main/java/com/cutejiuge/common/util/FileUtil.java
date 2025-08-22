package com.cutejiuge.common.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件工具类
 *
 * @author cutejiuge
 * @since 2025/8/22 下午10:09
 */
@Slf4j
public class FileUtil {
    /**
     * 允许上传的图片文件类型
     */
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    ));

    /**
     * 允许上传的文档文件类型
     */
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = new HashSet<>(Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "odt", "ods", "odp"
    ));

    /**
     * 允许上传的音频文件类型
     */
    private static final Set<String> ALLOWED_AUDIO_TYPES = new HashSet<>(Arrays.asList(
            "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a"
    ));

    /**
     * 允许上传的视频文件类型
     */
    private static final Set<String> ALLOWED_VIDEO_TYPES = new HashSet<>(Arrays.asList(
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp"
    ));

    /**
     * 允许上传的压缩文件类型
     */
    private static final Set<String> ALLOWED_ARCHIVE_TYPES = new HashSet<>(Arrays.asList(
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz"
    ));

    /**
     * 所有允许的文件类型
     */
    private static final Set<String> ALL_ALLOWED_TYPES = new HashSet<>();

    static {
        ALL_ALLOWED_TYPES.addAll(ALLOWED_IMAGE_TYPES);
        ALL_ALLOWED_TYPES.addAll(ALLOWED_DOCUMENT_TYPES);
        ALL_ALLOWED_TYPES.addAll(ALLOWED_AUDIO_TYPES);
        ALL_ALLOWED_TYPES.addAll(ALLOWED_VIDEO_TYPES);
        ALL_ALLOWED_TYPES.addAll(ALLOWED_ARCHIVE_TYPES);
    }

    /**
     * 获取文件扩展名（不包含点）
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        return FilenameUtils.getExtension(fileName).toLowerCase();
    }

    /**
     * 获取文件名（不包含扩展名）
     *
     * @param fileName 文件名
     * @return 文件名（不包含扩展名）
     */
    public static String getBaseName(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        return FilenameUtils.getBaseName(fileName);
    }

    /**
     * 获取MIME类型
     *
     * @param fileName 文件名
     * @return MIME类型
     */
    public static String getMimeType(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "application/octet-stream";
        }

        String extension = getExtension(fileName);
        return switch (extension) {
            // 图片类型
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";

            // 文档类型
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";

            // 音频类型
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "flac" -> "audio/flac";
            case "aac" -> "audio/aac";
            case "ogg" -> "audio/ogg";

            // 视频类型
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mkv" -> "video/x-matroska";
            case "mov" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";

            // 压缩文件类型
            case "zip" -> "application/zip";
            case "rar" -> "application/vnd.rar";
            case "7z" -> "application/x-7z-compressed";
            case "tar" -> "application/x-tar";
            case "gz" -> "application/gzip";
            default -> "application/octet-stream";
        };
    }

    /**
     * 获取文件类型分类
     *
     * @param fileName 文件名
     * @return 文件类型分类
     */
    public static String getFileCategory(String fileName) {
        if (isImageFile(fileName)) {
            return "image";
        } else if (isDocumentFile(fileName)) {
            return "document";
        } else if (isAudioFile(fileName)) {
            return "audio";
        } else if (isVideoFile(fileName)) {
            return "video";
        } else if (isArchiveFile(fileName)) {
            return "archive";
        } else {
            return "other";
        }
    }

    /**
     * 判断是否为视频文件
     *
     * @param fileName 文件名
     * @return 是否为视频文件
     */
    public static boolean isVideoFile(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }
        String extension = getExtension(fileName);
        return ALLOWED_VIDEO_TYPES.contains(extension);
    }

    /**
     * 判断是否为压缩文件
     *
     * @param fileName 文件名
     * @return 是否为压缩文件
     */
    public static boolean isArchiveFile(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }
        String extension = getExtension(fileName);
        return ALLOWED_ARCHIVE_TYPES.contains(extension);
    }

    /**
     * 判断是否为音频文件
     *
     * @param fileName 文件名
     * @return 是否为音频文件
     */
    public static boolean isAudioFile(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }
        String extension = getExtension(fileName);
        return ALLOWED_AUDIO_TYPES.contains(extension);
    }

    /**
     * 判断是否为文档文件
     *
     * @param fileName 文件名
     * @return 是否为文档文件
     */
    public static boolean isDocumentFile(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }
        String extension = getExtension(fileName);
        return ALLOWED_DOCUMENT_TYPES.contains(extension);
    }

    /**
     * 判断是否为图片文件
     *
     * @param fileName 文件名
     * @return 是否为图片文件
     */
    public static boolean isImageFile(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }
        String extension = getExtension(fileName);
        return ALLOWED_IMAGE_TYPES.contains(extension);
    }

    /**
     * 验证文件类型是否允许
     *
     * @param fileName 文件名
     * @return 是否允许
     */
    public static boolean isAllowedFileType(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }
        String extension = getExtension(fileName);
        return ALL_ALLOWED_TYPES.contains(extension);
    }

    /**
     * 解析文件大小字符串为字节数
     *
     * @param sizeStr 文件大小字符串（如：1.5MB）
     * @return 字节数
     */
    public static long parseFileSize(String sizeStr) {
        if (StrUtil.isBlank(sizeStr)) {
            return 0;
        }
        sizeStr = sizeStr.trim().toUpperCase();
        double size;
        long multiplier = 1;
        if (sizeStr.endsWith("TB")) {
            size = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 2));
            multiplier = 1024L * 1024L * 1024L * 1024L;
        } else if (sizeStr.endsWith("GB")) {
            size = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 2));
            multiplier = 1024L * 1024L * 1024L;
        } else if (sizeStr.endsWith("MB")) {
            size = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 2));
            multiplier = 1024L * 1024L;
        } else if (sizeStr.endsWith("KB")) {
            size = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 2));
            multiplier = 1024L;
        } else if (sizeStr.endsWith("B")) {
            size = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 1));
        } else {
            size = Double.parseDouble(sizeStr);
        }
        return (long) (size * multiplier);
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }
        double result = size / Math.pow(1024, digitGroups);
        DecimalFormat df = new DecimalFormat("#,##0.#");
        return df.format(result) + " " + units[digitGroups];
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalFileName) {
        String extension = getExtension(originalFileName);
        String uniqueId = System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);
        return StrUtil.isBlank(extension) ? uniqueId : uniqueId + "." + extension;
    }

    /**
     * 生成安全的文件名
     *
     * @param originalFileName 原始文件名
     * @return 安全的文件名
     */
    public static String generateSafeFileName(String originalFileName) {
        if (StrUtil.isBlank(originalFileName)) {
            return "unnamed_file";
        }
        // 移除危险字符
        String safeName = originalFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        // 限制文件名长度
        String baseName = getBaseName(safeName);
        String extension = getExtension(safeName);
        if (baseName.length() > 200) {
            baseName = baseName.substring(0, 200);
        }
        return StrUtil.isBlank(extension) ? baseName : baseName + "." + extension;
    }

    /**
     * 验证MultipartFile
     *
     * @param file MultipartFile对象
     * @return 验证结果
     */
    public static String validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "文件不能为空";
        }
        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            return "文件名不能为空";
        }
        if (!isAllowedFileType(fileName)) {
            return "不支持的文件类型";
        }
        // 检查文件大小（默认最大100MB）
        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.getSize() > maxSize) {
            return "文件大小不能超过" + formatFileSize(maxSize);
        }
        return null; // 验证通过
    }

    /**
     * 检测文件真实类型
     *
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @return 是否匹配
     */
    public static boolean isFileTypeMatched(byte[] fileBytes, String fileName) {
        if (fileBytes == null || fileBytes.length == 0 || StrUtil.isBlank(fileName)) {
            return false;
        }
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            String detectedType = FileTypeUtil.getType(inputStream);
            String expectedType = getExtension(fileName);

            // 特殊处理一些类型
            if ("jpg".equals(expectedType) && "jpeg".equals(detectedType)) {
                return true;
            }

            return expectedType.equals(detectedType);
        } catch (IOException e) {
            log.warn("检测文件类型失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成文件存储路径
     *
     * @param userId 用户ID
     * @param fileName 文件名
     * @return 文件存储路径
     */
    public static String generateFilePath(Long userId, String fileName) {
        String year = String.valueOf(java.time.LocalDate.now().getYear());
        String month = String.format("%02d", java.time.LocalDate.now().getMonthValue());
        String uniqueFileName = generateUniqueFileName(fileName);

        return String.format("user-files/%d/%s/%s/%s", userId, year, month, uniqueFileName);
    }

    /**
     * 计算分片数量
     *
     * @param fileSize 文件大小
     * @param chunkSize 分片大小
     * @return 分片数量
     */
    public static int calculateChunkCount(long fileSize, long chunkSize) {
        if (fileSize <= 0 || chunkSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) fileSize / chunkSize);
    }

    /**
     * 生成缩略图路径
     *
     * @param fileId 文件ID
     * @param size 缩略图尺寸
     * @return 缩略图路径
     */
    public static String generateThumbnailPath(Long fileId, String size) {
        return String.format("thumbnails/%d/%s.jpg", fileId, size);
    }
}
