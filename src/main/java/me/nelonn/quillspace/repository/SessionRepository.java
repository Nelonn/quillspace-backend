package me.nelonn.quillspace.repository;

import me.nelonn.quillspace.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}
