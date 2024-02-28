fun main() {
    //copy only lawsuit's images to src/main/resources/images folder
//    val images = findAllWithImage().map { it.image!! }
//    findAndCopyImages(images)

    //image migration
    val titleToImage = findAllWithImage2()
    val posts = findAllByTitle(titleToImage.keys.toList())
    setFeaturedImages(posts, titleToImage)
}