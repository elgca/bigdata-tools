package kervin.bigdata.common.email

import com.micronautics.Smtp
import com.typesafe.config.Config
import org.markdownj.MarkdownProcessor

import scala.util.Try

case class MailInfo(to: String, cc: List[String] = Nil, bcc: List[String] = Nil)

case class EMailConf(smtp: Smtp, email: MailInfo)

class EMailSender(val conf: Try[EMailConf]) {
  def send(title: String, markdown: String): Unit = {
    conf.map {
      case EMailConf(smtp, email) =>
        val markdownProcessor = new MarkdownProcessor
        val html = markdownProcessor.markdown(markdown)
        smtp.send(email.to, email.cc, email.bcc, title, html)
    }
  }
}

object EMailSender {
  def apply(conf: Config): EMailSender = {
    new EMailSender(Try(pureconfig.loadConfigOrThrow[EMailConf](conf)))
  }
}