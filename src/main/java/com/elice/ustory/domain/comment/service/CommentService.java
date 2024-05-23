package com.elice.ustory.domain.comment.service;

import com.elice.ustory.domain.comment.dto.CommentDto;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getComment(Long id) {
        return commentRepository.findById(id);
    }

    public CommentDto addComment(CommentDto commentDto) {
        Comment comment = Comment.builder()
                .id(commentDto.getId())
                .content(commentDto.getContent())
                .build();
        commentRepository.save(comment);

        return commentDto;
    }

    public CommentDto updateComment(CommentDto commentDto) {
        Optional<Comment> optionalComment = commentRepository.findById(commentDto.getId());

        if(optionalComment.isPresent()){
            Comment existingComment = optionalComment.get();

            Comment updatedComment = Comment.builder()
                    .id(existingComment.getId())
                    .content(commentDto.getContent())
                    .build();

            commentRepository.save(updatedComment);

            return CommentDto.builder()
                    .id(updatedComment.getId())
                    .content(updatedComment.getContent())
                    .build();
        }else{
            throw new RuntimeException("Comment not found");
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
