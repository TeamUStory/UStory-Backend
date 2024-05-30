package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.UserListDTO;

import java.util.List;

public interface FriendRepositoryCustom {
    List<UserFriendDTO> findFriends(Long userId, String nickname);
    List<FriendRequestDTO> findFriendRequests(Long userId);

}
