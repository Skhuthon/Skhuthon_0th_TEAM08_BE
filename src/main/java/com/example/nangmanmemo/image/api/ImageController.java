package com.example.nangmanmemo.image.api;

import com.example.nangmanmemo.global.template.RspTemplate;
import com.example.nangmanmemo.image.api.dto.response.ImageInfoResDto;
import com.example.nangmanmemo.image.application.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/upload",consumes = "multipart/form-data")
    public RspTemplate<ImageInfoResDto> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("postId") Long postId) {

            String imageUrl = imageService.upload(file);
            imageService.saveImageInfo(postId, imageUrl);
            return new RspTemplate<>(HttpStatus.OK,  "업로드 완료!", new ImageInfoResDto(imageUrl));

    }

    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "삭제 성공"),
                @ApiResponse(responseCode = "404", description = "이미지 없음")
        })
    @DeleteMapping("/{imageId}")
    public RspTemplate<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return new RspTemplate<>(HttpStatus.OK, "삭제 완료!");
    }

}
