package cohappy.backend.repositories;

import cohappy.backend.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByParticipatingContains(String userCode);

    Optional<Chat> findByChatCode(String chatCode);

}
