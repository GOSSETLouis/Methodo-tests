package com.jicay.bookmanagement

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort
import org.hamcrest.Matchers.`is`

class ReserveBookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @When("the user creates the book {string} written by {string} with reserved status false")
    fun createBook(title: String, author: String) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "id": 1,
                      "name": "$title",
                      "author": "$author",
                      "reserved": false
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user reserves the book with id {int}")
    fun reserveBookById(id: Int) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .pathParam("id", id)
            .`when`()
            .post("/books/reserve/{id}")
            .then()
            .statusCode(200)
    }

    @Then("the book with id {int} should be reserved")
fun getBookById(id: Int) {
    lastBookResult = given()
        .contentType(ContentType.JSON)
        .and()
        .pathParam("id", id)
        .`when`()
        .get("/books/{id}")
        .then()
        .statusCode(200)
        .body("reserved", `is`(true)) // Vérification que la propriété "reserved" est bien à true
}

    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }
}