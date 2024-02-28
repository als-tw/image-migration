import java.sql.DriverManager

data class Lawsuit(
    val id: Int,
    val title: String,
    val image: String?,
)

fun findAllWithImage(): List<Lawsuit> {
    val connection = DriverManager
        .getConnection("jdbc:mysql://ns6569.hostgator.com:3306/ilaworth_freedom", "ilaworth_alsu", "T]T0?RE+8C+j")

    println(connection.isValid(0))

    val lawsuits = mutableListOf<Lawsuit>()
    connection.prepareStatement("SELECT * FROM lawsuit").use { query ->
        val result = query.executeQuery()
        generateSequence { if (result.next()) result else null }
            .map {
                Lawsuit(
                    result.getInt("id"),
                    "${result.getString("title_th").trim()} - ${result.getString("defendant_th").trim()}",
                    result.getString("image")
                )
            }
            .filter { it.image != null }
            .forEach {
                lawsuits.add(it)
            }
    }
    println(lawsuits)
    println(lawsuits.size)
    println(findDuplicates(lawsuits))
    return lawsuits
}

fun findAllWithImage2(): Map<String, String> {
    val connection = DriverManager
        .getConnection("jdbc:mysql://ns6569.hostgator.com:3306/ilaworth_freedom", "ilaworth_alsu", "T]T0?RE+8C+j")

    println(connection.isValid(0))

    val lawsuits = mutableMapOf<String, String>()
    connection.prepareStatement("SELECT * FROM lawsuit").use { query ->
        val result = query.executeQuery()
        generateSequence { if (result.next()) result else null }
            .map {
                Lawsuit(
                    result.getInt("id"),
                    "${result.getString("title_th").trim()} - ${result.getString("defendant_th").trim()}",
                    result.getString("image")
                )
            }
            .filter { it.image != null }
            .forEach {
                lawsuits[it.title] = it.image!!
            }
    }
    println(lawsuits)
    println(lawsuits.size)
    return lawsuits
}

fun <T> findDuplicates(list: List<T>): Set<T> {
    val duplicates = list.groupBy { it }.filter { it.value.size > 1 }.keys
    return duplicates.toSet()
}