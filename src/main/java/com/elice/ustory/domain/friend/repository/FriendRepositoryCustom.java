package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.UserListDTO;

import java.util.List;

public interface FriendRepositoryCustom {
    List<UserFriendDTO> findAllFriendsByUserId(Long userId);
    List<UserFriendDTO> findFriendsByUserIdAndNickname(Long userId, String nickname);
    List<UserListDTO> findAllUsersByNickname(String nickname);

}
