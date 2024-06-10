package com.elice.ustory.domain.comment.service;

import com.elice.ustory.domain.comment.dto.AddCommentRequest;
import com.elice.ustory.domain.comment.dto.UpdateCommentRequest;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.repository.CommentRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.PaperService;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.exception.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PaperService paperService;
    private final UserService userService;

    private static final String NOT_FOUND_COMMENT_MESSAGE = "%d: 해당하는 댓글이 존재하지 않습니다.";

    public CommentService(CommentRepository commentRepository, PaperService paperService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.paperService = paperService;
        this.userService = userService;
    }

    public List<Comment> getComments(Long paperId, Long userId) {
        Paper paper = paperService.getPaperById(paperId);
        List<Comment> comments = commentRepository.findByPaper(paper);
        return comments;
    }

    public Optional<Comment> getComment(Long paperId, Long id) {
        Paper paper = paperService.getPaperById(paperId);
        List<Comment> comments = commentRepository.findByPaper(paper);
        return comments.stream().filter(comment -> comment.getId().equals(id)).findFirst();
    }

    @Transactional
    public Comment addComment(AddCommentRequest addCommentRequest, Long paperId, Long userId) {

        Paper paper = paperService.getPaperById(paperId);

        Comment comment = Comment.addCommentBuilder()
                .content(addCommentRequest.getContent())
                .paper(paper)
                .user(userService.findById(userId))
                .build();

        Comment savedComment = commentRepository.save(comment);

        paperService.noticeLocked(paper.getDiary(), paper);

        return savedComment;
    }

    @Transactional
    public Comment updateComment(Long id, UpdateCommentRequest updateCommentRequest) {
        Comment optionalComment = commentRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_COMMENT_MESSAGE, id)));
        return optionalComment.update(updateCommentRequest.getContent());
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
