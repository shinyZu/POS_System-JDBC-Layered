package lk.ijse.pos_system.dto;

public class UserDetailDTO {

    private String userId;
    private String userName;
    private String userPassword;
    //private User userType;
    private String userType;

    public UserDetailDTO() {}

    public UserDetailDTO(String userId, String userName, String userPassword, String userType) {
        this.setUserId(userId);
        this.setUserName(userName);
        this.setUserPassword(userPassword);
        this.setUserType(userType);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserDetailDTO{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userType=" + userType +
                '}';
    }
}
