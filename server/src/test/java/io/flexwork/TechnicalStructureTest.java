package io.flexwork;

// import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
// import com.tngtech.archunit.junit.AnalyzeClasses;

// @AnalyzeClasses(packagesOf = FlexworkApp.class, importOptions = DoNotIncludeTests.class)
class TechnicalStructureTest {

    // prettier-ignore
    //    @ArchTest
    //    static final ArchRule respectsTechnicalArchitectureLayers =
    //            layeredArchitecture()
    //                    .consideringAllDependencies()
    //                    .layer("Event")
    //                    .definedBy("..event..")
    //                    .layer("Config")
    //                    .definedBy("..config..")
    //                    .layer("Web")
    //                    .definedBy("..web..")
    //                    .optionalLayer("Service")
    //                    .definedBy("..service..")
    //                    .layer("Security")
    //                    .definedBy("..security..")
    //                    .optionalLayer("Persistence")
    //                    .definedBy("..repository..")
    //                    .layer("Domain")
    //                    .definedBy("..domain..")
    //                    .whereLayer("Config")
    //                    .mayNotBeAccessedByAnyLayer()
    //                    .whereLayer("Web")
    //                    .mayOnlyBeAccessedByLayers("Config")
    //                    .whereLayer("Service")
    //                    .mayOnlyBeAccessedByLayers("Web", "Config")
    //                    .whereLayer("Security")
    //                    .mayOnlyBeAccessedByLayers("Config", "Service", "Web")
    //                    .whereLayer("Persistence")
    //                    .mayOnlyBeAccessedByLayers("Service", "Security", "Web", "Config")
    //                    .whereLayer("Domain")
    //                    .mayOnlyBeAccessedByLayers(
    //                            "Persistence", "Service", "Security", "Web", "Config", "Domain",
    // "Event")
    //                    .ignoreDependency(belongToAnyOf(FlexworkApp.class), alwaysTrue())
    //                    .ignoreDependency(
    //                            alwaysTrue(),
    //                            belongToAnyOf(Constants.class, ApplicationProperties.class));
}
