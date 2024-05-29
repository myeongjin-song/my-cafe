package dev.rumble.cafe.repository;

import dev.rumble.cafe.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.phone = :phone AND u.gender = :gender AND u.birthDate = :birthDate")
    Optional<User> findDuplicateUser(@Param("name") String name, @Param("phone") String phone, @Param("gender") String gender, @Param("birthDate") LocalDate birthDate);
}
