package me.nelonn.quillspace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(generator = "xid")
    @GenericGenerator(name = "xid", strategy = "me.nelonn.quillspace.model.generator.XIDGenerator")
    @Column(name = "id", length = 20, nullable = false, unique = true)
    private String id;

    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(name = "ip")
    private String ip;

    @CreatedDate
    @Column(name = "created")
    private Date created;

}
