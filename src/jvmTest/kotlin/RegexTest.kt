

val regex = Regex("^[a-zA-Z0-9](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z0-9]){6,18}[a-zA-Z0-9]\$")
fun main() {
    println(regex.containsMatchIn("안녕하세요1234"))
    println(regex.containsMatchIn("hello1234"))
}