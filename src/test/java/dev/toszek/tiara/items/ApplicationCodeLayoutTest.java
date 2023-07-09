package dev.toszek.tiara.items;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ApplicationCodeLayoutTest {

    @Test
    void writeDocumentationSnippets() {

        var modules = ApplicationModules.of(Application.class).verify();

        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
