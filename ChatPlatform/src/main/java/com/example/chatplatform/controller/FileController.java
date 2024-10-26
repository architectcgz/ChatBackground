package com.example.chatplatform.controller;

import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author archi
 */
@RestController
@RequestMapping("/file")
public class FileController {
//    @Value("${file.upload-dir}")
//    private String uploadDir;
    @jakarta.annotation.Resource
    private FileService fileService;
    @PostMapping("/avatar/upload")
    public ResponseEntity uploadAvatar(@RequestBody MultipartFile file){
        return new ResponseEntity<>(fileService.uploadAvatar(file)).ok();
    }

    @PostMapping("/image/upload")
    public ResponseEntity uploadImage(@RequestBody MultipartFile image){
        return new ResponseEntity<>(fileService.uploadImage(image)).ok();
    }

    @PostMapping("/voice/upload")
    public ResponseEntity uploadVoice(@RequestBody MultipartFile voice){
        return new ResponseEntity(fileService.uploadVoice(voice)).ok();
    }

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestBody MultipartFile file){
        return new ResponseEntity(fileService.uploadFile(file)).ok();
    }

//    @GetMapping("/{fileName}")
//    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) {
//        try {
//            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists()) {
//                response.setStatus(HttpServletResponse.SC_OK);
//                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//
//                InputStream inputStream = resource.getInputStream();
//                StreamUtils.copy(inputStream, response.getOutputStream());
//                inputStream.close();
//                response.getOutputStream().flush();
//            } else {
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                response.getWriter().write(ResponseEnum.RESOURCE_NOT_FOUND_ERROR.getMessage());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            try {
//                response.getWriter().write("Internal server error");
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }


}
