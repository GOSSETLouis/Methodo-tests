package com.jicay.bookmanagement.infrastructure.driving.web

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest
class BookControllerIT(
    @MockkBean private val bookUseCase: BookUseCase,
    private val mockMvc: MockMvc
) : FunSpec({
    extension(SpringExtension)

    test("rest route get books") {
        // GIVEN
        every { bookUseCase.getAllBooks() } returns listOf(Book(1, "A", "B", false))

        // WHEN
        mockMvc.get("/books")
            //THEN
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content {
                    json(
                        // language=json
                        """
                        [
                          {
                            "id": 1,
                            "name": "A",
                            "author": "B",
                            "reserved": false
                          }
                        ]
                        """.trimIndent()
                    )
                }
            }
    }

    test("rest route post book") {
        val book = Book(1, "Les misérables", "Victor Hugo", false)
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "name": "Les misérables",
                  "author": "Victor Hugo",
                  "reserved": false
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }


        verify(exactly = 1) { bookUseCase.addBook(book) }
    }

    test("rest route post book should return 400 when body is not good") {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "title": "Les misérables",
                  "author": "Victor Hugo"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { bookUseCase.addBook(any()) }
    }

    test("rest route reserve book") {
        justRun { bookUseCase.reserveBook(any()) }

        mockMvc.post("/books/reserve/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { bookUseCase.reserveBook(1) }
    }
})