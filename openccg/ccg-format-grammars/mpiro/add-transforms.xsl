<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method='xml' indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="grammar">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>

    <LF-from-XML>
      <transform file="../../grammars/treeify-lists.xsl"/>
      <transform file="../../grammars/convert-to-hlds.xsl"/>
      <transform file="../../grammars/core-en/add-chunks.xsl"/>
    </LF-from-XML>

    <LF-to-XML>
      <transform file="../../grammars/core-en/raise-nodes.xsl"/>
      <transform file="../../grammars/convert-to-graph.xsl"/>
      <transform file="../../grammars/simplify-lists.xsl"/>
    </LF-to-XML>
    </xsl:copy>
    
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
