package io.github.xdiamond.web.shiro;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.crypto.hash.Sha512Hash;


public class PasswordUtil {
  public static final int Default_HashIterations = 99;
  public static final int Default_Salt_Length = 32;

  /**
   * return <salt, saltedPassword>
   * 
   * @param password
   * @return
   */
  public static Pair<String, String> generateSaltedPassword(String password) {
    String salt = RandomStringUtils.randomAlphanumeric(Default_Salt_Length);
    String saltedPassword =
        new Sha512Hash(password.getBytes(Charsets.UTF_8), salt.getBytes(Charsets.UTF_8),
            Default_HashIterations).toHex();
    return Pair.of(salt, saltedPassword);
  }

  public static boolean checkPassword(String password, String salt, String saltedPassword) {
    return new Sha512Hash(password, salt, Default_HashIterations).toHex().equals(saltedPassword);
  }
}
