import java.sql.DriverManager
import java.sql.Statement

data class Post(
    val id: Int,
    val title: String,
    val createdDate: String,
    val modifiedDate: String,
)

fun findAllByTitle(titles: List<String>): List<Post> {
    val connection = DriverManager
        .getConnection("jdbc:mysql://ns6569.hostgator.com:3306/ilaworth_new", "ilaworth_alsu", "T]T0?RE+8C+j")
    println(connection.isValid(0))

    val titlesFormatted = titles
        .filterNot { it == "แปดแอดมินเพจ 'เรารักพล.อ. ประยุทธ์' - นพเก้า" }
        .filterNot { it == "ปตท. ฟ้องแอดมินเพจ'ทวงคืนพลังงานไทย' - ศรัลย์" }
        .joinToString(separator = "', '", prefix = "('", postfix = "')") { it }

    val posts = mutableListOf<Post>()
    connection.prepareStatement("SELECT * FROM ilaw_posts WHERE post_type = 'post' and post_title in $titlesFormatted")
        .use { query ->
            val result = query.executeQuery()
            generateSequence { if (result.next()) result else null }
                .map {
                    Post(
                        result.getInt("ID"),
                        result.getString("post_title"),
                        result.getString("post_date"),
                        result.getString("post_modified"),
                    )
                }
                .forEach {
                    posts.add(it)
                }
        }
    println(posts)
    println(posts.size)
    return posts
}

fun setFeaturedImages(posts: List<Post>, postTitleToImage: Map<String, String>) {
    val connection = DriverManager
        .getConnection("jdbc:mysql://ns6569.hostgator.com:3306/ilaworth_new", "ilaworth_alsu", "T]T0?RE+8C+j")
    println(connection.isValid(0))

    //todo change to forEach
    posts.firstOrNull().let {
        val imageFile = postTitleToImage[it?.title]

        val imageName = imageFile?.removeSuffix(".jpg")
        val result = connection.prepareStatement(
            "INSERT INTO `ilaw_posts` (`post_author`, `post_date`, `post_date_gmt`, `post_content`, `post_title`, `post_excerpt`, `post_status`, `comment_status`, `ping_status`, `post_password`, `post_name`, `to_ping`, `pinged`, `post_modified`, `post_modified_gmt`, `post_content_filtered`, `post_parent`, `guid`, `menu_order`, `post_type`, `post_mime_type`, `comment_count`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
            Statement.RETURN_GENERATED_KEYS
        ).apply {
            setInt(1, 16)
            setString(2,  "2024-03-07 13:18:18") //todo wrong
            setString(3, it?.createdDate ?: "") //todo wrong
            setString(4, "")
            setString(5, imageName)
            setString(6, "")
            setString(7, "inherit")
            setString(8, "")
            setString(9, "closed")
            setString(10, "")
            setString(11, imageName)
            setString(12, "")
            setString(13, "")
            setString(14, it?.modifiedDate ?: "")  //todo wrong
            setString(15, it?.modifiedDate ?: "")  //todo wrong
            setString(16, "")
            setInt(17, it?.id ?: 0)
            setString(18, "http://new.ilaw.or.th/wp-content/uploads/2024/03/$imageFile")  // todo need to replace space to dash (exp: "8 admin Cover.jpg" -> 8-admin-Cover.jpg)
            setInt(19, 0)
            setString(20, "attachment")
            setString(21, "image/jpeg")
            setInt(22, 0)
        }

        result.execute()
        println(result)
        println("id: ${it?.id}, title: ${it?.title}, image: $imageFile")

        val generatedKeys = result.generatedKeys
        var id = 0
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1)
            println("Generated ID: $id")
        } else {
            println("No ID obtained.")
        }

        // Close resources
        generatedKeys.close()
        result.close()

        val preparedStatement = connection.prepareStatement(
            "INSERT INTO `ilaw_postmeta` (`post_id`, `meta_key`, `meta_value`) VALUES (?, ?, ?)"
        ).apply {
            setInt(1, it!!.id)
            setString(2, "_thumbnail_id")
            setString(3, "$id")
        }
        preparedStatement.execute()
        preparedStatement.close()
        connection.close()
    }
}