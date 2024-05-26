package com.elice.ustory.domain.comment.service;

import com.elice.ustory.domain.comment.dto.CommentDto;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.repository.CommentRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.PaperService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PaperService paperService;

    public CommentService(CommentRepository commentRepository, PaperService paperService) {
        this.commentRepository = commentRepository;
        this.paperService = paperService;
    }

    public List<Comment> getComments(Long paperId) {
        Paper paper = paperService.getPaperById(paperId);
        return commentRepository.findByPaper(paper);
    }

    public Optional<Comment> getComment(Long paperId, Long id) {
        Paper paper = paperService.getPaperById(paperId);
        List<Comment> comments = commentRepository.findByPaper(paper);
        return comments.stream().filter(comment -> comment.getId().equals(id)).findFirst();
    }

    public Comment addComment(CommentDto commentDto, Long paperId) {
        Comment comment = Comment.addCommentBuilder()
                .content(commentDto.getContent())
                .paper(paperService.getPaperById(paperId))
                .build();
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, CommentDto commentDto) {
        Optional<Comment> optionalComment = commentRepository.findById(id);

        if(optionalComment.isPresent()){
            return optionalComment.get().update(commentDto.getContent());
        }else{
            throw new RuntimeException("해당 Id에 대한 댓글을 찾을 수 없습니다.");
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
