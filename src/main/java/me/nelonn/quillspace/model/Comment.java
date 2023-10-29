package me.nelonn.quillspace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(generator = "xid")
    @GenericGenerator(name = "xid", strategy = "me.nelonn.quillspace.model.generator.XIDGenerator")
    @Column(name = "id", length = 20, nullable = false, unique = true)
    private String id;

    @ManyToOne
    private Article article;

    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> childComments = new ArrayList<>();

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    @Lob
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @LastModifiedDate
    @Column(name = "updated")
    private Date updated;

    @CreatedDate
    @Column(name = "created")
    private Date created;
}
