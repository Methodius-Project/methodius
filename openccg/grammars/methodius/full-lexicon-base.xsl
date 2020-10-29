<?xml version="1.0"?>

<!-- See ../core-en/lexicon.xsl for comments re grammar.

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
  <xsl:import href="core-en-lexicon.xsl"/>

  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>
  
  
  <!-- ***** Start Output Here ***** -->
  <xsl:template match="/">
    <ccg-lexicon name="methodius" 
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:noNamespaceSchemaLocation="lexicon.xsd"
		 >
      
      <!-- ***** Feature Declarations ******  -->
<!--      <xsl:call-template name="add-feature-declarations"/>-->
      
      <!-- ***** Relation Sorting ******  -->
      <relation-sorting order=
			"BoundVar PairedWith
			 Restr Body 
			 Det Card Num 
			 Arg Arg1 Arg2 Of
			 Core Trib
			 First Last List EqL
			 Agent Experiencer Fig FigInv Owner
			 Artifact Creator ElementOf Cognizer Communicator Perceiver 
			 *
			 Beneficiary Ground Poss Pred Prop Situation
			 Chosen Content Material Phenomenon Referent Where
			 Source
			 Location 
			 HasProp GenOwner
			 GenRel Next"/>
      
      
      <!-- ***** Derived Categories and Families ***** -->
      <xsl:call-template name="add-core-families"/>
      

      <xsl:variable name="E.Default.Speaker.X">  
	<lf>
	  <satop nomvar="E:statement">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Speaker"><nomvar name="X:causal-agent"/></diamond>
	  </satop>
	</lf>
      </xsl:variable>

      <xsl:variable name="E1.Default.Speaker.X">  
	<lf>
	  <satop nomvar="E:statement">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Speaker"><nomvar name="X:causal-agent"/></diamond>
	  </satop>
	</lf>
      </xsl:variable>


      <xsl:variable name="E.Default.Speaker.X.Message.E2">  
	<xsl:call-template name="extend">
	  <xsl:with-param name="elt" select="xalan:nodeset($E.Default.Speaker.X)/*"/>
	  <xsl:with-param name="ext">
	    <satop nomvar="E:statement">
	      <diamond mode="Message"><nomvar name="E2:situation"/></diamond>
	    </satop>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:variable>

      <family name="Attitude" pos="V" closed="true">
	<entry name="TV-SComp-Dcl">
	  <xsl:call-template name="extend">
	    <xsl:with-param name="elt" select="xalan:nodeset($tv.scomp.dcl)/*"/>
	    <xsl:with-param name="ext" select="$E.Default.Speaker.X.Message.E2"/>
	  </xsl:call-template>
	</entry>
	<entry name="TV-SComp-Emb">
	  <xsl:call-template name="extend">
	    <xsl:with-param name="elt" select="xalan:nodeset($tv.scomp.emb)/*"/>
	    <xsl:with-param name="ext" select="$E.Default.Speaker.X.Message.E2"/>
	  </xsl:call-template>
	</entry>
      </family>



      <!-- Adv -->

      <xsl:variable name="E.Modifier.M.Default">
	<lf>
	  <satop nomvar="E">
	    <diamond mode="Modifier">
	      <nomvar name="M"/>
	      <prop name="[*DEFAULT*]"/>
	    </diamond>
	  </satop>
	</lf>
      </xsl:variable>


  <family closed="true" pos="Adv" name="adv-and-but-also">
    <entry name="Primary">
      <complexcat>
        <atomcat type="s">
          <fs inheritsFrom="1">
            <feat val="fronted" attr="form"/>
          </fs>
        </atomcat>
        <slash mode="^" dir="/"/>
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
        <lf>
          <satop nomvar="E">
            <diamond mode="Modifier">
              <nomvar name="M"/>
              <prop name="[*DEFAULT*]"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>



  <family closed="true" pos="Adv" name="adv-initial">
    <entry name="Primary">
      <complexcat>
        <atomcat type="s">
          <fs inheritsFrom="1"/>
        </atomcat>
        <slash dir="/"/>
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
        <lf>
          <satop nomvar="E">
            <diamond mode="Modifier">
              <nomvar name="M"/>
              <prop name="[*DEFAULT*]"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>
  <!--      <family name="adv-initial" pos="Adv" closed="true">
       <entry name="Primary">
       <xsl:call-template name="extend">
       <xsl:with-param name="elt" select="xalan:nodeset($adv.initial)/*"/>
       <xsl:with-param name="ext" select="$E.Modifier.M.Default"/>
       </xsl:call-template>
       </entry>
       </family>-->

  <family closed="true" pos="Adv" name="adv-initial-final">
    <entry name="Primary">
      <complexcat>
	<atomcat type="s">
	  <fs inheritsFrom="1"/>
	</atomcat>
	<slash mode=">" dir="/"/>
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
        <lf>
          <satop nomvar="E">
            <diamond mode="Modifier">
              <nomvar name="M"/>
              <prop name="[*DEFAULT*]"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
    <entry name="final">
      <complexcat>
	<atomcat type="s">
	  <fs inheritsFrom="1"/>
	</atomcat>
	<slash mode=">" dir="\"/>
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
	<lf>
	  <satop nomvar="E">
	    <diamond mode="Modifier">
	      <nomvar name="M"/>
	      <prop name="[*DEFAULT*]"/>
	    </diamond>
	  </satop>
	</lf>
      </complexcat>
    </entry>
  </family>

  <family closed="true" pos="Adv" name="adv-final">
    <entry name="Primary">
      <complexcat>
	<atomcat type="s">
	  <fs inheritsFrom="1"/>
	</atomcat>
	<slash dir="\"/>
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
	<lf>
	  <satop nomvar="E">
	    <diamond mode="Modifier">
	      <nomvar name="M"/>
	      <prop name="[*DEFAULT*]"/>
	    </diamond>
	  </satop>
	</lf>
      </complexcat>
    </entry>
  </family>
  
      <family name="adv-middle" closed="true" pos="Adv">
	<entry name="Primary">
	  <complexcat>
	    <atomcat type="s">
	      <fs id="1">
		<feat attr="index">
		  <lf>
		    <nomvar name="E"/>
		  </lf>
		</feat>
	      </fs>
	    </atomcat>
	    <slash mode="&lt;" dir="\"/>
	    <atomcat type="np">
	      <fs id="2">
		<feat attr="index">
		  <lf>
		    <nomvar name="X"/>
		  </lf>
		</feat>
	      </fs>
	    </atomcat>
	    <slash mode="^" dir="/"/>
	    <complexcat>
	      <atomcat type="s">
		<fs id="1">
		  <feat attr="index">
		    <lf>
		      <nomvar name="E"/>
		    </lf>
		  </feat>
		</fs>
	      </atomcat>
	      <slash mode="&lt;" dir="\"/>
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
	    <lf>
	      <satop nomvar="E:situation">
		<diamond mode="HasProp">
		  <nomvar name="P:proposition"/>
		  <prop name="[*DEFAULT*]"/>
		</diamond>
	      </satop>
	    </lf>
	  </complexcat>
	</entry>
      </family>

