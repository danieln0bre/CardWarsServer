package br.ufrn.imd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.ufrn.imd.model.Player;
@Repository
public interface EventRankingRepository extends MongoRepository<Player, String> {
	// Apenas utiliza os métodos do MongoRepository.
}
