package com.example.nangmanmemo.post.api;

import com.example.nangmanmemo.global.template.RspTemplate;
import com.example.nangmanmemo.image.api.dto.request.ImageUpdateReqDto;
import com.example.nangmanmemo.image.api.dto.response.ImageInfoResDto;
import com.example.nangmanmemo.image.application.ImageService;

import com.example.nangmanmemo.post.api.request.PostSaveReqDto;
import com.example.nangmanmemo.post.api.request.PostUpdateReqDto;
import com.example.nangmanmemo.post.api.response.DetailPostResDto;
import com.example.nangmanmemo.post.api.response.PostImageInfoResDto;
import com.example.nangmanmemo.post.api.response.PostInfoResDto;
import com.example.nangmanmemo.post.api.response.PostListResDto;
import com.example.nangmanmemo.post.application.PostService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ImageService imageService;

    @Operation(summary = "게시물 및 이미지 등록", method = "POST")
    @PostMapping(consumes = "multipart/form-data")
    public RspTemplate<PostImageInfoResDto> postSave(
            @RequestPart("post") PostSaveReqDto postSaveReqDto,
            @RequestPart("file") MultipartFile file) throws IOException {

        Long postId = postService.postSave(postSaveReqDto);
        String imageUrl = imageService.upload(file);
        PostImageInfoResDto postImageInfoResDto = imageService.saveImageInfo(postId, imageUrl);

        return new RspTemplate<>(HttpStatus.OK,"등록완료!",postImageInfoResDto);
    }

    @Operation(summary = "좋아요 등록", method = "POST")
    @PostMapping("/{postId}/like")
    public RspTemplate<String> likePost(@PathVariable Long postId) {
        postService.incrementLike(postId);
        return new RspTemplate<>(HttpStatus.OK, "좋아요 등록");
    }


    @Operation(summary = "게시글 전체 조회", method = "GET")
    @GetMapping()
    public RspTemplate<PostListResDto> postFindAll() {
        List<PostInfoResDto> posts = postService.postFindAll();
        PostListResDto postListResDto = PostListResDto.from(posts);

        return new RspTemplate<>(HttpStatus.OK,"게시글 전체조회 성공", postListResDto);
    }

    @Operation(summary = "게시글 하나 조회", method = "GET")
    @GetMapping("/{postId}")
    public RspTemplate<DetailPostResDto> postFindOne(@PathVariable("postId") Long postId) {
        DetailPostResDto detailPostResDto = postService.postFindOne(postId);

        return new RspTemplate<>(HttpStatus.OK, "게시글 상세조회 성공", detailPostResDto);
    }

    @Operation(summary = "게시글 수정", method = "PATCH")
    @PatchMapping(value = "/{postId}", consumes = "multipart/form-data")
    public RspTemplate<DetailPostResDto> postUpdate(@PathVariable("postId") Long postId,
                                          @RequestPart("post") PostUpdateReqDto postUpdateReqDto,
                                          @RequestPart(value="file",required = false) MultipartFile file) throws IOException{

        postService.postUpdate(postId, postUpdateReqDto);

        if (file != null && !file.isEmpty()) {
            imageService.deleteImageByPostId(postId);
            String imageUrl = imageService.upload(file);
            imageService.saveImageInfo(postId, imageUrl);
        }

        DetailPostResDto detailPostResDto = postService.postImageUpdate(postId);

        return new RspTemplate<>(HttpStatus.OK, "게시글 수정", detailPostResDto);
    }

    @Operation(summary = "게시글 삭제", method = "DELETE")
    @DeleteMapping("/{postId}")
    public RspTemplate<String> postDelete(@PathVariable("postId") Long postId) {
        postService.postDelete(postId);

        return new RspTemplate<>( HttpStatus.OK, "게시글 삭제");
    }
}
