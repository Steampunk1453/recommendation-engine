package com.contentdiscovery.recommendation.architecture;

import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class HexagonalArchitectureTest {
    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter().importPackages("com.contentdiscovery.recommendation");
    }

    @Test
    void controllersDependOnConcreteUseCases() {
        classes().that().resideInAnyPackage("..infrastructure.adapter.in.web.controller..")
                .and().haveSimpleNameEndingWith("Controller")
                .should().dependOnClassesThat().resideInAnyPackage("..application.usecase..")
                .check(classes);
        noClasses().that().resideInAnyPackage("..infrastructure.adapter.in.web.controller..")
                .and().haveSimpleNameEndingWith("Controller")
                .should().dependOnClassesThat().resideInAnyPackage("..domain.port..")
                .check(classes);
    }

    @Test
    void adaptersImplementDomainPorts() {
        classes().that().haveSimpleName("UserPersistenceAdapter")
                .should().implement(UserRepository.class).check(classes);
        classes().that().haveSimpleName("VideoPersistenceAdapter")
                .should().implement(VideoRepository.class).check(classes);
        classes().that().haveSimpleName("InteractionPersistenceAdapter")
                .should().implement(UserInteractionRepository.class).check(classes);
    }

    @Test
    void useCasesAreConcreteClasses() {
        classes().that().resideInAnyPackage("..application.usecase..")
                .should().notBeInterfaces()
                .check(classes);
    }

    @Test
    void jpaEntitiesOnlyExistInInfrastructure() {
        classes().that().areAnnotatedWith(jakarta.persistence.Entity.class)
                .should().resideInAnyPackage("..infrastructure..").check(classes);
    }

    @Test
    void domainHasNoFrameworkOrInfrastructureDependencies() {
        noClasses().that().resideInAnyPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta..", "com.contentdiscovery.recommendation.infrastructure..")
                .check(classes);
    }

    @Test
    void applicationHasNoFrameworkOrInfrastructureDependencies() {
        noClasses().that().resideInAnyPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta..", "com.contentdiscovery.recommendation.infrastructure..")
                .check(classes);
    }

    @Test
    void packageLayersOnlyDependInward() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Application", "Domain")
                .check(classes);
    }
}
