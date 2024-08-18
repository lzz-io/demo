package io.lzz.demo.spring.batch.repository;

import io.lzz.demo.spring.batch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
