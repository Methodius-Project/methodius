<?xml version="1.0"?>
<!-- 
Copyright (C) 2003-5 University of Edinburgh (Michael White)
$Revision: 1.8 $, $Date: 2005/07/18 21:19:45 $ 

NB: These namespace declarations seem to work with the version of Xalan 
    that comes with JDK 1.4.  With newer versions of Xalan, 
    different namespace declarations may be required. 
-->
<xsl:transform 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xalan2="http://xml.apache.org/xslt"
  exclude-result-prefixes="xalan xalan2">

  
  <!-- ***** Unary Rules ***** -->
  
  <xsl:template name="add-unary-rules">

  <!-- Reduced relatives -->
  <typechanging name="rrel">
    <arg>
      <complexcat>
        <atomcat type="s">
          <fs id="1">
            <feat val="dcl" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash dir="/"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
      </complexcat>
    </arg>
    <result>
      <complexcat>
        <atomcat type="n">
          <fs inheritsFrom="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
            <feat attr="case">
              <featvar name="CASE"/>
            </feat>
          </fs>
        </atomcat>
        <slash mode="*" dir="\"/>
        <atomcat type="n">
          <fs inheritsFrom="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
            <feat attr="case">
              <featvar name="CASE"/>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X">
            <diamond mode="RedGenRel">
              <nomvar name="E"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </result>

  </typechanging>

<!-- Passive Reduced Relative -->
  <typechanging name="prrel">
    <arg>
      <complexcat>
        <atomcat type="s">
          <fs id="1">
            <feat val="ppart" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash dir="\"/>
        <atomcat type="np">
          <fs id="2">
            <feat val="3rd" attr="pers"/>
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
      </complexcat>
    </arg>
    <result>
      <complexcat>
        <atomcat type="n">
          <fs inheritsFrom="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
           <feat attr="case">
              <featvar name="CASE"/>
            </feat>
          </fs>
        </atomcat>
        <slash mode="*" dir="\"/>
        <atomcat type="n">
          <fs inheritsFrom="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
            <feat attr="case">
              <featvar name="CASE"/>
            </feat> 
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X">
            <diamond mode="GenRel">
              <nomvar name="E"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </result>
  </typechanging>


<!-- Non-Restrictive Relative -->
  <typechanging name="non-r-prrel">
    <arg>
      <complexcat>
        <atomcat type="s">
          <fs id="1">
            <feat val="ppart" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash dir="\"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
      </complexcat>
    </arg>
    <result>
      <complexcat>
        <atomcat type="np">
          <fs inheritsFrom="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
            <feat attr="case">
              <featvar name="CASE"/>
            </feat>
          </fs>
        </atomcat>
        <slash mode="*" dir="\"/>
        <atomcat type="np">
          <fs inheritsFrom="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
            <feat attr="case">
              <featvar name="CASE"/>
            </feat> 
          </fs>
        </atomcat>
        <slash mode="*" dir="\"/>
	<atomcat type="punct">
	  <fs attr="lex" val=","/>
	</atomcat>
	<slash mode="*" dir="/"/>
	<atomcat type="punct">
	  <fs attr="lex" val=","/>
	</atomcat>
        <lf>
          <satop nomvar="X">
            <diamond mode="CoreInv">
              <nomvar name="P"/>
	      <prop name="elab-rel"/>
	      <diamond mode="Trib">
		<nomvar name="E"/>
	      </diamond>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </result>
  </typechanging>

  <typechanging name="is-non-r-rrel">
    <arg>
      <atomcat type="np">
	<fs id="2">
	  <feat attr="index">
	    <lf>
	      <nomvar name="X"/>
	    </lf>
	  </feat>
	</fs>
      </atomcat>
    </arg>
    <result>
      <complexcat>
	<atomcat type="np">
	  <fs inheritsFrom="2">
	    <feat attr="index">
	      <lf>
		<nomvar name="X"/>
	      </lf>
	    </feat>
	    <feat attr="case">
	      <featvar name="CASE"/>
	    </feat>
	  </fs>
	</atomcat>
	<slash mode="*" dir="/"/>
	<atomcat type="punct">
	  <fs attr="lex" val=","/>
	</atomcat>
	<slash mode="*" dir="/"/>
	<atomcat type="np">
