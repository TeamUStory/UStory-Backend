package com.elice.ustory.domain.comment.repository;

import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long id);
    List<Comment> findByPaper(Paper paper);
}
