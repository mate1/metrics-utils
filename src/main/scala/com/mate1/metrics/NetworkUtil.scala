package com.mate1.metrics

import java.net.InetAddress
import java.util.logging.Logger

object NetworkUtil {
  // Logger
  val logger: Logger = Logger.getLogger(getClass().getName());
  
  lazy val getReverseCanonicalHostName: String = getCanonicalHostName.split("""\.""").reverse.mkString(".")

  lazy val getCanonicalHostName: String = try {
    InetAddress.getLocalHost().getCanonicalHostName()
  } catch {
    case e: Exception => {
      logger.severe(e.toString)
      "unknown_host"
    }
  }

  lazy val getHostName: String = try {
    InetAddress.getLocalHost().getHostName()
  } catch {
    case e: Exception => {
      logger.severe(e.toString)
      "unknown_host"
    }
  }
}