<!--	  <fs inheritsFrom="2">-->
<fs id="5">
	    <feat attr="index">
	      <lf>
		<nomvar name="Y"/>
	      </lf>
	    </feat>
	    <feat attr="case">
	      <featvar name="CASE"/>
	    </feat> 
	  </fs>
	</atomcat>
	<slash mode="*" dir="/"/>
	<atomcat type="punct">
	  <fs attr="lex" val=","/>
	</atomcat>
	<lf>
	  <satop nomvar="X">
	    <diamond mode="CoreInv">
	      <nomvar name="P"/>
	      <prop name="elab-rel"/>
	      <diamond mode="Trib">
		<nomvar name="E"/>
	      </diamond>
	    </diamond>
	  </satop>
	  <satop nomvar="E">
	    <prop name="be-verb"/>
	    <!--	    <diamond mode="voice">
	      <prop name="active"/>
	    </diamond>-->
	    <diamond mode="ArgOne">
	      <nomvar name="X"/>
	    </diamond>
	    <diamond mode="ArgTwo">
	      <nomvar name="Y"/>
	    </diamond>
	    
	  </satop>
	</lf>
      </complexcat>
    </result>
  </typechanging>


  <!-- Bare NPs: adds <det>nil to the semantics -->
  <typechanging name="bnp">
    <arg>
      <atomcat type="n">
        <fs id="2">
          <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          <feat attr="num" val="pl-or-mass"/>
        </fs>
      </atomcat>
    </arg>
    <result>
      <atomcat type="np">
        <fs id="2">
          <feat attr="pers" val="3rd"/>
        </fs>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="det"><prop name="nil"/></diamond>
          </satop>
        </lf>
      </atomcat>
    </result>
  </typechanging>

  
  <!-- Canned Text -->

 
  <!-- List completion -->
  <xsl:copy-of select="$s-list"/>
  <xsl:copy-of select="$pred-adj-list-to-adj"/>
  <xsl:copy-of select="$np-list-c"/>
  <xsl:copy-of select="$np-list-d-rightward-TR"/>
  <xsl:copy-of select="$np-list-d-leftward-TR"/>

  <!-- Cardinality prenominals -->
  <typechanging name="card">
    <arg>
      <atomcat type="num">
        <fs>
          <feat attr="index"><lf><nomvar name="Y"/></lf></feat>
          <feat attr="num"><featvar name="NUM"/></feat>
        </fs>
      </atomcat>
    </arg>
    <result>
      <complexcat>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
            <feat attr="num"><featvar name="NUM"/></feat>
          </fs>
        </atomcat>
        <slash dir="/" mode="^"/>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
            <feat attr="num"><featvar name="NUM"/></feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="Card"><nomvar name="Y:num"/></diamond>
          </satop>
        </lf>
      </complexcat>
    </result>
  </typechanging>
  <typechanging name="card-h">
    <arg>
      <atomcat type="num">
        <fs>
          <feat attr="index"><lf><nomvar name="Y"/></lf></feat>
          <feat attr="num" val="pl"/>
        </fs>
      </atomcat>
    </arg>
    <result>
      <atomcat type="n">
        <fs id="2">
          <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          <feat attr="num" val="pl"/>
        </fs>
        <lf>
          <satop nomvar="X:sem-obj">
            <prop name="pro-n"/>
            <diamond mode="Card"><nomvar name="Y:num"/></diamond>
          </satop>
        </lf>
      </atomcat>
    </result>
  </typechanging>

  <!-- purpose clauses -->
  <typechanging name="purp-i">
    <arg> 
      <complexcat>
        <atomcat type="s">
          <fs>
            <feat attr="form" val="inf"/>
            <feat attr="index"><lf><nomvar name="E2"/></lf></feat>
          </fs>
        </atomcat>
        <slash dir="\" mode="&lt;"/>
        <xsl:copy-of select="$np.X"/>
      </complexcat>
    </arg>
    <result>
      <complexcat>
        <atomcat type="s">
          <fs inheritsFrom="1">
            <feat attr="form" val="fronted"/>
            <feat attr="index"><lf><nomvar name="S"/></lf></feat>
          </fs>
        </atomcat>
        <slash dir="/" mode="^"/>
        <atomcat type="s">
          <fs id="1">
            <feat attr="form" val="dcl"/>
            <feat attr="index"><lf><nomvar name="E"/></lf></feat>
          </fs>
        </atomcat>
        <xsl:copy-of select="$S.purpose-rel.Core.E.Trib.E2"/>
      </complexcat>
    </result>
  </typechanging>
  </xsl:template>

</xsl:transform>


