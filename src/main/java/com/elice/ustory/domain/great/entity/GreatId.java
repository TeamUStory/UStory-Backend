package com.elice.ustory.domain.great.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GreatId implements Serializable {

    private Long user;
    private Long paper;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GreatId that = (GreatId) o;
        return Objects.equals(user, that.user) && Objects.equals(paper, that.paper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, paper);
    }
}
