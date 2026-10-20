class SignupForm {

    @NotBlank
    @MaxLength(10)
    String username;

    @NotBlank
    @MaxLength(15)
    String password;

    SignupForm(String username, String password) {
        this.username = username;
        this.password = password;
    }
}