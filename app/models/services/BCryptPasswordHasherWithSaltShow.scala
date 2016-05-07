package models.services

import com.mohiva.play.silhouette.api.util.{ PasswordHasher, PasswordInfo }
import com.mohiva.play.silhouette.password.BCryptPasswordHasher._
import org.mindrot.jbcrypt.BCrypt

/**
 * Created by root on 05/05/16.
 */
class BCryptPasswordHasherWithSaltShow(logRounds: Int = 10) extends PasswordHasher {
  /**
   * Gets the ID of the hasher.
   *
   * @return The ID of the hasher.
   */
  override def id = ID

  /**
   * Hashes a password.
   *
   * This implementation does not return the salt separately because it is embedded in the hashed password.
   * Other implementations might need to return it so it gets saved in the backing store.
   *
   * @param plainPassword The password to hash.
   * @return A PasswordInfo containing the hashed password.
   */
  override def hash(plainPassword: String) = {
    val salt = BCrypt.gensalt(logRounds)
    PasswordInfo(id, BCrypt.hashpw(plainPassword, salt), Option(salt))
  }

  /**
   * Checks if a password matches the hashed version.
   *
   * @param passwordInfo The password retrieved from the backing store.
   * @param suppliedPassword The password supplied by the user trying to log in.
   * @return True if the password matches, false otherwise.
   */
  override def matches(passwordInfo: PasswordInfo, suppliedPassword: String) = {
    BCrypt.checkpw(suppliedPassword, passwordInfo.password)
  }
}

/**
 * The companion object.
 */
object BCryptPasswordHasher {

  /**
   * The ID of the hasher.
   */
  val ID = "bcrypt"
}
