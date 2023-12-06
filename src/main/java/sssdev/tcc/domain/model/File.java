package sssdev.tcc.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String url;

    @Builder
    private File(String url) {
        this.url = url;
    }
}
