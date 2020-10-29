<?xml version="1.0"?>

<!-- See core-en-lexicon.xsl for comments re grammar.

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



  <family closed="true" pos="V" name="ComeFromVerbs">
    <entry name="Primary">
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
          <satop nomvar="E:proposition">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="ArgOne">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="ArgTwo">
              <nomvar name="Y:location"/>
            </diamond>
          </satop>
	  <satop nomvar="Y">
	    <diamond mode="prep">
	      <prop name="from"/>
	    </diamond>
	  </satop>


        </lf>
      </complexcat>
    </entry>
  </family>



  <family closed="true" pos="V" name="IVPrepVerbs">
    <entry name="Primary">
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
	  </fs>
	</atomcat>
        <lf>
          <satop nomvar="E:situation">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="ArgOne">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="ArgTwo">
              <nomvar name="Y:sem-obj"/>
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
            <diamond mode="ArgOne">
              <nomvar name="X:phys-obj"/>
            </diamond>
            <diamond mode="ArgTwo">
              <nomvar name="Y:date"/>
            </diamond>
          </satop>
	  <satop nomvar="Y">
	    <diamond mode="prep">
	      <prop name="from"/>
	    </diamond>
	  </satop>
        </lf>
      </complexcat>
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
              <diamond mode="ArgTwo">
                <nomvar name="Y:material"/>
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
            <diamond mode="ArgOne">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="ArgTwo">
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
              <diamond mode="ArgTwo">
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
            <diamond mode="ArgOne">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="ArgTwo">
              <nomvar name="Y:location"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>

  <family closed="true" pos="Adj" name="Inscribed-Adj">
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
	    <feat val="with" attr="lex"/>
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
            <diamond mode="InscribedWithInv">
              <nomvar name="P:proposition"/>
              <prop name="[*DEFAULT*]"/>
              <diamond mode="ArgTwo">
                <nomvar name="Y:inscription-says"/>
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
	    <feat val="with" attr="lex"/>
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
            <diamond mode="ArgOne">
              <nomvar name="X:sem-obj"/>
            </diamond>
            <diamond mode="ArgTwo">
              <nomvar name="Y:inscription-says"/>
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
	    <feat attr="voice" val="passive"/>
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
	      <feat attr="form" val="ppart"/>
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






  <family indexRel="ArgTwo" closed="true" pos="Prep" name="Prep-Filler">
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
        <slash dir="\" mode="."/>
        <atomcat type="s">
          <fs id="1">
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="/"/>
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
            <diamond mode="ArgTwo">
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


<!--  <family indexRel="ArgThree" pos="Prep" name="Second-Prep-Filler">
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
        <slash dir="\" mode="."/>
	<atomcat type="s">
	  <fs id="1">
	    <feat attr="index">
	      <lf>
		<nomvar name="E"/>
	      </lf>
	    </feat>
	  </fs>
	</atomcat>
	<slash mode="&lt;" dir="/"/>
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
            <diamond mode="ArgThree">
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



  <family closed="true" pos="V" name="Canned-VP">
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
<!--	    <feat val="active" attr="voice"/>-->
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

        <lf>
          <satop nomvar="E:situation">
            <prop name="[*DEFAULT*]"/>
            <diamond mode="ArgOne">
              <nomvar name="X:sem-obj"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>


<!--      <atomcat type="vp">
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
  </family>-->


<!--  <family closed="true" pos="V" name="CannedTextVerbs">
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
	    <diamond mode="ArgOne">
	      <nomvar name="X:object"/>
	    </diamond>
	    <diamond mode="ArgTwo">
	      <nomvar name="Y:canned-text"/>
	    </diamond>
	  </satop>
	</lf>
      </complexcat>
    </entry>
  </family>-->

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

<family closed="true" pos="Adv" name="Adverb-initial">
    <entry name="Initial">
      <complexcat>
        <atomcat type="s">
          <fs inheritsFrom="1">
<!--            <feat val="fronted" attr="form"/>-->
          </fs>
        </atomcat>
        <slash mode="^" dir="/"/>
        <atomcat type="s">
          <fs id="1">
<!--            <feat val="dcl" attr="form"/>-->
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
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

<family closed="true" pos="Adv" name="Adverb-final">
    <entry name="Initial">
      <complexcat>
        <atomcat type="s">
          <fs inheritsFrom="1">
<!--            <feat val="fronted" attr="form"/>-->
          </fs>
        </atomcat>
        <slash mode="^" dir="\"/>
        <atomcat type="s">
          <fs id="1">
<!--            <feat val="dcl" attr="form"/>-->
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
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

  <family closed="true" pos="Adv" name="Adverb-Comma">
    <entry name="Initial">
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
        <slash mode="*" dir="/"/>
        <atomcat type="punct">
	  <fs>
	    <feat val="," attr="lex"/>
	  </fs>
	</atomcat>
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
    <entry name="Forward">
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
        <slash mode="*" dir="\"/>
        <atomcat type="punct">
	  <fs>
	    <feat val="," attr="lex"/>
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
        <slash mode="*" dir="/"/>
        <atomcat type="punct">
	  <fs>
	    <feat val="," attr="lex"/>
	  </fs>
	</atomcat>

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
    <entry name="Backward">
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
        <slash dir="\"/>
        <atomcat type="s">
          <fs id="1">
            <feat attr="index">
              <lf>
                <nomvar name="E"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="*" dir="\"/>
        <atomcat type="punct">
	  <fs>
	    <feat val="," attr="lex"/>
	  </fs>
	</atomcat>
        <slash mode="*" dir="/"/>
        <atomcat type="punct">
	  <fs>
	    <feat val="," attr="lex"/>
	  </fs>
	</atomcat>

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











  <family indexRel="GenOwner" closed="true" pos="Prep" name="Prep-Poss">
    <entry name="Primary">
      <complexcat>
        <atomcat type="n">
          <fs id="2">
            <feat val="3rd" attr="pers"/>
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="^" dir="\"/>
        <atomcat type="n">
          <fs id="2">
            <feat attr="num">
              <featvar name="NUM:num-vals"/>
            </feat>
            <feat attr="index">
              <lf>
                <nomvar name="X"/>
              </lf>
            </feat>
          </fs>
        </atomcat>
        <slash mode="&lt;" dir="/"/>
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
          <satop nomvar="X:sem-obj">
            <diamond mode="GenOwner">
              <nomvar name="Y:sem-obj"/>
            </diamond>
          </satop>
        </lf>
      </complexcat>
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

