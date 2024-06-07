package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.FriendRequestListDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FriendQueryDslRepository {
    List<UserFriendDTO> findFriends(Long userId, String nickname, LocalDateTime requestTime, Pageable pageable);
    List<FriendRequestListDTO> findFriendRequests(Long userId, LocalDateTime requestTime, Pageable pageable);
    boolean existsBySenderAndReceiverAndStatus(Long senderId, Long receiverId, FriendStatus status);
    boolean existsBySenderAndReceiver(Long senderId, Long receiverId);
}

