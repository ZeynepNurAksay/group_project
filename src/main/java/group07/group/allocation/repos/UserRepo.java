package group07.group.allocation.repos;

import group07.group.allocation.model.account.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepo extends CrudRepository<User, Integer> {
    User findByEmail (String email);
}
