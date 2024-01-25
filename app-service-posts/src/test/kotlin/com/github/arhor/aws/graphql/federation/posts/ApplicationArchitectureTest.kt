package com.github.arhor.aws.graphql.federation.posts

import com.tngtech.archunit.core.domain.JavaCall.Predicates.target
import com.tngtech.archunit.core.domain.JavaClass.Predicates.implement
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.name
import com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTag
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import java.time.temporal.Temporal

@ArchTag("architecture")
@AnalyzeClasses(packagesOf = [PostsServiceRunner::class], importOptions = [DoNotIncludeTests::class])
internal class ApplicationArchitectureTest {

    @ArchTest
    fun `should check that correct layered architecture is observed`(appClasses: JavaClasses) {
        // given
        val applicationPackage = PostsServiceRunner::class.java.getPackage().name

        // when
        val architecture =
            layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer(API).definedBy("${applicationPackage}.api..")
                .layer(SERVICE).definedBy("${applicationPackage}.service..")
                .layer(DATA_ACCESS).definedBy("${applicationPackage}.data..")
                .layer(CONFIGURATION).definedBy("${applicationPackage}.config..")
                .whereLayer(API).mayNotBeAccessedByAnyLayer()
                .whereLayer(SERVICE).mayOnlyBeAccessedByLayers(API, CONFIGURATION)
                .whereLayer(DATA_ACCESS).mayOnlyBeAccessedByLayers(SERVICE, CONFIGURATION)

        // then
        architecture.check(appClasses)
    }

    @ArchTest
    fun `should check that only TimeUtils class calls now method on temporal objects`(appClasses: JavaClasses) {
        // given
        val restrictions = noClasses()
            .should()
            .callMethodWhere(target(name("now")).and(target(owner(implement(Temporal::class.java)))))

        // then
        restrictions.check(appClasses)
    }

    companion object {
        // @formatter:off
        private const val API           = "Api"
        private const val SERVICE       = "Service"
        private const val DATA_ACCESS   = "Data Access"
        private const val CONFIGURATION = "Configuration"
        // @formatter:on
    }
}
