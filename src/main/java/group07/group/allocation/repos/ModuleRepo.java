package group07.group.allocation.repos;

import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.Module;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ModuleRepo extends CrudRepository<Module, Integer> {
    Module findByCode (String code);
    List<Module> findModuleByConvenor(Convenor convenor);
}
