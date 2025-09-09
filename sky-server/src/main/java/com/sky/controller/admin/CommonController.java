package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/*
 * 通用接口
 * */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "文件上传接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传接口")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件名的扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //拼接新文件名
            String newFileName = UUID.randomUUID().toString() + extension;
            //文件上传到阿里云
            String filePath = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
