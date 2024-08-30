package group07.group.allocation.model.account.DTO;

public class ResetPasswordDTO {
    private String password1;
    private String password2;
    private String token;

    public ResetPasswordDTO(String token){
        this.token = token;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
