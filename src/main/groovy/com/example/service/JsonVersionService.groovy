package com.example.service


import com.example.repository.JsonVersionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton
import java.text.ParseException

@Singleton
class JsonVersionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonVersionService.class);
    private final String jsonVersionLogFileName = "changelog"

    @Inject
    JsonVersionRepository jsonVersionRepository


    List fetchFileNames() {
        List fileNames = []

        jsonVersionRepository.fetchFileNames().each {
            fileNames.add([name: it])
        }

        return fileNames
    }


    Map fetchFileVersions(String fileName) {
        jsonVersionRepository.getContentFromChangeLogFile(jsonVersionLogFileName, fileName)
    }


    Map fileContent(String fileName, String version) {
        jsonVersionRepository.getContentFromFile(fileName, version)
    }

    void revertFileCurrentVersion(Map inputData){
        Map changeLogContent = jsonVersionRepository.getContentFromChangeLogFile(jsonVersionLogFileName, inputData.name)
        LOGGER.info("change log file content  : " + changeLogContent.toString())
        changeLogContent.currentVersion = inputData.version
        jsonVersionRepository.createChangeLogFileWithContent(jsonVersionLogFileName, inputData.name, changeLogContent)
    }
    
    void saveFileWithVersion(Map inputData) {
        Map jsonData = inputData.fileContent
        def newTime = jsonData.fileTimestamp ? json2date(jsonData.fileTimestamp).getTime() : (new Date()).getTime()
        if (newTime <= (new Date()).getTime()) {
            jsonData.fileTimestamp = new Date().toInstant().toString()
        }

        // step 1 : extract file name --> constantName
        String name = jsonData.constantName
        // step 2 : check folder is exists or not, if not create a folder with
        if (!jsonVersionRepository.isDirectoryExists(name)) {
            LOGGER.info("$name folder is not exists, Hence folder has been created !!")
            if (jsonVersionRepository.creatDirectory(name)) {
                LOGGER.info("$name folder has been created !!")
            }
        }

        // step 3 : check file version is exists or not, if not create a version file
        // step 3.1 : check default version file exist or not, if not create default version, if exists
        //            get the latest version and create file with latest version
        if (jsonVersionRepository.isFileExists(name)) {
            LOGGER.info("$name version 0 file alredy exists, hence pull changelog.json file conent !")
            // get latest version number from changelog.json file
            Map changeLogContent = jsonVersionRepository.getContentFromChangeLogFile(jsonVersionLogFileName, name)
            LOGGER.info("change log file content  : " + changeLogContent.toString())
            String versionNumber = "v${getVersionNumber(changeLogContent.latestVersion) + 1}"
            jsonVersionRepository.createJsonFileWithContent(name, versionNumber, jsonData)
            changeLogContent.currentVersion = versionNumber
            changeLogContent.latestVersion = versionNumber
            changeLogContent.data << [name        : name, version: versionNumber, modifiedBy: jsonData.owner,
                                      modifiedDate: jsonData.fileTimestamp, commitMessage: inputData.commitMessage]
            jsonVersionRepository.createChangeLogFileWithContent(jsonVersionLogFileName, name, changeLogContent)

        } else {
            jsonVersionRepository.createJsonFileWithContent(name, jsonData)
            LOGGER.info("$name version 0 file has been created !!")
            jsonVersionRepository.createChangeLogFileWithContent(jsonVersionLogFileName, name, [currentVersion: "v0", latestVersion: "v0",
                                                                                                data         : [[name        : name, version: "v0", modifiedBy: jsonData.owner,
                                                                                                                 modifiedDate: jsonData.fileTimestamp, commitMessage: inputData.commitMessage]]])
        }

    }


    def json2date = { s ->
        Date date
        try {
            date = s ? javax.xml.bind.DatatypeConverter.parseDateTime(s).time : null
        } catch (ParseException e) {
            LOGGER.error "Exception in json2date ${e}"
        }
        date
    }

    Integer getVersionNumber(String version) {
        return Integer.valueOf(version.substring(version.lastIndexOf("v") + 1))
    }

}
