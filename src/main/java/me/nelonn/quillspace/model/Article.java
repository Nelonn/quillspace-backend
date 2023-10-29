package me.nelonn.quillspace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(generator = "xid")
    @GenericGenerator(name = "xid", strategy = "me.nelonn.quillspace.model.generator.XIDGenerator")
    @Column(name = "id", length = 20, nullable = false, unique = true)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    @Lob
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
