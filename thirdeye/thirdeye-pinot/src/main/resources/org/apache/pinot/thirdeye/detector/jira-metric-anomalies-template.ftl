--------------------------------------
<#if anomalyCount == 1>
  *[Anomaly|${dashboardHost}/app/#/anomalies?anomalyIds=${anomalyIds}]* on <#list metricsMap?keys as id>*${metricsMap[id].name}*</#list> between *${startTime}* and *${endTime}* (${timeZone})
<#else>
  [*${anomalyCount} anomalies*|${dashboardHost}/app/#/anomalies?anomalyIds=${anomalyIds}] on metrics below between *${startTime}* and *${endTime}* (${timeZone})
</#if>
<#list metricToAnomalyDetailsMap?keys as metric>
--------------------------------------
  <#list detectionToAnomalyDetailsMap?keys as detectionName>
    <#assign newTable = false>
    <#list detectionToAnomalyDetailsMap[detectionName] as anomaly>
      <#if anomaly.metric==metric>
        <#assign newTable=true>
        <#assign description=anomaly.funcDescription>
      </#if>
    </#list>
    <#if newTable>
    ${description}
    </#if>
    <#assign firstAnomaly=true>
    <#list detectionToAnomalyDetailsMap[detectionName] as anomaly>
      <#if anomaly.metric==metric>
      <#if firstAnomaly>
    Start: [${anomaly.startDateTime} ${anomaly.timezone}|${anomaly.anomalyURL}${anomaly.anomalyId}]
    Current: ${anomaly.currentVal}
    Expected: ${anomaly.baselineVal}
    Change: *${anomaly.positiveLift?string('+','')}${anomaly.lift}*
      <#assign firstAnomaly=false>
      </#if>

      </#if>
    </#list>
  </#list>
</#list>
<#if referenceLinks?has_content>
  *Reference Links:*
  <#list referenceLinks?keys as referenceLinkKey>
    - [${referenceLinkKey}|${referenceLinks[referenceLinkKey]}]
  </#list>
</#if>