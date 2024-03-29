package com.example

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class JsonVersionFileSystemTest {

    @Inject
    EmbeddedApplication<?> application

    @Test
    void testItWorks() {
        assert application.running == true
    }

}
