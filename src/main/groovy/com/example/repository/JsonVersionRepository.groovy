package com.example.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Property

import javax.inject.Singleton

@Singleton
class JsonVersionRepository {

    @Property(name = "json.file.location")
    protected String jsonFileLocation

    ObjectMapper mapper = new ObjectMapper(); // create once, reuse


    boolean creatDirectory(String name) {
        return new File(getJsonFileLocation() + "/" + name).mkdir()
    }

    File createFile(String fName, String version = "v0", String extension = "json") {
        File file = new File(getJsonFileLocation() + "/$fName/" + prepareJsonFileNameWithVersionAndExtension(fName, version, extension))
        if (file.exists() && file.isFile()) {
            return file
        }
        file.createNewFile()
        return file
    }

    boolean isDirectoryExists(String name) {
        boolean isDirectoryExists = false
        File file = new File(getJsonFileLocation() + "/$name")
        if (file.exists() && file.isDirectory()) {
            isDirectoryExists = true
        }
        return isDirectoryExists
    }

    boolean isFileExists(String fName, String version = "v0", String extension = "json") {
        boolean isFileExists = false
        File file = new File(getJsonFileLocation() + "/$fName/" + prepareJsonFileNameWithVersionAndExtension(fName, version, extension))
        if (file.exists() && file.isFile()) {
            isFileExists = true
        }
        return isFileExists
    }

    String prepareJsonFileNameWithVersionAndExtension(String fName, String version, String extension) {
        if (version) {
            return fName + "_" + version + "." + extension
        } else {
            return fName + "." + extension
        }
    }

    void createJsonFileWithContent(String name, version = "v0", String extension = "json", Map content) {
        File file = createFile(name, version, extension)
        file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(content))
    }

    void createChangeLogFileWithContent(String name, String rootFolderName, String extension = "json", Map content) {
        File file = new File(getJsonFileLocation() + "/$rootFolderName/" + prepareJsonFileNameWithVersionAndExtension(name, null, extension))
        if (!file.exists()) {
            file.createNewFile()
        }
        file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(content))
    }

    Map getContentFromChangeLogFile(String name, String rootFolderName, String extension = "json") {
        Map content = [:]
        File file = new File(getJsonFileLocation() + "/$rootFolderName/" + prepareJsonFileNameWithVersionAndExtension(name, null, extension))
        if (file.exists()) {
            content = mapper.readValue(file, Map.class);
        }
        return content
    }

    List fetchFileNames() {
        return Arrays.asList(new File(getJsonFileLocation()).list())
    }

    Map getContentFromFile(String fileName, String version, String extension = "json") {
        Map content = [:]
        File file = new File(getJsonFileLocation() + "/$fileName/" + prepareJsonFileNameWithVersionAndExtension(fileName, version, extension))
        if (file.exists()) {
            content = mapper.readValue(file, Map.class);
        }
        return content
    }

    String getJsonFileLocation() {
        return jsonFileLocation
    }


}
