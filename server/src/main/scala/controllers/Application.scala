package controllers

import java.nio.ByteBuffer

import boopickle.Default._
import com.typesafe.config.ConfigFactory
import play.api.mvc._
import services.ApiService
import spatutorial.shared.Api

import scala.concurrent.ExecutionContext.Implicits.global

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

object Config {
  val c = ConfigFactory.load().getConfig("spatutorial")

  val productionMode = c.getBoolean("productionMode")
}

object Application extends Controller {
  val apiService = new ApiService()

  def index = Action {
    Ok(views.html.index("SPA tutorial"))
  }

  def autowireApi(path: String) = Action.async(parse.raw) {
    implicit request =>
      if(!Config.productionMode)
        println(s"Request path: $path")

      // get the request body as Array[Byte]
      val b = request.body.asBytes(parse.UNLIMITED).get

      // call Autowire route
      Router.route[Api](apiService)(
        autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(ByteBuffer.wrap(b)))
      ).map(buffer => {
        val data = Array.ofDim[Byte](buffer.remaining())
        buffer.get(data)
        Ok(data)
      })
  }

  def logging = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}