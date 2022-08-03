package com.auth0.visit.repository;

import com.auth0.visit.domain.Rota;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Rota entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RotaRepository extends JpaRepository<Rota, Long> {}
