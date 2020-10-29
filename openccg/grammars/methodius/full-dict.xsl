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

      <entry stem="to" pos="Prep">
	<member-of family="Prep-Nom"/>
      </entry>

      <entry stem="of" pos="Prep">
	<member-of family="Prep-Nom"/>
      </entry>

      <entry stem="by" pos="Prep">
	<member-of family="Prep-Nom"/>
<!--	<member-of family="Prep-By"/>-->
      </entry>

      <entry stem="during" pos="Prep">
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="in" pos="Prep">
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="using" pos="Prep">
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Filler"/>
      </entry>

      <entry stem="with" pos="Prep">
	<member-of family="Prep-Nom"/>
	<member-of family="Prep-Filler"/>
      </entry>



      <entry stem="currently" pos="Adv">
	<member-of family="Adverb"/>
      </entry>

      <entry stem="originally" pos="Adv">
	<member-of family="Adverb"/>
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

      <entry stem="be" pos="Aux">
	<member-of family="PassiveBe"/>
	<member-of family="ComeFromVerbs"/>
	<member-of family="DateFromVerbs"/>
	<word form="be" macros="@base" excluded="Inverted"/>
	<word form="am" macros="@pres @sg-agr @1st-agr"/>
	<word form="'m" macros="@pres @sg-agr @1st-agr"/>
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

      <entry stem="come" pos="V">
	<member-of family="ComeFromVerbs"/>
	<word form="comes" macros="@pres @sg-agr @3rd-agr"/>
	<word macros="@past" form="came"/>
      </entry>

      <entry stem="originate" pos="V">
	<member-of family="ComeFromVerbs"/>
	<word form="originates" macros="@pres @sg-agr @3rd-agr"/>
	<word macros="@past" form="originated"/>
      </entry>

      <entry stem="date" pos="V">
	<member-of family="DateFromVerbs"/>
	<word form="dates" macros="@pres @sg-or-mass-agr @3rd-agr"/>
	<word macros="@past" form="dated"/>
      </entry>


      <entry stem="give" pos="V">
	<member-of family="Ditransitive"/>
	<member-of family="Ditransitive-To"/>
	<word macros="@base" form="give"/>
	<word macros="@past" form="gave"/>
      </entry>

      <entry pos="PPart" stem="give">
<!--	<member-of family="TransitiveVerbs-Passives"/>-->
	<word form="given"/>
      </entry>


      <entry stem="create" pos="V">
	<member-of family="TransitiveVerbs"/>
	<word macros="@base" form="create"/>
	<word macros="@pres @sg-or-mass-agr @3rd-agr" form="creates" />
	<word macros="@past" form="created"/>
	<word macros="@ng" form="creating"/>
	<!--	<word pos="PPart" form="created"/>-->
      </entry>

      <entry pos="PPart" stem="create">
<!--	<member-of family="TransitiveVerbs-Passives"/>-->
	<word form="created"/>
      </entry>

      <entry stem="paint" pos="V">
	<member-of family="TransitiveVerbs"/>
	<word macros="@base" form="paint"/>
	<word macros="@pres" form="paint" />
	<word macros="@past" form="painted"/>
	<word macros="@ng" form="painting"/>
      </entry>


      <entry pos="PPart" stem="paint">
<!--	<member-of family="TransitiveVerbs-Passives"/>-->
	<word form="painted"/>
      </entry>

      <entry stem="depict" pos="V">
	<member-of family="CannedTextVerb"/>
	<word macros="@pres" form="depicts" />
      </entry>

