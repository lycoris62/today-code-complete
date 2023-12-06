package sssdev.tcc.domain.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sssdev.tcc.domain.model.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User from;
    @ManyToOne(fetch = FetchType.LAZY)
    private User to;

    @Builder
    private Follow(User from, User to) {
        this.from = from;
        this.to = to;
    }
}
