package datavault

import java.io.File
import archive.ZipArchive
import java.io.FileWriter

object ExtractRawModel {

    def run(input: File, output: File) = {
        val archive = new ZipArchive(input)
        val model = Model.fromArchive(archive)
        Model.save(model, output)       
        Constants.Success
    }

}
