

package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Colaborador;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface ColaboradorRepository extends MongoRepository<Colaborador, String> {
  Optional<Colaborador> findByLogin(String login);
}