<!--      <xsl:variable name="E.Default.Opinion.X.Content.E2">  
	<lf>
	  <satop nomvar="E:statement">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Speaker"><nomvar name="X:person"/></diamond>
	    <diamond mode="Content"><nomvar name="Y:sem-obj"/></diamond>
	  </satop>
	</lf>
      </xsl:variable>-->


<!--      <family name="OpinionVerbs" pos="V" closed="true">
	<entry name="Primary">
	  <xsl:call-template name="extend">
	    <xsl:with-param name="elt" select="xalan:nodeset($tv)/*"/>
	    <xsl:with-param name="ext" select="$E.Default.Opinion.X.Content.E2"/>
	  </xsl:call-template>
	</entry>
      </family>-->

      <xsl:variable name="E.Default.Object.X.Content.E2">
	<lf>
	  <satop nomvar="E:situation">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Object"><nomvar name="X:phys-obj"/></diamond>
	    <diamond mode="Content"><nomvar name="Y:sem-obj"/></diamond>
	  </satop>
	</lf>
      </xsl:variable>

  <family closed="true" pos="V" name="ComeFromVerbs">
    <entry name="Primary">
      <complexcat>
        <atomcat type="s">
          <fs id="1">
            <feat attr="num">
              <featvar name="NUM:num-vals"/>
            </feat>
            <feat attr="pers">
              <featvar name="PERS:pers-vals"/>
            </feat>
            <feat val="dcl" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="\"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="num">
              <featvar name="NUM:num-vals"/>
            </feat>
            <feat attr="pers">
              <featvar name="PERS:pers-vals"/>
            </feat>
            <feat val="nom" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&gt;" dir="/"/>
        <atomcat type="pp">
          <fs id="3">
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
            <feat val="from" attr="lex"/>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="E:situation">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="Object">
              <nomvar name="X:phys-obj"/>
            </diamond>
            <diamond mode="From">
              <nomvar name="Y:location"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>

  <family closed="true" pos="V" name="DateFromVerbs">
    <entry name="Primary">
      <complexcat>
        <atomcat type="s">
          <fs id="1">
            <feat attr="num">
              <featvar name="NUM:num-vals"/>
            </feat>
            <feat attr="pers">
              <featvar name="PERS:pers-vals"/>
            </feat>
            <feat val="dcl" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="\"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="num">
              <featvar name="NUM:num-vals"/>
            </feat>
            <feat attr="pers">
              <featvar name="PERS:pers-vals"/>
            </feat>
            <feat val="nom" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&gt;" dir="/"/>
        <atomcat type="pp">
          <fs id="3">
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
            <feat val="from" attr="lex"/>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="E:situation">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="Object">
              <nomvar name="X:phys-obj"/>
            </diamond>
            <diamond mode="From">
              <nomvar name="Y:time"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>




      <!-- Booking (cf. FrameNet's Commerce): has Agent and Beneficiary, in addition to Goods --> 
      <xsl:variable name="E.Default.Agent.X">  
	<lf>
	  <satop nomvar="E:action">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Agent"><nomvar name="X:causal-agent"/></diamond>
	  </satop>
	</lf>
      </xsl:variable>

      <xsl:variable name="E.Default.Agent.X.Goods.Y">  
	<xsl:call-template name="extend">
	  <xsl:with-param name="elt" select="xalan:nodeset($E.Default.Agent.X)/*"/>
	  <xsl:with-param name="ext">
	    <satop nomvar="E:action">
	      <diamond mode="Object"><nomvar name="Y:phys-obj"/></diamond>
	    </satop>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:variable>
      
      <xsl:variable name="E.Default.Agent.X.Goods.Y.Beneficiary.Z">  
	<xsl:call-template name="extend">
	  <xsl:with-param name="elt" select="xalan:nodeset($E.Default.Agent.X.Goods.Y)/*"/>
	  <xsl:with-param name="ext">
	    <satop nomvar="E:action">
	      <diamond mode="Beneficiary"><nomvar name="Z:causal-agent"/></diamond>
	    </satop>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:variable>
      
      <xsl:variable name="E.Default.Agent.X.Beneficiary.Y.Goods.Z">  
	<xsl:call-template name="extend">
	  <xsl:with-param name="elt" select="xalan:nodeset($E.Default.Agent.X)/*"/>
	  <xsl:with-param name="ext">
	    <satop nomvar="E:action">
	      <diamond mode="Beneficiary"><nomvar name="Y:causal-agent"/></diamond>
	      <diamond mode="Object"><nomvar name="Z:phys-obj"/></diamond>
	    </satop>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:variable>

      
      <family name="Ditransitive" pos="V" closed="true">
	<entry name="DTV">
	  <xsl:call-template name="extend">
	    <xsl:with-param name="elt" select="xalan:nodeset($dtv)/*"/>
	    <xsl:with-param name="ext" select="$E.Default.Agent.X.Goods.Y.Beneficiary.Z"/>
	  </xsl:call-template>
	</entry>
      </family>

      <family name="Ditransitive-To" pos="V" closed="true">
	<entry name="DTV-To">
	  <xsl:call-template name="extend">
	    <xsl:with-param name="elt" select="xalan:nodeset($dtv.to)/*"/>
	    <xsl:with-param name="ext" select="$E.Default.Agent.X.Beneficiary.Y.Goods.Z"/>
	  </xsl:call-template>
	</entry>
      </family>


      <family name="Intensifier" pos="Adv" closed="true">
	<entry name="primary">
	  <complexcat>
	    <atomcat type="n">
	      <fs id="2">
		<feat attr="index">
		  <lf>
		    <nomvar name="X"/>
		  </lf>
		</feat>
	      </fs>
	    </atomcat>
	    <slash mode="^" dir="/"/>
	    <atomcat type="n">
	      <fs id="2">
		<feat attr="index">
		  <lf>
		    <nomvar name="X"/>
		  </lf>
		</feat>
	      </fs>
	    </atomcat>
	    <slash mode="^" dir="/"/>
	    <complexcat>
	      <atomcat type="n">
		<fs id="2">
		  <feat attr="index">
		    <lf>
		      <nomvar name="X"/>
		    </lf>
		  </feat>
		</fs>
	      </atomcat>
	      <slash mode="^" dir="/"/>
	      <atomcat type="n">
		<fs id="2">
		  <feat attr="index">
		    <lf>
		      <nomvar name="X"/>
		    </lf>
		  </feat>
		</fs>
	      </atomcat>
	    </complexcat>
	    <lf>
	      <satop nomvar="X:sem-obj">
		<diamond mode="HasProp">
		  <nomvar name="P:quality"/>
		  <prop name="[*DEFAULT*]"/>
		</diamond>
	      </satop>
	    </lf>
	  </complexcat>
	</entry>
	<entry name="Predicative">
	  <complexcat>
	    <atomcat type="s">
	      <fs>
		<feat val="adj" attr="form"/>
		<feat attr="index">
		  <lf>
		    <nomvar name="Y"/>
		  </lf>
		</feat>
	      </fs>
	    </atomcat>
	    <slash ability="inert" mode="&lt;" dir="\"/>
	    <atomcat type="np">
	      <fs id="2">
		<feat attr="index">
		  <lf>
		    <nomvar name="X"/>
		  </lf>
		</feat>
	      </fs>
	    </atomcat>
	    <slash dir="/"/>
	    <complexcat>
	      <atomcat type="s">
		<fs>
		  <feat val="adj" attr="form"/>
		  <feat attr="index">
		    <lf>
		      <nomvar name="Y"/>
		    </lf>
		  </feat>
		</fs>
	      </atomcat>
	      <slash ability="inert" mode="&lt;" dir="\"/>
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
	    <lf>
	      <satop nomvar="Y:quality">
		<diamond mode="HasProp">
		  <nomvar name="P:quality"/>
		  <prop name="[*DEFAULT*]"/>
		</diamond>
	      </satop>
	    </lf>
	  </complexcat>
	</entry>
      </family>

      <family name="V-inf" pos="V" closed="true">
	<entry name="Primary">
	  <complexcat>
	    <atomcat type="s">
	      <fs id="1">
		<feat attr="form" val="dcl"/>
		<feat attr="index"><lf><nomvar name="E"/></lf></feat>
	      </fs>
	    </atomcat>
	    <slash dir="\"/>
	    <xsl:copy-of select="$np.2.X.nom.default"/>	
	    <slash dir="/"/>
	    <complexcat>
	      <atomcat type="s">
		<fs id="3">
		  <feat attr="form" val="inf"/>
		  <feat attr="index"><lf><nomvar name="E1"/></lf></feat>
		</fs>
	      </atomcat>
	      <slash dir="\"/>
	      <xsl:copy-of select="$np.X"/>
	    </complexcat>
	    <xsl:copy-of select="$E.Default.Speaker.X"/>
	  </complexcat>
	</entry>
      </family>





      <family indexRel="GenRel" closed="true" pos="RelPro" name="WhenRelPro">
	<entry name="Primary">
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
	    <setarg>
	      <slash mode="*" dir="\"/>
	      <atomcat type="n">
		<fs id="2">
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
<!--	      <complexcat>-->
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
	    </setarg>
	    <lf>
	      <satop nomvar="X:sem-obj">
		<diamond mode="GenRel">
		  <nomvar name="E:situation"/>
		</diamond>
	      </satop>
	    </lf>
	  </complexcat>
	</entry>
      </family>



<!--      <family name="Crag-Canned-Sentence" pos="S" closed="true">
	<entry name="Primary">
	  <atomcat type="s">
	    <fs id="1">
	      <feat attr="index">
		<lf>
		  <nomvar name="E"/>
		</lf>
	      </feat>
	    </fs>
	    <lf>
	      <satop nomvar="E:sem-obj">
		<prop name="[*DEFAULT*]"/>
	      </satop>
	    </lf>
	  </atomcat>
	</entry>
      </family>

      <family name="Crag-Canned-Utterance" pos="S" closed="true">
	<entry name="Primary">
	  <atomcat type="s">
	    <fs id="1">
	      <feat attr="index">
		<lf>
		  <nomvar name="E"/>
		</lf>
	      </feat>
	    </fs>
	    <lf>
	      <satop nomvar="E:sem-obj">
		<prop name="[*DEFAULT*]"/>
	      </satop>
	    </lf>
	  </atomcat>
	</entry>
      </family>




      <family name="Crag-Canned-NP" pos="NP" closed="true">
	<entry name="Primary">
	  <atomcat type="np">
	    <fs id="1">
	      <feat attr="num" val="sg"/>
	      <feat val="3rd" attr="pers"/>
	      <feat attr="index">
		<lf>
		  <nomvar name="E"/>
		</lf>
	      </feat>
	    </fs>
	    <lf>
	      <satop nomvar="E:sem-obj">
		<prop name="[*DEFAULT*]"/>
	      </satop>
	    </lf>
	  </atomcat>
	</entry>
      </family>-->




	
  <family closed="true" pos="Adj" name="Made-Adj">
    <entry name="Primary">
      <complexcat>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="\"/>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="/"/>
        <atomcat type="pp">
          <fs id="3">
	    <feat val="of" attr="lex"/>
            <feat val="acc" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="MadeOfInv">
              <nomvar name="P:proposition"/>
              <prop name="[*DEFAULT*]"/>
              <diamond mode="Material">
                <nomvar name="Y:sem-obj"/>
              </diamond>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
    <entry name="Predicative">
      <complexcat>
        <atomcat type="s">
          <fs>
            <feat val="adj" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="P"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash ability="inert" mode="&lt;" dir="\"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="/"/>
        <atomcat type="pp">
          <fs id="3">
	    <feat val="of" attr="lex"/>
            <feat val="acc" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="P:proposition">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="MadeOf">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="Material">
              <nomvar name="Y:material"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>

  <family closed="true" pos="Adj" name="Located-Adj">
    <entry name="Primary">
      <complexcat>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="\"/>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="/"/>
        <atomcat type="pp">
          <fs id="3">
	    <feat val="in" attr="lex"/>
            <feat val="acc" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="LocatedInInv">
              <nomvar name="P:proposition"/>
              <prop name="[*DEFAULT*]"/>
              <diamond mode="Location">
                <nomvar name="Y:location"/>
              </diamond>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
    <entry name="Predicative">
      <complexcat>
        <atomcat type="s">
          <fs>
            <feat val="adj" attr="form"/>
            <feat attr="index">
              <lf>
                <nomvar name="P"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash ability="inert" mode="&lt;" dir="\"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="/"/>
        <atomcat type="pp">
          <fs id="3">
	    <feat val="in" attr="lex"/>
            <feat val="acc" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="P:proposition">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="LocatedIn">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="Location">
              <nomvar name="Y:location"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>


  <!-- =============== Passive Auxiliary "be" family =============== -->
  <!-- 
       The passive auxiliary "be" changes the form feature from 
       passive (pss) to declarative (dcl), and adds tense info.
       
       s[E,dcl]\np[X]/(s[E,pss]\np[X])
  -->
  <family name="PassiveBe" pos="Aux" closed="true" indexRel="tense">
    <entry name="Primary">
      <complexcat>
	<atomcat type="s">
	  <fs id="1">
	    <feat attr="index">
	      <lf>
		<nomvar name="E"/>
	      </lf>
	    </feat>
	    <feat attr="form" val="dcl"/>
	  </fs>
	</atomcat>
	<slash dir="\" mode="&lt;"/>
	<atomcat type="np">
	  <fs id="2">
	    <feat attr="num">
	      <featvar name="NUM"/>
	    </feat>
	    <feat attr="pers">
	      <featvar name="PERS:pers-vals"/>
	    </feat>
	    <feat attr="case" val="nom"/>
	    <feat attr="index">
	      <lf>
		<nomvar name="X"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
	<slash dir="/" mode="^"/>
	<complexcat>
	  <atomcat type="s">
	    <fs>
	      <feat attr="index">
		<lf>
		  <nomvar name="E"/>
		</lf>
	      </feat>
	      <feat attr="form" val="pss"/>
	    </fs>
	  </atomcat>
	  <slash dir="\" mode="&lt;"/>
	  <atomcat type="np">
	    <fs id="2"/>
	  </atomcat>
	</complexcat>
      </complexcat>
    </entry>
  </family>
  <family name="TransitiveVerbs" pos="V" closed="true">
    <entry name="Primary">
      <complexcat>
	<atomcat type="s">
	  <fs id="1">
	    <feat attr="index">
	      <lf>
		<nomvar name="E"/>
	      </lf>
	    </feat>
	    <feat attr="form" val="dcl"/>
	  </fs>
	</atomcat>
	<slash dir="\" mode="&lt;"/>
	<atomcat type="np">
	  <fs id="2">
	    <feat attr="num">
	      <featvar name="NUM"/>
	    </feat>
	    <feat attr="pers">
	      <featvar name="PERS:pers-vals"/>
	    </feat>
	    <feat attr="case" val="nom"/>
	    <feat attr="index">
	      <lf>
		<nomvar name="X"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
	<slash dir="/" mode="&gt;"/>
	<atomcat type="np">
	  <fs id="3">
	    <feat attr="case" val="acc"/>
	    <feat attr="index">
	      <lf>
		<nomvar name="Y"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
	<lf>
	  <satop nomvar="E:action">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Actor">
	      <nomvar name="X:animate-being"/>
	    </diamond>
	    <diamond mode="Patient">
	      <nomvar name="Y:sem-obj"/>
	    </diamond>
	  </satop>
	</lf>
      </complexcat>
    </entry>
  </family>

  <family indexRel="Filler" closed="true" pos="Prep" name="Prep-Filler">
    <entry name="Primary">
      <complexcat>
        <atomcat type="s">
          <fs id="1">
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash dir="\" mode="*"/>
	<atomcat type="np">
          <fs id="2">
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash dir="\" mode="*"/>
        <complexcat>
          <atomcat type="s">
            <fs id="1">
<!--              <feat attr="index">
                <lf>
                  <nomvar name="E"/>
                </lf>
              </feat>-->
              <feat val="pss" attr="form"/>
            </fs>
          </atomcat>
          <slash mode="&lt;" dir="\"/>
          <atomcat type="np">
            <fs id="2"/>
          </atomcat>
        </complexcat>
        <slash mode="*" dir="/"/>
        <atomcat type="np">
          <fs id="3">
            <feat val="acc" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="E:action">
            <diamond mode="Filler">
              <nomvar name="Y:sem-obj"/>
            </diamond>
          </satop>
	  <satop nomvar="Y">
	    <diamond mode="prep">
	      <prop name="[*DEFAULT*]"/>
	    </diamond>
	  </satop>
        </lf>
      </complexcat>
    </entry>
  </family>

<!--  <family indexRel="Filler" closed="true" pos="Prep" name="Prep-Filler">
    <entry name="Primary">
      <complexcat>
        <atomcat type="s">
          <fs inheritsFrom="1">
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash dir="\" mode="*"/>
        <atomcat type="s">
          <fs id="1">
	    <feat attr="form" val="pss"/>
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="*" dir="/"/>
        <atomcat type="np">
          <fs id="3">
            <feat val="acc" attr="case"/>
            <feat attr="index">
              <lf>
                <nomvar name="Y"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="E:situation">
            <diamond mode="Filler">
              <nomvar name="Y:sem-obj"/>
            </diamond>
          </satop>
	  <satop nomvar="Y">
	    <diamond mode="prep">
	      <prop name="[*DEFAULT*]"/>
	    </diamond>
	  </satop>
        </lf>
      </complexcat>
    </entry>
  </family>-->


<family closed="true" pos="V" name="CannedTextVerb">
    <entry name="Primary">
      <complexcat>
	<atomcat type="s">
	  <fs id="1">
	    <feat attr="index">
	      <lf>
		<nomvar name="E"/>
	      </lf>
	    </feat>
	    <feat attr="form" val="dcl"/>
	  </fs>
	</atomcat>
	<slash dir="\" mode="&lt;"/>
	<atomcat type="np">
	  <fs id="2">
	    <feat attr="num">
	      <featvar name="NUM"/>
	    </feat>
	    <feat attr="case" val="nom"/>
	    <feat attr="index">
	      <lf>
		<nomvar name="X"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
	<slash dir="/" mode="&gt;"/>
	<atomcat type="np">
	  <fs id="3">
	    <feat attr="case" val="acc"/>
	    <feat attr="index">
	      <lf>
		<nomvar name="Y"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
	<lf>
	  <satop nomvar="E:action">
	    <prop name="[*DEFAULT*]"/>
	    <diamond mode="Object">
	      <nomvar name="X:sem-obj"/>
	    </diamond>
	    <diamond mode="Canned-Text">
	      <nomvar name="Y:canned-text"/>
	    </diamond>
	  </satop>
	</lf>
      </complexcat>
    </entry>
  </family>

  <family name="Canned-NP" pos="NP" closed="true">
    <entry name="Primary">
      <atomcat type="np">
	<fs id="1">
	  <feat attr="num" val="sg"/>
	  <feat val="3rd" attr="pers"/>
	  <feat attr="index">
	    <lf>
	      <nomvar name="E"/>
	    </lf>
	  </feat>
	</fs>
	<lf>
	  <satop nomvar="E:sem-obj">
	    <prop name="[*DEFAULT*]"/>
	  </satop>
	</lf>
      </atomcat>
    </entry>
  </family>
  
	


  <family closed="true" pos="RelPro" name="RelPro">
    <entry indexRel="GenRel" name="Primary">
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
	<setarg>
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
	  <slash mode="*" dir="/"/>
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
	    <slash dir="|"/>
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
	</setarg>
	<lf>
	  <satop nomvar="X">
	    <diamond mode="GenRel">
	      <nomvar name="E"/>
	    </diamond>
	  </satop>
	</lf>
      </complexcat>
    </entry>
    <entry indexRel="CoreInv" name="Non-Restrictive">
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
	<slash dir="/" mode="^"/>
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
	  <slash dir="\"/>
	  <atomcat type="np">
	    <fs id="2">
	      <!--            <feat val="3rd" attr="pers"/>-->
	      <feat attr="index">
		<lf>
		  <nomvar name="X"/>
		</lf>
	      </feat>
	    </fs>
	  </atomcat>
	</complexcat>
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
    </entry>
    <entry indexRel="CoreInv" name="Non-Restrictive2">
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
<!--	<slash mode="*" dir="/"/>
	<atomcat type="punct">
	  <fs attr="lex" val=","/>
	</atomcat>-->
	<slash dir="/" mode="^"/>
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
	  <slash dir="\"/>
	  <atomcat type="np">
	    <fs id="2">
	      <!--            <feat val="3rd" attr="pers"/>-->
	      <feat attr="index">
		<lf>
		  <nomvar name="X"/>
		</lf>
	      </feat>
	    </fs>
	  </atomcat>
	</complexcat>
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
    </entry>
  </family>

<family name="Comparator" pos="Comparator" closed="true">
  <entry name="Primary">
    <complexcat>
      <atomcat type="s">
	<fs inheritsFrom="2">
	  <feat attr="index">
	    <lf>
	      <nomvar name="C"/>
	    </lf>
	  </feat>
	</fs>
      </atomcat>
      <slash dir="/" mode="*"/>
<!--      <complexcat>
	<atomcat type="s">
	  <fs id="2">
	    <feat attr="form" val="dcl"/>
	  </fs>
	</atomcat>
	<slash dir="\" mode="."/>
	<atomcat type="np">
	<fs id="4">
	  <feat attr="index">
	    <lf>
	      <nomvar name="Y"/>
	    </lf>
	  </feat>
	</fs>
	</atomcat>
      </complexcat>-->
	<atomcat type="s">
	  <fs id="2">
	    <feat attr="form" val="dcl"/>
	    <feat attr="index">
	      <lf>
		<nomvar name="Y"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
<!--      <slash dir="/" mode="*"/>
      <atomcat type="np">
	<fs id="4">
	  <feat attr="index">
	    <lf>
	      <nomvar name="Y"/>
	    </lf>
	  </feat>
	</fs>
      </atomcat>-->
      <slash dir="/" mode="*"/>
      <atomcat type="punct">
	<fs attr="lex" val=","/>
      </atomcat>
      <slash dir="/" mode="*"/>
      <atomcat type="np">
	<fs id="3">
	  <feat attr="index">
	    <lf>
	      <nomvar name="X"/>
	    </lf>
	  </feat>
	</fs>
      </atomcat>
      <lf>
	<satop nomvar="C:situation">
	  <prop name="[*DEFAULT*]"/>
	  <diamond mode="Comparator">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="Focus">
              <nomvar name="Y:situation"/>
            </diamond>
	</satop>
      </lf>
    </complexcat>
  </entry>
</family>


      <family name="Canned-Sentence" pos="S" closed="true">
	<entry name="Primary">
	  <atomcat type="s">
	    <fs id="1">
	      <feat attr="index">
		<lf>
		  <nomvar name="E"/>
		</lf>
	      </feat>
	    </fs>
	    <lf>
	      <satop nomvar="E:canned-text">
		<prop name="[*DEFAULT*]"/>
	      </satop>
	    </lf>
	  </atomcat>
	</entry>
      </family>

  
</ccg-lexicon>

    <!-- ***** Write type changing and lexical factor rules to unary-rules.xml ***** -->
    <redirect:write file="unary-rules.xml">
      <unary-rules>
	<xsl:call-template name="add-unary-rules"/>
	
	<!-- Num Element-Of -->
	<typechanging name="num-elt">
	  <arg>
	    <atomcat type="num">
	      <fs id="2">
		<feat attr="index"><lf><nomvar name="X"/></lf></feat>
	      </fs>
	    </atomcat>
	  </arg>
	  <result>
	    <complexcat>
	      <atomcat type="np">
		<fs inheritsFrom="2">
		  <feat attr="index"><lf><nomvar name="X"/></lf></feat>
		  <feat attr="pers" val="3rd"/>
		</fs>
	      </atomcat>
	      <slash dir="/" mode="^"/>
	      <atomcat type="pp">
		<fs>
		  <feat attr="index"><lf><nomvar name="Y"/></lf></feat>
		  <feat attr="lex" val="of"/>
		</fs>
	      </atomcat>
	      <lf>
		<satop nomvar="X:sem-obj">
		  <diamond mode="det"><prop name="nil"/></diamond>
		  <diamond mode="ElementOf"><nomvar name="Y:sem-obj"/></diamond>
		</satop>
	      </lf>
	    </complexcat>
	  </result>
	</typechanging>
      </unary-rules>
    </redirect:write>
    
  </xsl:template>
  
</xsl:transform>

