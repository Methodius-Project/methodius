<?xml version="1.0"?>
<!-- 
Copyright (C) 2003-4 University of Edinburgh (Michael White)
$Revision: 1.4 $, $Date: 2005/07/18 21:19:45 $ 
-->
<xsl:transform 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:set="xalan://java.util.HashSet"
  xmlns:xalan2="http://xml.apache.org/xslt"
  exclude-result-prefixes="set xalan xalan2">

  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>

  <!-- use shared raise-nodes.xsl, primarily -->
  <xsl:include href="../raise-nodes.xsl"/>
  
  <!-- raise nodes coordinated under 'but' --> 
  <xsl:template match="*[prop[@name='but'] and diamond[@mode='Arg1']]">
    <xsl:call-template name="raise-shared-nodes">
      <!-- look for shared nominals under different args -->
      <xsl:with-param name="rel">Arg1</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
</xsl:transform>

