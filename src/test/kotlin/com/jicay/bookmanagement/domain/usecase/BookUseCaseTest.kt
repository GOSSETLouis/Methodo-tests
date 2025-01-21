package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.justRun
import io.mockk.Runs
import io.mockk.mockk
import io.mockk.verify
import io.mockk.just

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    test("get all books should returns all books sorted by name") {
        every { bookPort.getAllBooks() } returns listOf(
            Book(1, "Les Misérables", "Victor Hugo", reserved = false),
            Book(2, "Hamlet", "William Shakespeare", reserved = false)
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainExactly(
            Book(1, "Hamlet", "William Shakespeare", reserved = false),
            Book(2, "Les Misérables", "Victor Hugo", reserved = false)
        )
    }

    test("add book") {
        justRun { bookPort.createBook(any()) }

        val book = Book(1, "Les Misérables", "Victor Hugo", reserved = false)

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("reserve book should reserve the book if available") {
        val bookId = 1L
        val book = Book(1, "Test Book", "Test Author", reserved = false)
        every { bookPort.getBookById(bookId) } returns book
        every { bookPort.reserveBook(bookId) } just Runs

        bookUseCase.reserveBook(bookId)

        verify(exactly = 1) { bookPort.reserveBook(bookId) }
    }

    test("reserve book should throw exception if book is already reserved") {
        val bookId = 1L
        val book = Book(1, "Test Book", "Test Author", reserved = true)
        every { bookPort.getBookById(bookId) } returns book

        shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBook(bookId)
        }
    }

})