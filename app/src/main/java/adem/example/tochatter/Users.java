// Adem VAROL - 200709078
package adem.example.tochatter;

public class Users {
    String isActive_tb, mail_tb, status_tb, uid_tb, uname_tb;

    public Users() {
    }

    public Users(String isActive_tb, String mail_tb, String status_tb, String uid_tb, String uname_tb) {
        this.isActive_tb = isActive_tb;
        this.mail_tb = mail_tb;
        this.status_tb = status_tb;
        this.uid_tb = uid_tb;
        this.uname_tb = uname_tb;
    }


    public String getIsActive_tb() {
        return isActive_tb;
    }

    public void setIsActive_tb(String isActive_tb) {
        this.isActive_tb = isActive_tb;
    }

    public String getMail_tb() {
        return mail_tb;
    }

    public void setMail_tb(String mail_tb) {
        this.mail_tb = mail_tb;
    }

    public String getStatus_tb() {
        return status_tb;
    }

    public void setStatus_tb(String status_tb) {
        this.status_tb = status_tb;
    }

    public String getUid_tb() {
        return uid_tb;
    }

    public void setUid_tb(String uid_tb) {
        this.uid_tb = uid_tb;
    }

    public String getUname_tb() {
        return uname_tb;
    }

    public void setUname_tb(String uname_tb) {
        this.uname_tb = uname_tb;
    }
}
