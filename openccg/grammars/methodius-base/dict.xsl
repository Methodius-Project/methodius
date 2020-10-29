<?xml version="1.0"?>

<xsl:transform 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:xalan2="http://xml.apache.org/xslt"
    exclude-result-prefixes="xalan xalan2">
  

  <!-- ***** Import Core Dictionary Definitions ***** -->

  <xsl:import href="core-en-dict.xsl"/>

  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>

  
  <!-- ***** Start Output Here ***** -->
  <xsl:template match="/">
    <dictionary name="crag"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:noNamespaceSchemaLocation="../dict.xsd">
      
      <!-- Add core entries -->
      <xsl:call-template name="add-entries"/>



      <entry stem="and" pos="Conj">
	<member-of family="Conj-Sentential-Binary"/>
      </entry>

      <entry stem=";" pos="Conj">
	<member-of family="Conj-Sentential-Binary"/>
      </entry>

      <entry stem="which" pos="RelPro" macros="@X-thing">
	<member-of family="RelPro"/>
	<!--	<member-of family="RelPro-Appos" pred="elab-rel"/>-->
      </entry>



      <entry stem="who" pos="RelPro" macros="@X-person">
	<member-of family="RelPro"/>
	<!--	<member-of family="RelPro-Appos" pred="elab-rel"/>-->
      </entry>

      <entry stem="from" pos="Prep">
	<member-of family="Prep-Nom"/>
      </entry>

      <entry stem="between" pos="Prep">
	<member-of family="Prep-Nom"/>
      </entry>

      <entry stem="to" pos="Prep">
	<member-of family="Prep-Nom"/>
      </entry>

      <entry stem="of" pos="Prep">
	<member-of family="Prep-Filler"/>
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Poss"/>
      </entry>

      <entry stem="by" pos="Prep">
	<!--	<member-of family="Prep-Nom"/>-->
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="during" pos="Prep">
	<!--	<member-of family="Prep-Nom"/>-->
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="in" pos="Prep">
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="on" pos="Prep">
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="using" pos="Prep">
	<!--	<member-of family="Prep-Nom"/>-->
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="with" pos="Prep">
	<!--	<member-of family="Prep-Nom"/>-->
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="the_opposite_of" pos="Prep">
	<member-of family="Prep-Nom"/>
      </entry>



      <entry stem="currently" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>

      <entry stem="now" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>

      <entry stem="just" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>

      <entry stem="recently" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>

      <entry stem="high" pos="Adv">
	<member-of family="Adverb-final"/>
      </entry>

      <entry stem="today" pos="Adv">
	<member-of family="Adverb-initial"/>
      </entry>


      <entry stem="happily" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>



      <entry stem="originally" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>

      <entry stem="initially" pos="Adv">
	<member-of family="forward-adverb"/>
      </entry>

      <entry stem="later" pos="Adv">
	<member-of family="Adverb-initial"/>
      </entry>

      <entry stem="like" pos="Comparator">
	<member-of family="Comparator"/>
      </entry>

      <entry stem="unlike" pos="Comparator">
	<member-of family="Comparator"/>
      </entry>

      <entry stem="previous" pos="Adj">
	<member-of family="Adjective" entry="Predicative"/>
      </entry>

      <entry stem="other" pos="Adj">
	<member-of family="Adjective" entry="Predicative"/>
      </entry>

      <entry stem="last" pos="Adj">
	<member-of family="Adjective" entry="Predicative"/>
      </entry>

      <entry stem="be-aux" pos="Aux">
	<member-of family="PassiveBe"/>
	<word form="be" macros="@base" excluded="Inverted"/>
	<!--	<word form="am" macros="@pres @sg-agr @1st-agr"/>
	    <word form="'m" macros="@pres @sg-agr @1st-agr"/>-->
	<word form="are" macros="@pres @sg-agr @2nd-agr"/>
	<!--	<word form="'re" macros="@pres @sg-agr @2nd-agr"/>-->
	<word form="is" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<!--	<word form="'s" macros="@pres @sg-or-mass-agr @3rd-agr"/>-->
	<word form="are" macros="@pres @pl-agr"/>
	<!--	<word form="'re" macros="@pres @pl-agr"/>-->
	<word form="was" macros="@past @sg-agr @1st-agr"/>
	<word form="were" macros="@past @sg-agr @2nd-agr"/>
	<word form="was" macros="@past @sg-or-mass-agr @3rd-agr"/>
	<word form="were" macros="@past @pl-agr"/>
      </entry>



      <entry pos="V" stem="missing-verb-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="missing_verb" macros="@pres @sg-or-mass-agr @3rd-agr"/>
      </entry>


      <entry pos="V" stem="rule-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="rule" macros="@base"/>
	<word form="rules" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="rule" macros="@pres @pl-agr @3rd-agr"/>
	<word form="ruled" macros="@past"/>
	<word form="ruled" macros="@ppart"/>
      </entry>

      <entry pos="V" stem="construct-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="construct" macros="@base"/>
	<word form="constructs" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="construct" macros="@pres @pl-agr @3rd-agr"/>
	<word form="constructed" macros="@past"/>
	<word form="constructed" macros="@ppart"/>
      </entry>

      <entry pos="V" stem="be-near-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="is_near" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="are_near" macros="@pres @pl-agr @3rd-agr"/>
	<word form="was_near" macros="@past"/>
      </entry>


      <!-- verbs automatically extracted from mpiro lexicon -->


      <entry pos="V" stem="attribute-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="attribute" macros="@base"/>
	<word form="attributes" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="attribute" macros="@pres @pl-agr @3rd-agr"/>
	<word form="attributed" macros="@past"/>
	<word form="attributed" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="belong-verb">
	<member-of family="IVPrepVerbs"/>
	<word form="belong" macros="@base"/>
	<word form="belongs" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="belong" macros="@pres @pl-agr @3rd-agr"/>
	<word form="belonged" macros="@past"/>
      </entry>

      <entry stem="come-verb" pos="V">
	<member-of family="IVPrepVerbs"/>
	<word form="comes" macros="@pres @sg-agr @3rd-agr"/>
	<word form="come" macros="@pres @pl-agr @3rd-agr"/>
	<word macros="@past" form="came"/>
      </entry>

      <entry pos="V" stem="cover-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="cover" macros="@base"/>
	<word form="covers" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="cover" macros="@pres @pl-agr @3rd-agr"/>
	<word form="covered" macros="@past"/>
	<word form="covered" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="create-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="create" macros="@base"/>
	<word form="creates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="create" macros="@pres @pl-agr @3rd-agr"/>
	<word form="created" macros="@past"/>
	<word form="created" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="date-verb">
	<member-of family="IVPrepVerbs"/>
	<word form="date" macros="@base"/>
	<word form="dates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="date" macros="@pres @pl-agr @3rd-agr"/>
	<word form="dated" macros="@past"/>
      </entry>
      <entry pos="V" stem="decorate-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="decorate" macros="@base"/>
	<word form="decorates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="decorate" macros="@pres @pl-agr @3rd-agr"/>
	<word form="decorated" macros="@past"/>
	<word form="decorated" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="depict-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="depict" macros="@base"/>
	<word form="depicts" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="depict" macros="@pres @pl-agr @3rd-agr"/>
	<word form="depicted" macros="@past"/>
	<word form="depicted" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="develop-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="develop" macros="@base"/>
	<word form="develops" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="develop" macros="@pres @pl-agr @3rd-agr"/>
	<word form="developed" macros="@past"/>
	<word form="developed" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="donate-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="donate" macros="@base"/>
	<word form="donates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="donate" macros="@pres @pl-agr @3rd-agr"/>
	<word form="donated" macros="@past"/>
	<word form="donated" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="exhibit-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="exhibit" macros="@base"/>
	<word form="exhibits" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="exhibit" macros="@pres @pl-agr @3rd-agr"/>
	<word form="exhibited" macros="@past"/>
	<word form="exhibited" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="find-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="find" macros="@base"/>
	<word form="finds" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="find" macros="@pres @pl-agr @3rd-agr"/>
	<word form="found" macros="@past"/>
	<word form="found" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="follow-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="follow" macros="@base"/>
	<word form="follows" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="follow" macros="@pres @pl-agr @3rd-agr"/>
	<word form="followed" macros="@past"/>
	<word form="followed" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="inscribe-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="inscribe" macros="@base"/>
	<word form="inscribes" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="inscribe" macros="@pres @pl-agr @3rd-agr"/>
	<word form="inscribed" macros="@past"/>
	<word form="inscribed" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="last-verb">
	<member-of family="IVPrepVerbs"/>
	<word form="last" macros="@base"/>
	<word form="lasts" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="last" macros="@pres @pl-agr @3rd-agr"/>
	<word form="lasted" macros="@past"/>
      </entry>
      <entry pos="V" stem="locate-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="locate" macros="@base"/>
	<word form="locates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="locate" macros="@pres @pl-agr @3rd-agr"/>
	<word form="located" macros="@past"/>
	<word form="located" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="make-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="make" macros="@base"/>
	<word form="makes" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="make" macros="@pres @pl-agr @3rd-agr"/>
	<word form="made" macros="@past"/>
	<word form="made" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="originate-verb">
	<member-of family="IVPrepVerbs"/>
	<word form="originate" macros="@base"/>
	<word form="originates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="originate" macros="@pres @pl-agr @3rd-agr"/>
	<word form="originated" macros="@past"/>
      </entry>
      <entry pos="V" stem="paint-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="paint" macros="@base"/>
	<word form="paints" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="paint" macros="@pres @pl-agr @3rd-agr"/>
	<word form="painted" macros="@past"/>
	<word form="painted" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="portray-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="portray" macros="@base"/>
	<word form="portrays" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="portray" macros="@pres @pl-agr @3rd-agr"/>
	<word form="portrayed" macros="@past"/>
	<word form="portrayed" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="range-verb">
	<member-of family="IVPrepVerbs"/>
	<word form="range" macros="@base"/>
	<word form="ranges" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="range" macros="@pres @pl-agr @3rd-agr"/>
	<word form="ranged" macros="@past"/>
      </entry>

      <entry stem="read-verb" pos="V">
	<member-of family="TransitiveVerbs"/>
	<word form="reads" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="reads" macros="@pres @pl-agr @3rd-agr"/>
	<word macros="@past" form="read"/>
	<word form="read" macros="@ppart"/>
      </entry>


      <entry pos="V" stem="sculpt-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="sculpt" macros="@base"/>
	<word form="sculpts" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="sculpt" macros="@pres @pl-agr @3rd-agr"/>
	<word form="sculpted" macros="@past"/>
	<word form="sculpted" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="see-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="see" macros="@base"/>
	<word form="sees" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="see" macros="@pres @pl-agr @3rd-agr"/>
	<word form="saw" macros="@past"/>
	<word form="seen" macros="@ppart"/>
      </entry>
      <entry pos="V" stem="show-verb">
	<member-of family="TransitiveVerbs"/>
	<word form="show" macros="@base"/>
	<word form="shows" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word form="show" macros="@pres @pl-agr @3rd-agr"/>
	<word form="showed" macros="@past"/>
	<word form="shown" macros="@ppart"/>
      </entry>

      <!-- end of verbs -->

      <!-- canned text automatically extracted from mpiro instances -->

      

      <!-- Add core macros -->
      <xsl:call-template name="add-macros"/>

    </dictionary>
  </xsl:template>
</xsl:transform>

