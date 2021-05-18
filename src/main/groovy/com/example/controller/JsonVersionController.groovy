package com.example.controller


import com.example.service.JsonVersionService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

import javax.inject.Inject

@Controller("/api")
class JsonVersionController {

    @Inject
    JsonVersionService jsonVersionService


    @Consumes([MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON])
    @Post("/createFileVersions")
    HttpResponse create(@Body Map data) {
        jsonVersionService.saveFileWithVersion(data)
        HttpResponse.created(data)
    }

    @Get(uri = "/fetchFileVersions/{fileName}", produces = MediaType.APPLICATION_JSON)
    HttpResponse fetchFileVersions(@PathVariable String fileName) {
        Map fileVersionsChangeLog = jsonVersionService.fetchFileVersions(fileName)
        HttpResponse.ok(fileVersionsChangeLog)
    }

    @Get(uri = "/fetchFileNames", produces = MediaType.APPLICATION_JSON)
    HttpResponse fetchFileNames() {
        List fetchFileNames = jsonVersionService.fetchFileNames()
        HttpResponse.ok(fetchFileNames)
    }

    @Get(uri = "/fileContent/{fileName}/{version}", produces = MediaType.APPLICATION_JSON)
    HttpResponse fileContent(@PathVariable String fileName, @PathVariable String version) {
        Map fileContent = jsonVersionService.fileContent(fileName, version)
        HttpResponse.ok(fileContent)
    }

    @Get(uri = "/fileContent/{fileName}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getCurrentVersionFileContent(@PathVariable String fileName) {
        Map changeLogFileContent = jsonVersionService.fetchFileVersions(fileName)
        Map fileContent = jsonVersionService.fileContent(fileName, changeLogFileContent.currentVesion)
        HttpResponse.ok(fileContent)
    }


    /**
     * Example :
     *
     * {
     *     "name" : "fileName",
     *     "version" : "version"
     * }
     *
     * @param data
     * @return
     */
    @Consumes([MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON])
    @Post("/revertFileContent")
    HttpResponse revert(@Body Map data) {
        jsonVersionService.revertFileCurrentVersion(data)
        Map fileVersionsChangeLog = jsonVersionService.fetchFileVersions(data.name)
        HttpResponse.created(fileVersionsChangeLog)
    }


}
