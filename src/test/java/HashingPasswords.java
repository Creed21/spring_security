import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HashingPasswords {

    @Test
    public void getHashedPasswords() {
        PasswordEncoder bcrypt = new BCryptPasswordEncoder();

        System.out.println("Hashed value for password: admin_pg -> "+bcrypt.encode("admin_pg"));
        System.out.println("Hashed value for password: user_pg -> "+bcrypt.encode("user_pg"));
        System.out.println("Hashed value for password: stuff_pg -> "+bcrypt.encode("stuff_pg"));
    }
}
