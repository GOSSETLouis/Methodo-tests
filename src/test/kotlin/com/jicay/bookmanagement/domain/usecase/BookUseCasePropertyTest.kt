package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class InMemoryBookPort : BookPort {
    private val books = mutableListOf<Book>()

    override fun getAllBooks(): List<Book> = books

    override fun createBook(book: Book) {
        books.add(book)
    }

    override fun getBookById(bookId: Long): Book {
        return books.find { it.id == bookId } ?: throw IllegalArgumentException("Book not found")
    }

    override fun reserveBook(bookId: Long) {
        val book = getBookById(bookId)
        books.remove(book)
        books.add(book.copy(reserved = true))
    }

    fun clear() {
        books.clear()
    }
}

class BookUseCasePropertyTest : FunSpec({

    val bookPort = InMemoryBookPort()
    val bookUseCase = BookUseCase(bookPort)

    test("should return all elements in the alphabetical order") {
        checkAll(Arb.int(1..100)) { nbItems ->
            bookPort.clear()

            val arb = Arb.stringPattern("""[a-z]{1,10}""")

            val titles = mutableListOf<String>()

            for (i in 1..nbItems) {
                val title = arb.next()
                titles.add(title)
                bookUseCase.addBook(Book(id = i.toLong(), name = title, author = "Victor Hugo", reserved = false))
            }

            val res = bookUseCase.getAllBooks()

            res.map { it.name } shouldContainExactly titles.sorted()
        }
    }
})
