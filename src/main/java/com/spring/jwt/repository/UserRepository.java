package com.spring.jwt.repository;

import com.spring.jwt.entity.Dealer;
import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findByUserId(@Param("userId") Integer userId);
    @Modifying
    @Query(value = "DELETE user_role, userprofile, users FROM user_role " +
            "LEFT JOIN userprofile ON user_role.user_id = userprofile.user_id " +
            "LEFT JOIN users ON user_role.user_id = users.user_id WHERE user_role.user_id = ?1", nativeQuery = true)
    public void DeleteById(int user_id);

    User findByResetPasswordToken(String token);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = ?1")
    List<User> findByRoleName(String roleName);

    @Query("SELECT u.dealer FROM User u WHERE u.dealer.id = :dealerId")
    Dealer findDealerById(Integer dealerId);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

}
