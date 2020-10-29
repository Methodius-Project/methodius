<?xml version="1.0"?>
<!-- 
This transformation adds passive categories corresponding to the 
active ones in the input file.
-->
<xsl:transform 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:xalan2="http://xml.apache.org/xslt"
  exclude-result-prefixes="xalan2">

  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>

  <!-- Add passives to verbal families -->
  <xsl:template match="family[@pos='V']">
    <!-- copy family -->
    <xsl:copy-of select="."/>
    <!-- set $sources to s\np../np entries -->
    <xsl:variable name="sources" 
		  select="entry[complexcat[atomcat[1][@type='s'] and
			  *[2][self::slash and @dir='\'] and
			  *[3][self::atomcat and @type='np'] and
			  *[last()-1][self::atomcat and @type='np'] and
			  *[last()-2][self::slash and @dir='/']]]"/>
    <!-- if $sources non-empty, make family of passive versions -->
    <!-- nb: use PPart (past participle) as part-of-speech -->
    <xsl:if test="$sources">
      <xsl:variable name="members" select="member"/>
      <family name="{concat(@name,'-Passives')}" pos="PPart" closed="true">
        <!-- for each existing s\np../np entry, make a passive one -->
        <!-- passive rule: s[E,dcl]\np[X,nom]$1/np[Z,acc] => s[E,pss]\np[Z]/pp[by,X]$1 : @Z(<top>E) -->
        <xsl:for-each select="$sources">
          <entry name="{concat(@name,'-Passive')}">
            <xsl:for-each select="complexcat"> <!-- change context to complexcat -->
              <complexcat>
                <!-- add result s with form=pss -->
                <xsl:variable name="result" select="atomcat[1]"/>
                <atomcat type="s">
                  <fs id="{$result/fs/@id}">
                    <feat attr="form" val="pss"/>
                    <xsl:copy-of select="$result/fs/feat[@attr != 'form']"/>
                  </fs>
                </atomcat>
                <!-- add \last-np with no case -->
                <slash dir="\" mode="&lt;"/>
                <xsl:variable name="last-np" select="*[last()-1]"/>
                <atomcat type="np">
                  <fs id="{$last-np/fs/@id}">
                    <xsl:copy-of select="$last-np/fs/feat[@attr != 'case']"/>
                  </fs>
                </atomcat>
                <!-- add /pp[by,X] where X is subj-np index -->
                <slash dir="/" mode="."/>
                <xsl:variable name="subj-np" select="*[3]"/>
                <atomcat type="pp">
                  <fs>
                    <feat attr="lex" val="by"/>
                    <xsl:copy-of select="$subj-np/fs/feat[@attr='index']"/>
                  </fs>
                </atomcat>
                <!-- copy any intervening args -->
                <xsl:copy-of select="*[position() &gt; 3 and position() &lt; last()-2]"/>
                <!-- add lf with @Z(<top>E) where Z is last-np index -->
                <xsl:variable name="lf" select="*[last()]"/>
                <xsl:variable name="last-np-index" select="$last-np/fs/feat[@attr='index']/lf/nomvar/@name"/>
                <xsl:variable name="result-index" select="$result/fs/feat[@attr='index']/lf/nomvar/@name"/>
                <lf>
                  <xsl:copy-of select="$lf/*"/>
                  <satop nomvar="{$last-np-index}">
                    <diamond mode="Top"><nomvar name="{$result-index}"/></diamond>
                  </satop>
                </lf>
              </complexcat>
            </xsl:for-each>
          </entry>


        </xsl:for-each>
        <!-- copy members -->
        <xsl:copy-of select="$members"/>
      </family>
      <family name="{concat(@name,'-Passives-NoActor')}" pos="PPart" closed="true">
        <!-- for each existing s\np../np entry, make a passive one -->
        <!-- passive rule: s[E,dcl]\np[X,nom]$1/np[Z,acc] => s[E,pss]\np[Z]/pp[by,X]$1 : @Z(<top>E) -->
        <xsl:for-each select="$sources">
          <entry name="{concat(@name,'-Passive')}">
            <xsl:for-each select="complexcat"> <!-- change context to complexcat -->
              <complexcat>
                <!-- add result s with form=pss -->
                <xsl:variable name="result" select="atomcat[1]"/>
                <atomcat type="s">
                  <fs id="{$result/fs/@id}">
                    <feat attr="form" val="pss"/>
                    <xsl:copy-of select="$result/fs/feat[@attr != 'form']"/>
                  </fs>
                </atomcat>
                <!-- add \last-np with no case -->
                <slash dir="\" mode="&lt;"/>
                <xsl:variable name="last-np" select="*[last()-1]"/>
                <atomcat type="np">
                  <fs id="{$last-np/fs/@id}">
                    <xsl:copy-of select="$last-np/fs/feat[@attr != 'case']"/>
                  </fs>
                </atomcat>
                <!-- copy any intervening args -->

                <xsl:copy-of select="*[position() &gt; 3 and position() &lt; last()-2]"/>
                <!-- add lf with @Z(<top>E) where Z is last-np index -->

                <xsl:variable name="subj-np" select="*[3]"/>
                <xsl:variable name="subj-np-index" select="$subj-np/fs/feat[@attr='index']/lf/nomvar/@name"/>
		<xsl:variable name="lf" select="*[last()]"/>

		<!-- find the satops which contains the diamond to the Actor/Owner/etc
		     which will be omitted in this case -->
		<xsl:variable name="satops" select="$lf/satop"/>
		<xsl:variable name="satops-subj-np" select="$satops[diamond/nomvar[starts-with(@name,concat($subj-np-index,':'))]]"/>
		<xsl:variable name="satops-just-subj-np" select="$satops-subj-np[not(diamond[2])]"/>
		<xsl:variable name="satops-just-subj-np-no-prop" select="$satops-just-subj-np[not(prop)]"/>
		<xsl:variable name="satops-just-subj-np-prop" select="$satops-just-subj-np[prop]"/>
		<xsl:variable name="satops-subj-np-two-diamond" select="$satops-subj-np[diamond[2]]"/>

		<xsl:variable name="prop-satop-name" select="$satops-just-subj-np-prop/@nomvar"/>
		<xsl:variable name="satops-no-subj-np" select="$satops[not(diamond[nomvar[starts-with(@name,concat($subj-np-index,':'))]])]"/>
		<xsl:variable name="satops-no-subj-np-name" select="$satops-no-subj-np[@nomvar!='$prop-satop-name']"/>
		<xsl:variable name="satops-no-subj-np-not-name" select="$satops-no-subj-np[@nomvar='$prop-satop-name']"/>

                <xsl:variable name="last-np-index" select="$last-np/fs/feat[@attr='index']/lf/nomvar/@name"/>
                <xsl:variable name="result-index" select="$result/fs/feat[@attr='index']/lf/nomvar/@name"/>

                <lf>
		  <xsl:for-each select="$satops-subj-np-two-diamond">
		    <satop nomvar="{@nomvar}">
		      <xsl:copy-of select="*[not(self::diamond)]"/>
		      <xsl:copy-of select="*[self::diamond][nomvar[not(starts-with(@name,concat($subj-np-index,':')))]]"/>
		    </satop>
		  </xsl:for-each>

		  <xsl:choose>
		    <xsl:when test="$satops-just-subj-np-prop">
<!--		      <fake-element/>-->
		      
		      <satop nomvar="{$satops-no-subj-np-name[1]/@nomvar}">
			<xsl:copy-of select="$satops-just-subj-np-prop[1]/prop"/>			
			<xsl:copy-of select="$satops-no-subj-np-name[1]/node()"/>
		      </satop>
		      
		      <xsl:copy-of select="$satops-no-subj-np-name[position() &gt; 1]"/>
		      <xsl:copy-of select="$satops-no-subj-np-not-name"/>
		    </xsl:when>
		    <xsl:otherwise>
		      <xsl:copy-of select="$satops-no-subj-np"/>
		    </xsl:otherwise>
		  </xsl:choose>
		      
                  <satop nomvar="{$last-np-index}">
                    <diamond mode="Top"><nomvar name="{$result-index}"/></diamond>
                  </satop>

                </lf>
              </complexcat>
            </xsl:for-each>
          </entry>
        </xsl:for-each>
        <!-- copy members -->
        <xsl:copy-of select="$members"/>
      </family>
    </xsl:if>
  </xsl:template>
  
  
  <!-- Copy -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>

