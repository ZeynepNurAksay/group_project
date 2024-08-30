package group07.group.allocation.repos;

import group07.group.allocation.model.account.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepo extends CrudRepository<PasswordResetToken, Integer> {
    PasswordResetToken findByToken(String token);
}
