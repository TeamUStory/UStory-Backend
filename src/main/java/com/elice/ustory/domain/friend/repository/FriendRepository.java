package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId>, QuerydslPredicateExecutor<Friend>
{
    List<Friend> findAllByIdUserId(Long userId);
}
