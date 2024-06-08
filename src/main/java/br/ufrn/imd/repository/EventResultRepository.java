package br.ufrn.imd.repository;

import br.ufrn.imd.model.EventResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventResultRepository extends MongoRepository<EventResult, String> {
    Optional<EventResult> findByEventId(String eventId);
}
