package com.github.arhor.aws.graphql.federation.comments;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;

import java.time.temporal.Temporal;

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.implement;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("architecture")
@AnalyzeClasses(packagesOf = CommentsServiceRunner.class, importOptions = DoNotIncludeTests.class)
class ApplicationArchitectureTest {

    // @formatter:off
    private static final String API           = "Api";
    private static final String SERVICE       = "Service";
    private static final String DATA_ACCESS   = "Data Access";
    private static final String CONFIGURATION = "Configuration";
    // @formatter:on

    @ArchTest
    void should_check_that_correct_layered_architecture_is_observed(
        // Given
        final JavaClasses appClasses
    ) {
        final var applicationPackage = CommentsServiceRunner.class.getPackage().getName();

        // When
        final var architecture =
            layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer(API).definedBy(applicationPackage + ".api..")
                .layer(SERVICE).definedBy(applicationPackage + ".service..")
                .layer(DATA_ACCESS).definedBy(applicationPackage + ".data..")
                .layer(CONFIGURATION).definedBy(applicationPackage + ".config..")
                .whereLayer(API).mayNotBeAccessedByAnyLayer()
                .whereLayer(SERVICE).mayOnlyBeAccessedByLayers(API, CONFIGURATION)
                .whereLayer(DATA_ACCESS).mayOnlyBeAccessedByLayers(SERVICE, CONFIGURATION);

        // Then
        architecture.check(appClasses);
    }

    @ArchTest
    void should_check_that_only_TimeUtils_class_calls_now_method_on_temporal_objects(
        // Given
        final JavaClasses appClasses
    ) {
        // When
        final var restrictions =
            noClasses()
                .should()
                .callMethodWhere(target(name("now")).and(target(owner(implement(Temporal.class)))));

        // Then
        restrictions.check(appClasses);
    }
}
