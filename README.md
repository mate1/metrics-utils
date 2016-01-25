# Metrics Utilities

Metrics Utilities provides a Scala wrapper around [dropwizard.io/metrics](https://github.com/dropwizard/metrics/) using InfluxDB as a data store.
It is designed to help you use Metrics throughout your system easily by having a single Scala Object as an interface.

## Usage

```Scala
MetricsUtil.enableInfluxDbReporting("influxdb", "database", "username", "password")

MetricsUtil.getTimer("MetricName1", TimeUnit.MILLISECONDS, TimeUnit.MINUTES)
MetricsUtil.getCounter("MetricName2")
MetricsUtil.getHistogram("MetricName3")
```