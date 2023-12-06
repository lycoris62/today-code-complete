package sssdev.tcc.domain.user.domain;

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

@Entity
@Table(name = "USER_FILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private File file;

    @Builder
    private UserFile(User user, File file) {
        this.user = user;
        this.file = file;
    }
}
