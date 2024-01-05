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

/**
 * 通用接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传，{}", file);
        //文件初始名
        String filename = file.getOriginalFilename();
        //文件扩展名
        assert filename != null;
        String fileExtensionName = filename.substring(filename.lastIndexOf('.'));
        //新的文件名
        String objectName = UUID.randomUUID() + fileExtensionName;
        try {
            String path = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(path);
        } catch (IOException e) {
            log.error("文件上传失败...");
            e.printStackTrace();
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
