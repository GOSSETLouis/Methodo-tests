package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }
    
    fun getBookById(bookId: Long): Book {
        return bookPort.getBookById(bookId)
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBook(bookId: Long) {
        val book = bookPort.getBookById(bookId)
        if (book.reserved) {
            throw IllegalArgumentException("Book is already reserved")
        }
        bookPort.reserveBook(bookId)
    }
}