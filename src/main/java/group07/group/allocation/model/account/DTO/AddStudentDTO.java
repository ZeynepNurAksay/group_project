package group07.group.allocation.model.account.DTO;

public class AddStudentDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String module_id;
    private String current_marks;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getCurrent_marks() {
        return current_marks;
    }

    public void setCurrent_marks(String current_marks) {
        this.current_marks = current_marks;
    }
}
