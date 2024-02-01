package io.github.kamilperczynski.javalint.formatter.ec

import io.github.kamilperczynski.javalint.formatter.IntellijFormatter
import io.github.kamilperczynski.javalint.formatter.IntellijFormatterOptions
import io.github.kamilperczynski.javalint.formatter.NoopFormattingEvents
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class ECCodeStyleSettingsAccessorsTest {

  companion object {
    private lateinit var formatter: IntellijFormatter

    @JvmStatic
    @BeforeAll
    fun setUp() {
      formatter = IntellijFormatter(
        IntellijFormatterOptions(Paths.get("."), NoopFormattingEvents.INSTANCE)
      )
    }
  }

  @Test
  fun testRootSettings() {
    val codeStyleSettings = someCodeSettings()

    val allProperties =
      ECCodeStyleSettingsAccessors(codeStyleSettings).rootSettings.enumProperties()

    assertThat(allProperties.sorted().joinToString("\n")).isEqualTo(
      """
        continuation_indent_size
        end_of_line
        formatter_enabled
        formatter_off_tag
        formatter_on_tag
        formatter_tags_accept_regexp
        formatter_tags_enabled
        indent_size
        indent_style
        max_line_length
        smart_tabs
        tab_width
        visual_guides
        wrap_on_typing
      """.trimIndent()
    )
  }

  @Test
  fun testCommonSettings() {
    val codeStyleSettings = someCodeSettings()

    val allProperties =
      ECCodeStyleSettingsAccessors(codeStyleSettings).commonSettings["java"]!!.enumProperties()

    assertThat(allProperties.sorted().joinToString("\n")).isEqualTo(
      """
        align_consecutive_assignments
        align_consecutive_variable_declarations
        align_group_field_declarations
        align_multiline_array_initializer_expression
        align_multiline_assignment
        align_multiline_binary_operation
        align_multiline_chained_methods
        align_multiline_extends_list
        align_multiline_for
        align_multiline_method_parentheses
        align_multiline_parameters
        align_multiline_parameters_in_calls
        align_multiline_parenthesized_expression
        align_multiline_resources
        align_multiline_ternary_operation
        align_multiline_throws_list
        align_subsequent_simple_methods
        align_throws_keyword
        array_initializer_new_line_after_left_brace
        array_initializer_right_brace_on_new_line
        array_initializer_wrap
        assert_statement_colon_on_next_line
        assert_statement_wrap
        assignment_wrap
        binary_operation_sign_on_next_line
        binary_operation_wrap
        blank_lines_after_anonymous_class_header
        blank_lines_after_class_header
        blank_lines_after_imports
        blank_lines_after_package
        blank_lines_around_class
        blank_lines_around_field
        blank_lines_around_field_in_interface
        blank_lines_around_method
        blank_lines_around_method_in_interface
        blank_lines_before_class_end
        blank_lines_before_imports
        blank_lines_before_method_body
        blank_lines_before_package
        block_brace_style
        block_comment_add_space
        block_comment_at_first_column
        builder_methods
        call_parameters_new_line_after_left_paren
        call_parameters_right_paren_on_new_line
        call_parameters_wrap
        case_statement_on_separate_line
        catch_on_new_line
        class_annotation_wrap
        class_brace_style
        continuation_indent_size
        do_not_indent_top_level_class_members
        do_while_brace_force
        else_on_new_line
        enum_constants_wrap
        extends_keyword_wrap
        extends_list_wrap
        field_annotation_wrap
        finally_on_new_line
        for_brace_force
        for_statement_new_line_after_left_paren
        for_statement_right_paren_on_new_line
        for_statement_wrap
        if_brace_force
        indent_break_from_case
        indent_case_from_switch
        indent_size
        indent_style
        keep_blank_lines_before_right_brace
        keep_blank_lines_between_package_declaration_and_header
        keep_blank_lines_in_code
        keep_blank_lines_in_declarations
        keep_builder_methods_indents
        keep_control_statement_in_one_line
        keep_first_column_comment
        keep_indents_on_empty_lines
        keep_line_breaks
        keep_multiple_expressions_in_one_line
        keep_simple_blocks_in_one_line
        keep_simple_classes_in_one_line
        keep_simple_lambdas_in_one_line
        keep_simple_methods_in_one_line
        label_indent_absolute
        label_indent_size
        lambda_brace_style
        line_comment_add_space
        line_comment_add_space_on_reformat
        line_comment_at_first_column
        max_line_length
        method_annotation_wrap
        method_brace_style
        method_call_chain_wrap
        method_parameters_new_line_after_left_paren
        method_parameters_right_paren_on_new_line
        method_parameters_wrap
        modifier_list_wrap
        parameter_annotation_wrap
        parentheses_expression_new_line_after_left_paren
        parentheses_expression_right_paren_on_new_line
        place_assignment_sign_on_next_line
        prefer_parameters_wrap
        resource_list_new_line_after_left_paren
        resource_list_right_paren_on_new_line
        resource_list_wrap
        smart_tabs
        space_after_colon
        space_after_comma
        space_after_comma_in_type_arguments
        space_after_for_semicolon
        space_after_quest
        space_after_type_cast
        space_before_annotation_array_initializer_left_brace
        space_before_annotation_parameter_list
        space_before_array_initializer_left_brace
        space_before_catch_keyword
        space_before_catch_left_brace
        space_before_catch_parentheses
        space_before_class_left_brace
        space_before_colon
        space_before_comma
        space_before_do_left_brace
        space_before_else_keyword
        space_before_else_left_brace
        space_before_finally_keyword
        space_before_finally_left_brace
        space_before_for_left_brace
        space_before_for_parentheses
        space_before_for_semicolon
        space_before_if_left_brace
        space_before_if_parentheses
        space_before_method_call_parentheses
        space_before_method_left_brace
        space_before_method_parentheses
        space_before_quest
        space_before_switch_left_brace
        space_before_switch_parentheses
        space_before_synchronized_left_brace
        space_before_synchronized_parentheses
        space_before_try_left_brace
        space_before_try_parentheses
        space_before_type_parameter_list
        space_before_while_keyword
        space_before_while_left_brace
        space_before_while_parentheses
        space_within_empty_array_initializer_braces
        space_within_empty_method_call_parentheses
        space_within_empty_method_parentheses
        spaces_around_additive_operators
        spaces_around_assignment_operators
        spaces_around_bitwise_operators
        spaces_around_equality_operators
        spaces_around_lambda_arrow
        spaces_around_logical_operators
        spaces_around_method_ref_dbl_colon
        spaces_around_multiplicative_operators
        spaces_around_relational_operators
        spaces_around_shift_operators
        spaces_around_unary_operator
        spaces_within_annotation_parentheses
        spaces_within_array_initializer_braces
        spaces_within_braces
        spaces_within_brackets
        spaces_within_cast_parentheses
        spaces_within_catch_parentheses
        spaces_within_for_parentheses
        spaces_within_if_parentheses
        spaces_within_method_call_parentheses
        spaces_within_method_parentheses
        spaces_within_parentheses
        spaces_within_switch_parentheses
        spaces_within_synchronized_parentheses
        spaces_within_try_parentheses
        spaces_within_while_parentheses
        special_else_if_treatment
        tab_width
        ternary_operation_signs_on_next_line
        ternary_operation_wrap
        throws_keyword_wrap
        throws_list_wrap
        use_relative_indents
        variable_annotation_wrap
        while_brace_force
        while_on_new_line
        wrap_comments
        wrap_first_method_in_call_chain
        wrap_long_lines
        wrap_on_typing
      """.trimIndent()
    )
  }

  @Test
  fun testCustomSettings() {
    val codeStyleSettings = someCodeSettings()

    val allProperties =
      ECCodeStyleSettingsAccessors(codeStyleSettings).customSettings["java"]!!.enumProperties()

    assertThat(allProperties.sorted().joinToString("\n")).isEqualTo(
      """
        align_multiline_annotation_parameters
        align_multiline_deconstruction_list_components
        align_multiline_records
        align_multiline_text_blocks
        align_types_in_multi_catch
        annotation_parameter_wrap
        blank_lines_around_initializer
        class_count_to_use_import_on_demand
        class_names_in_javadoc
        deconstruction_list_wrap
        do_not_wrap_after_single_annotation
        do_not_wrap_after_single_annotation_in_parameter
        doc_add_blank_line_after_description
        doc_add_blank_line_after_param_comments
        doc_add_blank_line_after_return
        doc_add_p_tag_on_empty_lines
        doc_align_exception_comments
        doc_align_param_comments
        doc_do_not_wrap_if_one_line
        doc_enable_formatting
        doc_enable_leading_asterisks
        doc_indent_on_continuation
        doc_keep_empty_lines
        doc_keep_empty_parameter_tag
        doc_keep_empty_return_tag
        doc_keep_empty_throws_tag
        doc_keep_invalid_tags
        doc_param_description_on_new_line
        doc_preserve_line_breaks
        doc_use_throws_not_exception_tag
        field_name_prefix
        field_name_suffix
        generate_final_locals
        generate_final_parameters
        imports_layout
        insert_inner_class_imports
        insert_override_annotation
        layout_static_imports_separately
        local_variable_name_prefix
        local_variable_name_suffix
        multi_catch_types_wrap
        names_count_to_use_import_on_demand
        new_line_after_lparen_in_annotation
        new_line_after_lparen_in_deconstruction_pattern
        new_line_after_lparen_in_record_header
        packages_to_use_import_on_demand
        parameter_name_prefix
        parameter_name_suffix
        prefer_longer_names
        record_components_wrap
        repeat_synchronized
        replace_instanceof_and_cast
        replace_null_check
        replace_sum_lambda_with_method_ref
        rparen_on_new_line_in_annotation
        rparen_on_new_line_in_deconstruction_pattern
        rparen_on_new_line_in_record_header
        space_after_closing_angle_bracket_in_type_argument
        space_before_colon_in_foreach
        space_before_deconstruction_list
        space_before_opening_angle_bracket_in_type_parameter
        space_inside_one_line_enum_braces
        spaces_around_annotation_eq
        spaces_around_type_bounds_in_type_parameters
        spaces_within_angle_brackets
        spaces_within_deconstruction_list
        spaces_within_record_header
        static_field_name_prefix
        static_field_name_suffix
        subclass_name_prefix
        subclass_name_suffix
        test_name_prefix
        test_name_suffix
        use_external_annotations
        use_fq_class_names
        use_single_class_imports
        visibility
      """.trimIndent()
    )
  }
}
