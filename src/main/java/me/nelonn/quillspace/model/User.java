package me.nelonn.quillspace.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(generator = "xid")
    @GenericGenerator(name = "xid", strategy = "me.nelonn.quillspace.model.generator.XIDGenerator")
    @Column(name = "id", length = 20, nullable = false, unique = true)
    private String id;

    @JsonView(Views.Private.class)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonView(Views.Public.class)
    @Column(name = "name", nullable = false)
    private String name;

    @JsonView(Views.Public.class)
    @Column(name = "avatar")
    private String avatar;

    @JsonView(Views.Internal.class)
    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @JsonView(Views.Internal.class)
    @LastModifiedDate
    @Column(name = "updated")
    private Date updated;

    @JsonView(Views.Public.class)
    @CreatedDate
    @Column(name = "created")
    private Date created;
}
