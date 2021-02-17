<#if anomalyCount == 1>
  ThirdEye has detected *[an anomaly|${dashboardHost}/app/#/anomalies?anomalyIds=${anomalyIds}]* on the metric <#list metricsMap?keys as id>*${metricsMap[id].name}*</#list> between *${startTime}* and *${endTime}* (${timeZone})
<#else>
  ThirdEye has detected [*${anomalyCount} anomalies*|${dashboardHost}/app/#/anomalies?anomalyIds=${anomalyIds}] on the metrics listed below between *${startTime}* and *${endTime}* (${timeZone})
</#if>
<#list metricToAnomalyDetailsMap?keys as metric>

--------------------------------------
  *Metric:* _${metric}_
  <#list detectionToAnomalyDetailsMap?keys as detectionName>
    <#assign newTable = false>
    <#list detectionToAnomalyDetailsMap[detectionName] as anomaly>
      <#if anomaly.metric==metric>
        <#assign newTable=true>
        <#assign description=anomaly.funcDescription>
      </#if>
    </#list>
    <#if newTable>
      *Description:* ${description}
    </#if>
    <#assign count = 0>
    <#list detectionToAnomalyDetailsMap[detectionName] as anomaly>
      <#if anomaly.metric==metric>
        Problem ${count}:
        Start: [${anomaly.startDateTime} ${anomaly.timezone}|${anomaly.anomalyURL}${anomaly.anomalyId}]
        Duration: ${anomaly.duration}
        Current: ${anomaly.currentVal}
        Predicted: ${anomaly.baselineVal}
        Change: *${anomaly.positiveLift?string('+','')}${anomaly.lift}*

      </#if>
      <#assign count = count + 1>
    </#list>
  </#list>
</#list>

*Reference Links:*
<#if referenceLinks?has_content>
  <#list referenceLinks?keys as referenceLinkKey>
    - [${referenceLinkKey}|${referenceLinks[referenceLinkKey]}]
  </#list>
</#if>