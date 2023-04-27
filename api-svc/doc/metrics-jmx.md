JMX metric publishing is only intended for use in local dev environments, it
is not suitable for observability in a multi-node cloud environment.

## View metrics via JMX in a local dev environment

* configure api-svc so that it is publishing via JMX
  * IMPROVE: document this when it's stable
  * `connectionPoolMetricsEnabled` etc. 
* download VisualVM: https://visualvm.github.io/
* download the VisualVM launcher plugin for IntelliJ: 
  https://visualvm.github.io/
* start the api-svc in VisualVM
  * make sure the api-svc is selected in the "run" widget
  * under the ellipsis, select the `Run 'Api' option that has the VisualVM icon
  under-laid under the run icon
    * when you run it, the IDE tool window should say "RunWithVisualVM"
    * the IDE should also open a new "VisualVM" window 
      * if not, click the VisualVM icon (tooltip: "Start VisualVM")
        * this should open a new window showing VisualVM
* in VisualVM, install the MBeans plugin
  * Open Tools/Plugins
    * go to "Available Plugings"
    * Select and install "VisualVM-MBeans" plugin
* under the `Local` node
* select `raido.apisvc.API`
* in the `MBeans` tab, look under the `metrics` node
  * might need to re-connect to the api-svc if the MBeans tab is not there, 
    after installing the MBeans plugin   

There's no way to make a dashboard of the metrics or anything, in either 
VisualVM or JConsole.  You just have to eyeball the metrics by hand - no, it's 
really not all that useful.

See [metrics-naming.md](./metrics-naming.md) for details on finding individual 
metrics.

