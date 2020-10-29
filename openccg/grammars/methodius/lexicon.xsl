<?xml version="1.0" encoding="utf-8"?>


See core-en-lexicon.xsl for comments re grammar.

The semantic roles are taken from FrameNet where possible.

NB: These namespace declarations seem to work with the version of Xalan 
    that comes with JDK 1.4.  In Xalan 2.5, the redirect namespace is 
    supposed to be declared as http://xml.apache.org/xalan/redirect, 
    but giving the classname (magically) seems to work.  
    With newer versions of Xalan, different namespace declarations may be required. 
-->
<xsl:transform 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xalan2="http://xml.apache.org/xslt"
  xmlns:redirect="org.apache.xalan.lib.Redirect" 
  extension-element-prefixes="redirect"
  exclude-result-prefixes="xalan xalan2">

  
  <!-- ***** Import Core Lexicon Definitions ***** -->
  <xsl:include href="core-en-lexicon.xsl"/>

  
  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>

  
  <!-- ***** Start Output Here ***** -->
  <xsl:template match="/">
  <ccg-lexicon name="crag" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="lexicon.xsd"
  >

  <!-- ***** Feature Declarations ******  -->
  <xsl:call-template name="add-feature-declarations"/>
  
