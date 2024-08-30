package group07.group.allocation.repos;

import group07.group.allocation.model.account.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepo extends CrudRepository<Role, String> {
    Role findByName (String name);
}
