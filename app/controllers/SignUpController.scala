package controllers

import java.io.{ File, FileInputStream }
import java.util.UUID
import javax.inject.Inject
import java.sql.Blob
import play.api.Logger
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.User
import models.services.UserService
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{ Controller, MaxSizeExceeded, Results }
import utils.auth.DefaultEnv
import akka.stream.{ ActorMaterializer, Materializer }

import scala.concurrent.Future

/**
 * The `Sign Up` controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param avatarService The avatar service implementation.
 * @param passwordHasher The password hasher implementation.
 * @param webJarAssets The webjar assets implementation.
 */
class SignUpController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher,
  implicit val mat: Materializer,
  implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.signUp(SignUpForm.form)))
  }

  def validationFile(size: Long, contentType: String): Boolean = {
    var result = true;

    if (contentType != "image/jpeg" && contentType != "image/jpg" && contentType != "image/png" && contentType != "image/gif") {
      Logger.debug("contentType invalid file " + contentType)
      result = false
    }
    if (size < 100 || size > 2000000) {
      Logger.debug("to Longt size " + size)
      result = false;
    }
    result

  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit = silhouette.UnsecuredAction.async(parse.maxLength(10 * 1024 * 1024, parse.multipartFormData)) { implicit request =>

    request.body match {
      case Left(MaxSizeExceeded(length)) => Future.successful(BadRequest("Your file is too large, we accept just " + length + " bytes!"))
      case Right(multipartForm) => {
        /* Handle the POSTed form with files */

        import java.io.File
        val filename = multipartForm.file("image").get.filename
        val contentType = multipartForm.file("image").get.contentType.get.toLowerCase()
        val size = multipartForm.file("image").get.ref.file.length()

        if (!validationFile(size, contentType)) {
          Logger.debug("invalid file")
          Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file nice")))

        } else {

          multipartForm.file("image").get.ref.moveTo(new File(s"./public/images/$filename"))

          //    val source = scala.io.Source.fromFile(multipartForm.file("image").get.ref.file)(scala.io.Codec.UTF8)

          ///  val byteArray = source.map(_.toByte).toArray

          val is = new FileInputStream(new File(s"./public/images/$filename"))
          val cnt = is.available
          val bytes = Array.ofDim[Byte](cnt)
          is.read(bytes)
          is.close()

          //  source.close()
          val dataBlobfile: Blob = new javax.sql.rowset.serial.SerialBlob(bytes)

          //val formSubmit = SignUpForm.form.bindFromRequest()
          val formSubmit = SignUpForm.form.bindFromRequest(multipartForm.dataParts)

          formSubmit.fold(
            form => Future.successful(BadRequest(views.html.signUp(form))),
            data => {

              /* request.body.file("image") match {
                case Some(file) => {
                  import java.io.File
                  val filename = file.filename
                  val contentType = file.contentType.get.toLowerCase()
                  val size = file.ref.file.length()

                  if (size < 100 || size > 2000000) {
                    Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file")))
                  }
                  if (contentType != "image/jpeg" && contentType != "image/jpg" && contentType != "image/png" && contentType != "image/gif") {
                    Logger.debug("invalid file")
                    Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file"))

                  } else {

                    file.ref.moveTo(new File(s"./public/images/$filename"))
                  }

                }
                case _ => Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file")))
              }
              */

              /*     Logger.debug("debut File uploaded ")
              request.body.file("image").map { picture =>
                import java.io.File
                val filename = picture.filename
                val contentType = picture.contentType.get.toLowerCase()
                val size = picture.ref.file.length()
                val canExecute = picture.ref.file.canExecute
                val exists = picture.ref.file.exists()
                val getTotalSpace = picture.ref.file.getTotalSpace
                val canRead = picture.ref.file.canRead
                val isFile = picture.ref.file.isFile


                Logger.debug("File contentType ")
                Logger.debug("File contentType " + contentType)
                Logger.debug("File size " + size)
                Logger.debug("File canExecute " + canExecute)
                Logger.debug("File exists " + exists)
                Logger.debug("File getTotalSpace " + getTotalSpace)
                Logger.debug("File canRead " + canRead)
                Logger.debug("File isFile " + isFile)

                if (size < 100 || size > 2000000) {
                  Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file"))
                }
                if (contentType != "image/jpeg" && contentType != "image/jpg" && contentType != "image/png" && contentType != "image/gif") {
                  Logger.debug("invalid file")
                  Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file"))).isCompleted
                  // Results.Redirect(routes.SignUpController.view())
                } else {
                  picture.ref.moveTo(new File(s"./public/images/$filename"))
                }

                // Ok("File uploaded")

              }.getOrElse {
                Logger.debug("File not uploaded")
                Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file"))).isCompleted
              }
              Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file nnnn")))
      */
              // Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file nnnn")))
              Logger.info("avatart url debut = " + filename)
              val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
              userService.retrieve(loginInfo).flatMap {
                case Some(user) =>
                  Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("user.exists")))
                case None =>
                  val authInfo = passwordHasher.hash(data.password)
                  Logger.info("avatart url pre = " + filename)
                  val user = User(
                    userID = UUID.randomUUID(),
                    loginInfo = loginInfo,
                    firstName = Some(data.firstName),
                    lastName = Some(data.lastName),
                    fullName = Some(data.firstName + " " + data.lastName),
                    email = Some(data.email),
                    avatarURL = Some(filename),
                    dataBlob = Some(dataBlobfile)
                  )
                  for {
                    avatar <- avatarService.retrieveURL(data.email)
                    user <- userService.save(user.copy(avatarURL = avatar))
                    authInfo <- authInfoRepository.add(loginInfo, authInfo)
                    authenticator <- silhouette.env.authenticatorService.create(loginInfo)
                    value <- silhouette.env.authenticatorService.init(authenticator)
                    result <- silhouette.env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
                  } yield {
                    silhouette.env.eventBus.publish(SignUpEvent(user, request))
                    silhouette.env.eventBus.publish(LoginEvent(user, request))
                    result
                  }
              }
            }
          )

        }

      }
    }
    /* request.body.file("image") match {
      case Some(file) => {
        import java.io.File
        val filename = file.filename
        val contentType = file.contentType.get.toLowerCase()
        val size = file.ref.file.length()

        if (!validationFile(size, contentType))
        {
          Logger.debug("invalid file")
          Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file nice")))

        } else {

          file.ref.moveTo(new File(s"./public/images/$filename"))

          formSubmit.fold(
            form => Future.successful(BadRequest(views.html.signUp(form))),
            data => {

              /* request.body.file("image") match {
                case Some(file) => {
                  import java.io.File
                  val filename = file.filename
                  val contentType = file.contentType.get.toLowerCase()
                  val size = file.ref.file.length()

                  if (size < 100 || size > 2000000) {
                    Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file")))
                  }
                  if (contentType != "image/jpeg" && contentType != "image/jpg" && contentType != "image/png" && contentType != "image/gif") {
                    Logger.debug("invalid file")
                    Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file"))

                  } else {

                    file.ref.moveTo(new File(s"./public/images/$filename"))
                  }

                }
                case _ => Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file")))
              }
              */

              /*     Logger.debug("debut File uploaded ")
              request.body.file("image").map { picture =>
                import java.io.File
                val filename = picture.filename
                val contentType = picture.contentType.get.toLowerCase()
                val size = picture.ref.file.length()
                val canExecute = picture.ref.file.canExecute
                val exists = picture.ref.file.exists()
                val getTotalSpace = picture.ref.file.getTotalSpace
                val canRead = picture.ref.file.canRead
                val isFile = picture.ref.file.isFile


                Logger.debug("File contentType ")
                Logger.debug("File contentType " + contentType)
                Logger.debug("File size " + size)
                Logger.debug("File canExecute " + canExecute)
                Logger.debug("File exists " + exists)
                Logger.debug("File getTotalSpace " + getTotalSpace)
                Logger.debug("File canRead " + canRead)
                Logger.debug("File isFile " + isFile)

                if (size < 100 || size > 2000000) {
                  Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file"))
                }
                if (contentType != "image/jpeg" && contentType != "image/jpg" && contentType != "image/png" && contentType != "image/gif") {
                  Logger.debug("invalid file")
                  Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file"))).isCompleted
                  // Results.Redirect(routes.SignUpController.view())
                } else {
                  picture.ref.moveTo(new File(s"./public/images/$filename"))
                }

                // Ok("File uploaded")

              }.getOrElse {
                Logger.debug("File not uploaded")
                Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file"))).isCompleted
              }
              Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file nnnn")))
      */
              // Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("Missing file nnnn")))

              val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
              userService.retrieve(loginInfo).flatMap {
                case Some(user) =>
                  Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("user.exists")))
                case None =>
                  val authInfo = passwordHasher.hash(data.password)
                  val user = User(
                    userID = UUID.randomUUID(),
                    loginInfo = loginInfo,
                    firstName = Some(data.firstName),
                    lastName = Some(data.lastName),
                    fullName = Some(data.firstName + " " + data.lastName),
                    email = Some(data.email),
                    avatarURL = None
                  )
                  for {
                    avatar <- avatarService.retrieveURL(data.email)
                    user <- userService.save(user.copy(avatarURL = avatar))
                    authInfo <- authInfoRepository.add(loginInfo, authInfo)
                    authenticator <- silhouette.env.authenticatorService.create(loginInfo)
                    value <- silhouette.env.authenticatorService.init(authenticator)
                    result <- silhouette.env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
                  } yield {
                    silhouette.env.eventBus.publish(SignUpEvent(user, request))
                    silhouette.env.eventBus.publish(LoginEvent(user, request))
                    result
                  }
              }
            }
          )

        }

      }
      case _ => Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid file another")))
    } */

  }
}
