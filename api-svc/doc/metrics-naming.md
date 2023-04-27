
Metric names and name-spacing are weird and I have only a tenuous grasp on it.

The identity of a Micrometer "meter" is modeled as an `Id` which has a 
`name`, a set of `tags` and a `type`, and other stuff like units and durations.

Ultimately, I believe we're using the `MicrometerMetricsTracker` under the 
covers to create our metrics, particularly the JDBC connection pool.


## Mapping a meter Id to a CloudWatch metric

For AWS CloudWatch, the MetricRegistry configures our CW stuff to push metrics
under a "custom namespace" like `api-svc-<env name>`.  Inside that, the `tag`
drives the next level of metrics grouping.  Micrometer is hardcoded the
tag name to `pool`, we'll consider changing it later to something like 
`jdbc-pool` when we have more pools (http connection pools, custom thread 
pools, etc.)

The `pool` tag value is driven from the Hikari JDBC pool, which we currently
set to `MainJdbcPool` (down the line we'll likely have multiple JDBC connection 
pools - for reading from DB replica's, etc.)

After that, the metric name comes straight from the meter Id defined by 
Micrometer (e.g `hikaricp.connections.acquire`).


## Mapping a metric Id to a JMX metric 

To find a specific Micrometer meter in VisualVM, follow these general steps:

* expand the the `metric` node (not sure what drives this)
* expand the node that matches  meter `type` (i.e. `gauge`, `timer`, etc.)
* Identify the Micrometer base name (e.g., `hikaricp.connections.pending`).
* Convert the base name to camelCase by removing the periods and capitalizing
  the first letter of each subsequent word (e.g., "hikaricpConnectionsPending").
* Identify the tags used in the Micrometer metric. In this case, the tag is
  `pool` (micrometer default tag), with a value of `MainJdbcPool` (custom 
  value for raido).
* Append the tag name and value to the camelCase name, separated by periods
  (e.g., `hikaricpConnectionsPending.pool.MainJdbcPool`).
  Keep in mind that this mapping is specific to the JmxMeterRegistry in
  Micrometer. Other registry implementations may have different formats for
  their metric names. 
