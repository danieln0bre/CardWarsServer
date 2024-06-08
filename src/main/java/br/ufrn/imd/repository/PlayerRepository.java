package br.ufrn.imd.repository;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {
    Optional<Player> findById(String id);
    List<Player> findTop10ByOrderByIdAsc();
    List<Player> findAllById(Iterable<String> ids);
    Optional<Player> findByUsername(String username);
    Optional<Player> findByEmail(String email);
    List<Player> findByAppliedEventsIdContaining(String eventId);
}