<!--      <entry stem="locate" pos="V">
	<member-of family="TransitiveVerbs"/>
	<word macros="@base" form="locate"/>
	<word macros="@pres" form="locate" />
	<word macros="@past" form="located"/>
      </entry>

      <entry pos="PPart" stem="locate">
	<member-of family="TransitiveVerbs-Passives"/>
	<word form="located"/>
      </entry>

      <entry pos="PPart" stem="find">
	<member-of family="TransitiveVerbs-Passives"/>
	<word form="found"/>
      </entry>-->


      <!--      <entry stem="date" pos="V">
	   <word macros="@base" form="date"/>
	   <word macros="@ng" form="dating"/>
	   <word macros="@pres @sg-agr @non-3rd-agr" form="date"/>
	   <word macros="@pres @sg-or-mass-agr @3rd-agr" form="dates"/>
	   <word macros="@pres @pl-agr" form="date"/>
	   <word macros="@past" form="dated"/>
	   </entry>-->

      <entry stem="έκθεμα" pos="N" class="exhibit">
	<word form="έκθεμα" macros="@sg"/>
	<word form="έκθεματα" macros="@pl"/>
      </entry>
      
      <entry stem="exhibit" pos="N" class="exhibit">
	<word form="exhibit" macros="@sg"/>
	<word form="exhibits" macros="@pl"/>
      </entry>


      <entry stem="amphora" pos="N" class="vessel">
	<word form="amphora" macros="@sg"/>
	<word form="amphoras" macros="@pl"/>
      </entry>

      <entry stem="stamnos" pos="N" class="vessel">
	<word form="stamnos" macros="@sg"/>
	<word form="stamnoses" macros="@pl"/>
      </entry>

      <entry stem="lekythos" pos="N" class="vessel">
	<word form="lekythos" macros="@sg"/>
	<word form="lekythoses" macros="@pl"/>
      </entry>

      <entry stem="rhyton" pos="N" class="vessel">
	<word form="rhyton" macros="@sg"/>
	<word form="rhytons" macros="@pl"/>
      </entry>

      <entry stem="drachma" pos="N" class="coin">
	<word form="drachma" macros="@sg"/>
	<word form="drachmas" macros="@pl"/>
      </entry>

      <entry stem="stater" pos="N" class="coin">
	<word form="stater" macros="@sg"/>
	<word form="staters" macros="@pl"/>
      </entry>

      <entry stem="coin" pos="N" class="coin">
	<word form="coin" macros="@sg"/>
	<word form="coins" macros="@pl"/>
      </entry>



      <entry stem="archaic_period" pos="N" class="historical-period">
	<word form="archaic_period" macros="@sg"/>
      </entry>

      <entry stem="classical_period" pos="N" class="historical-period">
	<word form="classical_period" macros="@sg"/>
      </entry>

      <entry stem="hellenistic_period" pos="N" class="historical-period">
	<word form="hellenistic_period" macros="@sg"/>
      </entry>

      <entry stem="roman_period" pos="N" class="historical-period">
	<word form="roman_period" macros="@sg"/>
      </entry>

      <entry stem="red_figure_technique" pos="N" class="painting-technique">
	<word form="red_figure_technique" macros="@sg"/>
      </entry>

      <entry stem="painter_of_kleofrades" pos="NNP" class="painter">
	<word form="the_painter_of_kleofrades" macros="@sg"/>
      </entry>

      <entry stem="the_national_museum_of_athens" pos="NNP" class="museum">
	<word form="the_national_museum_of_athens" macros="@sg"/>
      </entry>

      <entry stem="the_Getty_Museum" pos="NNP" class="museum">
	<word form="the_Getty_Museum" macros="@sg"/>
      </entry>

      <entry stem="Attica" pos="NNP" class="region">
	<word form="Attica" macros="@sg"/>
      </entry>

      <entry stem="320_B.C." pos="N" class="date">
	<word form="320_B.C."/>
      </entry>

      <entry stem="island" pos="N" class="island">
	<word form="island" macros="@sg"/>
	<word form="islands" macros="@pl"/>
      </entry>

      <entry stem="painter" pos="N" class="painter">
	<word form="painter" macros="@sg"/>
	<word form="painters" macros="@pl"/>
      </entry>

      <entry stem="Crete" pos="NNP" class="island">
	<word form="Crete" macros="@sg"/>
      </entry>

      <entry stem="Rhodes" pos="NNP" class="island">
	<word form="Rhodes" macros="@sg"/>
      </entry>



      <entry stem="made" pos="Adj">
	<member-of family="Made-Adj"/>
      </entry>

      <entry stem="located" pos="Adj">
	<member-of family="Located-Adj"/>
      </entry>


      <entry stem="gold" pos="N" class="material"/>
      <entry stem="silver" pos="N" class="material"/>



      <entry pos="S" class="exhibit-story" stem="Marriages_in_Ancient_Athens_served_to_establish_the_family_unit,_the_oikos,_which_ensured_civic_rights_in_Athenian_society_and_produced_legitimate_children._The_festivities_lasted_for_three_days._The_various_stages_of_the_wedding_ceremony_are_all_shown_on_Classical_Attic_pots.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_a_special_type_of_cauldron_used_only_for_wedding_ceremonies.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="other-work" stem="Another_very_popular_work_of_Polykleitos,_known_only_through_copies,_was_the_'Diadumenos'.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="person-story" stem="and_Phidias_were_the_most_important_sculptors_of_their_time._Their_figures_expressed,_through_their_beauty,_the_spirituality_and_the_anthropocentric_attitude_of_the_Classical_world.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="location-information" stem="Croton_was_a_Greek_colony_in_Southern_Italy.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="S" class="location-information" stem="The_Aetolian_league_was_among_the_leagues_that_played_an_important_political_role.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-characteristics" stem="is_representative_of_Early_Classical_vase_painting,_a_period_when_the_archaic_models_had_not_yet_been_totally_abandoned,_but_some_significant_differences_were_starting_to_appear-_among_these_differences_are_a_substantial_increase_in_the_variety_and_naturalness_of_figure_poses,_and_the_appearance_of_well_studied_and_harmonious_compositions.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_the_form_of_a_crocodile_tearing_apart_an_African.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-purpose" stem="was_enough_for_a_metic_(that_is,_a_foreigner_that_stayed_in_Athens),_to_pay_the_'metic_tax'_each_month.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="references" stem="American_School_of_Classical_Studies_at_Athens,_Greek_and_Roman_Coins_in_the_Athenian_Agora,_Princeton,_New_Jersey_1975,_fig._9.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_an_image_of_Athena_crowned_with_a_branch_of_olive,_her_tree,_on_its_obverse._On_the_other_side_there_is_a_picture_of_her_owl.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="is_rendered_with_youthful_features_and_an_athletic_bearing,_similar_to_the_poses_that_the_classic_sculptor_Polykleitos_was_using_in_his_statues.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="references" stem="Oikonomidis,_M.,_Elliniki_Techni-_Archaia_Nomismata,_Ekdotiki_Athinon_plc,_Athens_1996,_pp.106-107,_ill.79.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_an_image_of_Athena_crowned_with_a_branch_of_olive,_her_tree,_on_its_obverse._On_the_reverse_there_is_a_pciture_of_her_owl.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_a_picture_of_a_tripod_on_each_side._A_tripod_is_a_vessel_with_three_legs_and_it_was_the_sacred_symbol_of_the_god_Apollo.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_group_of_Nymphs.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="There_were_two_types_of_hydria-_one_with_a_continuous_outline_and_one_where_the_neck_was_separate_from_the_rest_of_the_body._This_one,_as_you_can_see,_belongs_to_the_first_type.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-purpose" stem="honours_the_memory_of_Kroissos,_a_young_man_who_died_in_battle.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="references" stem="Greek_Sculpture,_the_Archaic_period,_Thames_and_Hudson_1978,_fig_107.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="inscription-says" stem="`Stand_and_cry_in_front_of_the_grave_of_dead_Kroissos_who_found_death_by_Ares,_as_he_was_fighting_on_the_front`.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="was_secretly_cut_in_two_pieces_and_transported_to_Paris_in_1937,_before_it_was_returned_to_Greece.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="Kourosses_evolved_gradually-_as_the_years_went_by,_the_form_of_the_anatomy_and_the_muscles_became_more_realistic_and_a_discreet_smile_(meidiama)_was_added_as_a_stereotypical_feature_of_the_face._This_Kouros_effectively_demonstrates_these_gradual_changes.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="S" class="references" stem="Boardman_J-_Athenian_red_figure_vases,_the_Archaic_period,_Thames_and_Hudson,_1975.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_warrior_performing_splachnoscopy_before_leaving_for_battle._Splachnoscopy_is_the_study_of_animal_entrails,_through_which_people_tried_to_predict_the_future._It_was_one_of_the_most_common_divination_methods_used_in_the_archaic_period.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="person-story" stem="was_one_of_the_most_important_subjects_of_painting_and_sculpture_during_the_Hellenistic_period._The_evidence_that_we_have_for_what_his_face_looked_like_comes_from_copies_created_after_his_death._These_elements_greatly_influenced_the_depictions_of_the_other_Hellenistic_rulers.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_pots_which_were_mostly_used_for_storing_and_mixing._They_have_two_small_horizontal_handles_on_the_side_and_a_round_body_with_a_short_neck.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="location-definition" stem="Leagues_were_formed_after_the_merging_of_many_cities_into_a_unified_political_association.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="technique-description" stem="the_background_was_painted_black._The_figures,_which_were_predesigned,_had_the_natural_color_of_the_clay.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="opposite-technique" stem="is_the_opposite_of_the_black_figure_technique.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="opposite-technique" stem="is_the_opposite_of_the_red_figure_technique.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_coins_with_the_value_of_four_drachmas.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="technique-description" stem="the_decoration,_usually_floral,_is_made_of_added_clay._This_is_a_technique_typically_used_on_vases_of_the_Hellenistic_period._Its_name_comes_from_the_west_slopes_of_the_Acropolis,_where_several_of_these_vases_were_found.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="represent_young_men,_usually_naked,_always_standing_up_straight,_with_their_palms_forming_fists_and_their_left_foot_slightly_in_front_of_the_right._They_were_used_as_burial_monuments,_or_offerings_to_temples,_especially_those_of_the_God_Apollo._They_usually_have_inscriptions_on_their_bases.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_vessels_which_were_used_for_pouring_liquids._They_have_one_handle,_a_large_belly_and_a_rim_that_projects_so_that_the_liquid_comes_out_easily.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_a_seal_on_which_there_is_a_representation_of_Helios_on_his_four-horsed_chariot.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_sculptures_consisting_of_shapes_carved_on_a_surface_so_as_to_stand_out_from_the_surrounding_background.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-purpose" stem="probably_belonged_in_the_decoration_of_Philip_II's_death_bead,_in_the_large_grave_at_the_tomb_of_Vergina.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="is_thought_to_depict_Philip_II.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="references" stem="Vokotopoulou,I.,_Elliniki_Techni-_Argyra_kai_Chalkina_Erga_Technis,_Ekdotiki_Athinon,_Athens_1997,_p._108,_fig._96.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="Dionysus_(centre)_being_garlanded_by_maenads_in_a_state_of_ecstasy._One_maenad_(left)_is_filling_a_skyphos_with_wine,_another_(right)_is_playing_a_drum.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_shield_with_a_bust_in_its_centre,_as_was_customary_on_macedonian_coins.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="From_168_BC,_after_the_defeat_of_Perseus,_king_of_Macedonia,_by_the_Roman_Aemilius_Paulus_in_Pydna,_Macedonia_devolved_to_Roman_control._The_Romans_divided_the_region_into_four_smaller_administrative_districts,_the_so-called_merides_(sectors)._This_coin_comes_from_the_`first_merida'_of_Macedonia.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="an_actor_playing_a_slave._He_is_standing_on_a_pedestal_with_his_right_hand_on_his_mask_and_the_left_one_behind_his_back.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="Constant_wars_during_the_Hellenistic_period_resulted_in_an_increase_in_the_number_of_slaves_in_the_entire_Mediterranean.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_sport_of_running_is_so_ancient_that_it_is_impossible_to_find_out_exactly_when_and_how_it_started._Its_importance_was_never_in_doubt,_however;_during_antiquity_it_was_one_of_the_most_important_parts_of_a_child's_education._On_this_amphora_you_can_see_athletes_running_naked,_as_was_customary,_except_for_during_the_`race_in_armour',_in_which_athletes_ran_wearing_their_helmets,_shields_and_greaves_(armour_which_protected_the_bottom_half_of_the_leg)._There_were_many_running_events,_in_which_the_athletes_ran_different_distances.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_discus_thrower_preparing_to_throw_the_discus.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_discus_thrower_on_this_kylix_is_weighing_the_discus_in_his_hands_as_he_gets_ready_to_throw_it._Discus_throwing_techniques_have_changed_little_since_ancient_times,_but_the_discus_itself,_had_various_differences._In_antiquity_it_was_initially_made_of_stone,_and_later_of_bronze,_lead_or_iron._It_weighed_between_1.3_and_6.6_kilos_and_so_the_athlete_required_both_strength_and_precision_to_direct_its_course.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="an_athlete_preparing_to_throw_his_javelin.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="In_antiquity,_javelin_throwing_was_intimately_bound_up_with_the_Greek_way_of_life._Before_it_became_a_feature_of_sporting_life,_the_javelin_was_one_of_the_weapons_used_by_ancient_Greeks_in_war_and_hunting._A_javelin_is_a_sharp,_wooden_spear_about_the_height_of_a_tall_man.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="an_athlete_preparing_to_perform_a_jump.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_origin_of_the_long_jump_lies_in_the_challenge_presented_by_traversing_the_cliffs,_ravines_and_rough_terrain_of_the_Greek_countryside,_and,_accordingly,_by_the_challenge_of_waging_war_on_this_terrain._It_was_a_complicated_sport_in_which_the_athletes_used_special_weights,_the_halteres,_to_increase_their_momentum_and_the_distance_of_the_jump._On_this_lekythos,_the_athlete_holds_the_weights_in_his_hands_and_is_about_to_jump_away_from_the_springboard._In_order_to_win,_he_needs_not_only_great_speed_and_strong_legs_but_also_precise_coordination_between_his_hands_and_feet_as_they_make_contact_with_the_springboard._This_is_why_the_long_jump_was_occasionally_accompanied_by_music,_which_helped_the_jumper_pace_his_rhythm.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="consists_of_a_helmet_and_a_cuirass_(which_covered_the_chest_and_back).">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_made_of_fine_quality_clay_and_have_painted_decoration_on_a_white_background._They_took_their_name_from_the_Hadra_Necropolis,_near_Alexandria,_where_a_large_number_of_them_were_found._They_were_produced_between_the_end_of_the_4th_and_the_beginning_of_the_2nd_century_BC._Their_shape_and_decoration_was_replicated_by_many_workshops.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_view_of_the_harbour_of_Patras_from_the_sea._In_the_background_you_can_see_rows_of_columns,_temples_and_other_buildings;in_the_foreground_there_is_a_ship_and_a_statue_of_a_male_form.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-characteristics" stem="is_possibly_the_helmet_of_the_famous_Miltiades,_who_donated_it_to_the_sanctuary_of_Zeus_after_the_battle_of_Marathon.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="inscription-says" stem="&#34;MILTIADES_ETHEKEN_TO_DII&#34;_(Miltiades_offered_to_Zeus).">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="an_Athenian_warrior_bidding_his_wife_farewell;_she_is_seated_on_a_klismos,_which_is_a_type_of_chair.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="Scenes_related_to_war_were_quite_frequent_in_Athenian_art_during_this_period.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_young_woman_in_the_act_of_sacrificing_at_an_altar._She_is_carrying_a_basket_in_her_left_hand,_and_pouring_wine_on_the_blazing_altar_with_her_right.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="inscription-says" stem="'EUKARPOS_PHILOXENOU_MEILESIOS_PHILOXENOS_MEILESIOY'.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="two_farmers,_as_can_be_seen_from_the_ploughshare_depicted_on_the_pediment.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="the_goddess_Athena_shaking_the_hand_of_a_thoughtful_man_(representing_the_deme_(area)_of_Athens).">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="is_the_best_preserved_painting_among_the_ones_that_were_found_in_the_Pitsa_Cave._All_of_them_are_covered_with_a_white_coating_and_their_figures_are_decorated_in_several_colours.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_sacrifice.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_special_drinking_cups._They_had_a_high_base_and_projecting_handles_that_stretched_from_the_rim_to_the_bottom_of_the_cup._An_ancient_myth_said_that_Dionysus,_the_god_of_wine,_invented_them_himself!">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_carpenter_at_work.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-purpose" stem="was_used_in_religious_worship.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_woman_raising_the_cover_of_a_chest,_in_order_to_take_out_a_folded_cloth._A_basket,_a_mirror,_a_lekythos_and_a_kantharos_are_hanging_on_the_wall.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_broken_fragments_of_pottery.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_large_jars_used_mostly_for_carrying_water._They_had_three_handles,_two_horizontal_and_one_vertical.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="seven_relief_busts_of_the_members_of_a_family.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_family_was_the_basic_social_unit_of_Roman_society,_since_wealth_and_social_status_were_transmitted_through_it._Roman_private_law_is_our_main_source_of_information_for_the_Roman_family_(familia),_the_members_of_which_were_under_the_control_of_one_individual,_the_father,_known_as_the_pater_familias.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_Greek_hunting_a_Persian.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="end_in_a_conical_fitting.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_wine_cups_with_a_shallow_bowl_placed_on_top_of_a_base.__They_have_two_horizontal_handles.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="&#34;Mourning_Athena&#34;._The_goddess_is_clad_in_an_Attic_peplos_(gown)_with_a_belt,_leaning_on_her_spear_looking_melancholically_at_a_rectangular_stele_(gravestone),_which_according_to_some_scholars_bears_the_names_of_those_killed_in_a_battle.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="Dionysos_seated_and_wearing_a_crown_of_olives._In_front_of_him_stands_a_satyr_with_an_oinochoe_(wine_jug)_in_his_hands._Hanging_on_the_architrave_are_five_masks,_of_(from_left)-_the_Tetchy_Father;_the_Old_Woman;_the_Crafty_Slave;_the_Beardless_Youth;_and_the_Young_Woman_with_Short_Hair.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="is_spherical_in_shape.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="On_the_body_a_wide_zone_is_distinguished_with_pairs_of_comastes_among_supplementary_patterns,_mainly_rosettes_(jewels_representing_roses_with_open_radiate_leaves)._&#34;Comastes&#34;_were_the_participants_in_&#34;comous&#34;,_feasts_in_honour_of_Dionysus.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="inscription-says" stem="'Mantiklos_dedicated_me_as_a_tithe_(tax)_to_the_Archer_God_of_the_Silver_Bow._And_you,_Phoebus_(Apollo),_return_the_favour!'.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_an_inscription_on_its_thigh_in_the_shape_of_a_horseshoe.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_young_man_who_is_sitting_down_and_writing_with_a_stylus_(pen).">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="During_the_archaic_period,_the_most_poular_method_of_writing_must_have_been_wooden_tablets_coated_with_wax,_on_which_letters_were_written_with_the_stylus_and_could_easily_be_rubbed_out_and_rewritten.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="states_the_names_of_the_people_depicted_and_the_sculptor.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="the_Athenian_hoplite_(soldier)_Aristion.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_burial_customs_of_the_Archaic_period_make_it_clear_that_the_place_of_the_individual_was_beginning_to_be_defined_in_his_city.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_&#34;West_Slope&#34;_decoration_is_obvious_on_the_neck_of_this_kantharos.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="the_leader_of_the_Galatians_committing_suicide_after_having_already_killed_his_wife.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_original_statue_was_part_of_a_larger_bronze_synthesis,_placed_in_the_yard_of_Athena's_sanctuary.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="is_one_of_the_best_examples_of_a_propaganda_statue.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="In_keeping_with_the_classical_models,_the_emperor_is_shown_half_nude,_as_a_heroized_leader.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_banker_in_front_of_&#34;mensa_nummularia&#34;,_which_was_a_table_used_for_counting_out_coins.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_wedding_scene-_two_newlyweds_on_a_carriage_escorted_by_relatives_and_friends._The_event_of_the_bride's_transport_to_her_new_house_was_very_important,_because_a_marriage_was_considered_valid_only_after_the_bride_and_the_groom_started_living_together.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="an_old_woman_carrying_a_basket_of_fruit_and_vegetables.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="Compared_to_the_idealistic_portraits_of_the_classical_period,_the_hellenistic_ones_express_the_features_of_the_face_with_a_more_intense_realism._The_gradual_replacement_of_the_classical_idealisation_by_a_more_individualistic_realism_became_one_of_the_most_typical_characteristics_of_hellenistic_art.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-form" stem="has_the_head_of_Athena_with_a_Corinthian_helmet_on_the_reverse_side,_as_was_common_in_coins_of_the_Ancient_World._On_the_obverse_side_is_a_female_figure,_the_personification_of_Aetolia,_seated_on_Macedonian_and_Galatic_shields._The_scene_refers_to_the_fight_of_the_Aetolians_against_the_Macedonians_and_the_Galatians.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-depicts" stem="a_wedding_scene,_showing_a_bride_in_nymphides,_that_is_'bridal_footwear'._In_many_scenes_related_to_the_adorning_of_the_bride,_the_sandals_receive_especially_detailed_treatment.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="In_imperial_portraits,_all_the_Emperors,_even_the_older_ones,_were_portrayed_youthfully._These_portraits_were_strongly_inspired_by_the_classical_statues_of_the_5th_century_BC,_especially_the_ones_made_by_Polykleitus._Imperial_portraits_first_appeared_in_the_capital,_Rome,_but_later_they_spread_all_over_the_Empire,_with_the_help_of_casts,_known_as_&#34;imagines&#34;.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_side_of_a_coin_which_displays_the_principal_symbol_(`heads'_in_`heads_or_tails')_is_known_as_the_obverse.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_basically_oil_bottles._They_are_vessels_with_a_tall_shape,_usually_forming_an_ellipse;_they_have_a_base,_a_single_vertical_handle,_a_narrow_neck_and_a_small_mouth.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="have_an_ovoid_body_and_two_looped_handles,_reaching_from_the_shoulders_up._This_was_a_very_common_shape_in_ancient_times.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="are_a_special_sort_of_relief,_which_ancient_people_used_in_order_to_commemorate_members_of_their_family_and_friends_who_had_died._These_reliefs_were_placed_in_grave_areas._Many_of_them_depict_scenes_from_the_life_of_the_deceased_and_have_inscriptions_that_give_interesting_information_about_them._The_quality_of_the_relief_was_dependent_on_the_social_class_and_the_economic_status_of_the_deceased.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="person-story" stem="is_the_most_typical_representative_of_the_rich_style._His_figures_are_graceful;_they_are_often_posed_as_though_dancing,_with_expressions_that_range_from_tenderness_to_melancholy._The_atmosphere_is_low-key,_avoiding_drama_-_a_long_way_from_the_Classical_models_of_the_previous_generation._The_clothing_on_this_painter's_pots_is_always_very_carefully_drawn-_it_has_patterns_of_plant_motifs_or_stars._His_compositions_are_well_balanced;_They_manage_to_give_a_'lift'_to_the_hydria_and_other_kinds_of_pot_awkward_to_decorate.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_decoration_of_ancient_Greek_vessels_provides_a_lot_of_information_about_the_life,_the_habits_and_the_beliefs_of_the_Ancient_Greeks._Decoration_of_Ancient_Greek_pottery_began_with_simple_linear_drawings_but_gradually,_potters_started_to_depict_scenes_from_nature,_including_plants,_animals_and_finally_human_figures_on_their_vases._There_was_not_usually_any_decoration_on_pots_used_every_day_in_poor_households_and_in_shops._The_colours_and_decorations_used_on_vessels_help_to_determine_their_period_of_creation.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_large_open_globular_pots,_perhaps_based_on_bronze_originals._They_had_many_uses,_but_it_seems_that_they_was_mostly_used_for_mixing_wine_and_water_for_ritual_and_festival_events.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_pots_used_by_athletes_to_hold_oil,_with_which_they_cleaned_themselves_after_exercising._Each_athlete_probably_had_his_personal_aryballos._They_are_usually_ball_shaped_with_one_or_two_handles._Some_have_the_shape_of_animals,_birds,_or_human_heads.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="S" class="exhibit-story" stem="The_handles_of_Rhodian_amphoras,_which_have_been_found_dispersed_in_the_Mediterranean,_reveal_the_island's_trade_relations_with_different_regions_in_that_area.">
	<member-of family="Canned-Sentence"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_special_cups_for_wine,_mostly_used_in_rituals._They_were_made_in_a_variety_of_shapes,_resembling_a_horn,_the_head_of_an_animal,_or_the_figure_of_a_person.">
	<member-of family="Canned-NP"/>
      </entry>
      
      <entry pos="NP" class="exhibit-definition" stem="were_amphoras_given_as_prizes_to_the_winners_of_the_Panathenaic_games,_which_were_held_in_Athens_in_honor_of_the_city's_patron_godess,_Athena.">
	<member-of family="Canned-NP"/>
      </entry>
      

      <!-- Add core macros -->
      <xsl:call-template name="add-macros"/>

    </dictionary>
  </xsl:template>
</xsl:transform>

