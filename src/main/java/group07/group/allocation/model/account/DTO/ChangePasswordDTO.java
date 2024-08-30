package group07.group.allocation.model.account.DTO;

public class ChangePasswordDTO {
    public String password;
    public String newPassword;
    public String reNewPassword;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReNewPassword() {
        return reNewPassword;
    }

    public void setReNewPassword(String rePassword) {
        this.reNewPassword = rePassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
