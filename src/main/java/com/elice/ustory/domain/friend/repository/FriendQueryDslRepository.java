package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;

import java.util.List;

public interface FriendQueryDslRepository {
    List<UserFriendDTO> findFriends(Long userId, String nickname);
    List<FriendRequestDTO> findFriendRequests(Long userId);
    boolean existsByReceiverAndSender(Long receiverId, Long senderId);
}
