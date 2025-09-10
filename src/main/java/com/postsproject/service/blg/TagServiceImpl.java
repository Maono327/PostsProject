package com.postsproject.service.blg;

import com.postsproject.model.Post;
import com.postsproject.model.Tag;
import com.postsproject.repository.interfaces.TagRepository;
import com.postsproject.service.blg.interfaces.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void parseAndSaveTagsForPost(String tags, Post post) {
        String[] splitedTags = tags.split(" ");
        for (String tagName : splitedTags) {
            if (tagRepository.findByName(tagName).isEmpty()) {
                Tag saved = tagRepository.save(new Tag(null, tagName));
                tagRepository.addTagToPostByTagIdAndPostId(saved.getId(), post.getId());
            }
        }
    }

    @Override
    public void parseAndUpdateTagsForPost(String tags, Post post) {
        List<Tag> currentTags = (List<Tag>) tagRepository.findTagsByPostId(post.getId());
        List<String> splitedTags = List.of(tags.split(" "));

        for (Tag tag : currentTags) {
            if (!splitedTags.contains(tag.getName())) {
                tagRepository.removeTagFromPostByTagIdAndPostId(tag.getId(), post.getId());
                if (tagRepository.getTagRelationsCountByTagId(tag.getId()) == 0) {
                    tagRepository.remove(tag.getId());
                }
            }
        }

        List<String> tagNames = ((List<Tag>) tagRepository.findTagsByPostId(post.getId()))
                .stream()
                .map(Tag::getName)
                .toList();
        for (String tagName : splitedTags) {
            if (tagRepository.findByName(tagName).isEmpty()) {
                Tag saved = tagRepository.save(new Tag(null, tagName));
                tagRepository.addTagToPostByTagIdAndPostId(saved.getId(), post.getId());
            } else if (!tagNames.contains(tagName)) {
                tagRepository.addTagToPostByTagIdAndPostId(
                        tagRepository.findByName(tagName).get().getId(), post.getId());
            }
        }
    }

    @Override
    public List<Tag> findTagsByPostId(Long id) {
        return (List<Tag>) tagRepository.findTagsByPostId(id);
    }
}
