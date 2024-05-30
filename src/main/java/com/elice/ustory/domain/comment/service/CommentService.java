package com.elice.ustory.domain.comment.service;

import com.elice.ustory.domain.comment.dto.AddCommentRequest;
import com.elice.ustory.domain.comment.dto.UpdateCommentRequest;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.repository.CommentRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.PaperService;
import com.elice.ustory.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PaperService paperService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PaperService paperService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.paperService = paperService;
        this.userService = userService;
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

    @Transactional
    public Comment addComment(AddCommentRequest addCommentRequest, Long paperId, Long userId) {
        Comment comment = Comment.addCommentBuilder()
                .content(addCommentRequest.getContent())
                .paper(paperService.getPaperById(paperId))
                .user(userService.findById(userId).orElseThrow())
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long id, UpdateCommentRequest updateCommentRequest) {
        Optional<Comment> optionalComment = commentRepository.findById(id);

        if(optionalComment.isPresent()){
            return optionalComment.get().update(updateCommentRequest.getContent());
        }else{
            throw new RuntimeException("해당 Id에 대한 댓글을 찾을 수 없습니다.");
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
