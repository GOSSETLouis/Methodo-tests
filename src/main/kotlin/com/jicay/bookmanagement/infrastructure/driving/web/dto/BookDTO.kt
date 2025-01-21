package com.jicay.bookmanagement.infrastructure.driving.web.dto

import com.jicay.bookmanagement.domain.model.Book

data class BookDTO(val id: Long, val name: String, val author: String, val reserved: Boolean) {
    fun toDomain(): Book {
        return Book(
            id = this.id,
            name = this.name,
            author = this.author,
            reserved = this.reserved
        )
    }
}

fun Book.toDto() = BookDTO(
    id = this.id,
    name = this.name,
    author = this.author,
    reserved = this.reserved
)