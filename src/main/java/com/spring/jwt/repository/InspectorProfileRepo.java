package com.spring.jwt.repository;

import com.spring.jwt.entity.InspectorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectorProfileRepo extends JpaRepository<InspectorProfile,Integer> {
}
