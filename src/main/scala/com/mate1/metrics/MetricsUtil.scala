package com.mate1.metrics

import io.dropwizard.metrics.influxdb.{InfluxDbHttpSender, InfluxDbReporter}
import io.dropwizard.metrics._
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions

/**
 * Wrapper for some of the stuff provided by the metrics library.
 *
 * This is not meant (at least not yet) as a generic abstraction,
 * so the APIs we expose are still pretty similar to the metrics ones...
 */

object MetricsUtil extends MetricsUtilTrait

trait MetricsUtilTrait {

  private var metricsReporterType = ""

  /**
    *
    * @return The name of the Metrics Reporter
    */
  def getMetricsReporterType = metricsReporterType

  /**
   * Name of the top-level node in Graphite's hierarchy of logged metrics.
   */
  protected val groupName: String = "from-metrics"

  /**
   * Used by getCallingClassName to filter out this object's function calls from the current stacktrace.
   * 
   * The dollar sign is a scala internals-related implementation detail.
   */
  private val thisClassName = this.getClass().getName().replace("$", "")

  /**
   * Don't fuck around with the getCallingClassName function unless you know what you're doing. - Felix
   */
  private def getCallingClassName: String = new RuntimeException().getStackTrace().dropWhile(element => element.getClassName().contains(thisClassName)).apply(0).getClassName()

  val Metrics: MetricRegistry = new MetricRegistry
  // PUBLIC APIs BELOW:

  def getTimer(name: String, durationUnit: TimeUnit, rateUnit: TimeUnit): Timer = Metrics.timer(
    new MetricName(name, JavaConversions.mapAsJavaMap(Map[String, String](
      "group" -> groupName,
      "class" -> getCallingClassName,
      "hostname" -> NetworkUtil.getReverseCanonicalHostName,
      "durationUnit" -> durationUnit.toString,
      "rateUnit" -> rateUnit.toString
    ))
    )
  )

  def getCounter(name: String): Counter = Metrics.counter(
    new MetricName(name, JavaConversions.mapAsJavaMap(Map[String, String](
      "group" -> groupName,
      "class" -> getCallingClassName,
      "hostname" -> NetworkUtil.getReverseCanonicalHostName
    ))
    )
  )

  def getHistogram(name: String, biased: Boolean = false): Histogram = Metrics.histogram(
    new MetricName(name, JavaConversions.mapAsJavaMap(Map[String, String](
      "group" -> groupName,
      "class" -> getCallingClassName,
      "hostname" -> NetworkUtil.getReverseCanonicalHostName,
      "biased" -> biased.toString
    ))
    )
  )

  def enableReporter(scheduleReporter: ScheduledReporter, metricsReporterType: String): Boolean = scheduleReporter match {
    case null => false
    case reporter => {
      reporter.start(1, TimeUnit.MINUTES)
      this.metricsReporterType = metricsReporterType
      true
    }
  }

  /**
    * Create an InfluxDb Reporter and enable it
    * @param server The InfluxDb server hostname of IP
    * @param port The port InfluxDb is using, default to 8086
    * @param database The database name where the data will be store in influxdb
    * @param user The username to connect to the database
    * @param password The password of the user
    * @return Boolean: Whether the InfluxDbReporter was successfully enabled.
    */
  def enableInfluxDbReporting(server: String, port: Int = 8086, database: String, user: String, password: String): Boolean =
    enableReporter(
      InfluxDbReporter
        .forRegistry(Metrics)
        .build(new InfluxDbHttpSender(server, port, database, user, password)),
      "Influxdb reporter"
    )
}