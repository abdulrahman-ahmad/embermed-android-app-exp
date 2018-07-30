package osiris.com.socialmedialib.models;

/**
 * Created by ketan on 12/20/2017.
 */

public class SocialMediaUserData {
    private String email;
    private String firstName;
    private String lastName;
    private String signupType;
    private String userId;
    private String Token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getSignupType() {
        return signupType;
    }

    public void setSignupType(String signupType) {
        this.signupType = signupType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    @Override
    public String toString() {
        return "SocialMediaUserData{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", signupType='" + signupType + '\'' +
                ", userId='" + userId + '\'' +
                ", Token='" + Token + '\'' +
                '}';
    }
}
