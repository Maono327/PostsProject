package com.postsproject.service.blg.interfaces;

import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.dto.response.ResponsePostPageDTO;
import com.postsproject.dto.response.ResponsePostEditFormDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.util.Page;

import java.io.IOException;

public interface PostService {

    Post save(Post post);

    Post update(Post post);

    void remove(Long id);

    void addPostLike(Long id);

    Page<Post> findAllByPage(long pageSize, long pageNumber);

    Page<Post> findAllByTagAndPage(String searchTag, long pageSize, long pageNumber);

    Post findPostById(Long id);

    PostImage findPostImageByPostId(Long id);

    Page<ResponsePostsTapeDTO> mapToResponseTapeDto(Page<Post> postPage);

    ResponsePostEditFormDTO mapToResponseEditDto(Post post);

    ResponsePostPageDTO mapToPostPageDto(Post post);

    Post mapToModel(RequestPostDTO dto) throws IOException;
}
