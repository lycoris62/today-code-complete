package sssdev.tcc.domain.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sssdev.tcc.domain.model.BaseEntity;
import sssdev.tcc.domain.model.File;
import sssdev.tcc.domain.user.domain.User;

@Entity
@Table(name = "POST_FILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private File file;

    @Builder
    private PostFile(User user, File file) {
        this.user = user;
        this.file = file;
    }
}
