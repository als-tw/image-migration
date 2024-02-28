import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun findAndCopyImages(names: List<String>) {
    names.forEach {
        findAndCopyImage(it)
    }
}

fun findAndCopyImage(imageFileName: String) {
    val sourceFolder = File("src/main/resources/files")
    val destinationFolder = File("src/main/resources/images")

    if (!sourceFolder.exists() || !destinationFolder.exists()) {
        println("Source or destination folder does not exist.")
        return
    }

    val imageFile = searchImageFile(sourceFolder, imageFileName)

    if (imageFile == null) {
        println("Image file $imageFileName not found in the source folder or its subfolders.")
        return
    }

    try {
        val destinationPath = Paths.get("src/main/resources/images", imageFileName)
        Files.copy(imageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING)
        println("Image file $imageFileName copied successfully to src/main/resources/images")
    } catch (e: Exception) {
        println("Error occurred while copying the image file: ${e.message}")
    }
}


fun searchImageFile(folder: File, imageFileName: String): File? {
    val files = folder.listFiles() ?: return null

    for (file in files) {
        if (file.isDirectory) {
            val imageFile = searchImageFile(file, imageFileName)
            if (imageFile != null) {
                return imageFile
            }
        } else if (file.name.equals(imageFileName, ignoreCase = true)) {
            return file
        }
    }
    return null
}