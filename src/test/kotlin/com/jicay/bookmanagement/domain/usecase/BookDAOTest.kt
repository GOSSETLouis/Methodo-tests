package com.jicay.bookmanagement.infrastructure.driven.adapter

import com.jicay.bookmanagement.domain.model.Book
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.RowMapper

class BookDAOTest : FunSpec({

    val namedParameterJdbcTemplate = mockk<NamedParameterJdbcTemplate>()
    val bookDAO = BookDAO(namedParameterJdbcTemplate)

    test("get all books should return all books") {
        every { namedParameterJdbcTemplate.query(any<String>(), any<Map<String, *>>(), any<RowMapper<Book>>()) } returns listOf(
            Book(1, "Les Misérables", "Victor Hugo", reserved = false),
            Book(2, "Hamlet", "William Shakespeare", reserved = false)
        )

        val res = bookDAO.getAllBooks()

        res.shouldContainExactly(
            Book(1, "Les Misérables", "Victor Hugo", reserved = false),
            Book(2, "Hamlet", "William Shakespeare", reserved = false)
        )
    }

    test("create book should insert a new book") {
        every { namedParameterJdbcTemplate.update(any<String>(), any<Map<String, *>>()) } returns 1

        val book = Book(1, "Les Misérables", "Victor Hugo", reserved = false)

        bookDAO.createBook(book)

        verify(exactly = 1) { namedParameterJdbcTemplate.update(any<String>(), any<Map<String, *>>()) }
    }

    test("get book by id should return the correct book") {
        val bookId = 1L
        val book = Book(1, "Test Book", "Test Author", reserved = false)
        every { namedParameterJdbcTemplate.queryForObject(any<String>(), any<Map<String, *>>(), any<RowMapper<Book>>()) } returns book

        val res = bookDAO.getBookById(bookId)

        res shouldBe book
    }

    test("reserve book should update the book's reserved status") {
        val bookId = 1L
        every { namedParameterJdbcTemplate.update(any<String>(), any<Map<String, *>>()) } returns 1

        bookDAO.reserveBook(bookId)

        verify(exactly = 1) { namedParameterJdbcTemplate.update("UPDATE BOOK SET reserved = TRUE WHERE id = :id", mapOf("id" to bookId)) }
    }
})