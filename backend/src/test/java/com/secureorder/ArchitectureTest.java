package com.secureorder;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit test to verify hexagonal architecture constraints.
 * This test ensures that:
 * 1. domain package does not depend on Spring/JPA/Jackson
 * 2. application package does not depend on adapters
 * 3. adapters do not depend on each other
 */
@AnalyzeClasses(packages = "com.secureorder", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule domain_layer_independence = 
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..springframework..", "..jakarta.persistence..", "..com.fasterxml.jackson..")
                .because("Domain layer should not depend on Spring, JPA, or Jackson");

    @ArchTest
    public static final ArchRule application_layer_independence = 
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..adapter..")
                .because("Application layer should not depend on adapters");

    @ArchTest
    public static final ArchRule adapters_independence_input_to_output = 
        noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat()
                .resideInAPackage("..adapter.out..")
                .because("Input and output adapters should not depend on each other");

    @ArchTest
    public static final ArchRule adapters_independence_output_to_input = 
        noClasses()
            .that().resideInAPackage("..adapter.out..")
            .should().dependOnClassesThat()
                .resideInAPackage("..adapter.in..")
                .because("Output and input adapters should not depend on each other");
}