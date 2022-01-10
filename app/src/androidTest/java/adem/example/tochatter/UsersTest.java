// Adem VAROL - 200709078
package adem.example.tochatter;

import static org.junit.Assert.*;

import org.junit.Test;

public class UsersTest {
    Users users=new Users();

    @Test
    public void getIsActive_tb() {
        users.setUname_tb("Adem");
        assertEquals(users.getUname_tb(),"Adem");

    }

    @Test
    public void setIsActive_tb() {
        users.setIsActive_tb("ON");
        assertEquals(users.isActive_tb,"ON");
    }

    @Test
    public void getMail_tb() {
    }

    @Test
    public void setMail_tb() {
    }

    @Test
    public void getStatus_tb() {
    }

    @Test
    public void setStatus_tb() {
    }

    @Test
    public void getUid_tb() {
    }

    @Test
    public void setUid_tb() {
    }

    @Test
    public void getUname_tb() {
    }

    @Test
    public void setUname_tb() {
    }
}