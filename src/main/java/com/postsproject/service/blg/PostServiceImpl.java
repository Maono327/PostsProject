package com.postsproject.service.blg;

import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.dto.response.ResponsePostPageDTO;
import com.postsproject.dto.response.ResponsePostEditFormDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.model.Tag;
import com.postsproject.repository.interfaces.PostRepository;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.service.blg.interfaces.PostService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.service.util.B64Transformer;
import com.postsproject.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final CommentService commentService;
    private final B64Transformer b64Transformer;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService, CommentService commentService, B64Transformer b64Transformer) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.commentService = commentService;
        this.b64Transformer = b64Transformer;
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post update(Post post) {
        Post updatable = postRepository.findById(post.getId()).orElseThrow(NoSuchElementException::new);
        updatable.setName(post.getName());
        updatable.setPostImage(post.getPostImage());
        updatable.setText(post.getText());
        return postRepository.update(updatable);
    }

    @Override
    public void remove(Long id) {
        postRepository.remove(id);
    }

    @Override
    public void addPostLike(Long id) {
        Post post = postRepository.findById(id).orElseThrow(NoSuchElementException::new);
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.update(post);
    }

    @Override
    public Page<Post> findAllByPage(long pageSize, long pageNumber) {
        return postRepository.findAll(pageSize, pageNumber);
    }

    @Override
    public Page<Post> findAllByTagAndPage(String searchTag, long pageSize, long pageNumber) {
        return postRepository.findAllByTag(searchTag, pageSize, pageNumber);
    }

    @Override
    public Post findPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public PostImage findPostImageByPostId(Long id) {
        return postRepository.findById(id).orElseThrow(NoSuchElementException::new).getPostImage();
    }

    @Override
    public Page<ResponsePostsTapeDTO> mapToResponseTapeDto(Page<Post> postPage) {
        Function<Post, ResponsePostsTapeDTO> postToDtoFunction = post -> new ResponsePostsTapeDTO(
                post.getId(),
                post.getName(),
                b64Transformer.transform(post.getPostImage()),
                post.getText().length() > 128 ? post.getText().substring(0, 128) + "..." : post.getText(),
                (List<Tag>) tagService.findTagsByPostId(post.getId()),
                post.getLikesCount(),
                (long) commentService.findCommetsByPostId(post.getId()).size()

        );

        List<ResponsePostsTapeDTO> postPreviewDTOS = ((List<Post>) postPage.posts())
                .stream()
                .map(postToDtoFunction)
                .toList();

        return new Page<>(
                postPage.search(),
                postPage.pageSize(),
                postPage.pageNumber(),
                postPage.pageCount(),
                postPreviewDTOS);
    }

    @Override
    public ResponsePostEditFormDTO mapToResponseEditDto(Post post) {
        StringBuilder tagBuilder = new StringBuilder();
        List<Tag> postTags = (List<Tag>) tagService.findTagsByPostId(post.getId());
        for (int i = 0; i < postTags.size(); i++) {
            tagBuilder.append(postTags.get(i).getName());
            if (i != postTags.size() - 1) {
                tagBuilder.append(" ");
            }
        }

        return new ResponsePostEditFormDTO(
                post.getId(),
                post.getName(),
                b64Transformer.transform(post.getPostImage()),
                post.getText(),
                tagBuilder.toString()
        );
    }

    @Override
    public ResponsePostPageDTO mapToPostPageDto(Post post) {
        return new ResponsePostPageDTO(
                post.getId(),
                post.getName(),
                b64Transformer.transform(post.getPostImage()),
                post.getText(),
                (List<Tag>) tagService.findTagsByPostId(post.getId()),
                post.getLikesCount(),
                commentService.findCommetsByPostId(post.getId())
        );
    }

    @Override
    public Post mapToModel(RequestPostDTO dto) throws IOException {
        PostImage postImage = new PostImage(dto.image().getContentType(), dto.image().getBytes());
        return new Post(null, dto.name(), postImage, dto.text(), 0);
    }
}
