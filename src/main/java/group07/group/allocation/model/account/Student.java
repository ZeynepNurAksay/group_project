package group07.group.allocation.model.account;

import group07.group.allocation.model.Module;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends User {

    @ManyToMany(mappedBy = "students")
    private List<Module> modules;

    public Student() {
        this.modules = new ArrayList<>();
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
